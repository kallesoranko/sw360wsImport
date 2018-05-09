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
package io.verifa.sw360.ws.entitytranslation;

import io.verifa.sw360.ws.domain.WsLibrary;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author ksoranko@verifa.io
 */
public class WsLibraryToSw360ComponentTranslator implements EntityTranslator<WsLibrary, org.eclipse.sw360.datahandler.thrift.components.Component> {

    @Override
    public org.eclipse.sw360.datahandler.thrift.components.Component apply(WsLibrary wsLibrary) {

        org.eclipse.sw360.datahandler.thrift.components.Component sw360component = new org.eclipse.sw360.datahandler.thrift.components.Component(wsLibrary.getName());

        sw360component.setCategories(new HashSet<>(Collections.singletonList(wsLibrary.getType())));
        sw360component.setExternalIds(new HashMap<>());
        sw360component.getExternalIds().put("Filename", wsLibrary.getFilename());
        /*sw360component.getExternalIds().put("Coordinates", wsLibrary.getCoordinates());
        */

        return sw360component;
    }

}
