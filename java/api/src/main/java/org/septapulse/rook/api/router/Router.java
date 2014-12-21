/*
 * (C) Copyright 2014 Eric Thill
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Eric Thill
 */
package org.septapulse.rook.api.router;

import org.septapulse.rook.api.service.Service;
import org.septapulse.rook.api.service.id.ServiceId;

/**
 * A router is responsible for managing services and routing messages between
 * them.
 * 
 * @author Eric Thill
 *
 */
public interface Router {

	/**
	 * Add a service to the router
	 * 
	 * @param service
	 *            The service to add
	 * @param id
	 *            The id of the service
	 */
	void addService(Service service, ServiceId id);

	/**
	 * Initialize the router and all of its child services
	 * @throws Exception 
	 */
	void init() throws Exception;

	/**
	 * Destroy the router and all of its child services
	 */
	void destroy();
}
