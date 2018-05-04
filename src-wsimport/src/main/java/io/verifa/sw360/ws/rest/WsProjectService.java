/*
 * Copyright (c) Verifa Oy, 2018. Part of the SW360 Project.
 *
 * SPDX-License-Identifier: EPL-1.0
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.verifa.sw360.ws.rest;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.verifa.sw360.ws.domain.WsLibrary;
import io.verifa.sw360.ws.domain.WsProjectHierarchy;
import io.verifa.sw360.ws.domain.WsProjectVitals;
import io.verifa.sw360.ws.utility.WsType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author ksoranko@verifa.io
 */
public class WsProjectService {

    private static final Logger LOGGER = LogManager.getLogger(WsProjectService.class);

    private static final WsRestClient restClient = new WsRestClient();
    private static final Gson gson = new Gson();

    public WsLibrary[] getProjectHierarchy(String projectToken) {
        LOGGER.info("getProjectHierarchy, projectToken: " + projectToken);
        String projectHierarchyString = restClient.getData("getProjectHierarchy", projectToken, WsType.PROJECT);
        WsProjectHierarchy wsProjectHierarchy = null;
        try {
            wsProjectHierarchy = gson.fromJson(projectHierarchyString, WsProjectHierarchy.class);
        } catch (JsonSyntaxException jse) {
            jse.printStackTrace();
        }
        return wsProjectHierarchy.getLibraries();
    }

    public String getProjectName(String projectToken) {
        LOGGER.info("getProjectName, projectToken: " + projectToken);
        String projectVitalString = restClient.getData("getProjectVitals", projectToken, WsType.PROJECT);
        WsProjectVitals wsProjectVitals = null;
        try {
            wsProjectVitals = gson.fromJson(projectVitalString, WsProjectVitals.class);
        } catch (JsonSyntaxException jse) {
            jse.printStackTrace();
        }
        return wsProjectVitals.getProjectVitals()[0].getName();
    }
}
