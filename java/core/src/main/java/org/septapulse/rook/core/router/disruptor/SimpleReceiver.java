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

import org.septapulse.rook.api.message.Message;
import org.septapulse.rook.api.service.MessageCallback;
import org.septapulse.rook.api.service.Receiver;
import org.septapulse.rook.api.service.id.ServiceId;

/**
 * 
 * @author Eric Thill
 *
 */
class SimpleReceiver implements Receiver {

	private ImmutableMessageCallbacks callbacks = new ImmutableMessageCallbacks();
	
	public void handle(Message message) {
		callbacks.handle(message);
	}
	
	@Override
	public void register(MessageCallback callback) {
		callbacks = callbacks.add(callback, null);
	}

	@Override
	public void register(MessageCallback callback, ServiceId from) {
		callbacks = callbacks.add(callback, from);
	}

}
