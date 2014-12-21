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

import java.util.Map;

/**
 * 
 * @author Eric Thill
 *
 */
public class JavaVersion {

	public static void check(Ver ver) {
		// FIXME implement JRE vs JSE8 check!!!
		for(Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
			System.out.println(entry.getKey() + " = " + entry.getValue());
		}
	}
	
	public enum Ver {
		JRE8, JSE8, JRE8_AND_JSE8;
	}
}
