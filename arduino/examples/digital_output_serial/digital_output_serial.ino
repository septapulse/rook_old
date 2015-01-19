#include <SerialRookIO.h>

SerialRookIO io(9600);

void primitiveOutput(long id, long value) {
  digitalWrite(id, value == 0 ? LOW : HIGH);
}

void bufferOutput(long id, uint8_t* buffer, unsigned int length) {

}

void setup() {
  for(int i = 0; i < 13; i++)
    pinMode(i, OUTPUT);
  io.setPrimitiveOutputCallback(primitiveOutput);
  io.setBufferOutputCallback(bufferOutput);
}

void loop() {
  io.read();
}
