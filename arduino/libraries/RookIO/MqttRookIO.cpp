#include "MqttRookIO.h"

MqttRookIO::MqttRookIO(PubSubClient& client, unsigned int bufferSize=24) {
  this->client = &client;
  this->buffer = (uint8_t *)malloc(sizeof(uint8_t)*bufferSize);
}

void MqttRookIO::read() {
  client->loop();
}

void MqttRookIO::setPrimitiveOutputCallback(PrimitiveCallback& callback) {
  primitiveCallback = callback;
}

void MqttRookIO::setBufferOutputCallback(BufferCallback& callback) {
  bufferCallback = callback;
}

void MqttRookIO::sendPrimitiveInput(long id, long value) {
  // message type
  buffer[0] = 0;

  // number of values
  buffer[1] = 1;
  buffer[2] = 0;
  buffer[3] = 0;
  buffer[4] = 0;

  // skip: 8-byte align
  buffer[5] = 0;
  buffer[6] = 0;
  buffer[7] = 0;

  // id
  buffer[8] = id & 0xFF;
  buffer[9] = (id >> 8) & 0xFF;
  buffer[10] = (id >> 16) & 0xFF;
  buffer[11] = (id >> 24) & 0xFF;
  buffer[12] = (id >> 32) & 0xFF;
  buffer[13] = (id >> 40) & 0xFF;
  buffer[14] = (id >> 48) & 0xFF;
  buffer[15] = (id >> 56) & 0xFF;

  // value
  buffer[16] = value & 0xFF;
  buffer[17] = (value >> 8) & 0xFF;
  buffer[18] = (value >> 16) & 0xFF;
  buffer[19] = (value >> 24) & 0xFF;
  buffer[20] = (value >> 32) & 0xFF;
  buffer[21] = (value >> 40) & 0xFF;
  buffer[22] = (value >> 48) & 0xFF;
  buffer[23] = (value >> 56) & 0xFF;

  client->publish("io",buffer,24);
}

void MqttRookIO::sendBufferInput(long id, uint8_t* buf, unsigned int length) {
  // message type
  buffer[0] = 1;

  // length
  buffer[1] = length & 0xFF;
  buffer[2] = (length >> 8) & 0xFF;
  buffer[3] = (length >> 16) & 0xFF;
  buffer[4] = (length >> 24) & 0xFF;

  // skip: 8-byte align
  buffer[5] = 0;
  buffer[6] = 0;
  buffer[7] = 0;

  // id
  buffer[8] = id & 0xFF;
  buffer[9] = (id >> 8) & 0xFF;
  buffer[10] = (id >> 16) & 0xFF;
  buffer[11] = (id >> 24) & 0xFF;
  buffer[12] = (id >> 32) & 0xFF;
  buffer[13] = (id >> 40) & 0xFF;
  buffer[14] = (id >> 48) & 0xFF;
  buffer[15] = (id >> 56) & 0xFF;

  // buffer
  for(int i = 0; i < length; i++) {
    buffer[16+i] = buf[i];
  }

  client->publish("io",buffer,16+length);
}

