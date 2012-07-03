package play.modules.protocolbuffers;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.quickserver.net.server.ClientEventHandler;
import org.quickserver.net.server.ClientHandler;

public class GPBEventHandler implements ClientEventHandler {

	@Override
	public void closingConnection(ClientHandler arg0) throws IOException {
		System.out.println("Closing Connection");

	}

	@Override
	public void gotConnected(ClientHandler arg0) throws SocketTimeoutException,
			IOException {
		System.out.println("Got Connected");

	}

	@Override
	public void lostConnection(ClientHandler arg0) throws IOException {
		System.out.println("Lost Connection");

	}

}
