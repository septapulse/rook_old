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
package org.septapulse.rook.io.proxy;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.septapulse.rook.api.message.Message;
import org.septapulse.rook.api.service.MessageCallback;
import org.septapulse.rook.api.service.Receiver;
import org.septapulse.rook.api.service.Sender;
import org.septapulse.rook.api.service.id.ServiceId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Eric Thill
 *
 */
public class IOProxy {

	private static final byte ZERO = 0;
	
	private static final byte MSG_TYPE_PRIMITIVE_BATCH = 0;
	private static final byte MSG_TYPE_VARIABLE_BUFFER = 1;

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final ServiceId ioServiceID;
	private final Sender sender;
	private final IOCallbacks callbacks;

	/**
	 * Constructor
	 * 
	 * @param ioServiceID
	 *            The ID of the I/O Service
	 * @param sender
	 *            The sender
	 * @param receiver
	 *            The receiver. Optional if callbacks is null.
	 * @param callbacks
	 *            Optional callbacks to use when messages are received from the
	 *            service.
	 * @param sendBufferLength
	 *            The size of the buffer that is reused to send messages. If a
	 *            message cannot fit into this size, a temporary buffer is
	 *            created so the message can be sent.
	 */
	public IOProxy(
			final ServiceId ioServiceID, 
			final Sender sender,
			final Receiver receiver, 
			final IOCallbacks callbacks,
			final int sendBufferLength) {
		this.ioServiceID = ioServiceID;
		this.sender = sender;
		this.callbacks = callbacks;
		if (callbacks != null) {
			receiver.register(messageCallback, ioServiceID);
		}
	}

	public void sendPrimitiveOutput(final long id, final long value) {
		final ByteBuffer sendBuffer = sender.nextMessage().asWritableByteBuffer(24);
		sendBuffer.order(ByteOrder.LITTLE_ENDIAN);
		
		// header
		sendBuffer.put(MSG_TYPE_PRIMITIVE_BATCH);
		sendBuffer.putInt(1);
		for(int i = 0; i < 3; i++)
			sendBuffer.put(ZERO);
		
		// block
		sendBuffer.putLong(id);
		sendBuffer.putLong(value);
		
		// send
		sender.send(ioServiceID);
	}
	
	public void sendBufferOutput(final long id, final ByteBuffer value) {
		final ByteBuffer sendBuffer = sender.nextMessage().asWritableByteBuffer(16+value.remaining());
		sendBuffer.order(ByteOrder.LITTLE_ENDIAN);
		
		// header
		sendBuffer.put(MSG_TYPE_VARIABLE_BUFFER);
		sendBuffer.putInt(value.remaining());
		for(int i = 0; i < 3; i++)
			sendBuffer.put(ZERO);
		sendBuffer.putLong(id);
		
		// variable buffer
		sendBuffer.put(value);
		
		// send
		sender.send(ioServiceID);
	}

	private final MessageCallback messageCallback = new MessageCallback() {

		@Override
		public void onMessage(final Message message) {
			final ByteBuffer payload = message.getPayload().asByteBuffer();
			payload.order(ByteOrder.LITTLE_ENDIAN);

			final byte type = payload.get();
			switch (type) {
			case MSG_TYPE_PRIMITIVE_BATCH:
				processBatchUpdate(payload);
				break;
			case MSG_TYPE_VARIABLE_BUFFER:
				processBufferUpdate(payload);
				break;
			default:
				logger.warn("Unknown type '" + type + "'");
				break;
			}
		}

		private void processBatchUpdate(ByteBuffer payload) {
			final int numBlocks = payload.getInt();

			skip(payload, 3); // 8-byte aligned

			for (int i = 0; i < numBlocks; i++) {
				final long id = payload.getLong();
				final long value = payload.getLong();
				callbacks.onPrimitiveInputUpdate(id, value);
			}
		}

		private void processBufferUpdate(ByteBuffer payload) {
			final int length = payload.getInt();
			skip(payload, 3); // 8-byte aligned
			final long id = payload.getLong();
			if(payload.remaining() != length)
				logger.warn("Payload length '" + length + "' does not agree with message length '" + 
						payload.remaining() + "' in buffer update.");
			callbacks.onBufferInputUpdate(id, payload);
		}

	};
	
	private static void skip(ByteBuffer bb, int numBytes) {
		for (int i = 0; i < numBytes; i++)
			bb.get();
	}
}
