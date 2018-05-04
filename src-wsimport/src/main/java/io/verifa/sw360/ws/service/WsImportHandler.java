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
package io.verifa.sw360.ws.service;

import io.verifa.sw360.ws.domain.WsProject;
import io.verifa.sw360.ws.rest.WsProjectService;
import io.verifa.sw360.ws.thrift.ThriftUploader;
import io.verifa.sw360.ws.utility.TranslationConstants;
import io.verifa.sw360.ws.utility.WsApiAccess;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.eclipse.sw360.datahandler.thrift.importstatus.ImportStatus;
import org.eclipse.sw360.datahandler.thrift.projectimport.ProjectImportService;
import org.eclipse.sw360.datahandler.thrift.projectimport.RemoteCredentials;

import org.eclipse.sw360.datahandler.thrift.projectimport.WSCredentials;
import org.eclipse.sw360.datahandler.thrift.projects.Project;
import org.eclipse.sw360.datahandler.thrift.users.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author ksoranko@verifa.io
 */
public class WsImportHandler implements ProjectImportService.Iface {

    private static final Logger LOGGER = Logger.getLogger(WsImportHandler.class);

    private static WsApiAccess getWsApiAccessWrapper(WSCredentials wsCredentials) {
        LOGGER.info("server: " + wsCredentials.getServerUrl());
        return new WsApiAccess(wsCredentials);
    }

    @Override
    public synchronized ImportStatus importWsDatasources(List<String> projectTokens, User user, WSCredentials wsCredentials) throws TException {
        LOGGER.info("wsimport: importWsDatasources method");
        WsApiAccess wsApiAccess = getWsApiAccessWrapper(wsCredentials);
        ThriftUploader thriftUploader = new ThriftUploader(wsApiAccess);
        Collection<WsProject> wsProjects = new ArrayList<>(projectTokens.size());
        WsProjectService wsProjectService = new WsProjectService();

        for (String token : projectTokens) {
            LOGGER.info("whitesource project with token, " + token + ", will be imported!");
            String projectName = wsProjectService.getProjectName(token);
            WsProject wsProject = new WsProject(projectName, token);
            wsProjects.add(wsProject);
        }
        return thriftUploader.importWsProjects(wsProjects, user);
    }

    @Override
    public String getIdName(){
        return TranslationConstants.WS_ID;
    }
    @Override
    public boolean validateCredentials(RemoteCredentials credentials) { return false; }
    @Override
    public List<Project> loadImportables(RemoteCredentials reCred) { return null; }
    @Override
    public List<Project> suggestImportables( RemoteCredentials reCred, String projectName) { return null; }
    @Override
    public ImportStatus importDatasources( List<String> projectIds, User user, RemoteCredentials reCred) { return null; }

}
