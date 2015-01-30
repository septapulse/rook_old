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
package org.septapulse.rook.io.service;

import java.util.Properties;

import org.septapulse.rook.api.service.AbstractService;
import org.septapulse.rook.api.util.Instantiate;
import org.septapulse.rook.api.util.PropertiesUtil;

/**
 * 
 * @author Eric Thill
 *
 */
public class IOService extends AbstractService {

	private static final String KEY_STREAM_FACTORY_IMPL = "streamFactory"; 
	
	private final StreamFactory streamFactory;
	private final Properties streamProperties;
	private Stream stream;
	private ReadLoop readLoop;
	
	public IOService(Properties props) throws Exception {
		final String streamFactoryImpl = PropertiesUtil.getRequiredProperty(props, KEY_STREAM_FACTORY_IMPL);
		streamFactory = (StreamFactory)Instantiate.instantiate(streamFactoryImpl);
		streamProperties = props;
	}
	
	@Override
	public void init() throws Exception {
		// start reader
		stream = streamFactory.create(streamProperties);
		readLoop = new ReadLoop(stream.getInputStream(), getSender());
		new Thread(readLoop, "IOService Reader").start();
		
		// start writer/forwarder
		final MessageForwarder forwarder = new MessageForwarder(stream.getOutputStream());
		getReceiver().register(forwarder);
	}

	@Override
	public void destroy() {
		readLoop.stop();
		readLoop = null;
		stream.close();
	}
	
}
