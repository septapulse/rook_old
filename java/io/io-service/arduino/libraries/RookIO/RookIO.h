#ifndef RookIO_h
#define RookIO_h

typedef void (PrimitiveCallback)(long, long); // id, value
typedef void (BufferCallback)(long, char*, int); // id, buffer, length

class RookIO
{
  public:
    RookIO() { }
    virtual void read() = 0;
    virtual void setPrimitiveOutputCallback(PrimitiveCallback& callback) = 0;
    virtual void setBufferOutputCallback(BufferCallback& callback) = 0;
    virtual void sendPrimitiveInput(long id, long value) = 0;
    virtual void sendBufferInput(long id, char* buffer, int length) = 0;
    virtual ~RookIO() { }
};

#endif
