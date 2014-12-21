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

import java.util.Properties;

import org.septapulse.rook.api.runner.RookRunner;

/**
 * A services that can send and receives messages from other services. It
 * provides a set of work that can be utilized by the entire system. In order to
 * be dynamically created by the {@link RookRunner}, a Service must define a
 * constructor that has a single {@link Properties} argument. "name" and
 * "implementation" keys in this properties object are reserved for use by the
 * {@link RookRunner} and should not be used to configure the service.
 * 
 * @author Eric Thill
 *
 */
public interface Service {
	/**
	 * Sets the message receiver to be used by this service. Called before
	 * init().
	 * 
	 * @param receiver
	 *            The receiver
	 */
	void setReceiver(Receiver receiver);

	/**
	 * Sets the message sender to be used by this service. Called before init().
	 * 
	 * @param sender
	 *            The sender
	 */
	void setSender(Sender sender);

	/**
	 * Called to initialize this module. No threads should be spawned by this
	 * module prior to init() being called.
	 * 
	 * @throws Exception
	 *             thrown to stop the initialization process of the system
	 */
	void init() throws Exception;

	/**
	 * Called to shutdown this module. All threads should gracefully terminate
	 * within an acceptable amount of time after this method is called.
	 */
	void destroy();
}
