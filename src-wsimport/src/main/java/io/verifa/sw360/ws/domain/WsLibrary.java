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
package io.verifa.sw360.ws.domain;

/**
 * @author: ksoranko@verifa.io
 */
public class WsLibrary {

    //private String keyUuid;
    //private int keyId;
    private String filename;
    private String name;
    //private String groupId;
    //private String artifactId;
    private String version;
    //private String sha1;
    private String type;
    //private String coordinates;
    //private WsLibrary[] dependencies;
    private WsLicense[] licenses;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public WsLicense[] getLicenses() {
        return licenses;
    }

    public void setLicenses(WsLicense[] licenses) {
        this.licenses = licenses;
    }
}

