#ifndef MqttRookIO_h
#define MqttRookIO_h

#include "RookIO.h"
#include "PubSubClient.h"

class MqttRookIO : public RookIO {
  public:
    MqttRookIO(PubSubClient& client, unsigned int bufferSize);
    void read();
    void setPrimitiveOutputCallback(PrimitiveCallback& callback);
    void setBufferOutputCallback(BufferCallback& callback);
    void sendPrimitiveInput(long id, long value);
    void sendBufferInput(long id, uint8_t* buffer, unsigned int length);
    ~MqttRookIO() { }
  private:
    PrimitiveCallback* primitiveCallback = 0;
    BufferCallback* bufferCallback = 0;
    PubSubClient* client = 0;
    uint8_t* buffer = 0;
};

#endif
