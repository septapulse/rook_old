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
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 
 * @author Eric Thill
 *
 */
public class DirectoryConfigurationLoader {

	private static final String EXT_CFG = ".cfg";
	private static final String FILE_ROUTER = "router"+EXT_CFG;
	
	public static Configuration load(File directory, String... configs) throws Exception {
		if(!directory.exists() || !directory.isDirectory()) {
			throw new IllegalArgumentException(directory.getAbsolutePath() + " is not a directory");
		}
		final File routerFile = new File(directory, "router.cfg");
		if(!routerFile.exists() || !routerFile.isFile()) {
			throw new IllegalArgumentException(routerFile.getAbsolutePath() + " is not present");
		}
		
		final Configuration config = new Configuration();
		
		final Properties router = loadProperties(routerFile);
		config.setRouter(router);
		
		final List<Properties> services = loadServices(directory, configs);
		config.setServices(services);
		
		return config;
	}
	
	private static List<Properties> loadServices(File directory,
			String[] configs) throws Exception {
		List<Properties> services = new ArrayList<>();
		if(configs == null || configs.length == 0) {
			for(File file : directory.listFiles()) {
				if(file.getName().endsWith(EXT_CFG) && !file.getName().equals(FILE_ROUTER)) {
					final Properties props = loadProperties(file);
					services.add(props);
				}
			}
		} else {
			for(String config : configs) {
				final File file = new File(directory, config+EXT_CFG);
				if(file.getName().endsWith(EXT_CFG) && !file.getName().equals(FILE_ROUTER)) {
					final Properties props = loadProperties(file);
					services.add(props);
				}
			}
		}
		return services;
	}

	private static Properties loadProperties(File cfg) throws Exception {
		final Properties props = new Properties();
		props.load(new FileReader(cfg));
		return props;
	}
	
}
