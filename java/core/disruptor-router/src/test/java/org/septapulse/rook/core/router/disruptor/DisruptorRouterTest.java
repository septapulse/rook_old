package org.septapulse.rook.core.router.disruptor;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.septapulse.rook.api.message.Message;
import org.septapulse.rook.api.message.MutableMessage;
import org.septapulse.rook.api.router.Router;
import org.septapulse.rook.api.service.Sender;
import org.septapulse.rook.api.service.id.ServiceId;
import org.septapulse.rook.core.router.TestService;

public class DisruptorRouterTest {

	private Router router;
	private TestService testService1;
	private TestService testService2;
	private ServiceId testService1Id = ServiceId.fromName("TEST1");
	private ServiceId testService2Id = ServiceId.fromName("TEST2");
	
	@Before
	public void before() throws Exception {
		testService1 = new TestService();
		testService2 = new TestService();
		
		router = DisruptorRouter.builder().build();
		router.addService(testService1, testService1Id);
		router.addService(testService2, testService2Id);
		router.init();
	}
	
	@After
	public void after() {
		if(router != null)
			router.destroy();
		router = null;
	}
	
	@Test
	public void testSendReceive() throws Exception {
		// send message from TEST1 to TEST2
		Sender sender = testService1.getServiceSender();
		MutableMessage msg = sender.nextMessage();
		ByteBuffer sendBuffer = msg.getPayload().asWritableByteBuffer(8);
		sendBuffer.putInt(1111);
		sendBuffer.putInt(2222);
		msg.setTo(testService2Id);
		sender.send(msg);
		
		Message received = testService2.getQueue().poll(1, TimeUnit.SECONDS);
		Assert.assertEquals(received.getTo(), testService2Id);
		Assert.assertEquals(received.getFrom(), testService1Id);
		Assert.assertEquals(received.getLocalSender(), testService1Id);
		ByteBuffer bb = received.getPayload().asByteBuffer();
		Assert.assertEquals(bb.getInt(), 1111);
		Assert.assertEquals(bb.getInt(), 2222);
	}
	
	@Test
	public void testSendReceiveMultiple() throws Exception {
		// send message 1 from TEST1 to TEST2
		Sender sender = testService1.getServiceSender();
		MutableMessage msg = sender.nextMessage();
		ByteBuffer sendBuffer = msg.getPayload().asWritableByteBuffer(8);
		sendBuffer.putInt(1111);
		sendBuffer.putInt(2222);
		msg.setTo(testService2Id);
		sender.send(msg);
		
		// send message 2 from TEST1 to TEST2
		msg = sender.nextMessage();
		sendBuffer = msg.getPayload().asWritableByteBuffer(8);
		sendBuffer.putInt(3333);
		sendBuffer.putInt(4444);
		msg.setTo(testService2Id);
		sender.send(msg);
		
		Message received = testService2.getQueue().poll(1, TimeUnit.SECONDS);
		Assert.assertEquals(received.getTo(), testService2Id);
		Assert.assertEquals(received.getFrom(), testService1Id);
		Assert.assertEquals(received.getLocalSender(), testService1Id);
		ByteBuffer bb = received.getPayload().asByteBuffer();
		Assert.assertEquals(bb.getInt(), 1111);
		Assert.assertEquals(bb.getInt(), 2222);
		
		received = testService2.getQueue().poll(1, TimeUnit.SECONDS);
		Assert.assertEquals(received.getTo(), testService2Id);
		Assert.assertEquals(received.getFrom(), testService1Id);
		Assert.assertEquals(received.getLocalSender(), testService1Id);
		bb = received.getPayload().asByteBuffer();
		Assert.assertEquals(bb.getInt(), 3333);
		Assert.assertEquals(bb.getInt(), 4444);
	}
	
	@Test
	public void testBroadcast() throws Exception {
		// send message from TEST1 to TEST2
		Sender sender = testService1.getServiceSender();
		MutableMessage msg = sender.nextMessage();
		ByteBuffer sendBuffer = msg.getPayload().asWritableByteBuffer(8);
		sendBuffer.putInt(1111);
		sendBuffer.putInt(2222);
		msg.setTo(null);
		sender.send(msg);
		
		Message received1 = testService1.getQueue().poll(1, TimeUnit.SECONDS);
		Assert.assertNull(received1.getTo());
		Assert.assertEquals(received1.getFrom(), testService1Id);
		Assert.assertEquals(received1.getLocalSender(), testService1Id);
		ByteBuffer bb1 = received1.getPayload().asByteBuffer();
		Assert.assertEquals(bb1.getInt(), 1111);
		Assert.assertEquals(bb1.getInt(), 2222);
		
		Message received2 = testService2.getQueue().poll(1, TimeUnit.SECONDS);
		Assert.assertNull(received2.getTo());
		Assert.assertEquals(received2.getFrom(), testService1Id);
		Assert.assertEquals(received2.getLocalSender(), testService1Id);
		ByteBuffer bb2 = received1.getPayload().asByteBuffer();
		Assert.assertEquals(bb2.getInt(), 1111);
		Assert.assertEquals(bb2.getInt(), 2222);
	}
}
