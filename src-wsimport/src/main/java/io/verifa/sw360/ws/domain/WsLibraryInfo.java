/*
 * Copyright (c) Verifa Oy, 2018.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package io.verifa.sw360.ws.domain;

import java.util.Collection;

/**
 * @author: ksoranko@verifa.io
 */
public class WsLibraryInfo {

    private String keyUuid;
    private String keyId;
    private String filename;
    private String name;
    private String groupId;
    private String artifactId;
    private String version;
    private String sha1;
    private String type;
    private Collection<WsLicense> licenses;
    private boolean directDependency;
    private Collection<WsProject> projects;

    public String getKeyUuid() {
        return keyUuid;
    }

    public void setKeyUuid(String keyUuid) {
        this.keyUuid = keyUuid;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Collection<WsLicense> getLicenses() {
        return licenses;
    }

    public void setLicenses(Collection<WsLicense> licenses) {
        this.licenses = licenses;
    }

    public boolean isDirectDependency() {
        return directDependency;
    }

    public void setDirectDependency(boolean directDependency) {
        this.directDependency = directDependency;
    }

    public Collection<WsProject> getProjects() {
        return projects;
    }

    public void setProjects(Collection<WsProject> projects) {
        this.projects = projects;
    }
}
