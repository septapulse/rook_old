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

import java.util.Properties;

/**
 * 
 * @author Eric Thill
 *
 */
public final class PropertiesUtil {
	
	public static String getRequiredProperty(Properties props, String key) throws IllegalArgumentException {
		final String value = props.getProperty(key);
		if(value == null)
			throw new IllegalArgumentException("Required property '" + key + "' does not exist in " + props);
		return value;
	}
	
	public static String getProperty(Properties props, String key) {
		return props.getProperty(key);
	}
	
	public static String getProperty(Properties props, String key, String defaultValue) {
		return props.getProperty(key, defaultValue);
	}
	
	public static Number getRequiredNumber(Properties props, String key) {
		final Number value = getNumber(props, key);
		if(value == null)
			throw new IllegalArgumentException("Required property '" + key + "' does not exist in " + props);
		return value;
	}

	public static Number getNumber(Properties props, String key) {
		return getNumber(props, key, (Number)null);
	}
	
	public static Number getNumber(Properties props, String key, String defaultValue) {
		return getNumber(props, key, parseNumber(defaultValue));
	}
	
	public static Number getNumber(Properties props, String key, Number defaultValue) {
		final String value = props.getProperty(key);
		if(value == null)
			return defaultValue;
		return parseNumber(value);
	}
	
	private static Number parseNumber(String value) {
		if(value.contains("."))
			return Double.parseDouble(value);
		else
			return Long.parseLong(value);
	}
}
