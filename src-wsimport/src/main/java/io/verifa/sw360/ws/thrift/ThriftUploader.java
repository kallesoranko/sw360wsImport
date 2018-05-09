/*
 * Copyright (c) Bosch Software Innovations GmbH 2017.
 * With modifications by Verifa Oy, 2018.
 * Part of the SW360 Portal Project.
 *
 * SPDX-License-Identifier: EPL-1.0
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.verifa.sw360.ws.thrift;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import io.verifa.sw360.ws.domain.WsLibrary;
import io.verifa.sw360.ws.domain.WsLicense;
import io.verifa.sw360.ws.domain.WsProject;
import io.verifa.sw360.ws.entitytranslation.WsLibraryToSw360ComponentTranslator;
import io.verifa.sw360.ws.entitytranslation.WsLibraryToSw360ReleaseTranslator;
import io.verifa.sw360.ws.entitytranslation.WsLicenseToSw360LicenseTranslator;
import io.verifa.sw360.ws.entitytranslation.WsProjectToSw360ProjectTranslator;
import io.verifa.sw360.ws.entitytranslation.helper.ReleaseRelation;
import io.verifa.sw360.ws.rest.WsProjectService;
import io.verifa.sw360.ws.thrift.helper.ProjectImportError;
import io.verifa.sw360.ws.thrift.helper.ProjectImportResult;
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
import static io.verifa.sw360.ws.utility.TranslationConstants.UNKNOWN;

/**
 * @author: ksoranko@verifa.io
 */
public class ThriftUploader {

    private static final Logger LOGGER = Logger.getLogger(ThriftUploader.class);
    private final WsLibraryToSw360ComponentTranslator libraryToComponentTranslator = new WsLibraryToSw360ComponentTranslator();
    private final WsLibraryToSw360ReleaseTranslator libraryToReleaseTranslator = new WsLibraryToSw360ReleaseTranslator();
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
                        LOGGER.info(wsName + " to import matches a " + sw360name + " with id: " + nomineeId.get());
                        nominees.stream()
                                .skip(1)
                                .forEach(n -> LOGGER.error(wsName + " to import would also match a " + sw360name + " with id: " + idExtractor.apply(n)));
                    }
                    return nomineeId;
                }
        );
    }

    protected ProjectImportResult createProject(WsProject wsProject, User user) throws TException  {
        LOGGER.info("Try to import whitesource project: " + wsProject.getProjectName());
        LOGGER.info("Sw360-User: " + user.email);

        if (wsProject.getProjectName() == null || wsProject.getProjectToken() == null) {
            LOGGER.error("Unable to get project: " + wsProject.getProjectName() + " with token: " + wsProject.getProjectToken() + " from whitesource!");
            return new ProjectImportResult(ProjectImportError.PROJECT_NOT_FOUND);
        }

        if (thriftExchange.doesProjectAlreadyExists(wsProject.getProjectToken(), wsProject.getProjectName(), user)) {
            LOGGER.error("Project already in database: " + wsProject.getProjectName());
            return new ProjectImportResult(ProjectImportError.PROJECT_ALREADY_EXISTS);
        }

        Project projectSW360 = projectToProjectTranslator.apply(wsProject);
        Set<ReleaseRelation> releases = createReleases(wsProject, user);
        projectSW360.setProjectResponsible(user.getEmail());
        projectSW360.setDescription("Imported from Whitesource with project token: " + wsProject.getProjectToken());
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
                LOGGER.error("Error when creating the project", e);
                wsImportStatus.setRequestStatus(RequestStatus.FAILURE);
                return wsImportStatus;
            }
            if (projectImportResult.isSuccess()) {
                successfulIds.add(wsProject.getProjectName());
            } else {
                LOGGER.error("Could not import project with whitesource name: " + wsProject.getProjectName());
                failedIds.put(wsProject.getProjectName(), projectImportResult.getError().getText());
            }
        }
        return wsImportStatus
                .setFailedIds(failedIds)
                .setSuccessfulIds(successfulIds);
    }

    protected String getOrCreateLicenseId(io.verifa.sw360.ws.domain.WsLicense wsLicense, User user) {
        LOGGER.info("Try to import whitesource License: " + wsLicense.getName());

        Optional<String> potentialLicenseId = searchExistingEntityId(thriftExchange.searchLicenseByWsName(wsLicense.getName()),
                License::getId,
                "License",
                "Licence");

        if (potentialLicenseId.isPresent()) {
            return potentialLicenseId.get();
        } else {
            License sw360license = licenseToLicenseTranslator.apply(wsLicense);
            String licenseId = thriftExchange.addLicense(sw360license, user);
            LOGGER.info("Imported license: " + licenseId);
            return licenseId;
        }
    }

    private String getOrCreateComponent(io.verifa.sw360.ws.domain.WsLibrary wsLibrary, User sw360user) {
        LOGGER.info("Try to import whitesource Component: " + wsLibrary.getName());

        String componentVersion = isNullOrEmpty(wsLibrary.getVersion()) ? UNKNOWN : wsLibrary.getVersion();
        Optional<String> potentialReleaseId = searchExistingEntityId(thriftExchange.searchReleaseByNameAndVersion(wsLibrary.getName(), componentVersion),
                Release::getId,
                "Library",
                "Release");
        if (potentialReleaseId.isPresent()) {
            return potentialReleaseId.get();
        }

        Release releaseSW360 = libraryToReleaseTranslator.apply(wsLibrary);
        releaseSW360.getModerators().add(sw360user.getEmail());
        Optional<String> potentialComponentId = searchExistingEntityId(thriftExchange.searchComponentByName(wsLibrary.getName()),
                Component::getId,
                "Library",
                "Component");

        String componentId;
        if (potentialComponentId.isPresent()) {
            componentId = potentialComponentId.get();
        } else {
            Component sw360component = libraryToComponentTranslator.apply(wsLibrary);
            componentId = thriftExchange.addComponent(sw360component, sw360user);
        }
        releaseSW360.setComponentId(componentId);

        if (wsLibrary.getLicenses() == null) {
            releaseSW360.setMainLicenseIds(Collections.singleton(UNKNOWN));
        } else {
            Set<String> mainLicenses = new HashSet<>();
            for (WsLicense wsLicense : wsLibrary.getLicenses()) {
                mainLicenses.add(getOrCreateLicenseId(wsLicense, sw360user));
            }
            releaseSW360.setMainLicenseIds(mainLicenses);
        }

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
        io.verifa.sw360.ws.domain.WsLibrary[] libraries =  new WsProjectService().getProjectLicenses(wsProject.getProjectToken());
        List<WsLibrary> libraryList = Arrays.asList(libraries);
        if (libraryList == null) {
            return ImmutableSet.of();
        }

        Set<ReleaseRelation> releases = libraryList .stream()
                .map(c -> createReleaseRelation(c, user))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (releases.size() != libraryList .size()) {
            LOGGER.warn("expected to get " + libraryList.size() + " different ids of releases but got " + releases.size());
        } else {
            LOGGER.info("The expected number of releases was imported or already found in database.");
        }

        return releases;
    }

    /* Needed if project hierarchy is used */
    /*
    private static void getDeps(WsLibrary[] wsLibraries) {
        for (WsLibrary wsLibrary : wsLibraries)
            if (wsLibrary.getDependencies() == null) {
                libraryList.add(wsLibrary);
            } else {
                libraryList.add(wsLibrary);
                getDeps(wsLibrary.getDependencies());
            }
    }
    */





}
