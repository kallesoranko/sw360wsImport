/*
 * Copyright (c) Verifa Oy, 2018.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package io.verifa.sw360.ws.rest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import io.verifa.sw360.ws.utility.WsSettings;
import io.verifa.sw360.ws.utility.WsType;
import org.apache.log4j.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;


/**
 * @author: ksoranko@verifa.io
 */
public class WsRestClient {

    private static final Logger logger = Logger.getLogger(WsRestClient.class);

    public WsRestClient() {
    }

    public String getData(String request, String token, WsType type) {
        try {
            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);
            WebResource webResource = client.resource(UriBuilder.fromUri(WsSettings.WS_REST_ENDPOINT).build());
            MultivaluedMap formData = new MultivaluedMapImpl();
            formData.add("requestType", request);
            switch (type) {
                case ORGANIZATION:
                    formData.add("orgToken", token);
                    break;
                case PRODUCT:
                    formData.add("productToken", token);
                    break;
                case PROJECT:
                    formData.add("projectToken", token);
                    break;
            }
            ClientResponse response = webResource.type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, formData);
            if (response.getStatus() != 201) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
            String output = response.getEntity(String.class);
            return output;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}