package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import utils.Protocols;

public class CollectoServer implements Runnable {

	/* The ServerSocket of this CollectoServer */
	private ServerSocket ssock;
	private int port;

	/* List of CollectoClientHandler, one for each connected client */
	private List<CollectoClientHandler> clients;
	
	/* The view of this CollectoServer */
	private CollectoServerTUI view;
	
	/* List of CollectoClientHandler in queue */
	private List<CollectoClientHandler> clientsInQueue;

	/**
	 * Constructs a new CollectoServer. Initializes the clients list, the view 
	 */
	public CollectoServer() {
		view = new CollectoServerTUI(this);
		clients = new ArrayList<>();
		clientsInQueue = new ArrayList<>();
	}

	/**
	 * Opens a new socket by calling {@link #setup()} and starts a new
	 * HotelClientHandler for every connecting client.
	 * 
	 * If {@link #setup()} throws a ExitProgram exception, stop the program. 
	 */
	public void run() {
		view.start();
		boolean openNewSocket = true;
		while (openNewSocket) {
			try {
				// Sets up the Collecto application
				setup();
				while (true) {
					Socket socket = ssock.accept();
					System.out.println("Client connected from: " 
							+ socket.getLocalAddress().getHostName());
					
					// Starts a thread 
					CollectoClientHandler handler = new CollectoClientHandler(socket, this);
					new Thread(handler).start();
				}
			} catch (IOException e) {
				openNewSocket = false;
				System.out.println("A server IO error occurred: " + e.getMessage());
			}
		}
	}
	
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Sets up a new game using {@link #setupHotel()} and opens a new 
	 * ServerSocket at localhost on a user-defined port.
	 * 
	 * The user is asked to input a port, after which a socket is attempted 
	 * to be opened. If the attempt succeeds, the method ends.
	 * 
	 * @ensures a serverSocket is opened.
	 */
	public void setup() {
		ssock = null;
		while (ssock == null) {
			// try to open a new ServerSocket
			try {
				view.showMessage("Attempting to open a socket on port " 
						+ port + "...");
				ssock = new ServerSocket(port);
				view.showMessage("Server started on port " + port);
			} catch (Exception e) {
				e.printStackTrace();
				view.showMessage("ERROR: could not create a socket on port " + port + ".");
				view.start();
			}
		}
	}
	
	/**
	 * Add the client to the list.
	 * @param client
	 * @return true if the list doesn't contains this client, otherwise return false
	 */
	public synchronized void addClient(CollectoClientHandler client) {
		clients.add(client);
	}
	
	public boolean clientExists(String name) {
		boolean res = false;
		for (CollectoClientHandler client : clients) {
			if (client.getName().equals(name)) {
				res = true;
				break;
			}
		}
		return res;
	}
	
	/**
	 * Removes a clientHandler from the client list.
	 * @requires client != null
	 */
	public synchronized void removeClient(CollectoClientHandler client) {
		clients.remove(client);
	}
	
	/**
	 * Removes a clientHandler from the client list.
	 * @requires client != null
	 */
	public synchronized void removeClientInQueue(CollectoClientHandler client) {
		clientsInQueue.remove(client);
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
	 * And create a game room for the players and then start the game
	 * @param client
	 */
	public synchronized void putInQueue(CollectoClientHandler client) {
		clientsInQueue.add(client);
		if (clientsInQueue.size() > 1) {
			CollectoClientHandler player01 = clientsInQueue.get(0);
			CollectoClientHandler player02 = clientsInQueue.get(1);
			GameRoom gameRoom = new GameRoom(player01, player02);
			clientsInQueue.remove(player01);
			clientsInQueue.remove(player02);
			gameRoom.startGame();
		}
	}
	
	public List<CollectoClientHandler> getClientList() {
		return clients;
	}
	
	/* Start a new CollectoServer */
	public static void main(String[] args) {
		CollectoServer server = new CollectoServer();
		server.run();
	}
	
}
