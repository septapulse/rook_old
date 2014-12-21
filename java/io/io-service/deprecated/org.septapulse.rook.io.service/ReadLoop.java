package org.septapulse.rook.io.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.septapulse.rook.api.service.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ReadLoop implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final InputStream in;
	private final Sender sender;
	private volatile boolean run = true;
	
	public ReadLoop(final InputStream in, final Sender sender) {
		this.in = in;
		this.sender = sender;
	}
	
	public void stop() {
		run = false;
	}
	
	@Override
	public void run() {
		try {
			while (run) {
				final byte msgType = read(in);
				final int length = readInt(in);
				final int skipBytes = 3;
				if (msgType == 0) {
					final int remainingBytes = skipBytes + length * 16;
					ByteBuffer payload = sender.nextMessage().asWritableByteBuffer(5 + remainingBytes);
					payload.order(ByteOrder.LITTLE_ENDIAN);
					payload.put(msgType);
					payload.putInt(length);
					for (int i = 0; i < remainingBytes; i++) {
						payload.put(read(in));
					}
					sender.send(null);
				} else if (msgType == 1) {
					final int remainingBytes = skipBytes + 8 + length;
					ByteBuffer payload = sender.nextMessage().asWritableByteBuffer(5 + remainingBytes);
					payload.order(ByteOrder.LITTLE_ENDIAN);
					payload.put(msgType);
					payload.putInt(length);
					for (int i = 0; i < remainingBytes; i++) {
						payload.put(read(in));
					}
					sender.send(null);
				}
			}
		} catch (IOException e) {
			logger.error("Input stream crashed", e);
		}
	}

	private byte read(InputStream in) throws IOException {
		int v = in.read();
		if (v == -1)
			throw new IOException("Unexpected end of stream");
		return (byte) v;
	}

	private int readInt(InputStream in) throws IOException {
		return (read(in) & 0xFF) | ((read(in) & 0xFF) << 8)
				| ((read(in) & 0xFF) << 16) | ((read(in) & 0xFF) << 24);
	}
}