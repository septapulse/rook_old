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
package org.septapulse.rook.core.service.mqtt;

import java.nio.ByteBuffer;
import java.util.Properties;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.septapulse.rook.api.message.MutableBuffer;
import org.septapulse.rook.api.message.Message;
import org.septapulse.rook.api.service.AbstractService;
import org.septapulse.rook.api.service.MessageCallback;
import org.septapulse.rook.api.service.id.MutableServiceId;
import org.septapulse.rook.api.service.id.ServiceId;
import org.septapulse.rook.api.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Bus service that uses MQTT as the middleware
 * 
 * @author ejthill
 *
 */
public class MqttService extends AbstractService {

	private static final byte FALSE = 0;
	private static final byte TRUE = 1;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final ServiceFilter serviceFilter;
	private final String serverUrl;
	private final String clientId;
	private final String topic;
	private final ServiceId myServiceName;
	private MqttClient client;
	
	public MqttService(Properties props) {
		myServiceName = ServiceId.fromName(props.getProperty("name"));
		serviceFilter = new ServiceFilter(props);
		serverUrl = props.getProperty("serverUrl", "tcp://localhost:1883");
		clientId = props.getProperty("clientId");
		topic = props.getProperty("topic", "default");
		if(clientId == null) {
			throw new IllegalArgumentException("clientId not defined");
		}
	}
	
	@Override
	public void init() throws Exception {
		client = new MqttClient(serverUrl,clientId);
		client.connect();
		client.setCallback(mqttCallback);
		getReceiver().register(internalCallback);
	}
	
	private final MqttCallback mqttCallback = new MqttCallback() {
		
		private final MutableServiceId from = new MutableServiceId();
		private final MutableServiceId to = new MutableServiceId();
		
		@Override
		public void messageArrived(String topic, MqttMessage message)
				throws Exception {
			// received message over the wire
			byte[] receiveBuffer = message.getPayload();
			boolean broadcast = receiveBuffer[0] != FALSE;
			to.setValue(IOUtil.readLong(receiveBuffer, 1));
			from.setValue(IOUtil.readLong(receiveBuffer, 9));
			final MutableBuffer payload = getSender().nextMessage();
			payload.copyFrom(receiveBuffer, 0, receiveBuffer.length);
			if(broadcast) {
				getSender().send(null, from);
			} else {
				getSender().send(to, from);
			}
		}
		
		@Override
		public void deliveryComplete(IMqttDeliveryToken token) {
			if(logger.isTraceEnabled()) {
				logger.trace("Delivery Complete");
			}
		}
		
		@Override
		public void connectionLost(Throwable cause) {
			if(logger.isTraceEnabled()) {
				logger.error("Connection Lost");
			}
		}
	};
	
	private final MessageCallback internalCallback = new MessageCallback() {
		private final MqttMessage mqttMessage = new MqttMessage();
		@Override
		public void onMessage(Message message) {
			// Don't re-send messages we received on the wire
			if(message.getLocalSender().getValue() == myServiceName.getValue()) {
				return;
			}
			
			final ServiceId from = message.getFrom();
			final ServiceId to = message.getTo();
			final ByteBuffer payload = message.getPayload().asByteBuffer();
			final int length = payload.remaining();
			
			// check if this service is allowed to send messages out
			if(serviceFilter.isSender(from)) {
				// send the message over the wire
				byte[] sendBuffer = new byte[17+length];
				sendBuffer[0] = (to == null ? TRUE : FALSE);
				IOUtil.writeLong(sendBuffer, 1, to == null ? 0 : to.getValue());
				IOUtil.writeLong(sendBuffer, 9, from == null ? 0 : from.getValue());
				payload.get(sendBuffer, 17, length);
				mqttMessage.setPayload(sendBuffer);
				try {
					client.publish(topic, mqttMessage);
				} catch (MqttException e) {
					logger.error("Could not send message", e);
				}
			}
		}
	};

	@Override
	public void destroy() {
		try {
			client.disconnect();
		} catch (MqttException e) {
			logger.error("Could not disconnect client", e);
		}
	}

}
