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
	private GameLobby gameLobby;
	private States state = States.NEWIN;

	public CollectoClientHandler(Socket socket, CollectoServer collectoServer) throws IOException {
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			sock = socket;
			server = collectoServer;
		} catch (IOException e) {
			e.getStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			String msg;
			while (!state.equals(States.QUITED)) {
				msg = in.readLine();
				if (msg != null) {
					handleCommand(msg);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void handleHello(String[] command) throws IOException {
		if (command.length == 1) {
			sendMessage(Protocols.ERROR + Protocols.TILDE 
					+ "Received HELLO message without description!");
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
					state = States.HANDSHACK;
					server.addClient(this);
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
		/*if (state.equals(States.QUEUEING)) {
			server.removeClientInQueue(this);
		} else*/ 
		if (state.equals(States.HANDSHACK) || state.equals(States.GAMEOVER)) {
			state = States.QUEUEING;
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
				gameLobby.makeMove(move, this);
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
				shutdown();
				break;
			default:
				sendMessage("Unkown command: " + msg);
		}
	}
	
	/**
	 * Shut down the connection to this client by closing the socket and 
	 * the In- and OutputStreams.
	 * @throws IOException 
	 */
	private void shutdown() throws IOException {
		try {
			in.close();
			out.close();
			sock.close();
			System.out.println("Player " + this.getName() + " disconnected");
			server.removeClient(this);
			state = States.QUITED;
			
			if (state.equals(States.PLAYING)) {
				gameLobby.removePlayer(this);
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
		out.write(msg);
		out.newLine();
		out.flush();
	}
	
	/**
	 * @return the total score of the player
	 */
	public int getTotalScore() {
		return score + this.getScore();
	}
	
	@Override
	public String determineMove(Board board) {
		return new NaiveStrategy().determineMove(board);
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
	
	@Override
	public int compareTo(CollectoClientHandler o) {
		return this.getScore() < o.getScore() ? -1 : (this.getScore() == o.getScore() ? 0 : 1);
	}
	
	/**
	 * When the game is over, send a message to each player and reset both players.
	 * @param res the result of the game
	 * @throws IOException
	 */
	public void gameOver(String res) throws IOException {
		try {
			gameLobby = null;
			state = States.GAMEOVER;
			sendMessage(res);
			this.reset();
		} catch (IOException e) {
			e.getStackTrace();
		}
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * Start the game and send a message containing the board, players' names to both players.
	 * @param gameLobby
	 * @param initStr
	 */
	public void startGame(GameLobby lobby, String initStr) {
		try {
			this.gameLobby = lobby;
			state = States.PLAYING;
			sendMessage(initStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
