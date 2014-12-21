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
package org.septapulse.rook.api.runner;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * 
 * @author Eric Thill
 *
 */
public class Configuration {
	private Properties router;
	private List<Properties> services;
	
	public Properties getRouter() {
		return router;
	}
	
	public void setRouter(Properties router) {
		this.router = router;
	}
	
	public List<Properties> getServices() {
		return Collections.unmodifiableList(services);
	}
	
	public void setServices(List<Properties> services) {
		this.services = services;
	}
}
