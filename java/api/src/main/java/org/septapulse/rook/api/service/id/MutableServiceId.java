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
 * A mutable service id
 * 
 * @author Eric Thill
 *
 */
public class MutableServiceId extends ServiceId {

	private long value;

	public void parseFromString(String string) {
		if (string.length() == 0 || string.length() > 12) {
			throw new IllegalArgumentException(
					"Service name must be between 1 and 12 characters: "
							+ string);
		}
		setValue(Long.parseLong(string, BASE));
	}

	public void setValue(long value) {
		this.value = value;
	}

	public MutableServiceId copy() {
		MutableServiceId copy = new MutableServiceId();
		copy.value = value;
		return copy;
	}

	@Override
	public long getValue() {
		return value;
	}

}
