/*
 * Copyright (c) Verifa Oy, 2018.
 * Part of the SW360 Portal Project.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.verifa.sw360.ws.thrift;

import org.apache.thrift.TException;
import org.eclipse.sw360.datahandler.thrift.ThriftClients;
import org.eclipse.sw360.datahandler.thrift.components.ComponentService;
import org.eclipse.sw360.datahandler.thrift.licenses.LicenseService;
import org.eclipse.sw360.datahandler.thrift.projects.ProjectService;
import org.eclipse.sw360.datahandler.thrift.users.User;
import org.eclipse.sw360.datahandler.thrift.users.UserService;

public class ThriftApiSimple implements ThriftApi {
	
	private ThriftClients thriftClients;
	private UserService.Iface userClient;
	private ComponentService.Iface componentClient;
	private ProjectService.Iface projectClient;
	private LicenseService.Iface licenseClient;

	public ThriftApiSimple() {
		this.thriftClients = new ThriftClients();
		this.userClient = thriftClients.makeUserClient();
		this.componentClient = thriftClients.makeComponentClient();
		this.projectClient = thriftClients.makeProjectClient();
		this.licenseClient = thriftClients.makeLicenseClient();
		this.userClient = thriftClients.makeUserClient();
	}

	/* (non-Javadoc)
	 * @see ThriftApi#getThriftClients()
	 */
	@Override
	public ThriftClients getThriftClients() {
		return thriftClients;
	}

	/* (non-Javadoc)
	 * @see ThriftApi#getUserClient()
	 */
	@Override
	public UserService.Iface getUserClient() {
		return userClient;
	}

	/* (non-Javadoc)
	 * @see ThriftApi#getComponentClient()
	 */
	@Override
	public ComponentService.Iface getComponentClient() {
		return componentClient;
	}

	/* (non-Javadoc)
	 * @see ThriftApi#getProjectClient()
	 */
	@Override
	public ProjectService.Iface getProjectClient() {
		return projectClient;
	}

	private UserService.Iface addUserToUserClient(User user) {
		try {
			this.userClient.addUser(user);
		} catch (TException e) {
			throw new IllegalArgumentException(e);
		}
		return this.userClient;
	}

	@Override
	public LicenseService.Iface getLicenseClient() {
		return licenseClient;
	}

}
