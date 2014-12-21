package org.septapulse.rook.test.io;

import org.septapulse.rook.api.service.AbstractService;
import org.septapulse.rook.api.service.Receiver;
import org.septapulse.rook.api.service.Sender;

public class DummyService extends AbstractService {
	@Override
	public void init() throws Exception {
	}

	@Override
	public void destroy() {
	}
	
	public Sender getServiceSender() {
		return getSender();
	}
	
	public Receiver getServiceReceiver() {
		return getReceiver();
	}
}