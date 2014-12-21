package org.septapulse.rook.api.util;

import java.nio.ByteBuffer;

public class BufferUtil {

	public static String toHex(ByteBuffer bb) {
		StringBuilder sb = new StringBuilder("[ ");
		while(bb.hasRemaining()) {
			sb.append(String.format("%02X ", bb.get()));
		}
		sb.append("]");
		return sb.toString();
	}
	
	public static String toString(ByteBuffer bb) {
		StringBuilder sb = new StringBuilder();
		while(bb.hasRemaining()) {
			sb.append((char)bb.get());
		}
		return sb.toString();
	}
}
