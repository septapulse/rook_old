package org.septapulse.rook.io.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DummyStream implements Stream {

	private final InputStream in;
	private final ByteArrayOutputStream out;
	
	public DummyStream(byte[] inputStreamData, int outputStreamLength) {
		in = new ByteArrayInputStream(inputStreamData != null ? inputStreamData : new byte[0]);
		out = new ByteArrayOutputStream(outputStreamLength);
	}
	
	@Override
	public InputStream getInputStream() {
		return in;
	}

	@Override
	public OutputStream getOutputStream() {
		return out;
	}
	
	public byte[] getOutputBytes() {
		return out.toByteArray();
	}

	@Override
	public void close() {
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
