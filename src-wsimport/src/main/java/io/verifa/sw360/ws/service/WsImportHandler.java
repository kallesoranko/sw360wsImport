/*
 * Copyright (c) Verifa Oy, 2018.
 * Part of the SW360 Portal Project.
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

import java.util.Collection;
import java.util.List;

/**
 * @author ksoranko@verifa.io
 */
public class WsImportHandler implements ProjectImportService.Iface {

    private static final Logger log = Logger.getLogger(WsImportHandler.class);

    private static WsApiAccess getWsApiAccessWrapper(WSCredentials wsCredentials) {
        log.info("server: " + wsCredentials.getServerUrl());
        return new WsApiAccess(wsCredentials);
    }

    @Override
    public synchronized ImportStatus importWsDatasources(List<String> projectTokens, User user, WSCredentials wsCredentials) throws TException {
        log.info("wsimport: importWsDatasources method");
        WsApiAccess wsApiAccess = getWsApiAccessWrapper(wsCredentials);
        ThriftUploader thriftUploader = new ThriftUploader(wsApiAccess);
        Collection<WsProject> wsProjects = null;
        WsProjectService wsProjectService = new WsProjectService();

        for (String token : projectTokens) {
            log.info("whitesource project with token, " + token + ", will be imported!");
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
