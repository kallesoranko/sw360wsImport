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
package io.verifa.sw360.ws.service;

import org.apache.thrift.protocol.TCompactProtocol;
import org.eclipse.sw360.datahandler.thrift.projectimport.ProjectImportService;
import org.eclipse.sw360.projects.Sw360ThriftServlet;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;

/**
 * @author ksoranko@verifa.io
 */
public class WsImportServlet extends Sw360ThriftServlet {
    public WsImportServlet() throws MalformedURLException, FileNotFoundException {
        // Create a service processor using the provided handler
        super(new ProjectImportService.Processor<>(new WsImportHandler()), new TCompactProtocol.Factory());
    }
}
