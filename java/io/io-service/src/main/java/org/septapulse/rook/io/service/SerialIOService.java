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
package org.septapulse.rook.io.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;

import jssc.SerialPort;
import jssc.SerialPortException;

import org.septapulse.rook.api.message.Message;
import org.septapulse.rook.api.message.MutableBuffer;
import org.septapulse.rook.api.service.AbstractService;
import org.septapulse.rook.api.service.MessageCallback;
import org.septapulse.rook.api.util.BufferUtil;
import org.septapulse.rook.api.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SerialIOService is able to connect to a Serial Port that provides Rook IO
 * functionality. There are libraries and examples for various hardware provided
 * in the root of this project.<br>
 * <br>
 * Configuration:<br>
 * ### Required ###<br>
 * portName = COM1<br>
 * ### Optional ###<br>
 * baud = 9600<br>
 * dataBits = 8<br>
 * stopBits = 1<br>
 * parity = 0<br>
 * 
 * @author Eric Thill
 *
 */
public class SerialIOService extends AbstractService {

	private static final String KEY_PORT_NAME = "portName";
	private static final String KEY_BAUD = "baud";
	private static final String KEY_DATA_BITS = "dataBits";
	private static final String KEY_STOP_BITS = "stopBits";
	private static final String KEY_PARITY = "parity";
	private static final int DEFAULT_BAUD = 9600;
	private static final int DEFAULT_DATA_BITS = 8;
	private static final int DEFAULT_STOP_BITS = 1;
	private static final int DEFAULT_PARITY = 0;

	private static final byte MSG_TYPE_PRIMITIVE_BATCH = 0;
	private static final byte MSG_TYPE_VARIABLE_BUFFER = 1;

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final SerialPort serialPort;
	private final String portName;
	private final int baud;
	private final int dataBits;
	private final int stopBits;
	private final int parity;

	private volatile ReadLoop readLoop;

	public SerialIOService(Properties props) {
		this(PropertiesUtil.getRequiredProperty(props, KEY_PORT_NAME),
				PropertiesUtil.getNumber(props, KEY_BAUD, DEFAULT_BAUD)
						.intValue(), PropertiesUtil.getNumber(props,
						KEY_DATA_BITS, DEFAULT_DATA_BITS).intValue(),
				PropertiesUtil.getNumber(props, KEY_STOP_BITS,
						DEFAULT_STOP_BITS).intValue(), PropertiesUtil
						.getNumber(props, KEY_PARITY, DEFAULT_PARITY)
						.intValue());
	}

	public SerialIOService(String portName, int baud, int dataBits,
			int stopBits, int parity) {
		this.portName = portName;
		this.baud = baud;
		this.dataBits = dataBits;
		this.stopBits = stopBits;
		this.parity = parity;
		serialPort = new SerialPort(portName);
	}

	@Override
	public void destroy() {
		readLoop = null;
		try {
			if (!serialPort.closePort())
				logger.error("Error closing Serial Port");
		} catch (SerialPortException e) {
			logger.error("Error closing Serial Port", e);
		}
	}

	@Override
	public void init() throws Exception {
		logger.info("Connecting to serial port " + portName);
		if (!serialPort.openPort()) {
			throw new IOException("Could not open serial port");
		}
		logger.info("Connected");
		logger.info("baud=" + baud + " dataBits=" + dataBits + " stopBits="
				+ stopBits + " parity=" + parity);
		if (!serialPort.setParams(baud, dataBits, stopBits, parity)) {
			throw new IOException("Could not set params: baud=" + baud
					+ " dataBits=" + dataBits + " stopBits=" + stopBits
					+ " parity=" + parity);
		}

		// Arduino resets when you connect to it via serial. Wait a bit.
		logger.info("Waiting for Arduino...");
		Thread.sleep(5000);

		logger.info("Starting Read Loop");
		readLoop = new ReadLoop();
		new Thread(readLoop, "SerialIOService Reader").start();

		getReceiver().register(messageCallback);
		logger.info("Started");
	}

	private final MessageCallback messageCallback = new MessageCallback() {
		@Override
		public void onMessage(Message message) {
			if (message.getTo() != null) {
				try {
					// FIXME creates garbage. Forced by underlying library to
					// have array of proper length.
					final ByteBuffer bb = message.getPayload().asByteBuffer();
					byte[] copy = new byte[bb.remaining()];
					bb.get(copy);
					serialPort.writeBytes(copy);
				} catch (SerialPortException e) {
					logger.error("Serial Send Failure", e);
				}
			}
		}
	};

	private class ReadLoop implements Runnable {
		@Override
		public void run() {
			try {
				while (readLoop == this) {
					// read message bytes
					final byte[] header = serialPort.readBytes(8);
					final int messageType = header[0] & 0xFF;
					byte[] body;
					if (messageType == MSG_TYPE_PRIMITIVE_BATCH) {
						final int numBlocks = parseInt(header, 1);
						final int size = numBlocks * 16;
						body = serialPort.readBytes(size);
					} else if (messageType == MSG_TYPE_VARIABLE_BUFFER) {
						final int size = parseInt(header, 1);
						body = serialPort.readBytes(8 + size);
					} else {
						throw new IOException("Unexpected message type: "
								+ messageType);
					}

					// forward message to router
					final MutableBuffer sendBuffer = getSender().nextMessage();
					final ByteBuffer sendByteBuffer = sendBuffer
							.asWritableByteBuffer(header.length + body.length);
					sendByteBuffer.put(header);
					sendByteBuffer.put(body);
					if (logger.isDebugEnabled()) {
						logger.debug("Sending Input Update "
								+ BufferUtil.toHex(sendBuffer.asByteBuffer()));
					}
					getSender().send(null);
				}
			} catch (Throwable t) {
				logger.error("Serial Read Failure", t);
			}
		}

		private int parseInt(byte[] b, int off) {
			return (b[off] & 0xFF) | ((b[off + 1] & 0xFF) << 8)
					| ((b[off + 2] & 0xFF) << 16) | ((b[off + 3] & 0xFF) << 24);
		}
	}

}
