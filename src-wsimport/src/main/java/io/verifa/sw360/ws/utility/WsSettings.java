/*
 * Copyright (c) Bosch Software Innovations GmbH 2015.
 * Part of the SW360 Portal Project.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package io.verifa.sw360.ws.utility;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author: ksoranko@verifa.io
 */
public class WsSettings {

    public static final String WS_FILE = "whitesource.properties";
    public static final String WS_REST_ENDPOINT;
    public static final String WS_ORG_TOKEN;

    public static Properties readProperties(String path) {
        Properties props = new Properties();
        try {
            InputStream input = new FileInputStream(path);
            props.load(input);
        } catch (Exception e) {}
        return props;
    }

    static {
        Properties properties = readProperties(WS_FILE);
        WS_REST_ENDPOINT = properties.getProperty("ws.rest.endpoint");
        WS_ORG_TOKEN = properties.getProperty("ws.token.org");
    }

    private WsSettings(){}
}
