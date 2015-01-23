// (C) Copyright 2014 Eric Thill
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the GNU Lesser General Public License
// (LGPL) version 2.1 which accompanies this distribution, and is available at
// http://www.gnu.org/licenses/lgpl-2.1.html
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.

#include <SerialRookIO.h>

SerialRookIO rook;

void primitiveOutput(long id, long value) {
  // forward callback to digitalWrite
  digitalWrite(id, value == 0 ? LOW : HIGH);
}

void setup() {
  // setup serial for rook to use
  Serial.begin(9600);
  
  // set all pins to OUTPUT
  for(int i = 0; i < 13; i++)
    pinMode(i, OUTPUT);
    
  // setup callback to listen for events
  rook.setPrimitiveOutputCallback(primitiveOutput);
}

void loop() {
  rook.read();
}
