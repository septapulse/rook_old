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
package org.septapulse.rook.api.service.id;

/**
 * A service identifier. It provides methods to parse and convert readable
 * service names to and from a processor friendly long value. Service values are
 * the Base36 counter-part to the service name. As such, the string
 * representation of a service name should only consist of characters [0-9][A-Z]
 * and must be 1 to 12 characters in length.
 * 
 * @author Eric Thill
 *
 */
public abstract class ServiceId {

	protected static final int BASE = 36;

	public static ServiceId fromName(String name) {
		Long value = Long.parseLong(name, BASE);
		return new ImmutableServiceId(value);
	}

	public static ServiceId fromValue(long value) {
		return new ImmutableServiceId(value);
	}

	public abstract long getValue();

	@Override
	public final int hashCode() {
		final long value = getValue();
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (value ^ (value >>> 32));
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ServiceId))
			return false;
		ServiceId other = (ServiceId) obj;
		if (getValue() != other.getValue())
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getValue() + ":" + Long.toString(getValue(), BASE);
	}
}
