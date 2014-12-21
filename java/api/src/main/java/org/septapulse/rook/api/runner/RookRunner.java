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

import java.io.File;
import java.util.Properties;

import org.septapulse.rook.api.router.Router;
import org.septapulse.rook.api.service.Service;
import org.septapulse.rook.api.service.id.ServiceId;
import org.septapulse.rook.api.util.Instantiate;

/**
 * 
 * @author Eric Thill
 *
 */
public class RookRunner {

	private static final String KEY_IMPL = "implementation";
	private static final String KEY_NAME = "name";

	public static void main(String[] args) throws Exception {
		if(args.length != 1) {
			System.out.println("Use: RookRunner <cfgDirectory>");
			System.exit(-1);
		}
		String dirPath = args[0];
		File dir = new File(dirPath);
		System.out.println("Loading configuration from directory: " + dir.getAbsolutePath());
		Configuration config = DirectoryConfigurationLoader.load(dir);
		
		start(config);
	}
	
	public static Router start(Configuration config) throws Exception {
		Router router = instantiate(config.getRouter());
		for(Properties serviceProps : config.getServices()) {
			Service service = instantiate(serviceProps);
			String nameStr = serviceProps.getProperty(KEY_NAME);
			if(nameStr == null) {
				throw new IllegalArgumentException("'" + KEY_NAME 
						+ "' property is not defined in configuration");
			}
			ServiceId id = ServiceId.fromName(nameStr);
			router.addService(service, id);
		}
		router.init();
		return router;
	}

	private static <T> T instantiate(Properties props) throws Exception {
		String name = props.getProperty(KEY_NAME);
		String impl = props.getProperty(KEY_IMPL);
		if (impl == null) {
			throw new IllegalAccessException("service with configured name '"
					+ name + "' does not specify " + KEY_IMPL);
		}
		return Instantiate.instantiate(impl, props);
	}

}
