package utils;

public class Protocols {
	
	/**
	 * Use tilde(~) to separate arguments sent over the network.
	 */
	public static final String TILDE = "~";

	/**
	 * Indicate the end of the text.
	 */
	public static final String EOT = "--EOT--";
	
	public static final String HELP = "HELP";
	public static final String HINT = "HINT";

	/* Used for the server-client handshake */
	public static final String HELLO = "HELLO";
	
	public static final String ERROR = "ERROR";
	public static final String QUIT = "QUIT";
	
	public static final String LOGIN = "LOGIN";
	public static final String QUEUE = "QUEUE";
	public static final String MOVE = "MOVE";
	public static final String LIST = "LIST";
	public static final String RANK = "RANK";
	public static final String AI = "AI";
	
	public static final String ALREADYLOGGEDIN = "ALREADYLOGGEDIN";
	
	public static final String NEWGAME = "NEWGAME";
	public static final String GAMEOVER = "GAMEOVER";
	public static final String DISCONNECT = "DISCONNECT";
	public static final String VICTORY = "VICTORY";
	public static final String DRAW = "DRAW";

}
