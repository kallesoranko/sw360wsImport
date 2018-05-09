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

import io.verifa.sw360.ws.domain.WsLicense;
import io.verifa.sw360.ws.utility.TranslationConstants;

import java.util.HashMap;

/**
 * @author ksoranko@verifa.io
 */
public class WsLicenseToSw360LicenseTranslator implements EntityTranslator<WsLicense, org.eclipse.sw360.datahandler.thrift.licenses.License> {



    @Override
    public org.eclipse.sw360.datahandler.thrift.licenses.License apply(WsLicense wslicense) {
        org.eclipse.sw360.datahandler.thrift.licenses.License licenseSW360 = new org.eclipse.sw360.datahandler.thrift.licenses.License();

        licenseSW360.setExternalIds(new HashMap<>());
        licenseSW360.setId(wslicense.getName());
        licenseSW360.setShortname(wslicense.getName());
        licenseSW360.getExternalIds().put(TranslationConstants.WS_ID, wslicense.getName());
        licenseSW360.setFullname(wslicense.getName());
        licenseSW360.setExternalLicenseLink(wslicense.getUrl());

        return licenseSW360;
    }

}
