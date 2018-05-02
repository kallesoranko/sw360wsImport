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
import io.verifa.sw360.ws.domain.WsLibrary;
import io.verifa.sw360.ws.domain.WsProjectHierarchy;
import io.verifa.sw360.ws.domain.WsProjectVitals;
import io.verifa.sw360.ws.utility.WsType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

/**
 * @author ksoranko@verifa.io
 */
public class WsProjectService {

    private static final Logger LOGGER = LogManager.getLogger(WsProjectService.class);

    private static final WsRestClient restClient = new WsRestClient();
    private static final Gson gson = new Gson();

    public Collection<WsLibrary> getProjectHierarchy(String projectToken) {
        String projectHierarchyString = restClient.getData("getProjectHierarchy", projectToken, WsType.PROJECT);
        WsProjectHierarchy wsProjectHierarchy = gson.fromJson(projectHierarchyString, WsProjectHierarchy.class);
        return wsProjectHierarchy.getLibraries();
    }

    public String getProjectName(String projectToken) {
        String projectVitalString = restClient.getData("getProjectVitals", projectToken, WsType.PROJECT);
        WsProjectVitals wsProjectVitals = gson.fromJson(projectVitalString, WsProjectVitals.class);
        return wsProjectVitals.getProjectVitals().getName();
    }
}
