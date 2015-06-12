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
package org.septapulse.rook.core.service.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Properties;

import org.septapulse.rook.api.message.Message;
import org.septapulse.rook.api.message.MutableBuffer;
import org.septapulse.rook.api.message.MutableMessage;
import org.septapulse.rook.api.service.AbstractService;
import org.septapulse.rook.api.service.MessageCallback;
import org.septapulse.rook.api.util.BufferUtil;
import org.septapulse.rook.api.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TcpIOService is able to accept a connection from a device via a TCP connection<br>
 * <br>
 * Configuration:<br>
 * ### Required ###<br>
 * port = 4444<br>
 * ### Optional ###<br>
 * host = arduinoyun
 * readBufferSize = 1480
 * 
 * @author Eric Thill
 *
 */
public class TcpIOService extends AbstractService {

	private static final String KEY_PORT = "port";
	private static final String KEY_HOST = "host";
	private static final String KEY_BUFFER_SIZE = "bufferSize";

	private static final int DEFAULT_BUFFER_SIZE = 1500;
	
	private static final byte MSG_TYPE_PRIMITIVE_BATCH = 0;
	private static final byte MSG_TYPE_VARIABLE_BUFFER = 1;

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final String host;
	private final int port;
	private final int bufferSize;
	
	private Socket connection;
	private InputStream in;
	private OutputStream out;
	
	private volatile ReadLoop readLoop;
	
	public TcpIOService(Properties props) {
		this(   props.getProperty(KEY_HOST),
				PropertiesUtil.getRequiredNumber(props, KEY_PORT).intValue(),
				PropertiesUtil.getNumber(props, KEY_BUFFER_SIZE, DEFAULT_BUFFER_SIZE).intValue());
	}

	public TcpIOService(String host, int port, int bufferSize) {
		this.host = host;
		this.port = port;
		this.bufferSize = bufferSize;
	}

	@Override
	public void destroy() {
		readLoop = null;
		try {
			if (connection != null)
				connection.close();
		} catch (IOException e) {
			logger.error("Error closing connection", e);
		}
	}

	@Override
	public void init() throws Exception {
		if(host == null) {
			logger.info("Starting TCP server on port " + port);
			ServerSocket serverSocket = new ServerSocket(port);
			logger.info("Waiting for connection from client...");
			connection = serverSocket.accept();
			logger.info("Connected from " + connection.getInetAddress());
			serverSocket.close();
		} else {
			logger.info("Connecting to " + host + ":" + port);
			connection = new Socket(host, port);
			logger.info("Connected");
		}
		
		in = connection.getInputStream();
		out = connection.getOutputStream();

		logger.info("Starting Read Loop");
		readLoop = new ReadLoop(bufferSize);
		new Thread(readLoop, "TcpIOService Reader").start();

		getReceiver().register(new IOServiceMessageCallback(bufferSize));
		logger.info("Started");
	}

	private class IOServiceMessageCallback implements MessageCallback {
		private final byte[] sendBuffer;
		public IOServiceMessageCallback(int bufferSize) {
			sendBuffer = new byte[bufferSize];
		}
		@Override
		public void onMessage(Message message) {
			if (message.getTo() != null) {
				try {
					final ByteBuffer bb = message.getPayload().asByteBuffer();
					final int length = bb.remaining();
					bb.get(sendBuffer, 0, length);
					out.write(sendBuffer, 0, length);
				} catch (IOException e) {
					logger.error("Send Failure", e);
				}
			}
		}
	};

	private class ReadLoop implements Runnable {
		private final byte[] buffer;
		private int offset = 0;
		
		public ReadLoop(int bufferSize) {
			buffer = new byte[bufferSize];
		}
		
		@Override
		public void run() {
			try {
				while (readLoop == this) {
					if(offset < 8) {
						final int read = in.read(buffer, offset, 8-offset);
						if(read == -1)
							throw new IOException("Client Disconnected");
						offset += read;
					}
					if(offset >= 8) {
						final int messageType = buffer[0] & 0xFF;
						int size;
						if (messageType == MSG_TYPE_PRIMITIVE_BATCH) {
							final int numBlocks = parseInt(buffer, 1);
							size = 8 + numBlocks * 16;
						} else if (messageType == MSG_TYPE_VARIABLE_BUFFER) {
							size = 8 + parseInt(buffer, 1);
						} else {
							throw new IOException("Unexpected message type: "
									+ messageType);
						}
						final int read = in.read(buffer, offset, size-offset);
						if(read == -1)
							throw new IOException("Client Disconnected");
						offset += read;
						if(offset == size) {
							// forward message to router
							final MutableMessage msg = getSender().nextMessage();
							final MutableBuffer sendBuffer = msg.getPayload();
							final ByteBuffer sendByteBuffer = sendBuffer
									.asWritableByteBuffer(size);
							sendByteBuffer.put(buffer, 0, size);
							if (logger.isTraceEnabled()) {
								logger.trace("Sending Input Update "
										+ BufferUtil.toHex(sendBuffer.asByteBuffer()));
							}
							getSender().send(null);
							
							// reset offset
							offset = 0;
						}
					}
				}
			} catch (Throwable t) {
				logger.error("Read Failure", t);
			}
		}
		
		private int parseInt(byte[] b, int off) {
			return (b[off] & 0xFF) | ((b[off + 1] & 0xFF) << 8)
					| ((b[off + 2] & 0xFF) << 16) | ((b[off + 3] & 0xFF) << 24);
		}

	}

}
