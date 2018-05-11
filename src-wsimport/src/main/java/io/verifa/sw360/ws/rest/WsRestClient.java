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

import io.verifa.sw360.ws.utility.WsType;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import java.io.IOException;

import static io.verifa.sw360.ws.utility.WsSettings.WS_REST_ENDPOINT;

/**
 * @author: ksoranko@verifa.io
 */
public class WsRestClient {

    private static final Logger LOGGER = Logger.getLogger(WsRestClient.class);
    private static final String REST_URI = WS_REST_ENDPOINT;

    public WsRestClient() {
    }

    public String getData(String requestString, String token, WsType type) {
        String result = null;
        String input = null;
        switch (type) {
            case ORGANIZATION:
                input = "{\"requestType\":\"" + requestString + "\",\"orgToken\":\"" + token + "\"}";
                break;
            case PRODUCT:
                input = "{\"requestType\":\"" + requestString + "\",\"productToken\":\"" + token + "\"}";
                break;
            case PROJECT:
                input = "{\"requestType\":\"" + requestString + "\",\"projectToken\":\"" + token + "\"}";
                break;
        }

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(REST_URI);
        request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        StringEntity in = new StringEntity(input, ContentType.create("application/json"));
        request.setEntity(in);

        try {
            HttpResponse response = httpClient.execute(request);
            result = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return result;
    }
}
