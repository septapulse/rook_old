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
package org.septapulse.rook.core.service.mqtt;

import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;

import java.util.Properties;

import org.septapulse.rook.api.service.id.ServiceId;

/**
 * 
 * @author Eric Thill
 *
 */
public class ServiceFilter {

	private TLongSet senders = new TLongHashSet();
	
	public ServiceFilter(Properties props) {
		populate(props.getProperty("senders"), senders);
	}
	
	private void populate(String csv, TLongSet... dests) {
		if(csv == null) {
			return;
		}
		for(String v : csv.split(",")) {
			for(TLongSet dest : dests) {
				dest.add(ServiceId.fromName(v).getValue());
			}
		}
	}

	public boolean isSender(ServiceId serviceId) {
		return senders.contains(serviceId.getValue());
	}
	
}
