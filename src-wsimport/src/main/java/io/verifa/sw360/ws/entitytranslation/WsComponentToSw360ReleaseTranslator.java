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
import io.verifa.sw360.ws.utility.TranslationConstants;
import org.eclipse.sw360.datahandler.thrift.components.ClearingState;
import org.eclipse.sw360.datahandler.thrift.components.Release;

import java.util.HashMap;
import java.util.HashSet;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author ksoranko@verifa.io
 */
public class WsComponentToSw360ReleaseTranslator implements EntityTranslator<WsLibrary, Release> {

    public static final String unknownVersionString = "UNKNOWN";

    @Override
    public Release apply(WsLibrary wslibrary) {

        Release releaseSW360 = new Release();
        releaseSW360.setExternalIds(new HashMap<>());
        releaseSW360.setName(wslibrary.getName());
        releaseSW360.getExternalIds().put(TranslationConstants.WS_ID, wslibrary.getName());

        if (!isNullOrEmpty(wslibrary.getVersion())) {
            releaseSW360.setVersion(wslibrary.getVersion());
        } else {
            releaseSW360.setVersion(unknownVersionString);
        }

        releaseSW360.setModerators(new HashSet<>());
        releaseSW360.setClearingState(ClearingState.NEW_CLEARING);

        return releaseSW360;
    }

}
