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

import java.util.Arrays;

import org.septapulse.rook.api.message.Message;
import org.septapulse.rook.api.service.MessageCallback;
import org.septapulse.rook.api.service.id.ServiceId;

/**
 * 
 * @author Eric Thill
 *
 */
class ImmutableMessageCallbacks {

	private final CallbackFilterPair[] callbacks;

	public ImmutableMessageCallbacks() {
		this.callbacks = new CallbackFilterPair[0];
	}

	private ImmutableMessageCallbacks(CallbackFilterPair[] callbacks) {
		this.callbacks = callbacks;
	}

	public ImmutableMessageCallbacks add(MessageCallback callback, ServiceId from) {
		CallbackFilterPair newFilter = new CallbackFilterPair(from == null ? -1 : from.getValue(), callback);
		CallbackFilterPair[] newCallbacks = Arrays.copyOf(callbacks, callbacks.length+1);
		newCallbacks[newCallbacks.length-1]=newFilter;
		return new ImmutableMessageCallbacks(newCallbacks);
	}

	public void handle(Message message) {
		for(CallbackFilterPair cfp : callbacks) {
			if(cfp.filter == -1 || cfp.filter == message.getFrom().getValue()) {
				cfp.callback.onMessage(message);
			}
		}
	}

	private static class CallbackFilterPair {
		public final long filter;
		public final MessageCallback callback;

		public CallbackFilterPair(long filter, MessageCallback callback) {
			this.filter = filter;
			this.callback = callback;
		}
	}
}
