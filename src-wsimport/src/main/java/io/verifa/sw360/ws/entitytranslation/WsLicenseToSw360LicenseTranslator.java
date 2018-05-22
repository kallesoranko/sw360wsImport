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
import org.eclipse.sw360.datahandler.thrift.licenses.License;

import java.util.HashMap;

/**
 * @author ksoranko@verifa.io
 */
public class WsLicenseToSw360LicenseTranslator implements EntityTranslator<WsLicense, org.eclipse.sw360.datahandler.thrift.licenses.License> {

        @Override
    public License apply(WsLicense wsLicense) {
        String license = wsLicense.getName().replaceFirst("Suspected ", "");
        String licenseWithDashes = license.replaceAll("\\s","-");

        License licenseSW360 = new License();
        licenseSW360.setId(licenseWithDashes);
        licenseSW360.setExternalIds(new HashMap<>());
        licenseSW360.getExternalIds().put(TranslationConstants.WS_ID, wsLicense.getName());
        licenseSW360.setShortname(licenseWithDashes);
        licenseSW360.setFullname(license);
        licenseSW360.setExternalLicenseLink(wsLicense.getUrl());

        return licenseSW360;
    }

}
