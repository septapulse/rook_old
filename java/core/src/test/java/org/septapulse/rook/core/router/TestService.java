package org.septapulse.rook.core.router;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.septapulse.rook.api.message.Message;
import org.septapulse.rook.api.message.MutableMessage;
import org.septapulse.rook.api.service.AbstractService;
import org.septapulse.rook.api.service.MessageCallback;
import org.septapulse.rook.api.service.Receiver;
import org.septapulse.rook.api.service.Sender;

public class TestService extends AbstractService {

	public BlockingQueue<Message> receiveQueue = new LinkedBlockingQueue<>();
	
	@Override
	public void init() throws Exception {
		getReceiver().register(new MessageCallback() {
			@Override
			public void onMessage(Message message) {
				MutableMessage copy = new MutableMessage(1024, true, true);
				if(message.getTo() == null) {
					copy.setBroadcast(true);
				} else {
					copy.setBroadcast(false);
					copy.getTo().setValue(message.getTo().getValue());
				}
				copy.getFrom().setValue(message.getFrom().getValue());
				copy.getLocalSender().setValue(message.getLocalSender().getValue());
				copy.getPayload().copyFrom(message.getPayload());
				receiveQueue.add(copy);
			}
		});
	}

	@Override
	public void destroy() {

	}

	public BlockingQueue<Message> getQueue() {
		return receiveQueue;
	}
	
	public Sender getServiceSender() {
		return getSender();
	}
	
	public Receiver getServiceReceiver() {
		return getReceiver();
	}
}
