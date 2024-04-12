package server;

// import java.io.PrintWriter;

import utils.TextIO;

public class CollectoServerTUI {

	/* The PrintWriter to write messages to */
	// private PrintWriter console;
	
	private CollectoServer server;

	/**
	 * Constructs a new CollectoServerTUI. Initializes the console.
	 */
	public CollectoServerTUI() {
		// console = new PrintWriter(System.out, true);
		this.server = new CollectoServer();
	}
	
	public void start() {
		System.out.print("Input a port for server: ");
		int port = TextIO.getlnInt();
		this.server.setPort(port);
		this.server.run();
	}

	/*
	public void showMessage(String message) {
		console.println(message);
	}
	*/

	/* Start a new CollectoServer */
	public static void main(String[] args) {
		CollectoServerTUI serverTUI = new CollectoServerTUI();
		serverTUI.start();
	}
	
}
