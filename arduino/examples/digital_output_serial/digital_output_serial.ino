#include <SerialRookrook.h>

SerialRookIO rook(9600);

void primitiveOutput(long id, long value) {
  digitalWrite(id, value == 0 ? LOW : HIGH);
}

void setup() {
  for(int i = 0; i < 13; i++)
    pinMode(i, OUTPUT);
  rook.setPrimitiveOutputCallback(primitiveOutput);
}

void loop() {
  rook.read();
}
