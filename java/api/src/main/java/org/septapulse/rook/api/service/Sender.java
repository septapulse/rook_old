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
package org.septapulse.rook.api.service;

import org.septapulse.rook.api.message.MutableBuffer;
import org.septapulse.rook.api.router.Router;
import org.septapulse.rook.api.service.id.ServiceId;

/**
 * Sends messages to services.
 * 
 * @author Eric Thill
 *
 */
public interface Sender {
	/**
	 * Reserve the next message to send. This message should be populated and
	 * sent immediately as various {@link Router} implementations may block all
	 * thread receivers until this message is sent. As a result, Buffers
	 * returned from this method must NEVER be cached.
	 * 
	 * @return the buffer to populate
	 */
	MutableBuffer nextMessage();

	/**
	 * Send the GrowableBuffer returned by next() to the given Service ID. It
	 * will be addressed from this sender. If 'to' is null, this message will be
	 * broadcast to all services. Otherwise it will only be sent to services
	 * with the given 'to' service ID.
	 * 
	 * @param to
	 *            The 'to' ID to address in the message. Null will broadcast to
	 *            all Services.
	 */
	void send(ServiceId to);

	/**
	 * Send the GrowableBuffer returned by next() to the given Service ID. It
	 * will be addressed from the given "from" sender ID. If 'to' is null, this
	 * message will be broadcast to all services. Otherwise it will only be sent
	 * to services with the given 'to' service ID.
	 * 
	 * @param to
	 *            The 'to' ID to address in the message. Null will broadcast to
	 *            all Services.
	 * @param from
	 *            The 'from' ID to address int the message.
	 */
	void send(ServiceId to, ServiceId from);

}
