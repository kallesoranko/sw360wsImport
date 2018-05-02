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
package io.verifa.sw360.ws.utility;

import org.eclipse.sw360.datahandler.thrift.projectimport.WSCredentials;

/**
 * @author: ksoranko@verifa.io
 */
public class WsApiAccess {

    private final String serverUrl;
    private final String orgToken;

    public WsApiAccess(WSCredentials wsCredentials) {
        this.serverUrl = wsCredentials.getServerUrl();
        this.orgToken = wsCredentials.getToken();
    }

    public WsApiAccess(String serverUrl, String orgToken) {
        this.serverUrl = serverUrl;
        this.orgToken = orgToken;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getOrgToken() {
        return orgToken;
    }

}
