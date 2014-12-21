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
package org.septapulse.rook.core.router.disruptor;

import org.septapulse.rook.api.message.MutableBuffer;
import org.septapulse.rook.api.message.MutableMessage;
import org.septapulse.rook.api.service.Sender;
import org.septapulse.rook.api.service.id.ServiceId;

import com.lmax.disruptor.RingBuffer;

/**
 * 
 * @author Eric Thill
 *
 */
class DisruptorSender implements Sender {
	
	private final RingBuffer<MutableMessage> ringBuffer;
	private final long serviceId;
	private long curSequence;
	private MutableMessage curEvent;
	
	public DisruptorSender(long serviceId, RingBuffer<MutableMessage> ringBuffer) {
		this.serviceId = serviceId;
		this.ringBuffer = ringBuffer;
	}
	
	@Override
	public MutableBuffer nextMessage() {
		if(curEvent != null)
			throw new IllegalStateException("Cannot make conncurrent calls to next(). Must call send(...)");
		curSequence = ringBuffer.next();
		curEvent = ringBuffer.get(curSequence);
		curEvent.reset();
		return curEvent.getPayload();
	}
	
	@Override
	public void send(ServiceId to) {
		send(to, serviceId);
	}
	
	@Override
	public void send(ServiceId to, ServiceId from) {
		send(to, from.getValue());
	}
	
	private void send(ServiceId to, long from) {
		curEvent.getFrom().setValue(from);
		if(to == null) {
			curEvent.setBroadcast(true);
		} else {
			curEvent.setBroadcast(false);
			curEvent.getTo().setValue(to.getValue());
		}
		curEvent.getLocalSender().setValue(serviceId);
		ringBuffer.publish(curSequence);
		curEvent = null;
	}
	
}
