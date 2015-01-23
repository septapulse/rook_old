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
