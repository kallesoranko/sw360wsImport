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
package io.verifa.sw360.ws.utility;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * @author: ksoranko@verifa.io
 */
public class WsSettings {

    public static final String WS_PROPS_FILE = "ws.properties";
    public static final String WS_REST_ENDPOINT;

    public static Properties readProperties(String path) {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String defaultConfigPath = rootPath + path;
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(defaultConfigPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return props;
    }

    static {
        Properties properties = readProperties(WS_PROPS_FILE);
        WS_REST_ENDPOINT = properties.getProperty("ws.rest.endpoint");
    }

    private WsSettings(){}
}
