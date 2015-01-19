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
package org.septapulse.rook.api.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

/**
 * 
 * @author Eric Thill
 *
 */
public class Instantiate {

	@SuppressWarnings("unchecked")
	public static <T> T instantiate(final ClassLoader classLoader,
			final String className) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			MalformedURLException {
		Class<?> c = Class.forName(className, true, classLoader);
		return (T) c.newInstance();
	}

	@SuppressWarnings("unchecked")
	public static <T> T instantiate(final ClassLoader classLoader,
			final String className, final Object... args)
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			MalformedURLException {
		final Class<?> c = Class.forName(className, true, classLoader);
		final Class<?>[] parameterTypes = (Class<?>[]) new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			parameterTypes[i] = args[i].getClass();
		}
		final Constructor<?> constructor = c.getConstructor(parameterTypes);
		return (T) constructor.newInstance(args);
	}
	
}
