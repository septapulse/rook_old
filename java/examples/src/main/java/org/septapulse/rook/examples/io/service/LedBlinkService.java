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
package org.septapulse.rook.examples.io.service;

import java.util.Properties;

import org.septapulse.rook.api.service.AbstractService;
import org.septapulse.rook.api.service.id.ServiceId;
import org.septapulse.rook.io.proxy.IOProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Eric Thill
 *
 */
public class LedBlinkService extends AbstractService {

	private static final String KEY_IO_SERVICE_NAME = "io.service.name";
	private static final String KEY_LED_ID = "led.id";
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final int ledId;
	private final ServiceId ioServiceId;
	private IOProxy ioProxy;
	private volatile Loop loop;
	
	public LedBlinkService(Properties properties) {
		String idStr = properties.getProperty(KEY_LED_ID);
		if(idStr == null)
			throw new IllegalArgumentException(KEY_LED_ID + " is not defined in " + properties);
		ledId = Integer.parseInt(idStr);
		
		String ioServiceNameStr = properties.getProperty(KEY_IO_SERVICE_NAME);
		if(ioServiceNameStr == null)
			throw new IllegalArgumentException(KEY_IO_SERVICE_NAME + " is not defined in " + properties);
		ioServiceId = ServiceId.fromName(ioServiceNameStr);
	}
	
	@Override
	public void init() throws Exception {
		ioProxy = new IOProxy(ioServiceId, 
				getSender(), getReceiver(), null, 1024);
		loop = new Loop();
	}
	
	@Override
	public void started() {
		// Don't start the blink thread until all threads are initialized
		new Thread(loop, "LED Blinker").start();
	}

	@Override
	public void destroy() {
		loop = null;
	}

	private class Loop implements Runnable {
		@Override
		public void run() {
			boolean state = false;
			while(loop == this) {
				sleep();
				state = !state;
				logger.info("Setting LED " + ledId + " = " + state);
				ioProxy.sendPrimitiveOutput(ledId, state ? 1 : 0);
			}
		}

		private void sleep() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
