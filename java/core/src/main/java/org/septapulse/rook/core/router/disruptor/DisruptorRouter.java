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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.septapulse.rook.api.message.MutableMessage;
import org.septapulse.rook.api.router.Router;
import org.septapulse.rook.api.service.Service;
import org.septapulse.rook.api.service.id.ServiceId;
import org.septapulse.rook.api.util.Instantiate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * Implementation of a {@link Router} that uses the Lmax Disruptor 
 * for the threading model and central message buffer.
 * 
 * @author Eric Thill
 *
 */
public class DisruptorRouter implements Router {

	public static DisruptorRouterBuilder builder() {
		return new DisruptorRouterBuilder();
	}
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Map<Service, Long> serviceIds = new LinkedHashMap<>();
	private final List<ServiceHandler> handlers = new ArrayList<>();
	private final EventFactory<MutableMessage> eventFactory;
	private final ExecutorServiceFactory executorServiceFactory;
	private final int ringBufferSize;
	private final WaitStrategy waitStrategy;
	private ExecutorService executorService;
	private Disruptor<MutableMessage> disruptor;
	private volatile boolean init;
	
	public DisruptorRouter(Properties props) throws Exception {
		this(Integer.parseInt(props.getProperty("ringBufferSize", "64")),
				Instantiate.instantiate(DisruptorRouterBuilder.class.getClassLoader(), props.getProperty("waitStrategy", "com.lmax.disruptor.BlockingWaitStrategy")),
				Instantiate.instantiate(DisruptorRouterBuilder.class.getClassLoader(), props.getProperty("executorFactory", "org.septapulse.rook.core.router.disruptor.FixedExecutorServiceFactory")),
				Integer.parseInt(props.getProperty("payload.initialSize", "1024")),
				Boolean.parseBoolean(props.getProperty("payload.checkBounds", "true")),
				Boolean.parseBoolean(props.getProperty("payload.fillOnReset", "true")));
	}
	
	public DisruptorRouter(
			final int ringBufferSize,
			final WaitStrategy waitStrategy,
			final ExecutorServiceFactory executorServiceFactory,
			final int initialPayloadSize,
			final boolean checkPayloadBounds,
			final boolean fillPayloadOnReset) {
		this.ringBufferSize = ringBufferSize;
		this.waitStrategy = waitStrategy;
		this.eventFactory = new MessageEventFactory(initialPayloadSize, checkPayloadBounds, fillPayloadOnReset);
		this.executorServiceFactory = executorServiceFactory;
	}
	
	@Override
	public void addService(Service service, ServiceId id) {
		if(init) {
			throw new IllegalStateException("Cannot add a new service after initialization");
		}
		long serviceId = id.getValue();
		SimpleReceiver receiver = new SimpleReceiver();
		service.setReceiver(receiver);
		serviceIds.put(service, serviceId);
		ServiceHandler handler = new ServiceHandler(serviceId, receiver);
		handlers.add(handler);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init() throws Exception {
		if(init) {
			throw new IllegalStateException("Already Initialized");
		}
		init = true;
		
		logger.info("Initializing...");
		
		executorService = executorServiceFactory.create(serviceIds.size());
		disruptor = new Disruptor<>(
				eventFactory, 
				ringBufferSize, 
				executorService, 
				ProducerType.MULTI, 
				waitStrategy);
		disruptor.handleEventsWith(handlers.toArray(new EventHandler[handlers.size()]));
		
		final List<Service> initializedServices = new ArrayList<>();
		for(Map.Entry<Service, Long> serviceIdEntry : serviceIds.entrySet()) {
			final Service service = serviceIdEntry.getKey();
			long id = serviceIdEntry.getValue();
			service.setSender(new DisruptorSender(id, disruptor.getRingBuffer()));
			try {
				logger.info("Initializing Service: " + ServiceId.fromValue(id).toString());
				service.init();
			} catch (Exception e) {
				for(Service initializedService : initializedServices) {
					initializedService.destroy();
				}
				throw e;
			}
			initializedServices.add(service);
		}
		
		logger.info("Starting Disruptor");
		disruptor.start();
		logger.info("Initialized");
	}
	
	@Override
	public void destroy() {
		if(!init) {
			throw new IllegalStateException("Never Initialized");
		}
		executorService.shutdown();
		disruptor.halt();
		disruptor.shutdown();
	}

}
