/*
 * Copyright (c) Verifa Oy, 2018.
 * Part of the SW360 Portal Project.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.verifa.sw360.ws.thrift;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import io.verifa.sw360.ws.thrift.helper.ProjectImportError;
import io.verifa.sw360.ws.thrift.helper.ProjectImportResult;
import io.verifa.sw360.ws.domain.WsProject;
import io.verifa.sw360.ws.entitytranslation.WsComponentToSw360ComponentTranslator;
import io.verifa.sw360.ws.entitytranslation.WsComponentToSw360ReleaseTranslator;
import io.verifa.sw360.ws.entitytranslation.WsLicenseToSw360LicenseTranslator;
import io.verifa.sw360.ws.entitytranslation.WsProjectToSw360ProjectTranslator;
import io.verifa.sw360.ws.entitytranslation.helper.ReleaseRelation;
import io.verifa.sw360.ws.rest.WsProjectService;
import io.verifa.sw360.ws.utility.WsApiAccess;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.eclipse.sw360.datahandler.thrift.ReleaseRelationship;
import org.eclipse.sw360.datahandler.thrift.RequestStatus;
import org.eclipse.sw360.datahandler.thrift.components.Component;
import org.eclipse.sw360.datahandler.thrift.components.Release;
import org.eclipse.sw360.datahandler.thrift.importstatus.ImportStatus;
import org.eclipse.sw360.datahandler.thrift.licenses.License;
import org.eclipse.sw360.datahandler.thrift.projects.Project;
import org.eclipse.sw360.datahandler.thrift.users.User;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author: ksoranko@verifa.io
 */
public class ThriftUploader {

    private static final Logger logger = Logger.getLogger(ThriftUploader.class);

    private final WsComponentToSw360ComponentTranslator componentToComponentTranslator = new WsComponentToSw360ComponentTranslator();
    private final WsComponentToSw360ReleaseTranslator componentToReleaseTranslator = new WsComponentToSw360ReleaseTranslator();
    private final WsLicenseToSw360LicenseTranslator licenseToLicenseTranslator = new WsLicenseToSw360LicenseTranslator();
    private final WsProjectToSw360ProjectTranslator projectToProjectTranslator = new WsProjectToSw360ProjectTranslator();

    private final ThriftExchange thriftExchange;
    private WsApiAccess wsApiAccess;

    public ThriftUploader(WsApiAccess wsApiAccess) {
        this(new ThriftExchange(), wsApiAccess);
    }

    @VisibleForTesting
    ThriftUploader(ThriftExchange thriftExchange, WsApiAccess wsApiAccess) {
        this.thriftExchange=thriftExchange;
        this.wsApiAccess = wsApiAccess;
    }

    private <T> Optional<String> searchExistingEntityId(Optional<List<T>> nomineesOpt, Function<T, String> idExtractor, String wsName, String sw360name) {
        return nomineesOpt.flatMap(
                nominees -> {
                    Optional<String> nomineeId = nominees.stream()
                            .findFirst()
                            .map(idExtractor);
                    if (nomineeId.isPresent()) {
                        logger.info(wsName + " to import matches a " + sw360name + " with id: " + nomineeId.get());
                        nominees.stream()
                                .skip(1)
                                .forEach(n -> logger.error(wsName + " to import would also match a " + sw360name + " with id: " + idExtractor.apply(n)));
                    }
                    return nomineeId;
                }
        );
    }

    /*
     *
     * Project <-> ProjectInfo relation
     * Create doesProjectAlreadyExists method into ThriftExchange.java
     *
     */
    protected ProjectImportResult createProject(WsProject wsProject, User user) throws TException  {
        logger.info("Try to import whitesource project: " + wsProject.getProjectName());
        logger.info("Sw360-User: " + user.email);

        if (wsProject.getProjectName() == null || wsProject.getProjectToken() == null) {
            logger.error("Unable to get project: " + wsProject.getProjectName() + " with token: " + wsProject.getProjectToken() + " from whitesource!");
            return new ProjectImportResult(ProjectImportError.PROJECT_NOT_FOUND);
        }

        if (thriftExchange.doesProjectAlreadyExists(wsProject.getProjectToken(), wsProject.getProjectName(), user)) {
            logger.error("Project already in database: " + wsProject.getProjectName());
            return new ProjectImportResult(ProjectImportError.PROJECT_ALREADY_EXISTS);
        }

        Project projectSW360 = projectToProjectTranslator.apply(wsProject);
        Set<ReleaseRelation> releases = createReleases(wsProject, user);
        projectSW360.setProjectResponsible(user.getEmail());
        projectSW360.setDescription("whitesource project token: " + wsProject.getProjectToken());
        projectSW360.setReleaseIdToUsage(releases.stream()
                .collect(Collectors.toMap(ReleaseRelation::getReleaseId, ReleaseRelation::getProjectReleaseRelationship)));

        String project = thriftExchange.addProject(projectSW360, user);
        if(isNullOrEmpty(project)) {
            return new ProjectImportResult(ProjectImportError.OTHER);
        } else {
            return new ProjectImportResult(project);
        }
    }

    public ImportStatus importWsProjects(Collection<WsProject> wsProjects, User user) {
        List<String> successfulIds = new ArrayList<>();
        Map<String, String> failedIds = new HashMap<>();
        ImportStatus wsImportStatus = new ImportStatus().setRequestStatus(RequestStatus.SUCCESS);

        for (WsProject wsProject : wsProjects) {
            ProjectImportResult projectImportResult;
            try{
                projectImportResult = createProject(wsProject, user);
            } catch (TException e){
                logger.error("Error when creating the project", e);
                wsImportStatus.setRequestStatus(RequestStatus.FAILURE);
                return wsImportStatus;
            }
            if (projectImportResult.isSuccess()) {
                successfulIds.add(wsProject.getProjectName());
            } else {
                logger.error("Could not import project with whitesource name: " + wsProject.getProjectName());
                failedIds.put(wsProject.getProjectName(), projectImportResult.getError().getText());
            }
        }
        return wsImportStatus
                .setFailedIds(failedIds)
                .setSuccessfulIds(successfulIds);
    }

    /*
     *
     * License ID
     *
     */
    protected String getOrCreateLicenseId(io.verifa.sw360.ws.domain.WsLicense wsLicense, User user) {
        logger.info("Try to import whitesource License: " + wsLicense.getName());

        Optional<String> potentialLicenseId = searchExistingEntityId(thriftExchange.searchLicenseByWsName(wsLicense.getName()),
                License::getId,
                "License",
                "Licence");

        if (potentialLicenseId.isPresent()) {
            return potentialLicenseId.get();
        } else {
            License sw360license = licenseToLicenseTranslator.apply(wsLicense);
            String licenseId = thriftExchange.addLicense(sw360license, user);
            logger.info("Imported license: " + licenseId);
            return licenseId;
        }
    }

    /*
     *
     * Component's license
     *
     */
    private String getOrCreateComponent(io.verifa.sw360.ws.domain.WsLibrary wsLibrary, User sw360user) {
        logger.info("Try to import whitesource Component: " + wsLibrary.getName());

        String componentVersion = isNullOrEmpty(wsLibrary.getVersion()) ? WsComponentToSw360ReleaseTranslator.unknownVersionString : wsLibrary.getVersion();
        Optional<String> potentialReleaseId = searchExistingEntityId(thriftExchange.searchReleaseByNameAndVersion(wsLibrary.getName(), componentVersion),
                Release::getId,
                "Library",
                "Release");
        if (potentialReleaseId.isPresent()) {
            return potentialReleaseId.get();
        }

        Release releaseSW360 = componentToReleaseTranslator.apply(wsLibrary);
        releaseSW360.getModerators().add(sw360user.getEmail());
        Optional<String> potentialComponentId = searchExistingEntityId(thriftExchange.searchComponentByName(wsLibrary.getName()),
                Component::getId,
                "Library",
                "Component");
        String componentId;
        if (potentialComponentId.isPresent()) {
            componentId = potentialComponentId.get();
        } else {
            Component sw360component = componentToComponentTranslator.apply(wsLibrary);
            componentId = thriftExchange.addComponent(sw360component, sw360user);
        }

        releaseSW360.setComponentId(componentId);

        return thriftExchange.addRelease(releaseSW360, sw360user);
    }

    private ReleaseRelation createReleaseRelation(io.verifa.sw360.ws.domain.WsLibrary wsLibrary, User user) {
        String releaseId = getOrCreateComponent(wsLibrary, user);
        if (releaseId == null) {
            return null;
        } else {
            ReleaseRelationship releaseRelationship = ReleaseRelationship.UNKNOWN;
            return new ReleaseRelation(releaseId, releaseRelationship);
        }
    }

    private Set<ReleaseRelation> createReleases(WsProject wsProject, User user) {
        WsProjectService wsProjectService = new WsProjectService();
        Collection<io.verifa.sw360.ws.domain.WsLibrary> libraries =  wsProjectService.getProjectHierarchy(wsProject.getProjectToken());

        if (libraries == null) {
            return ImmutableSet.of();
        }

        Set<ReleaseRelation> releases = libraries.stream()
                .map(c -> createReleaseRelation(c, user))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (releases.size() != libraries.size()) {
            logger.warn("expected to get " + libraries.size() + " different ids of releases but got " + releases.size());
        } else {
            logger.info("The expected number of releases was imported or already found in database.");
        }

        return releases;
    }


}
