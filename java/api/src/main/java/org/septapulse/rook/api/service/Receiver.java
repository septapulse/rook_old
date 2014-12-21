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

import org.septapulse.rook.api.service.id.ServiceId;

/**
 * Is responsible for routing messages to created callbacks. From a
 * {@link Service} perspective, this is used to create callbacks to receive
 * messages.
 * 
 * @author Eric Thill
 *
 */
public interface Receiver {
	/**
	 * Register a {@link MessageCallback} for messages from all services
	 * 
	 * @param callback
	 *            The callback being registered
	 */
	void register(MessageCallback callback);

	/**
	 * Register a {@link MessageCallback} for message from a specified module
	 * 
	 * @param callback
	 *            The callback being registered
	 * @param from
	 *            id of service to callback messages from
	 */
	void register(MessageCallback callback, ServiceId from);
}
