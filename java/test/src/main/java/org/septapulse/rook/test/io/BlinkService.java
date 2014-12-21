package org.septapulse.rook.test.io;

import java.util.Properties;

import org.septapulse.rook.api.service.AbstractService;
import org.septapulse.rook.api.service.id.ServiceId;
import org.septapulse.rook.api.util.PropertiesUtil;
import org.septapulse.rook.io.proxy.IOProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlinkService extends AbstractService {
	
	private static final long OFF = 0;
	private static final long ON = 1;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final ServiceId ioServiceId;
	private final long ledId;
	private volatile BlinkLoop blinkLoop;

	public BlinkService(Properties props) {
		this(ServiceId.fromName(PropertiesUtil.getRequiredProperty(props, "ioServiceId")),
				PropertiesUtil.getRequiredNumber(props, "ledId").longValue());
			
	}
	
	public BlinkService(ServiceId ioServiceId, long ledId) {
		this.ioServiceId = ioServiceId;
		this.ledId = ledId;
	}

	@Override
	public void init() throws Exception {
		blinkLoop = new BlinkLoop();
		new Thread(blinkLoop).start();
	}

	@Override
	public void destroy() {
		blinkLoop = null;
	}

	private class BlinkLoop implements Runnable {
		@Override
		public void run() {
			final IOProxy ioProxy = new IOProxy(ioServiceId, getSender(), getReceiver(), null, 1024);
			while (blinkLoop == this) {
				logger.info("ON");
				ioProxy.sendPrimitiveOutput(ledId, ON);
				sleep(1000);

				logger.info("OFF");
				ioProxy.sendPrimitiveOutput(ledId, OFF);
				sleep(1000);
			}
		}
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}