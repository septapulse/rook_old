package org.septapulse.rook.io.service;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ServerLog {

	public static void main(String[] args) throws Exception {
		final ServerSocket serverSocket = new ServerSocket(1958);
		System.out.println("Waiting...");
		final Socket socket = serverSocket.accept();
		final InputStream in = socket.getInputStream();
		serverSocket.close();
		
		final byte[] buf = new byte[48];
		int offset = 0;
		System.out.println("Connected");
		
		
		try {
			while(true) {
				int n = in.read(buf, offset, buf.length-offset);
				if(n == -1) {
					System.out.println("Client Disconnected");
					return;
				}
				offset+=n;
				if(offset == buf.length) {
					ByteBuffer bb = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN);
					bb.getLong();
					System.out.print(bb.getLong());
					System.out.print(" = ");
					System.out.print(bb.getLong());
					System.out.print("   ");
					bb.getLong();
					System.out.print(bb.getLong());
					System.out.print(" = ");
					System.out.print(bb.getLong());
					System.out.println();
					offset = 0;
				}
			}
			
		} finally {
			socket.close();
		}
	}
}
