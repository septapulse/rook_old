package org.septapulse.rook.test.io;

import java.nio.ByteBuffer;
import java.util.Properties;

import org.septapulse.rook.api.service.AbstractService;
import org.septapulse.rook.api.service.id.ServiceId;
import org.septapulse.rook.api.util.BufferUtil;
import org.septapulse.rook.api.util.PropertiesUtil;
import org.septapulse.rook.io.proxy.IOCallbacks;
import org.septapulse.rook.io.proxy.IOProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IOLoggingService extends AbstractService implements IOCallbacks {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final ServiceId ioServiceId;
	
	public IOLoggingService(Properties props) {
		this(ServiceId.fromName(PropertiesUtil.getRequiredProperty(props, "ioServiceId")));
			
	}
	
	public IOLoggingService(ServiceId ioServiceId) {
		this.ioServiceId = ioServiceId;
	}

	@Override
	public void init() throws Exception {
		new IOProxy(ioServiceId, getSender(), getReceiver(), this, 1024);
	}
	
	@Override
	public void onPrimitiveInputUpdate(long id, long value) {
		logger.info("Primitive Update: " + id + "=" + value);
	}
	
	@Override
	public void onBufferInputUpdate(long id, ByteBuffer value) {
		logger.info("Buffer Update: " + id + "=" + BufferUtil.toHex(value));
	}

	@Override
	public void destroy() {

	}

}