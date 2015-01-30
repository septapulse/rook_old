package org.septapulse.rook.io.service;

import java.io.InputStream;
import java.io.OutputStream;

public interface Stream {
	InputStream getInputStream();
	OutputStream getOutputStream();
	void close();
}
