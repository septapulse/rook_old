package org.septapulse.rook.test.io;

import org.septapulse.rook.api.service.id.ServiceId;
import org.septapulse.rook.core.router.disruptor.DisruptorRouter;
import org.septapulse.rook.core.router.disruptor.FixedExecutorServiceFactory;
import org.septapulse.rook.io.service.SerialIOService;

import com.lmax.disruptor.BlockingWaitStrategy;

public class TestIO {

	private static final String SERIAL_PORT = "/dev/serial/by-id/usb-Arduino__www.arduino.cc__0043_95333353037351E01121-if00";
	private static final long LED_PIN = 3;
	private static final long BUTTON_PIN = 6;
	
	public static void main(String[] args) throws Exception {
		// create router
		final DisruptorRouter router = new DisruptorRouter(1024, 
				new BlockingWaitStrategy(), 
				new FixedExecutorServiceFactory(), 
				1024, 
				true, 
				true);
		
		
		// create IO Service
		final ServiceId ioServiceId = ServiceId.fromName("IO");
		final SerialIOService ioService = new SerialIOService(
				SERIAL_PORT, 
				9600, 8, 1, 0);
		router.addService(ioService, ioServiceId);
		
		
//		// create Blink Service
//		final BlinkService dummyService = new BlinkService(ioServiceId, LED_PIN);
//		router.addService(dummyService, ServiceId.fromName("BLINK"));
		
//		// create Logging Service
//		final IOLoggingService ioLoggingService = new IOLoggingService(ioServiceId);
//		router.addService(ioLoggingService, ServiceId.fromName("LOG"));
				
		// create Forward Service
		final ForwardService forwardService = new ForwardService(ioServiceId, BUTTON_PIN, LED_PIN);
		router.addService(forwardService, ServiceId.fromName("FORWARD"));
				
		// start router and services
		router.init();
	}
	
}
