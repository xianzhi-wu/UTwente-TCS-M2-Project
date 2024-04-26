package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.List;

import utils.Protocols;
import utils.States;
import utils.MessageHandler;

public class CollectoServer implements Runnable {

	/* The ServerSocket of this CollectoServer */
	private ServerSocket ssock;
	private int port;

	/* List of CollectoClientHandler, one for each connected client */
	private List<CollectoClientHandler> clients;
	
	/* List of CollectoClientHandler in queue */
	private List<CollectoClientHandler> clientsInQueue;

	/**
	 * Constructs a new CollectoServer. Initializes the clients list
	 */
	public CollectoServer() {
		this.clients = new ArrayList<>();
		this.clientsInQueue = new ArrayList<>();
	}

	/**
	 * Opens a new socket by calling  {@link #setup()} and starts a new
	 * CollectoClientHandler for every connecting client.
	 * 
	 * If {@link #setup()} throws a ExitProgram exception, stop the program. 
	 */
	public void run() {
		try {
			// Sets up the Collecto application
			this.setup();
			while (true) {
				Socket socket = this.ssock.accept();
				MessageHandler.printMessage("Client connected");
				
				// Starts a thread 
				CollectoClientHandler handler = new CollectoClientHandler(socket, this);
				new Thread(handler).start();
			}
		} catch (IOException e) {
			MessageHandler.handleError(e, "A server IO error occurred");
		} catch (Exception e) {
			MessageHandler.handleError(e);
		}
	}
	
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Sets up a new game and opens a new 
	 * ServerSocket at localhost on a user-defined port.
	 * 
	 * The user is asked to input a port, after which a socket is attempted 
	 * to be opened. If the attempt succeeds, the method ends.
	 * @ensures a serverSocket is opened.
	 */
	public void setup() {
		this.ssock = null;
		while (this.ssock == null) {
			// try to open a new ServerSocket
			try {
				MessageHandler.printMessage("Attempting to open a socket on port " + this.port + "...");
				this.ssock = new ServerSocket(this.port);
				MessageHandler.printMessage("Server started on port " + port);
			} catch (Exception e) {
				MessageHandler.handleError(e);
			}
		}
	}
	
	/**
	 * Add the client to the list.
	 * @param client
	 * @return true if the list doesn't contains this client, otherwise return false
	 */
	public synchronized void addClient(CollectoClientHandler client) {
		this.clients.add(client);
	}
	
	public boolean clientExists(String name) {
		for (CollectoClientHandler client : clients) {
			if (client.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Removes a clientHandler from the client list.
	 * @requires client != null
	 */
	public synchronized void removeClient(CollectoClientHandler client) {
		this.clients.remove(client);
	}
	
	/**
	 * Removes a clientHandler from the client list.
	 * @requires client != null
	 */
	public synchronized void removeClientInQueue(CollectoClientHandler client) {
		this.clientsInQueue.remove(client);
	}
	
	public synchronized String showClienList() {
		String res = Protocols.LIST;
		for (CollectoClientHandler client : clients) {
			res += Protocols.TILDE + client.getName();
		}
		return res;
	}
	
	
	/**
	 * Put the client in queue, if there are two players in the queue.
	 * And create a game gameSession for the players and then start the game
	 * @param client
	 */
	public synchronized void putInQueue(CollectoClientHandler client) {
		this.clientsInQueue.add(client);
		client.setState(States.QUEUEING);
		if (this.clientsInQueue.size() > 1) {
			CollectoClientHandler player01 = this.clientsInQueue.get(0);
			CollectoClientHandler player02 = this.clientsInQueue.get(1);
			GameSession gameSession = new GameSession(player01, player02);
			gameSession.startGame();
			this.clientsInQueue.remove(player01);
			this.clientsInQueue.remove(player02);
		}
	}
	
	public List<CollectoClientHandler> getClientList() {
		return this.clients;
	}

}
