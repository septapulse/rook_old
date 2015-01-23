#include "SerialRookIO.h"

void SerialRookIO::writeByte(int value)
{
  while(Serial.write(value) == 0);
}

void SerialRookIO::writeLong(long value)
{
  writeByte(value & 0xFF);
  writeByte((value >> 8) & 0xFF);
  writeByte((value >> 16) & 0xFF);
  writeByte((value >> 24) & 0xFF);
  writeByte((value >> 32) & 0xFF);
  writeByte((value >> 40) & 0xFF);
  writeByte((value >> 48) & 0xFF);
  writeByte((value >> 56) & 0xFF);
}

void SerialRookIO::writeInt(int value)
{
  writeByte(value & 0xFF);
  writeByte((value >> 8) & 0xFF);
  writeByte((value >> 16) & 0xFF);
  writeByte((value >> 24) & 0xFF);
}

int SerialRookIO::readByte()
{
  while(!Serial.available());
  return Serial.read();
}

long SerialRookIO::readLong()
{
  long v = 0;
  for(int i = 0; i < 8; i++) {
    v |= (readByte() >> i*8);
  }
  return v;
}

int SerialRookIO::readInt()
{
  int v = 0;
  for(int i = 0; i < 4; i++) {
    v |= (readByte() >> i*8);
  }
  return v;
}

void SerialRookIO::read()
{
  if (Serial.available()) {
    int type = readByte();
    if(type == 0) {
      int numValues = readInt();
      readByte();
      readByte();
      readByte();
      for(int i = 0; i < numValues; i++) {
        long id = readLong();
        long value = readLong();
        if(primitiveCallback != NULL) {
          (*primitiveCallback)(id, value);
        }
      }
    } else if(type == 1) {
      int length = readInt();
      uint8_t* buf = new uint8_t[length];
      readByte();
      readByte();
      readByte();
      long id = readLong();
      for(int i = 0; i < length; i++) {
        buf[i] = readByte();
      }
      if(bufferCallback != NULL) {
        (*bufferCallback)(id, buf, length);
      }
      delete [] buf;
    }
  }
}

void SerialRookIO::setPrimitiveOutputCallback(PrimitiveCallback& callback)
{
  primitiveCallback = callback;
}

void SerialRookIO::setBufferOutputCallback(BufferCallback& callback)
{
  bufferCallback = callback;
}

void SerialRookIO::sendPrimitiveInput(long id, long value)
{
  // message type
  writeByte(0);

  // number of values
  writeInt(1);

  // skip: 8-byte align
  writeByte(0);
  writeByte(0);
  writeByte(0);

  // id
  writeLong(id);

  // value
  writeLong(value);
}

void SerialRookIO::sendBufferInput(long id, uint8_t* buffer, unsigned int length)
{
  // message type
  writeByte(1);

  // length
  writeInt(length);

  // skip: 8-byte align
  writeByte(0);
  writeByte(0);
  writeByte(0);

  // id
  writeLong(id);

  // buffer
  for(int i = 0; i < length; i++) {
    writeByte(buffer[i]);
  }
}
