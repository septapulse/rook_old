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

import org.septapulse.rook.api.message.MutableMessage;
import org.septapulse.rook.api.router.Router;

/**
 * Sends messages to services.
 * 
 * @author Eric Thill
 *
 */
public interface Sender{
	/**
	 * Reserve the next message to send. This message should be populated and
	 * sent immediately as various {@link Router} implementations may block all
	 * thread receivers until this message is sent. As a result, Messages
	 * returned from this method must NEVER be cached. The default value of the
	 * "from" field in the message will be this service's ID.
	 * 
	 * @return the message to populate
	 */
	MutableMessage nextMessage();

	/**
	 * Dispatch the given message. The message must have been retrieved from
	 * this class's nextMessage() function.
	 * 
	 * @param message
	 *            The message to send
	 */
	void send(MutableMessage message);

}
