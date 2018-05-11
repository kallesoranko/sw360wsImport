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
import io.verifa.sw360.ws.domain.WsProject;
import io.verifa.sw360.ws.domain.WsProjectLicenses;
import io.verifa.sw360.ws.domain.WsProjectVitals;
import io.verifa.sw360.ws.utility.WsType;

/**
 * @author ksoranko@verifa.io
 */
public class WsProjectService {

    private static final WsRestClient restClient = new WsRestClient();
    private static final Gson gson = new Gson();

    public WsProject getWsProject(String projectToken) {
        String projectVitalString = restClient.getData("getProjectVitals", projectToken, WsType.PROJECT);
        WsProjectVitals wsProjectVitals = null;
        try {
            wsProjectVitals = gson.fromJson(projectVitalString, WsProjectVitals.class);
        } catch (JsonSyntaxException jse) {
            jse.printStackTrace();
        }
        return new WsProject(wsProjectVitals.getProjectVitals()[0].getId(),
                                wsProjectVitals.getProjectVitals()[0].getName(),
                                wsProjectVitals.getProjectVitals()[0].getToken(),
                                wsProjectVitals.getProjectVitals()[0].getCreationDate());
    }

    public WsLibrary[] getProjectLicenses(String projectToken) {
        String projectLicensesString = restClient.getData("getProjectLicenses", projectToken, WsType.PROJECT);
        WsProjectLicenses wsProjectLicenses = null;
        try {
            wsProjectLicenses = gson.fromJson(projectLicensesString, WsProjectLicenses.class);
        } catch (JsonSyntaxException jse) {
            jse.printStackTrace();
        }
        return wsProjectLicenses.getLibraries();
    }
}
