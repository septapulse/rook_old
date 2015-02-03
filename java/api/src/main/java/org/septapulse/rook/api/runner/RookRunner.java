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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.septapulse.rook.api.router.Router;
import org.septapulse.rook.api.service.Service;
import org.septapulse.rook.api.service.id.ServiceId;
import org.septapulse.rook.api.util.Instantiate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Eric Thill
 *
 */
public class RookRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(RookRunner.class);
	
	private static final String KEY_NAME = "name";
	private static final String KEY_JAR = "jar";
	private static final String KEY_IMPL = "impl";
	private static final String DIRECTORY_LIB = "lib/";
	private static final String DIRECTORY_CFG_SERVICES = "cfg/services/";
	private static final String DIRECTORY_CFG_LISTS = "cfg/lists/";
	private static final String DIRECTORY_CFG = "cfg/";
	private static final String EXTENSION_JAR = ".jar";
	private static final String EXTENSION_CFG = ".cfg";
	private static final String EXTENSION_LIST = ".list";
	
	public static void main(final String[] args) throws Exception {
		String servicesStr = null;
		String serviceList = null;
		String router = "router";
		for(int i = 0; i < args.length; i++) {
			if("--services".equals(args[i]) && i < args.length-1)
				servicesStr = args[i+1];
			else if("--serviceList".equals(args[i]) && i < args.length-1)
				serviceList = args[i+1];
			else if("--router".equals(args[i]) && i < args.length-1)
				router = args[i+1];
		}
		if(servicesStr == null && serviceList == null) {
			log("servicesList or services must be defined");
			log("Examples: ");
			log("    RookRunner --services io-service-serial,example-led-service");
			log("    RookRunner --serviceList example-led --router router");
			System.exit(-1);
		}
		if(servicesStr != null && serviceList != null) {
			log("serviceList and services cannot both be defined");
			System.exit(-1);
		}

		final String[] servicePaths = parseServicePaths(servicesStr, serviceList);
		final String routerPath = DIRECTORY_CFG + router + EXTENSION_CFG;
		
		log("Resolved Router Path: " + routerPath);
		log("Resolved Service Paths: " + Arrays.toString(servicePaths));
		
		Configuration config = new Configuration();
		List<Properties> services = new ArrayList<>();
		for(String servicePath : servicePaths)
			services.add(loadProperties(servicePath));
		config.setServices(services);
		config.setRouter(loadProperties(routerPath));
		
		log("Loaded configuration: " + config);
		start(config);
	}
	
	private static Properties loadProperties(String path) throws IOException {
		FileInputStream in = null;
		try {
			Properties props = new Properties();
			in = new FileInputStream(path);
			props.load(in);
			return props;
		} finally {
			if(in != null)
				in.close();
		}
	}

	private static String[] parseServicePaths(String servicesStr, String serviceList) throws IOException {
		if(serviceList == null) {
			final String[] split = servicesStr.split(",");
			final String[] services = new String[split.length];
			for(int i = 0; i < split.length; i++) {
				services[i] = DIRECTORY_CFG_SERVICES + split[i].trim() + EXTENSION_CFG;
			}
			return services;
		} else {
			final BufferedReader reader = new BufferedReader(new FileReader(
					DIRECTORY_CFG_LISTS + serviceList + EXTENSION_LIST));
			final List<String> services = new ArrayList<>();
			try {
				String line;
				while((line = reader.readLine()) != null)
					services.add(DIRECTORY_CFG_SERVICES + line + EXTENSION_CFG);
			} finally {
				reader.close();
			}
			return services.toArray(new String[services.size()]);
		}
	}

	public static Router start(Configuration config) throws Exception {
		final Router router = instantiate(config.getRouter());
		for(final Properties serviceProps : config.getServices()) {
			final Service service = instantiate(serviceProps);
			final String nameStr = serviceProps.getProperty(KEY_NAME);
			if(nameStr == null) {
				throw new IllegalArgumentException("'" + KEY_NAME 
						+ "' property is not defined in configuration");
			}
			final ServiceId id = ServiceId.fromName(nameStr);
			router.addService(service, id);
		}
		router.init();
		return router;
	}

	private static <T> T instantiate(Properties props) throws Exception {
		final String name = props.getProperty(KEY_NAME);
		final String jar = props.getProperty(KEY_JAR);
		final String impl = props.getProperty(KEY_IMPL);
		final String[] classpath = jar == null ? null : new String[1];
		classpath[0] = "file:"+new File(".").getAbsolutePath()+"/"+DIRECTORY_LIB+jar+EXTENSION_JAR;
		if (impl == null) {
			throw new IllegalAccessException("service with configured name '"
					+ name + "' does not specify " + KEY_IMPL);
		}
		final ClassLoader classLoader = getClassLoader(classpath);
		return Instantiate.instantiate(classLoader, impl, props);
	}
	
	private static ClassLoader getClassLoader(final String[] classpath)
			throws MalformedURLException {
		if (classpath == null || classpath.length == 0) {
			final ClassLoader threadClassLoader = Thread.currentThread()
					.getContextClassLoader();
			return threadClassLoader != null ? threadClassLoader : ClassLoader
					.getSystemClassLoader();
		}
		final URL[] urls = new URL[classpath.length];
		for (int i = 0; i < classpath.length; i++)
			urls[i] = new URL(classpath[i]);
		return new URLClassLoader(urls);
	}
	
	private static void log(String str) {
		if(LOGGER.isInfoEnabled()) {
			LOGGER.info(str);
		} else {
			System.out.println(str);
		}
	}

}
