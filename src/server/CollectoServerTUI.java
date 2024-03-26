package server;

import java.io.PrintWriter;

import utils.TextIO;

public class CollectoServerTUI {

	/* The PrintWriter to write messages to */
	private PrintWriter console;
	
	private CollectoServer server;

	/**
	 * Constructs a new CollectoServerTUI. Initializes the console.
	 */
	public CollectoServerTUI(CollectoServer server) {
		console = new PrintWriter(System.out, true);
		this.server = server;
	}
	
	public void start() {
		System.out.print("Input a port for server: ");
		int port = TextIO.getlnInt();
		server.setPort(port);
	}

	public void showMessage(String message) {
		console.println(message);
	}
	
}
