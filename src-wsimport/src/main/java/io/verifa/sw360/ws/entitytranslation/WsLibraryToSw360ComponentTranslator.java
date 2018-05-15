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
import org.eclipse.sw360.datahandler.thrift.components.Component;

import java.util.Collections;
import java.util.HashSet;

/**
 * @author ksoranko@verifa.io
 */
public class WsLibraryToSw360ComponentTranslator implements EntityTranslator<WsLibrary, org.eclipse.sw360.datahandler.thrift.components.Component> {

    @Override
    public org.eclipse.sw360.datahandler.thrift.components.Component apply(WsLibrary wsLibrary) {

        Component sw360component = new Component(wsLibrary.getName());
        sw360component.setCategories(new HashSet<>(Collections.singletonList(wsLibrary.getType())));

        if (wsLibrary.getReferences() != null) {
            sw360component.setHomepage(wsLibrary.getReferences().getUrl());
        }

        return sw360component;
    }

}
