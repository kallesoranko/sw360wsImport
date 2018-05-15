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
import io.verifa.sw360.ws.utility.TranslationConstants;
import org.eclipse.sw360.datahandler.thrift.components.ClearingState;
import org.eclipse.sw360.datahandler.thrift.components.Release;

import java.util.HashMap;
import java.util.HashSet;

import static com.google.common.base.Strings.isNullOrEmpty;
import static io.verifa.sw360.ws.utility.TranslationConstants.UNKNOWN;

/**
 * @author ksoranko@verifa.io
 */
public class WsLibraryToSw360ReleaseTranslator implements EntityTranslator<WsLibrary, Release> {

    @Override
    public Release apply(WsLibrary wsLibrary) {

        Release sw360release = new Release();
        sw360release.setExternalIds(new HashMap<>());
        sw360release.setName(wsLibrary.getName());
        sw360release.getExternalIds().put(TranslationConstants.WS_ID, Integer.toString(wsLibrary.getKeyId()));
        sw360release.getExternalIds().put("Filename", wsLibrary.getFilename());
        sw360release.setDownloadurl(wsLibrary.getReferences().getScmUrl());

        if (!isNullOrEmpty(wsLibrary.getVersion())) {
            sw360release.setVersion(wsLibrary.getVersion());
        } else {
            sw360release.setVersion(UNKNOWN);
        }

        sw360release.setModerators(new HashSet<>());
        sw360release.setClearingState(ClearingState.NEW_CLEARING);

        return sw360release;
    }

}
