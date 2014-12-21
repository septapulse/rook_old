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

import java.nio.ByteOrder;

import org.septapulse.rook.api.service.id.MutableServiceId;

/**
 * Mutable implementation of a {@link Message}
 * 
 * @author Eric Thill
 *
 */
public class MutableMessage implements Message {
	private final MutableServiceId from = new MutableServiceId();
	private final MutableServiceId to = new MutableServiceId();
	private final MutableServiceId sender = new MutableServiceId();
	private final boolean fillPayloadOnReset;
	private MutableBuffer payload;
	private boolean broadcast;
	
	public MutableMessage(
			final int initialPayloadSize, 
			final boolean checkPayloadBounds, 
			final boolean fillPayloadOnReset) {
		this.fillPayloadOnReset = fillPayloadOnReset;
		this.payload = new DefaultMutableBuffer(initialPayloadSize, 
				checkPayloadBounds, 
				ByteOrder.BIG_ENDIAN // this is Java, after all
		);
	}
	
	public void reset() {
		from.setValue(0);
		to.setValue(0);
		sender.setValue(0);
		payload.clear(fillPayloadOnReset);
		broadcast = false;
	}
	
	@Override
	public MutableServiceId getFrom() {
		return from;
	}
	
	@Override
	public MutableServiceId getTo() {
		return broadcast ? null : to;
	}
	
	@Override
	public MutableServiceId getLocalSender() {
		return sender;
	}
	
	@Override
	public MutableBuffer getPayload() {
		return payload;
	}
	
	public void setBroadcast(boolean broadcast) {
		this.broadcast = broadcast;
	}
}
