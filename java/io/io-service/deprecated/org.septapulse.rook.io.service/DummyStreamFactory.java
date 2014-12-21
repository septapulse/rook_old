package org.septapulse.rook.io.service;

import java.util.Properties;

public class DummyStreamFactory implements StreamFactory {
	
	public static byte[] inputStreamData;
	public static int outputStreamLength = 1024;
	public static DummyStream dummyStream;
	@Override
	public Stream create(Properties props) {
		dummyStream = new DummyStream(inputStreamData, outputStreamLength);
		return dummyStream;
	}

}
