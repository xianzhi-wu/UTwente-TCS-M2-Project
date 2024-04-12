package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.Socket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import collecto.Board;
import collecto.NaiveStrategy;
import collecto.Player;

import utils.Protocols;
import utils.States;

public class CollectoClientHandler extends Player 
	implements Runnable, Comparable<CollectoClientHandler> {

	private BufferedReader in;
	private BufferedWriter out;
	private Socket sock;
	
	private CollectoServer server;

	private String name;
	private int score;
	private States state = States.NEWIN;

	private GameLobby lobby;

	public CollectoClientHandler(Socket socket, CollectoServer collectoServer) throws IOException {
		try {
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.sock = socket;
			this.server = collectoServer;
		} catch (IOException e) {
			e.getStackTrace();
			this.shutdown();
		}
	}
	
	@Override
	public void run() {
		try {
			String msg = this.in.readLine();
			while (msg != null && !msg.equals(Protocols.QUIT)) {
				handleCommand(msg);
				msg = this.in.readLine();
			}
			shutdown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void handleHello(String[] command) throws IOException {
		if (command.length == 1) {
			this.sendMessage(Protocols.ERROR + Protocols.TILDE + "Received HELLO message without description!");
		} else if (command.length == 2) {
			this.sendMessage(Protocols.HELLO + Protocols.TILDE + "Collecto server!");
			this.state = States.SAYHELLO;
		}
	}

	public void handleLogin(String[] command) throws IOException {
		if (this.state.equals(States.NEWIN)) {
			this.sendMessage(Protocols.ERROR + Protocols.TILDE + "Untimely LOGIN");
		} else if (this.state.equals(States.SAYHELLO)) {
			if (command.length == 2) {
				if (!this.server.clientExists(command[1])) {
					this.name = command[1];
					this.state = States.HANDSHANK;
					this.server.addClient(this);
					this.sendMessage(Protocols.LOGIN);
				} else {
					this.sendMessage(Protocols.ALREADYLOGGEDIN);
				}
			} else {
				this.sendMessage(Protocols.ERROR + Protocols.TILDE + "Received LOGIN message with invalid number of parameters!");
			}
		}
	}

	public void handleQueue() throws IOException {
		if (this.state.equals(States.QUEUEING)) {
			this.server.removeClientInQueue(this);
		} else if (this.state.equals(States.HANDSHANK) || this.state.equals(States.GAMEOVER)) {
			this.state = States.QUEUEING;
			this.server.putInQueue(this);
		} else {
			this.sendMessage(Protocols.ERROR + Protocols.TILDE + "Untimely QUEUE");
		}
	}

	public void handleMove(String[] command) throws IOException {
		if (state.equals(States.PLAYING)) {
			if (command.length > 1) {
				String move = String.join(Protocols.TILDE, command);
				this.lobby.makeMove(move, this);
			} else {
				this.sendMessage(Protocols.ERROR + Protocols.TILDE + "invalid input");
			}
		} else {
			this.sendMessage(Protocols.ERROR + Protocols.TILDE + "Untimely MOVE");
		}
	}

	/**
	 * @return the total score of the player
	 */
	public int getTotalScore() {
		return this.score + this.getScore();
	}
	
	/**
	 * Rank the (online) players by the score they have.
	 * @return the player list
	 * @throws IOException 
	 */
	public void getRankList() throws IOException {
		String res = "\nRanking\n";
		List<CollectoClientHandler> players = new ArrayList<>(this.server.getClientList());
		Collections.sort(players);
		for (CollectoClientHandler player : players) {
			res += "Player: " + player.getName() + ", score: " + player.getTotalScore() + "\n";
		}
		res += Protocols.EOT;

		this.sendMessage(res);
	}

	public void handleList() throws IOException {
		this.sendMessage(this.server.showClienList());
	}

	/**
	 * Handles commands received from the client by calling the according 
	 * methods at the CollectoServer. 
	 * 
	 * If the received input is not valid, send an "Unknown Command" 
	 * message to the server.
	 * 
	 * @param msg command from player
	 * @throws IOException if an IO errors occur.
	 */
	private void handleCommand(String msg) throws IOException {
		String[] command = msg.split(Protocols.TILDE);
		switch (command[0]) {
			case Protocols.HELLO:
				this.handleHello(command);
				break;
			case Protocols.LOGIN:
				this.handleLogin(command);
				break;
			case Protocols.QUEUE:
				this.handleQueue();
				break;
			case Protocols.MOVE:
				this.handleMove(command);
				break;
			case Protocols.LIST:
				this.handleList();
				break;
			case Protocols.RANK:
				this.getRankList();
				break;
			/*case Protocols.QUIT:
				shutdown();
				break;*/
			default:
				this.sendMessage("Unkown command: " + msg);
		}
	}
	
	/**
	 * Shut down the connection to this client by closing the socket and 
	 * the In- and OutputStreams.
	 * @throws IOException 
	 */
	private void shutdown() throws IOException {
		try {
			this.in.close();
			this.out.close();
			this.sock.close();
			System.out.println("Player " + this.getName() + " disconnected");

			this.server.removeClient(this);
			
			if (this.state.equals(States.PLAYING)) {
				this.lobby.removePlayer(this);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send message to the player.
	 * @param msg
	 * @throws IOException
	 */
	public void sendMessage(String msg) throws IOException {
		this.out.write(msg);
		this.out.newLine();
		this.out.flush();
	}
	
	@Override
	public int compareTo(CollectoClientHandler o) {
		return this.getScore() < o.getScore() ? -1 : (this.getScore() == o.getScore() ? 0 : 1);
	}

	@Override
	public String determineMove(Board board) {
		return new NaiveStrategy().determineMove(board);
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	/**
	 * Start the game and send a message containing the board, players' names to both players.
	 * @param lobby
	 * @param initStr
	 */
	public void startGame(GameLobby lobby, String initStr) {
		try {
			this.lobby = lobby;
			this.state = States.PLAYING;
			sendMessage(initStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * When the game is over, send a message to each player and reset both players.
	 * @param res the result of the game
	 * @throws IOException
	 */
	public void gameOver(String res) throws IOException {
		try {
			this.lobby = null;
			this.state = States.GAMEOVER;
			sendMessage(res);
			this.reset();
		} catch (IOException e) {
			shutdown();
		}
	}

}
