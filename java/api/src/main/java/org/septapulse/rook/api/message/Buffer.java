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
package org.septapulse.rook.api.message;

import java.nio.ByteBuffer;

/**
 * Read-Only Buffer
 * 
 * @author Eric Thill
 *
 */
public interface Buffer {
	/**
	 * Get the value at the given offset
	 * 
	 * @param i
	 *            offset
	 * @return value at offset
	 */
	byte get(int i);

	/**
	 * Get the length of the buffer
	 * 
	 * @return the length
	 */
	int length();

	/**
	 * Get the buffer as a read-only ByteBuffer. Calling this method will reset
	 * the position and limit of the underlying Byte-Buffer, which will then be
	 * returned. As such, multiple calls to this method will return the same
	 * ByteBuffer, all of which will be reset upon calling this method.
	 * 
	 * @return The read-only ByteBuffer
	 */
	ByteBuffer asByteBuffer();
}
