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

/**
 * An abstract implementation of a {@link Service} that takes care of
 * pre-initialization setters.
 * 
 * @author Eric Thill
 *
 */
public abstract class AbstractService implements Service {

	private Receiver receiver;
	private Sender sender;

	@Override
	public final void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	@Override
	public final void setSender(Sender sender) {
		this.sender = sender;
	}

	protected final Receiver getReceiver() {
		return receiver;
	}

	protected final Sender getSender() {
		return sender;
	}

	@Override
	public void started() {
		// can be overridden by implementing class
	}
}
