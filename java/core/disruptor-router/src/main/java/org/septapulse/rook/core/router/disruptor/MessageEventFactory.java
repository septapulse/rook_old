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

import com.lmax.disruptor.EventFactory;

/**
 * 
 * @author Eric Thill
 *
 */
class MessageEventFactory implements EventFactory<MutableMessage> {

	private final int initialPayloadSize;
	private final boolean checkPayloadBounds;
	private final boolean fillPayloadOnReset;
	
	public MessageEventFactory(
			final int initialPayloadSize, 
			final boolean checkPayloadBounds, 
			final boolean fillPayloadOnReset) {
		this.initialPayloadSize = initialPayloadSize;
		this.checkPayloadBounds = checkPayloadBounds;
		this.fillPayloadOnReset = fillPayloadOnReset;
	}
	
	@Override
	public MutableMessage newInstance() {
		return new MutableMessage(initialPayloadSize, checkPayloadBounds, fillPayloadOnReset);
	}

}
