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

import io.verifa.sw360.ws.utility.TranslationConstants;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.eclipse.sw360.datahandler.common.CommonUtils;
import org.eclipse.sw360.datahandler.thrift.AddDocumentRequestStatus;
import org.eclipse.sw360.datahandler.thrift.AddDocumentRequestSummary;
import org.eclipse.sw360.datahandler.thrift.components.Component;
import org.eclipse.sw360.datahandler.thrift.components.Release;
import org.eclipse.sw360.datahandler.thrift.licenses.License;
import org.eclipse.sw360.datahandler.thrift.projects.Project;
import org.eclipse.sw360.datahandler.thrift.users.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.eclipse.sw360.datahandler.common.CommonUtils.nullToEmptyList;
import static org.eclipse.sw360.datahandler.thrift.AddDocumentRequestStatus.DUPLICATE;
import static org.eclipse.sw360.datahandler.thrift.AddDocumentRequestStatus.SUCCESS;

/**
 * @author: ksoranko@verifa.io
 */
public class ThriftExchange {

    private static final Logger logger = Logger.getLogger(ThriftExchange.class);
    private final ThriftApi thriftApi = new ThriftApiSimple();

    public Optional<List<Release>> searchReleaseByNameAndVersion(String name, String version) {
        List<Release> releases = null;
        try {
            releases = thriftApi.getComponentClient().searchReleaseByNamePrefix(name);
        } catch (TException e) {
            logger.error("Could not fetch Release list for name=[" + name + "], version=[" + version + "]:" + e);
        }

        if (releases != null) {
            return Optional.of(releases.stream()
                    .filter(r -> r.getVersion().equals(version))
                    .collect(Collectors.toList()));
        } else {
            return Optional.empty();
        }
    }

    public Optional<List<Component>> searchComponentByName(String name) {
        try {
            return Optional.of(thriftApi.getComponentClient().searchComponentForExport(name));
        } catch (TException e) {
            logger.error("Could not fetch Component list for name=[" + name + "]:" + e);
            return Optional.empty();
        }
    }

    /**
     * Add the Component to DB. Required fields are: name.
     *
     * @param component Component to be added
     * @param user
     * @return ComponentId-String from DB.
     */
    public String addComponent(Component component, User user) {
        String componentId = null;
        try {
            logger.info("------ component name: " +  component.getName());
            AddDocumentRequestSummary summary = getComponentClient().addComponent(component, user);
            if (SUCCESS.equals(summary.getRequestStatus())) {
                componentId = summary.getId();
            } else {
                logFailedAddDocument(summary.getRequestStatus(), "component");
            }
        } catch (TException e) {
            logger.error("Could not add Component for user with email=[" + user.getEmail() + "]:" + e);
        }
        return componentId;
    }

    private void logFailedAddDocument(AddDocumentRequestStatus status, String documentTypeString) {
        if (DUPLICATE.equals(status)) {
            logger.error("Could not add duplicate " + documentTypeString + ".");
        } else {
            logger.error("Adding the " + documentTypeString + "failed.");
        }
    }

    /**
     * Add the Release to DB. Required fields are: name, version, componentId.
     *
     * @param release Release to be added
     * @param user
     * @return releaseId-String from DB.
     */
    public String addRelease(Release release, User user) {
        String releaseId = null;
        try {
            AddDocumentRequestSummary summary = getComponentClient().addRelease(release, user);
            if (SUCCESS.equals(summary.getRequestStatus())) {
                releaseId = summary.getId();
            } else {
                logFailedAddDocument(summary.getRequestStatus(), "release");
            }
        } catch (TException e) {
            logger.error("Could not add Release for user with email=[" + user.getEmail() + "]:" + e);
        }
        return releaseId;
    }

    public String addProject(Project project, User user) {
        String projectId = null;
        try {
            AddDocumentRequestSummary summary = thriftApi.getProjectClient().addProject(project, user);
            if (SUCCESS.equals(summary.getRequestStatus())) {
                projectId = summary.getId();
            } else {
                logFailedAddDocument(summary.getRequestStatus(), "project");
            }
        } catch (TException e) {
            logger.error("Could not add Project for user with email=[" + user.getEmail() + "]:" + e);
        }
        return projectId;
    }

    public String addLicense(License license, User user) {
        List<License> licenses = null;
        try {
            licenses = thriftApi.getLicenseClient().addLicenses(Collections.singletonList(license), user);
        } catch (TException e) {
            logger.error("Could not add License for user with email=[" + user.getEmail() + "]:" + e);
        }
        return licenses == null ? null : licenses.get(0).getId();
    }

    private org.eclipse.sw360.datahandler.thrift.components.ComponentService.Iface getComponentClient() {
        return thriftApi.getComponentClient();
    }


    public Optional<List<License>> searchLicenseByWsName(String wsId) {
        return getFilteredLicenseList(license ->
                        license.isSetExternalIds() && CommonUtils.nullToEmptyString(license.getExternalIds().get(TranslationConstants.WS_ID)).equals(wsId),
                "wsId=[" + wsId + "]:"
        );
    }

    private Optional<List<License>> getFilteredLicenseList(Predicate<License> filter, String selector) {
        try {
            return Optional.of(thriftApi.getLicenseClient()
                    .getLicenses()
                    .stream()
                    .filter(filter)
                    .collect(Collectors.toList()));
        } catch (TException e) {
            logger.error("Could not fetch License list for " + selector + ": " + e);
            return Optional.empty();
        }
    }

    /*
     * Check whether projehct already exist
     */
    boolean doesProjectAlreadyExists(String wsProjectToken, String wsProjectName, User user) throws TException {
        List<Project> accessibleProjects = getAccessibleProjectsSummary(user);

        if (hasAccessibleProjectWithWsToken(wsProjectToken, accessibleProjects)) {
            logger.info("Project to import was already imported with bdpId: " + wsProjectToken);
            return true;
        }
        if (hasAccessibleProjectWithWsName(wsProjectName, accessibleProjects)) {
            logger.info("Project to import already exists in the DB with name: " + wsProjectName);
            return true;
        }
        return false;
    }

    private List<Project> getAccessibleProjectsSummary(User user) {
        List<Project> accessibleProjectsSummary = null;
        try {
            accessibleProjectsSummary = getProjectClient().getAccessibleProjectsSummary(user);
        } catch (TException e) {
            logger.error("Could not fetch Project list for user with email=[" + user.getEmail() + "]:" + e);
        }
        return nullToEmptyList(accessibleProjectsSummary);
    }

    private org.eclipse.sw360.datahandler.thrift.projects.ProjectService.Iface getProjectClient() {
        return thriftApi.getProjectClient();
    }

    private boolean hasAccessibleProjectWithWsToken(String wsProjectToken, List<Project> accessibleProjects) throws TException {
        return accessibleProjects.stream()
                .filter(Project::isSetExternalIds)
                .anyMatch(project -> wsProjectToken.equals(project.getExternalIds().get(TranslationConstants.WS_ID)));
    }

    private boolean hasAccessibleProjectWithWsName(String wsProjectName, List<Project> accessibleProjects) throws TException {
        return accessibleProjects.stream()
                .anyMatch(project -> wsProjectName.equals(project.getName()));
    }
}
