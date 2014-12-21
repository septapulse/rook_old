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

/**
 * 
 * @author Eric Thill
 *
 */
public class IOUtil {

	public static long readLong(byte[] b, int off) {
		long val = 0;
		for(int i = 0; i < 8; i++) {
			val <<= 8;
			val |= b[i+off] & 0xFF;
		}
		return val;
	}
	
	public static void writeLong(byte[] b, int off, long value) {
		b[off+0] = (byte)(value >>> 56);
		b[off+1] = (byte)(value >>> 48);
		b[off+2] = (byte)(value >>> 40);
		b[off+3] = (byte)(value >>> 32);
		b[off+4] = (byte)(value >>> 24);
		b[off+5] = (byte)(value >>> 16);
		b[off+6] = (byte)(value >>> 8);
		b[off+7] = (byte)(value >>> 0);
	}
}
