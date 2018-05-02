/*
 * Copyright (c) Verifa Oy, 2018.
 * Part of the SW360 Portal Project.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.verifa.sw360.ws.thrift.helper;

/**
 * @author: ksoranko@verifa.io
 */
public enum ProjectImportError {
    PROJECT_NOT_FOUND("Unable to get project from server"),
    PROJECT_ALREADY_EXISTS("Project already in database"),
    OTHER("Other error");

    private final String text;

    ProjectImportError(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
