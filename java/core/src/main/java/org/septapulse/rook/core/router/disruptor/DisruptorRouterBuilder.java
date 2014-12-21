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

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;

/**
 * 
 * @author Eric Thill
 *
 */
public class DisruptorRouterBuilder {

	private ExecutorServiceFactory executorServiceFactory = new FixedExecutorServiceFactory();
	private int ringBufferSize = 64;
	private int payloadInitialSize = 1024;
	private boolean payloadCheckBounds = true;
	private boolean payloadFillOnReset = true;
	private WaitStrategy waitStrategy = new BlockingWaitStrategy();
	
	public DisruptorRouter build() {
		return new DisruptorRouter(ringBufferSize, waitStrategy, 
				executorServiceFactory, payloadInitialSize,
				payloadCheckBounds, payloadFillOnReset);
	}
	
	public DisruptorRouterBuilder executorServiceFactory(ExecutorServiceFactory executorServiceFactory) {
		this.executorServiceFactory = executorServiceFactory;
		return this;
	}
	
	public DisruptorRouterBuilder ringBufferSize(int ringBufferSize) {
		this.ringBufferSize = ringBufferSize;
		return this;
	}
	
	public DisruptorRouterBuilder payloadInitialSize(int payloadInitialSize) {
		this.payloadInitialSize = payloadInitialSize;
		return this;
	}
	
	public DisruptorRouterBuilder payloadCheckBounds(boolean payloadCheckBounds) {
		this.payloadCheckBounds = payloadCheckBounds;
		return this;
	}
	
	public DisruptorRouterBuilder payloadFillOnReset(boolean payloadFillOnReset) {
		this.payloadFillOnReset = payloadFillOnReset;
		return this;
	}
	
	public DisruptorRouterBuilder waitStrategy(WaitStrategy waitStrategy) {
		this.waitStrategy = waitStrategy;
		return this;
	}
}
