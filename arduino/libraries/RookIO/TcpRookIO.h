#ifndef TcpRookIO_h
#define TcpRookIO_h

#include "RookIO.h"
#include <Client.h>

class TcpRookIO : public RookIO {
  public:
    TcpRookIO() { };
    TcpRookIO(Client& client) {  this->client = &client; };
    void setClient(Client& client) { this->client = &client; }
    void read();
    void setPrimitiveOutputCallback(PrimitiveCallback& callback);
    void setBufferOutputCallback(BufferCallback& callback);
    void sendPrimitiveInput(long id, long value);
    void sendBufferInput(long id, uint8_t* buffer, unsigned int length);
    ~TcpRookIO() { }
  private:
    Client* client;
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
