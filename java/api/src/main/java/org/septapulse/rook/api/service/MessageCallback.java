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

import org.septapulse.rook.api.message.Message;

/**
 * Callback for received messages.
 * 
 * @author Eric Thill
 *
 */
public interface MessageCallback {
	/**
	 * Called when a message is received. All calls to this callback will occur
	 * from a single thread.
	 * 
	 * @param message
	 *            The received message
	 */
	void onMessage(Message message);
}
