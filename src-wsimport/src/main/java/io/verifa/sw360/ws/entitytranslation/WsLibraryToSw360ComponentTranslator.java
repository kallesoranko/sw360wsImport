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

import java.util.*;

/**
 * @author ksoranko@verifa.io
 */
public class WsLibraryToSw360ComponentTranslator implements EntityTranslator<WsLibrary, org.eclipse.sw360.datahandler.thrift.components.Component> {

    @Override
    public org.eclipse.sw360.datahandler.thrift.components.Component apply(WsLibrary wsLibrary) {

        org.eclipse.sw360.datahandler.thrift.components.Component sw360component = new org.eclipse.sw360.datahandler.thrift.components.Component(wsLibrary.getName());

        sw360component.setCategories(new HashSet<>(Collections.singletonList(wsLibrary.getType())));
        Map<String, String> externalIds = new HashMap<>();
        externalIds.put("Filename", wsLibrary.getFilename());
        externalIds.put("Coordinates", wsLibrary.getCoordinates());
        sw360component.setExternalIds(externalIds);

        /* TO BE DONE
        Set<String> mainLicenses = new HashSet<>();
        mainLicenses.add(wsLibrary.get)
        sw360component.setMainLicenseIds()
        */

        return sw360component;
    }

}
