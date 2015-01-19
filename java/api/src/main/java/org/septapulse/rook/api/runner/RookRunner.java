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
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
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
	private static final String EXTENSION_JAR = ".jar";
	
	public static void main(String[] args) throws Exception {
		if(args.length == 0) {
			log("No configuration argument specified. Using working directory.");
			args = new String[] { "." };
		}
		final String cfgDirectory = args[0];
		final String cfgFile = args.length > 1 ? args[1] : null;
		final File dir = new File(cfgDirectory);
		final File file = cfgFile == null ? null : new File(cfgFile);

		if(!dir.exists() || !dir.isDirectory()) {
			log("ERROR! Configuration directory does not exist. path=" + dir.getAbsolutePath());
			System.exit(-1);
		} 
		
		Configuration config = null;
		if(file == null) {
			log("Loading from all configurations in directory '" + dir.getAbsolutePath() + "'");
			config = DirectoryConfigurationLoader.load(dir);
		} else {
			final List<String> configs = parseConfigs(file);
			log("Loading from configurations " + configs + " from directory '" + dir.getAbsolutePath() + "'");
			config = DirectoryConfigurationLoader.load(file, configs.toArray(new String[configs.size()]));
		}
		
		log("Loaded configuration: " + config);
		start(config);
	}
	
	private static List<String> parseConfigs(File file) throws IOException {
		final BufferedReader reader = new BufferedReader(new FileReader(file));
		try {
			final List<String> configs = new ArrayList<>();
			String line;
			while((line = reader.readLine()) != null)
				configs.add(line);
			return configs;
		} finally {
			reader.close();
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
		classpath[0] = "file://"+new File(".").getAbsolutePath()+"/"+DIRECTORY_LIB+jar+EXTENSION_JAR;
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
