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
package io.verifa.sw360.ws.domain;

/**
 * @author: ksoranko@verifa.io
 */
public class WsProjectHierarchy {

    private WsLibrary[] libraries;

    public WsLibrary[] getLibraries() {
        return libraries;
    }

    public void setLibraries(WsLibrary[] libraries) {
        this.libraries = libraries;
    }
}
