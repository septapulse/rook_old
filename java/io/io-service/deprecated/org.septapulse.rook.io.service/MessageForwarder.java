package org.septapulse.rook.io.service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import org.septapulse.rook.api.message.Message;
import org.septapulse.rook.api.service.MessageCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MessageForwarder implements MessageCallback {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final OutputStream out;
	private byte[] copyArray = new byte[1024];

	public MessageForwarder(OutputStream out) {
		this.out = out;
	}

	@Override
	public void onMessage(Message message) {
		final ByteBuffer payload = message.getPayload().asByteBuffer();
		payload.order(ByteOrder.LITTLE_ENDIAN);
		final int len = payload.remaining();
		byte[] array = getCopyArray(len);
		payload.get(array, 0, len);
		try {
			out.write(array, 0, len);
		} catch (IOException e) {
			logger.error("Could not write to stream", e);
		}
	}

	private byte[] getCopyArray(int requiredCapacity) {
		if (copyArray == null || copyArray.length < requiredCapacity)
			copyArray = new byte[requiredCapacity];
		return copyArray;
	}
}