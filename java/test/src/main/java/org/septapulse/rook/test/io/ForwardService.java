package org.septapulse.rook.test.io;

import java.nio.ByteBuffer;
import java.util.Properties;

import org.septapulse.rook.api.service.AbstractService;
import org.septapulse.rook.api.service.id.ServiceId;
import org.septapulse.rook.api.util.PropertiesUtil;
import org.septapulse.rook.io.proxy.IOCallbacks;
import org.septapulse.rook.io.proxy.IOProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForwardService extends AbstractService implements IOCallbacks {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final ServiceId ioServiceId;
	private final long inPin;
	private final long outPin;
	private IOProxy ioProxy;
	
	public ForwardService(Properties props) {
		this(ServiceId.fromName(PropertiesUtil.getRequiredProperty(props, "ioServiceId")),
				PropertiesUtil.getRequiredNumber(props, "inPin").longValue(),
				PropertiesUtil.getRequiredNumber(props, "outPin").longValue());
	}
	
	public ForwardService(ServiceId ioServiceId, long inPin, long outPin) {
		this.ioServiceId = ioServiceId;
		this.inPin = inPin;
		this.outPin = outPin;
	}

	@Override
	public void init() throws Exception {
		ioProxy = new IOProxy(ioServiceId, getSender(), getReceiver(), this, 1024);
	}
	
	@Override
	public void onPrimitiveInputUpdate(long id, long value) {
		if(inPin == id) {
			logger.info("Received Input from " + id);
			ioProxy.sendPrimitiveOutput(outPin, value == 1L ? 0L : 1L);
		}
	}
	
	@Override
	public void onBufferInputUpdate(long id, ByteBuffer value) {

	}

	@Override
	public void destroy() {

	}

}