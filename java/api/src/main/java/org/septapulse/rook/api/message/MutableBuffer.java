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
 * Writable, auto-growing buffer
 * 
 * @author Eric Thill
 *
 */
public interface MutableBuffer extends Buffer {
	/**
	 * Set the value at the given offset
	 * 
	 * @param i
	 *            the offset
	 * @param v
	 *            the value
	 */
	void set(int i, byte v);

	/**
	 * Grow the buffer to the given capacity. If the buffer is already at the
	 * given capacity, the underlying data will remain unchanged.
	 * 
	 * @param capacity
	 *            the required capacity
	 */
	void reserve(int capacity);

	/**
	 * Set the length of the buffer. If the length is bigger than the current
	 * capacity, it will be grown automatically.
	 * 
	 * @param length
	 *            the length
	 */
	void length(int length);

	/**
	 * Sets the underlying buffer length to 0.
	 * 
	 * @param fill
	 *            true will fill the underlying buffer with 0's, false will
	 *            leave it untouched.
	 */
	void clear(boolean fill);

	/**
	 * Returns the underlying buffer as a writable ByteBuffer with the position
	 * set to 0 and the limit set to the current length.
	 * 
	 * @return the ByteBuffer
	 */
	ByteBuffer asWritableByteBuffer();

	/**
	 * Sets the buffer length to the given length, and returns the underlying
	 * buffer as a writable ByteBuffer with the position set to 0 and the limit
	 * set to the given length.
	 * 
	 * @return the ByteBuffer
	 */
	ByteBuffer asWritableByteBuffer(int length);

	/**
	 * Copies the data from the given buffer into this one. Capacity will grow
	 * only as necessary to fit the given buffer.
	 * 
	 * @param src
	 *            the source
	 */
	void copyFrom(Buffer src);

	/**
	 * Copies the data from the given buffer into this one. Capacity will grow
	 * only as necessary to fit the given buffer.
	 * 
	 * @param src
	 *            the source
	 */
	void copyFrom(ByteBuffer src);

	/**
	 * Copies the data from the given buffer into this one. Capacity will grow
	 * only as necessary to fit the given buffer.
	 * 
	 * @param src
	 *            the source
	 * @param off
	 *            the offset
	 * @param len
	 *            the length
	 */
	void copyFrom(byte[] src, int off, int len);
}
