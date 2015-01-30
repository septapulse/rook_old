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

import com.lmax.disruptor.EventHandler;

/**
 * 
 * @author Eric Thill
 *
 */
class ServiceHandler implements EventHandler<MutableMessage> {

	private final long serviceId;
	private final SimpleReceiver receiver;

	public ServiceHandler(long serviceId, SimpleReceiver receiver) {
		this.serviceId = serviceId;
		this.receiver = receiver;
	}

	public void onEvent(MutableMessage event, long sequence, boolean endOfBatch)
			throws Exception {
		if (event.getTo() == null) {
			receiver.handle(event);
		} else if (serviceId == 0 || event.getTo().getValue() == serviceId) {
			receiver.handle(event);
		}
	}
}
