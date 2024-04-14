package server;

// import java.io.PrintWriter;

import utils.TextIO;

public class CollectoServerTUI {
	
	private CollectoServer server;

	/**
	 * Constructs a new CollectoServerTUI. Initializes the console.
	 */
	public CollectoServerTUI() {
		this.server = new CollectoServer();
	}
	
	public void start() {
		System.out.print("Input a port for server: ");
		int port = TextIO.getlnInt();
		this.server.setPort(port);
		this.server.run();
	}

	/* Start a new CollectoServer */
	public static void main(String[] args) {
		CollectoServerTUI serverTUI = new CollectoServerTUI();
		serverTUI.start();
	}
	
}