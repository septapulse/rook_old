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
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * 
 * @author Eric Thill
 *
 */
class DefaultMutableBuffer implements MutableBuffer {

	private static final byte ZERO = 0;
	
	private final boolean checkBounds;
	private final ByteOrder defaultByteOrder;
	private byte[] bytes;
	private ByteBuffer writeByteBuffer;
	private ByteBuffer readByteBuffer;
	private int length;
	
	public DefaultMutableBuffer(
			final int initialCapacity, 
			final boolean checkBounds,
			final ByteOrder defaultByteOrder) {
		this.checkBounds = checkBounds;
		this.defaultByteOrder = defaultByteOrder;
		bytes = new byte[initialCapacity];
		writeByteBuffer = ByteBuffer.wrap(bytes);
		readByteBuffer = writeByteBuffer.asReadOnlyBuffer();
	}
	
	@Override
	public byte get(final int i) {
		if(checkBounds && (i < 0 || i >= length))
			throw new ArrayIndexOutOfBoundsException("index=" + i + " length="+length);
		return bytes[i];
	}

	@Override
	public int length() {
		return length;
	}

	@Override
	public ByteBuffer asByteBuffer() {
		readByteBuffer.position(0);
		readByteBuffer.limit(length);
		readByteBuffer.order(defaultByteOrder);
		return readByteBuffer;
	}

	@Override
	public void set(final int i, final byte v) {
		if(v >= length)
			reserve(v+1);
		bytes[i] = v;
	}

	@Override
	public void reserve(int capacity) {
		while(capacity > bytes.length)
			capacity *= 2;
		bytes = new byte[capacity];
		writeByteBuffer = ByteBuffer.wrap(bytes);
		readByteBuffer = writeByteBuffer.asReadOnlyBuffer();
	}

	@Override
	public void length(final int length) {
		reserve(length);
		this.length = length;
	}

	@Override
	public void clear(final boolean fill) {
		length = 0;
		if(fill)
			Arrays.fill(bytes, ZERO);
	}

	@Override
	public ByteBuffer asWritableByteBuffer() {
		writeByteBuffer.position(0);
		writeByteBuffer.limit(length);
		writeByteBuffer.order(defaultByteOrder);
		return writeByteBuffer;
	}

	@Override
	public ByteBuffer asWritableByteBuffer(int length) {
		length(length);
		return asWritableByteBuffer();
	}

	@Override
	public void copyFrom(Buffer src) {
		asWritableByteBuffer(src.length()).put(src.asByteBuffer());
	}

	@Override
	public void copyFrom(ByteBuffer src) {
		asWritableByteBuffer(src.remaining()).put(src);
	}
	
	@Override
	public void copyFrom(byte[] src, int off, int len) {
		asWritableByteBuffer(len).put(src, off, len);
	}

}
