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

import org.septapulse.rook.api.message.MutableMessage;
import org.septapulse.rook.api.service.Sender;

import com.lmax.disruptor.RingBuffer;

/**
 * 
 * @author Eric Thill
 *
 */
class DisruptorSender implements Sender {
	
	private final RingBuffer<MutableMessage> ringBuffer;
	private final long serviceId;
	
	public DisruptorSender(long serviceId, RingBuffer<MutableMessage> ringBuffer) {
		this.serviceId = serviceId;
		this.ringBuffer = ringBuffer;
	}
	
	@Override
	public MutableMessage nextMessage() {
		final long seq = ringBuffer.next();
		MutableMessage msg = ringBuffer.get(seq);
		msg.reset();
		msg.setUniqueID(seq);
		msg.getFrom().setValue(serviceId);
		return msg;
	}
	
	@Override
	public void send(MutableMessage msg) {
		msg.getLocalSender().setValue(serviceId);
		ringBuffer.publish(msg.getUniqueID());
	}
	
}
