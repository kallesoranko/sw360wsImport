/*
 * Copyright (c) Verifa Oy, 2018.
 * Part of the SW360 Portal Project.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.verifa.sw360.ws.entitytranslation;

import io.verifa.sw360.ws.domain.WsLibrary;

/**
 * @author ksoranko@verifa.io
 */
public class WsComponentToSw360ComponentTranslator implements EntityTranslator<WsLibrary, org.eclipse.sw360.datahandler.thrift.components.Component> {

    @Override
    public org.eclipse.sw360.datahandler.thrift.components.Component apply(WsLibrary wsLibrary) {

        org.eclipse.sw360.datahandler.thrift.components.Component sw360component = new org.eclipse.sw360.datahandler.thrift.components.Component(wsLibrary.getName());

        return sw360component;
    }

}
