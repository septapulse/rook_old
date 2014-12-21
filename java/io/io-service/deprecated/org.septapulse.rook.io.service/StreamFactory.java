package org.septapulse.rook.io.service;

import java.util.Properties;

public interface StreamFactory {
	Stream create(Properties props);
}
