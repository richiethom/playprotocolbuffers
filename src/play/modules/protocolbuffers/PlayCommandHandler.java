package play.modules.protocolbuffers;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.quickserver.net.server.ClientCommandHandler;
import org.quickserver.net.server.ClientHandler;

public class PlayCommandHandler implements ClientCommandHandler {

	@Override
	public void handleCommand(ClientHandler arg0, String arg1)
			throws SocketTimeoutException, IOException {
		System.out.println("PlayCommandHandler:"+arg1);

	}

}
