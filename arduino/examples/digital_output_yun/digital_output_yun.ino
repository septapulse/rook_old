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

#include <Bridge.h>
#include <YunClient.h>
#include <TcpRookIO.h>

YunClient client;
TcpRookIO rook(client);
byte ip[] = { 10, 0, 1, 12 };
int port = 1958;

void primitiveOutput(long id, long value) {
  digitalWrite(id, value == 0 ? LOW : HIGH);
}

void setup() {
  for(int i = 0; i < 13; i++)
    pinMode(i, OUTPUT);

  rook.setPrimitiveOutputCallback(primitiveOutput);

  Bridge.begin();
  while (!client.connect(ip, port)) {
    delay(2000);
    client.stop();
  }
}

void loop() {
  rook.read();
}
