package exceptions;

import utils.MessageHandler;

public class ServerUnavailableException extends Exception {

    public ServerUnavailableException(String msg) {
		super(msg);
    }

	public ServerUnavailableException(Exception e, String msg) {
		super(msg);
        MessageHandler.handleError(e, msg);
	}

}