#ifndef SerialRookIO_h
#define SerialRookIO_h

#include "RookIO.h"

class SerialRookIO : public RookIO {
  public:
    SerialRookIO() { }
    void read();
    void setPrimitiveOutputCallback(PrimitiveCallback& callback);
    void setBufferOutputCallback(BufferCallback& callback);
    void sendPrimitiveInput(long id, long value);
    void sendBufferInput(long id, uint8_t* buffer, unsigned int length);
    ~SerialRookIO() { }
  private:
    PrimitiveCallback* primitiveCallback = 0;
    BufferCallback* bufferCallback = 0;
    void writeLong(long value);
    void writeInt(int value);
    long readLong();
    int readInt();
    int readByte();
    void writeByte(int b);
};

#endif
