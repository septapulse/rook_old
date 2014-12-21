#include <SerialRookIO.h>

SerialRookIO io;

boolean lastD6 = false;
boolean lastD7 = false;

void primitiveOutput(long id, long value) {
  digitalWrite(id, value == 0 ? LOW : HIGH);
}

void bufferOutput(long id, char* buffer, int length) {

}

void setup() {
  pinMode(3, OUTPUT);
  pinMode(4, OUTPUT);
  pinMode(6, INPUT);
  pinMode(7, INPUT);

  io.setPrimitiveOutputCallback(primitiveOutput);
  io.setBufferOutputCallback(bufferOutput);
  io.init(9600);
}

void loop() {
  io.read();
  
  boolean d6 = digitalRead(6);
  boolean d7 = digitalRead(7);
  if(d6 != lastD6)
    io.sendPrimitiveInput(6, d6 ? 1 : 0);
  //if(d7 != lastD7)
  //  io.sendPrimitiveInput(7, d7 ? 1 : 0);
  lastD6 = d6;
  lastD7 = d7;
}
