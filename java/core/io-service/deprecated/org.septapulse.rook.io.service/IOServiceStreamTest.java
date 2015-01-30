package org.septapulse.rook.io.service;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.septapulse.rook.api.message.Message;
import org.septapulse.rook.api.router.Router;
import org.septapulse.rook.api.service.Sender;
import org.septapulse.rook.api.service.id.ServiceId;
import org.septapulse.rook.core.router.disruptor.DisruptorRouter;

public class IOServiceStreamTest {

	private Router router;
	private IOService ioService;
	private TestService testService;
	private ServiceId ioServiceId = ServiceId.fromName("IO");
	private ServiceId testServiceId = ServiceId.fromName("TEST");
	
	@After
	public void after() {
		if(router != null)
			router.destroy();
		router = null;
	}
	
	@Test
	public void testBatchSend() throws Exception {
		DummyStreamFactory.inputStreamData = null;
		Properties props = new Properties();
		props.put("streamFactory", DummyStreamFactory.class.getName());
		ioService = new IOService(props);
		
		testService = new TestService();
		
		router = DisruptorRouter.builder().build();
		router.addService(ioService, ioServiceId);
		router.addService(testService, testServiceId);
		router.init();
		
		Sender sender = testService.getServiceSender();
		ByteBuffer sendBuffer = sender.nextMessage().asWritableByteBuffer(40);
		sendBuffer.order(ByteOrder.LITTLE_ENDIAN);
		
		// header
		sendBuffer.put((byte)0);
		sendBuffer.putInt(2);
		for(int i = 0; i < 3; i++)
			sendBuffer.put((byte)0);
		
		// block 1
		sendBuffer.putLong(1);
		sendBuffer.putLong(1234);
		
		// block 2
		sendBuffer.putLong(11111111111L);
		sendBuffer.putLong(22222222222L);
		
		// send
		sender.send(ioServiceId);
		
		long start = System.currentTimeMillis();
		while(System.currentTimeMillis()-start < 2000 && DummyStreamFactory.dummyStream.getOutputBytes().length < 40)
			Thread.sleep(10);
		
		byte[] array = DummyStreamFactory.dummyStream.getOutputBytes();
		ByteBuffer payload = ByteBuffer.wrap(array);
		payload.order(ByteOrder.LITTLE_ENDIAN);
		Assert.assertEquals(40, payload.remaining());
		Assert.assertEquals(0, payload.get());
		Assert.assertEquals(2, payload.getInt());
		Assert.assertEquals(0, payload.get());
		Assert.assertEquals(0, payload.get());
		Assert.assertEquals(0, payload.get());
		Assert.assertEquals(1L, payload.getLong());
		Assert.assertEquals(1234L, payload.getLong());
		Assert.assertEquals(11111111111L, payload.getLong());
		Assert.assertEquals(22222222222L, payload.getLong());
	}
	
	@Test
	public void testBatchReceive() throws Exception {
		ByteBuffer sendBuffer = ByteBuffer.allocate(40);
		sendBuffer.order(ByteOrder.LITTLE_ENDIAN);
		
		// header
		sendBuffer.put((byte)0);
		sendBuffer.putInt(2);
		for(int i = 0; i < 3; i++)
			sendBuffer.put((byte)0);
		
		// block 1
		sendBuffer.putLong(1);
		sendBuffer.putLong(1234);
		
		// block 2
		sendBuffer.putLong(11111111111L);
		sendBuffer.putLong(22222222222L);
		
		DummyStreamFactory.inputStreamData = sendBuffer.array();
		Properties props = new Properties();
		props.put("streamFactory", DummyStreamFactory.class.getName());
		ioService = new IOService(props);
		
		testService = new TestService();
		
		router = DisruptorRouter.builder().build();
		router.addService(ioService, ioServiceId);
		router.addService(testService, testServiceId);
		router.init();
		
		Message m = testService.getQueue().poll(2, TimeUnit.SECONDS);
		Assert.assertNotNull("Message was not received", m);
		ByteBuffer payload = m.getPayload().asByteBuffer();
		payload.order(ByteOrder.LITTLE_ENDIAN);
		Assert.assertEquals(payload.remaining(), 40);
		Assert.assertEquals(0, payload.get());
		Assert.assertEquals(2, payload.getInt());
		Assert.assertEquals(0, payload.get());
		Assert.assertEquals(0, payload.get());
		Assert.assertEquals(0, payload.get());
		Assert.assertEquals(1L, payload.getLong());
		Assert.assertEquals(1234L, payload.getLong());
		Assert.assertEquals(11111111111L, payload.getLong());
		Assert.assertEquals(22222222222L, payload.getLong());
	}
	
	@Test
	public void testBufferSend() throws Exception {
		DummyStreamFactory.inputStreamData = null;
		Properties props = new Properties();
		props.put("streamFactory", DummyStreamFactory.class.getName());
		ioService = new IOService(props);
		
		testService = new TestService();
		
		router = DisruptorRouter.builder().build();
		router.addService(ioService, ioServiceId);
		router.addService(testService, testServiceId);
		router.init();
		
		Sender sender = testService.getServiceSender();
		ByteBuffer sendBuffer = sender.nextMessage().asWritableByteBuffer(19);
		sendBuffer.order(ByteOrder.LITTLE_ENDIAN);
		
		// header
		sendBuffer.put((byte)1);
		sendBuffer.putInt(10);
		for(int i = 0; i < 3; i++)
			sendBuffer.put((byte)0);
		
		// buffer
		sendBuffer.put(new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
		
		// send
		sender.send(ioServiceId);
		
		long start = System.currentTimeMillis();
		while(System.currentTimeMillis()-start < 2000 && DummyStreamFactory.dummyStream.getOutputBytes().length < 19)
			Thread.sleep(10);
		
		byte[] array = DummyStreamFactory.dummyStream.getOutputBytes();
		ByteBuffer payload = ByteBuffer.wrap(array);
		payload.order(ByteOrder.LITTLE_ENDIAN);
		Assert.assertEquals(payload.remaining(), 19);
		Assert.assertEquals(payload.get(), 1);
		Assert.assertEquals(payload.getInt(), 10);
		Assert.assertEquals(payload.get(), 0);
		Assert.assertEquals(payload.get(), 0);
		Assert.assertEquals(payload.get(), 0);
		byte[] receivedBuf = new byte[10];
		payload.get(receivedBuf);
		Assert.assertArrayEquals(new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, receivedBuf);
	}
	
	@Test
	public void testBufferReceive() throws Exception {
		ByteBuffer sendBuffer = ByteBuffer.allocate(26);
		sendBuffer.order(ByteOrder.LITTLE_ENDIAN);
		
		// header
		sendBuffer.put((byte)1);
		sendBuffer.putInt(10);
		for(int i = 0; i < 3; i++)
			sendBuffer.put((byte)0);
		sendBuffer.putInt(123456);
		
		// buffer
		sendBuffer.put(new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
		
		DummyStreamFactory.inputStreamData = sendBuffer.array();
		Properties props = new Properties();
		props.put("streamFactory", DummyStreamFactory.class.getName());
		ioService = new IOService(props);
		
		testService = new TestService();
		
		router = DisruptorRouter.builder().build();
		router.addService(ioService, ioServiceId);
		router.addService(testService, testServiceId);
		router.init();
		
		Message m = testService.getQueue().poll(2, TimeUnit.SECONDS);
		Assert.assertNotNull("Message was not received", m);
		ByteBuffer payload = m.getPayload().asByteBuffer();
		payload.order(ByteOrder.LITTLE_ENDIAN);
		Assert.assertEquals(26, payload.remaining());
		Assert.assertEquals(1, payload.get());
		Assert.assertEquals(10, payload.getInt());
		Assert.assertEquals(0, payload.get());
		Assert.assertEquals(0, payload.get());
		Assert.assertEquals(0, payload.get());
		Assert.assertEquals(123456, payload.getInt());
		byte[] receivedBuf = new byte[10];
		payload.get(receivedBuf);
		Assert.assertArrayEquals(new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, receivedBuf);
	}
	
}
