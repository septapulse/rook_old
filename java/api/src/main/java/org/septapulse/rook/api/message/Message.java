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

import org.septapulse.rook.api.service.id.ServiceId;

/**
 * Contains a payload and addressing information
 * 
 * @author Eric Thill
 *
 */
public interface Message {

	/**
	 * The identifier of the service this message was addressed from.
	 * 
	 * @return from
	 */
	ServiceId getFrom();

	/**
	 * The identifier of the service this message was addressed to. If this
	 * message is a broadcast, this will return null.
	 * 
	 * @return to
	 */
	ServiceId getTo();

	/**
	 * The identifier of the local service that last sent this message. This can
	 * differ from the "from" identifier if this message was forwarded from
	 * another source or was purposefully addressed from a different service.
	 * 
	 * @return local sender
	 */
	ServiceId getLocalSender();

	/**
	 * The Buffer containing the payload
	 * 
	 * @return payload
	 */
	Buffer getPayload();
	
	/**
	 * Used internally by the Router
	 * 
	 * @return the unique ID
	 */
	long getUniqueID();

}
