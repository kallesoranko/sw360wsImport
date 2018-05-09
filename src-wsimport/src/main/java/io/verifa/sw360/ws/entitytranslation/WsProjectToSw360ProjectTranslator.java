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
package io.verifa.sw360.ws.entitytranslation;

import io.verifa.sw360.ws.domain.WsProject;

import java.util.HashMap;

import static io.verifa.sw360.ws.utility.TranslationConstants.WS_ID;

/**
 * @author ksoranko@verifa.io
 */
public class WsProjectToSw360ProjectTranslator implements EntityTranslator<WsProject, org.eclipse.sw360.datahandler.thrift.projects.Project>{

    @Override
    public org.eclipse.sw360.datahandler.thrift.projects.Project apply(WsProject wsProject) {

        org.eclipse.sw360.datahandler.thrift.projects.Project projectSW360 = new org.eclipse.sw360.datahandler.thrift.projects.Project();

        projectSW360.setExternalIds(new HashMap<>());
        projectSW360.getExternalIds().put(WS_ID, wsProject.getProjectName());
        projectSW360.setDescription(wsProject.getProjectToken());
        projectSW360.setName(wsProject.getProjectName());

        return projectSW360;
    }

}
