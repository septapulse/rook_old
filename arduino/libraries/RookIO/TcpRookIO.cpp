#include "TcpRookIO.h"

TcpRookIO::TcpRookIO(Client& client)
{
  this->client = &client;
}

void TcpRookIO::writeByte(int value)
{
  client->write((uint8_t)value);
}

void TcpRookIO::writeLong(long value)
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

void TcpRookIO::writeInt(int value)
{
  writeByte(value & 0xFF);
  writeByte((value >> 8) & 0xFF);
  writeByte((value >> 16) & 0xFF);
  writeByte((value >> 24) & 0xFF);
}

int TcpRookIO::readByte()
{
  return client->read();
}

long TcpRookIO::readLong()
{
  long v = 0;
  for(int i = 0; i < 8; i++) {
    v |= (readByte() >> i*8);
  }
  return v;
}

int TcpRookIO::readInt()
{
  int v = 0;
  for(int i = 0; i < 4; i++) {
    v |= (readByte() >> i*8);
  }
  return v;
}

void TcpRookIO::read()
{
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

void TcpRookIO::setPrimitiveOutputCallback(PrimitiveCallback& callback)
{
  primitiveCallback = callback;
}

void TcpRookIO::setBufferOutputCallback(BufferCallback& callback)
{
  bufferCallback = callback;
}

void TcpRookIO::sendPrimitiveInput(long id, long value)
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

  client->flush();
}

void TcpRookIO::sendBufferInput(long id, uint8_t* buffer, unsigned int length)
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

  client->flush();
}
