package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.InetAddress;
import java.net.Socket;

import collecto.Board;
import collecto.ComputerPlayer;
import collecto.HumanPlayer;
import collecto.NaiveStrategy;
import collecto.Player;
import collecto.SmartStrategy;

import exceptions.ServerUnavailableException;

import utils.Colors;
import utils.Protocols;
import utils.States;
import utils.MessageHandler;

public class CollectoClient {
	
	private Socket serverSock;
	private BufferedReader in;
	private BufferedWriter out;
	
	private Player player;
	private int playerType;
	private Player opponent;
	private Board board;
	
	private States state = States.NEWIN;
	
	private String host;
	private int port;
	
	public States getState() {
		return this.state;
	}
	
	public void resetGame() {
		this.board = null;
		this.opponent = null;
		this.player.reset();
		this.state = States.GAMEOVER;
	}
	/**
	 * player type: 1 for AI player using smart strategy, 
	 * 2 for AI player using naive strategy, 3 for human player
	 */
	public void setPlayerType(int type) {
		this.playerType = type;
	}
	
	/**
	 * Get the current player
	 * @return the player
	 */
	public Player getPlayer() {
		return this.player;
	}
	
	public Board getBoard() {
		return this.board;
	}
	
	// Set up the server address and port
	public void setUp(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * Creates a connection to the server.
	 * @throws ServerUnavailableException 
	 * @ensures serverSock contains a valid socket connection to a server
	 */
	public void createConnection() throws ServerUnavailableException {
		clearConnection();
		// try to open a Socket to the server
		try {
			InetAddress addr = InetAddress.getByName(host);
			this.serverSock = new Socket(addr, port);

			this.in = new BufferedReader(new InputStreamReader(this.serverSock.getInputStream()));
			this.out = new BufferedWriter(new OutputStreamWriter(this.serverSock.getOutputStream()));

			MessageHandler.printMessage("Connected to " + addr + ":" + this.port);
		} catch (Exception e) {
			throw new ServerUnavailableException(e, "Could not write to server.");
		}
	}

	/**
	 * Resets the serverSocket, inputStream and outputStream to null
	 * Always make sure to close current connections via closeConnection() 
	 * before calling this method!
	 */
	public void clearConnection() {
		this.serverSock = null;
		this.in = null;
		this.out = null;
	}

	/**
	 * Sends a message to the connected server, followed by a new line. 
	 * The stream is then flushed.
	 * @param msg the message to write to the OutputStream.
	 * @throws ServerUnavailableException 
	 */
	public synchronized void sendMessage(String msg) throws ServerUnavailableException {
		try {
			this.out.write(msg);
			this.out.newLine();
			this.out.flush();
		} catch (IOException e) {
			throw new ServerUnavailableException(e, "Could not write to server.");
		}
	}

	/**
	 * Reads and returns one line from the server.
	 * @return the line sent by the server.
	 * @throws ServerUnavailableException 
	 */
	public String readLineFromServer() throws ServerUnavailableException {
		try {
			// Read and return answer from Server
			String answer = this.in.readLine();
			// If the connection is closed
			if (answer == null) {
				throw new ServerUnavailableException("Could not read from server.");
			}
			return answer;
		} catch (IOException e) {
			throw new ServerUnavailableException(e, "Could not read from server.");
		}
	}
	
	/**
	 * Reads and returns multiple lines from the server until the end of 
	 * the text is indicated using a line containing Protocols.EOT.
	 * @return the concatenated lines sent by the server.
	 * @throws ServerUnavailableException 
	 */
	public String readMultipleLinesFromServer() throws ServerUnavailableException {
		try {
			// Read and return answer from Server
			StringBuilder sb = new StringBuilder();
			for (String line = this.in.readLine(); 
					line != null && !line.equals(Protocols.EOT); 
					line = this.in.readLine()) {
				sb.append(line + System.lineSeparator());
			}
			return sb.toString();
		} catch (IOException e) {
			throw new ServerUnavailableException(e, "Could not read from server.");
		}
	}
	
	/**
	 * Closes the connection by closing the In- and OutputStreams, as 
	 * well as the serverSocket.
	 */
	public void closeConnection() {
		try {
			this.in.close();
			this.out.close();
			this.serverSock.close();
		} catch (Exception e) {
			MessageHandler.handleError(e);
		}
	}
	
	/**
	 * Say hello to server.
	 * @param input HELLO~<client description>[~extension]
	 * @throws ServerUnavailableException *
	 */
	public void handleHello(String input) throws ServerUnavailableException {
		this.sendMessage(input);
		String res = this.readLineFromServer();
		if (res.startsWith(Protocols.HELLO + Protocols.TILDE)) {
			this.state = States.SAYHELLO;
		}
	}
	
	/**
	 * @param input HELLO~[player name]
	 * @throws ServerUnavailableException 
	 */
	public void handleLogin(String input) throws ServerUnavailableException {
		this.sendMessage(input);
		String res = this.readLineFromServer();
		if (res.equals(Protocols.LOGIN)) {
			String name = input.split(Protocols.TILDE)[1];
			if (this.playerType == 1 || this.playerType == 2) {
				this.player = new ComputerPlayer(name, this.playerType);
			} else if (this.playerType == 3) {
				this.player = new HumanPlayer(name);
			}
			this.state = States.HANDSHACK;
		} else {
			MessageHandler.printMessage("Server: " + res);
		}
	}

	/**
	 * Join the queue for the game.
	 * @param input QUEUE
	 * @throws ServerUnavailableException 
	 */
	public void handleQueue(String input) throws ServerUnavailableException {
		this.sendMessage(input);
		String res = this.readLineFromServer();
		if (res.equals(Protocols.QUEUE)) {
			this.state = States.QUEUEING;
			this.handleQueueing();
		} else if (res.startsWith(Protocols.NEWGAME)) {
			this.state = States.PLAYING;
			this.setBoard(res);
			this.setOpponent(res);
		} else {
			MessageHandler.printMessage("Server: " + res);
		}
	}

	public void handleQueueing() throws ServerUnavailableException {
		while (this.state.equals(States.QUEUEING)) {
			String res = this.readLineFromServer();
			if (res.startsWith(Protocols.NEWGAME)) {
				this.state = States.PLAYING;
				this.setBoard(res);
				this.setOpponent(res);
			} else {
				MessageHandler.printMessage("Server: " + res);
			}
		}
	}

	/**
	 * See the client list.
	 * @param input LIST
	 * @throws ServerUnavailableException 
	 */
	public void handleList(String input) throws ServerUnavailableException {
		this.sendMessage(input);
		MessageHandler.printMessage("Players List: " + this.readLineFromServer());
	}
	
	/**
	 * See the rank.
	 * @param input RANK
	 * @throws ServerUnavailableException 
	 */
	public void handleRank(String input) throws ServerUnavailableException {
		this.sendMessage(input);
		MessageHandler.printMessage(this.readMultipleLinesFromServer());
	}
	
	/**
	 * @param input MOVE~<first push>[~second push]
	 * @throws ServerUnavailableException 
	 */
	public void handleMove(String input) throws ServerUnavailableException {
		this.sendMessage(input);
		String res = this.readLineFromServer();

		if (input.equals(res)) {
			res = res.replace(Protocols.MOVE + Protocols.TILDE, "");
			this.player.makeMove(this.board, res);
			MessageHandler.printMessage(this.board.toString());


			// Get response from the opponent
			res = this.readLineFromServer();
			if (res.startsWith(Protocols.MOVE + Protocols.TILDE)) {
				MessageHandler.printMessage(this.opponent.getName() + ": " + res);
				res = res.replace(Protocols.MOVE + Protocols.TILDE, "");
				this.opponent.makeMove(this.board, res);
				MessageHandler.printMessage(this.board.toString());

				if (this.board.gameOver()) {
					res = this.readLineFromServer();
					MessageHandler.printMessage("Server: " + res);
					if (res.startsWith(Protocols.GAMEOVER + Protocols.TILDE)) {
						this.resetGame();
					}
				}
			} else if (res.startsWith(Protocols.GAMEOVER + Protocols.TILDE)) {
				this.resetGame();
				MessageHandler.printMessage("Server: " + res);
			} else {
				MessageHandler.printMessage("Server: " + res);
			}
		} else {
			MessageHandler.printMessage("Server: " + res);
		}
	}
	
	public void handleQuit() throws ServerUnavailableException {
		this.state = States.QUITED;
		this.sendMessage(Protocols.QUIT);
		this.closeConnection();
	}
	
	public void handleChangeAI(int type) {
		if (this.playerType != type) {
			if (type == 1) {
				((ComputerPlayer) this.player).setStrategy(new SmartStrategy(this.player));
			} else if (type == 2) {
				((ComputerPlayer) this.player).setStrategy(new NaiveStrategy());
			}
			this.playerType = type;
		}
	}
	
	public void handleHint() {
		if (this.state.equals(States.PLAYING)) {
			MessageHandler.printMessage("Hint for move: " + new NaiveStrategy().determineMove(this.board));
		} else {
			MessageHandler.printMessage(Protocols.ERROR + Protocols.TILDE + "Untimely HINT");
		}
	}
	/**
	 * @param res is a string received from server, containing NEWGAME, 
	 * a string of board fields and both plaers's names.
	 * NEWGAME~<cell value>^49~<player1 name>~<player2 name>
	 */
	public void setBoard(String res) {
		this.player.reset();

		String[] gameStr = res.split(Protocols.TILDE);
		Colors[][] fields = new Colors[Board.DIM][Board.DIM];
		for (int i = 1; i < Board.DIM * Board.DIM + 1; i++) {
			int row = (i - 1) / Board.DIM,
				col = (i - 1) % Board.DIM;
			fields[row][col] = Colors.values()[Integer.parseInt(gameStr[i])];
		}

		this.board = new Board(fields);
		MessageHandler.printMessage(this.board.toString());
	}

	public void setOpponent(String res) throws ServerUnavailableException {
		String[] gameStr = res.split(Protocols.TILDE);
		int len = gameStr.length;

		if (this.player.getName().equals(gameStr[len - 2])) {
			this.opponent = new HumanPlayer(gameStr[len- 1]);
		} else {
			// Wait repsonse from the opponent
			String msg = this.readLineFromServer();
			if (msg.startsWith(Protocols.MOVE + Protocols.TILDE)) {
				this.opponent = new HumanPlayer(gameStr[len - 2]);
				MessageHandler.printMessage(this.opponent.getName() + ": " + msg);

				msg = msg.replace(Protocols.MOVE + Protocols.TILDE, "");
				this.opponent.makeMove(this.board, msg);
				MessageHandler.printMessage(this.board.toString());
			} else if (msg.startsWith(Protocols.GAMEOVER)) {
				this.resetGame();
				MessageHandler.printMessage("Server: " + msg);
			} else {
				MessageHandler.printMessage("Server: " + msg);
			}
		}
	}
	
}
