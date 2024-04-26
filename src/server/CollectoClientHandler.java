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

import utils.MessageHandler;

public class CollectoClientHandler extends Player 
								   implements Runnable, Comparable<CollectoClientHandler> {

	private BufferedReader in;
	private BufferedWriter out;
	private Socket socket;
	
	private CollectoServer server;

	private String name;
	private int score;
	private GameSession gameSession;
	private States state = States.NEWIN;

	public CollectoClientHandler(Socket socket, CollectoServer collectoServer) {
		try {
			this.socket = socket;
			server = collectoServer;
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
		} catch (IOException e) {
			e.getStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			String msg = in.readLine();
			while (!state.equals(States.QUITED) && msg != null) {
				handleCommand(msg);
				msg = in.readLine();
			}
			 
			if (!state.equals(States.QUITED) && msg == null) {
				handleQuit();
			}
		} catch (Exception e) {
			MessageHandler.handleError(e);
		}
	}

	public void setState(States state) {
		this.state = state;
	}

	public void handleHello(String[] command) throws IOException {
		if (command.length == 1) {
			sendMessage(Protocols.ERROR + Protocols.TILDE + "Received HELLO message without description!");
		} else if (command.length == 2) {
			sendMessage(Protocols.HELLO + Protocols.TILDE + "Collecto server!");
			state = States.SAYHELLO;
		}
	}

	public void handLogin(String[] command) throws IOException {
		if (state.equals(States.NEWIN)) {
			sendMessage(Protocols.ERROR + Protocols.TILDE + "Untimely LOGIN");
		} else if (state.equals(States.SAYHELLO)) {
			if (command.length == 2) {
				if (!server.clientExists(command[1])) {
					name = command[1];
					server.addClient(this);
					state = States.HANDSHACK;
					sendMessage(Protocols.LOGIN);
				} else {
					sendMessage(Protocols.ALREADYLOGGEDIN);
				}
			} else {
				sendMessage(Protocols.ERROR + Protocols.TILDE 
						+ "Received LOGIN message with invalid number of parameters!");
			}
		}
	}

	public void handleQueue() throws IOException {
		if (state.equals(States.HANDSHACK) || state.equals(States.GAMEOVER)) {
			server.putInQueue(this);
		} else {
			sendMessage(Protocols.ERROR + Protocols.TILDE + "Untimely QUEUE");
		}
	}

	public void handleMove(String[] command) throws IOException {
		if (state.equals(States.PLAYING)) {
			if (command.length > 1) {
				String move;
				if (command.length == 2) {
					move = command[1];
				} else {
					move = command[1] + Protocols.TILDE + command[2];
				}
				gameSession.makeMove(move, this);
			} else {
				sendMessage(Protocols.ERROR + Protocols.TILDE + "invalid input");
			}
		} else {
			sendMessage(Protocols.ERROR + Protocols.TILDE + "Untimely MOVE");
		}
	}

	public void handleList() throws IOException {
		sendMessage(server.showClienList());
	}

	public void handleRank() throws IOException {
		sendMessage(getRankList());
	}

	/**
	 * @return the total score of the player
	 */
	public int getTotalScore() {
		score += this.getScore();
		return score;
	}
	
	/**
	 * Rank the (online) players by the score they have.
	 * @return the player list
	 */
	public String getRankList() {
		String res = "\nRanking\n";
		List<CollectoClientHandler> players = new ArrayList<>(server.getClientList());
		Collections.sort(players);
		for (CollectoClientHandler player : players) {
			res += "Player: " + player.getName() + ", score: " + player.getTotalScore() + "\n";
		}
		res += Protocols.EOT;
		return res;
	}

	/**
	 * Shut down the connection to this client by closing the socket and 
	 * the In- and OutputStreams.
	 * @throws IOException 
	 */
	private void handleQuit() throws IOException {
		try {
			MessageHandler.printMessage("Player " + this.getName() + "(" + state + ") disconnected!");

			if (state.equals(States.QUEUEING)) {
				server.removeClientInQueue(this);
			} else if (state.equals(States.PLAYING) || state.equals(States.GAMEOVER)) {
				if(gameSession != null) {
					gameSession.removePlayer(this);
				}
			}

			server.removeClient(this);
			state = States.QUITED;
			in.close();
			out.close();
			socket.close();
		} catch (Exception e) {
			MessageHandler.handleError(e);
		}
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
				handleHello(command);
				break;
			case Protocols.LOGIN:
				handLogin(command);
				break;
			case Protocols.QUEUE:
				handleQueue();
				break;
			case Protocols.MOVE:
				handleMove(command);
				break;
			case Protocols.LIST:
				handleList();
				break;
			case Protocols.RANK:
				handleRank();
				break;
			case Protocols.QUIT:
				handleQuit();
				break;
			default:
				sendMessage("Unkown command: " + msg);
		}
	}
	
	/**
	 * Send message to the player.
	 * @param msg
	 * @throws IOException
	 */
	public void sendMessage(String msg) throws IOException {
		out.write(msg);
		out.newLine();
		out.flush();
	}
	
	@Override
	public int compareTo(CollectoClientHandler o) {
		return this.getScore() < o.getScore() ? -1 : (this.getScore() == o.getScore() ? 0 : 1);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String determineMove(Board board) {
		return new NaiveStrategy().determineMove(board);
	}
	
	/**
	 * Start the game and send a message containing the board, players' names to both players.
	 * @param gameSession
	 * @param initStr
	 */
	public void startGame(GameSession gameSession, String initStr) {
		try {
			this.gameSession = gameSession;
			state = States.PLAYING;
			sendMessage(initStr);
		} catch (IOException e) {
			MessageHandler.handleError(e);
		}
	}

	/**
	 * When the game is over, send a message to each player and reset both players.
	 * @param res the result of the game
	 * @throws IOException
	 */
	public void endGame(String res) throws IOException {
		try {
			gameSession = null;
			state = States.GAMEOVER;
			this.reset();
			sendMessage(res);
		} catch (IOException e) {
			e.getStackTrace();
		}
	}

}
