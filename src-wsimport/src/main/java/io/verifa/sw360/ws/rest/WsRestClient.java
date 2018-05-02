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
import io.verifa.sw360.ws.utility.WsSettings;
import io.verifa.sw360.ws.utility.WsType;
import org.apache.log4j.Logger;


/**
 * @author: ksoranko@verifa.io
 */
public class WsRestClient {

    private static final Logger logger = Logger.getLogger(WsRestClient.class);

    public WsRestClient() {
    }

    public String getData(String request, String token, WsType type) {
        try {
            Client client = Client.create();

            logger.info("..... In getData");
            logger.info("..... REST ENDPOINT (WsSettings.WS_REST_ENDPOINT): " + WsSettings.WS_REST_ENDPOINT);
            logger.info("..... TOKEN (WsSettings.WS_ORG_TOKEN): " + WsSettings.WS_ORG_TOKEN);

            WebResource webResource = client.resource(WsSettings.WS_REST_ENDPOINT);
            String input = null;
            switch (type) {
                case ORGANIZATION:
                    input = "{\"requestType\":\"" + request + "\",\"orgToken\":\"" + token + "\"}";
                    break;
                case PRODUCT:
                    input = "{\"requestType\":\"" + request + "\",\"productToken\":\"" + token + "\"}";
                    break;
                case PROJECT:
                    input = "{\"requestType\":\"" + request + "\",\"projectToken\":\"" + token + "\"}";
                    break;
            }
            ClientResponse response = webResource.type("application/json").post(ClientResponse.class, input);
            if (response.getStatus() != 201) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
            String output = response.getEntity(String.class);
		    return output;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

}
