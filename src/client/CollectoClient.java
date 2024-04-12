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

import exceptions.ProtocolException;
import exceptions.ServerUnavailableException;

import utils.Colors;
import utils.Protocols;
import utils.States;

public class CollectoClient {
	
	private Socket serverSock;
	private BufferedReader in;
	private BufferedWriter out;
	
	private Player player;
	private int playerType;
	private Player opponent;
	private Board board;
	
	//private String name;
	private States state = States.NEWIN;
	
	private String host;
	private int port;
	
	/**
	 * Constructs a new CollectClient and initializes the view.
	 */
	public CollectoClient() {
		//board = new Board();
	}
	
	public States getState() {
		return state;
	}
	
	public void resetGame() {
		board = null;
		opponent = null;
		player.reset();
		state = States.GAMEOVER;
	}
	
	public void setPlayerType(int type) {
		playerType = type;
	}
	
	/**
	 * Get the current player.
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}
	
	public Board getBoard() {
		return board;
	}
	
	public void setUp(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * Creates a connection to the server.
	 * @ensures serverSock contains a valid socket connection to a server
	 */
	public boolean createConnection() {
		clearConnection();
		// try to open a Socket to the server
		try {
			InetAddress addr = InetAddress.getByName(host);
			serverSock = new Socket(addr, port);
			System.out.println("Connected to " + addr + ":" + port);
			in = new BufferedReader(new InputStreamReader(serverSock.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(serverSock.getOutputStream()));
			return true;
		} catch (Exception e) {
			System.out.println("ERROR: could not create a socket on "
					+ host + " and port " + port + ".");
			return false;
		}
	}

	/**
	 * Resets the serverSocket, inputStream and outputStream to null
	 * Always make sure to close current connections via shutdown() 
	 * before calling this method!
	 */
	public void clearConnection() {
		serverSock = null;
		in = null;
		out = null;
	}

	/**
	 * Sends a message to the connected server, followed by a new line. 
	 * The stream is then flushed.
	 * @param msg the message to write to the OutputStream.
	 * @throws ServerUnavailableException if IO errors occur.
	 */
	public synchronized void sendMessage(String msg) throws ServerUnavailableException, ProtocolException {
		if (out != null) {
			try {
				out.write(msg);
				out.newLine();
				out.flush();
			} catch (IOException e) {
				System.out.println(e.getMessage());
				throw new ServerUnavailableException("Could not write to server.");
			}
		} else {
			throw new ServerUnavailableException("Could not write to server.");
		}
	}

	/**
	 * Reads and returns one line from the server.
	 * @return the line sent by the server.
	 * @throws ServerUnavailableException if IO errors occur.
	 */
	public String readLineFromServer() throws ServerUnavailableException {
		if (in != null) {
			try {
				// Read and return answer from Server
				String answer = in.readLine();
				if (answer == null) {
					throw new ServerUnavailableException("Could not read from server.");
				}
				return answer;
			} catch (IOException e) {
				throw new ServerUnavailableException("Could not read from server.");
			}
		} else {
			throw new ServerUnavailableException("Could not read from server.");
		}
	}
	
	/**
	 * Reads and returns multiple lines from the server until the end of 
	 * the text is indicated using a line containing Protocols.EOT.
	 * 
	 * @return the concatenated lines sent by the server.
	 * @throws ServerUnavailableException if IO errors occur.
	 */
	public String readMultipleLinesFromServer() 
			throws ServerUnavailableException {
		if (in != null) {
			try {
				// Read and return answer from Server
				StringBuilder sb = new StringBuilder();
				for (String line = in.readLine(); line != null
						&& !line.equals(Protocols.EOT); 
						line = in.readLine()) {
					sb.append(line + System.lineSeparator());
				}
				return sb.toString();
			} catch (IOException e) {
				throw new ServerUnavailableException("Could not read "
						+ "from server.");
			}
		} else {
			throw new ServerUnavailableException("Could not read "
					+ "from server.");
		}
	}
	
	/**
	 * Closes the connection by closing the In- and OutputStreams, as 
	 * well as the serverSocket.
	 */
	public void closeConnection() {
		try {
			in.close();
			out.close();
			serverSock.close();
			System.out.println("Quit!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Say hello to server.
	 * @param input HELLO~<client description>[~extension]*
	 * @throws ServerUnavailableException
	 * @throws ProtocolException
	 */
	public void handleHello(String input) throws ServerUnavailableException, ProtocolException {
		sendMessage(input);
		String res = readLineFromServer();
		System.out.println("Server: " + res);
		if (res.contains(Protocols.HELLO + Protocols.TILDE)) {
			state = States.SAYHELLO;
		}
	}
	
	/**
	 * @param input HELLO~[player name]
	 * @throws ServerUnavailableException
	 * @throws ProtocolException
	 */
	public void handleLogin(String input) throws ServerUnavailableException, ProtocolException {
		String[] inputArr = input.split(Protocols.TILDE);
		if (playerType == 1 || playerType == 2) {
			player = new ComputerPlayer(inputArr[1], playerType);
			inputArr[1] = player.getName();
			sendMessage(String.join(Protocols.TILDE, inputArr));
		} else if (playerType == 3) {
			player = new HumanPlayer(inputArr[1]);
			sendMessage(input);
		}
		String res = readLineFromServer();
		if (res.equals(Protocols.LOGIN)) {
			state = States.HANDSHANK;
		} else {
			player = null;
			System.out.println("Server: " + res);
		}
	}
	
	/**
	 * See the client list.
	 * @param input LIST
	 * @throws ServerUnavailableException
	 * @throws ProtocolException
	 */
	public void handleList(String input) throws ServerUnavailableException, ProtocolException {
		sendMessage(input);
		System.out.println("Players List: " + readLineFromServer());
	}
	
	/**
	 * See the rank.
	 * @param input RANK
	 * @throws ServerUnavailableException
	 * @throws ProtocolException
	 */
	public void handleRank(String input) throws ServerUnavailableException, ProtocolException {
		sendMessage(input);
		System.out.println(readMultipleLinesFromServer());
	}
	
	/**
	 * Join the queue for the game.
	 * @param input QUEUE
	 * @throws ServerUnavailableException
	 * @throws ProtocolException
	 */
	public void handleQueue(String input) throws ServerUnavailableException, ProtocolException {
		sendMessage(input);
		//state = States.QUEUEING;
		String res = readLineFromServer();
		//if (res.length() > 0) {
		if (res.contains(Protocols.NEWGAME)) {
			System.out.println(res);
			setBoard(res);
		} else {
			System.out.println("Server: " + res);
		}
		//}
	}
	
	/**
	 * @param input MOVE~<first push>[~second push]
	 * @throws ServerUnavailableException
	 * @throws ProtocolException
	 */
	public void handleMove(String input) throws ServerUnavailableException, ProtocolException {
		sendMessage(input);
		String res = readLineFromServer();
		if (input.equals(res)) {
			res = res.replace(Protocols.MOVE + Protocols.TILDE, "");
			player.makeMove(board, res);
			System.out.println(board.toString());
			// Show move from the peer client
			res = readLineFromServer();
			if (res.contains(Protocols.MOVE + Protocols.TILDE)) {
				System.out.println(opponent.getName() + ": " + res);
				res = res.replace(Protocols.MOVE + Protocols.TILDE, "");
				opponent.makeMove(board, res);
				System.out.println(board.toString());
				if (board.gameOver()) {
					resetGame();
					System.out.println("Server: " + readLineFromServer());
				}
			} else if (res.contains(Protocols.GAMEOVER + Protocols.TILDE)) {
				resetGame();
				System.out.println("Server: " + res);
			} else {
				System.out.println("Server: " + res);
			}
		} else {
			System.out.println("Server: " + res);
		}
	}
	
	public void handleQuit() throws ServerUnavailableException, ProtocolException {
		sendMessage(Protocols.QUIT);
		closeConnection();
	}
	
	public void handleAI(int type) throws ServerUnavailableException, ProtocolException {
		playerType = type;
		if (playerType == 1) {
			((ComputerPlayer) player).setStrategy(new SmartStrategy(player));
		} else if (playerType == 2) {
			((ComputerPlayer) player).setStrategy(new NaiveStrategy());
		}
	}
	
	/**
	 * @param res is a string received from server, containing NEWGAME, 
	 * a string of board fields and both plaers's names.
	 * NEWGAME~<cell value>^49~<player1 name>~<player2 name>
	 * @throws ServerUnavailableException
	 * @throws ProtocolException
	 */
	public void setBoard(String res) throws ServerUnavailableException, ProtocolException {
		player.reset();
		String[] gameStr = res.split(Protocols.TILDE);
		Colors[][] fields = new Colors[Board.DIM][Board.DIM];
		for (int i = 1; i < Board.DIM * Board.DIM + 1; i++) {
			int row = (i - 1) / Board.DIM,
				col = (i - 1) % Board.DIM;
			fields[row][col] = Colors.values()[Integer.parseInt(gameStr[i])];
		}
		// Loop through the array and print each element
        for (int i = 0; i < Board.DIM; i++) {
            for (int j = 0; j < Board.DIM; j++) {
                System.out.print(fields[i][j] + " ");
            }
            System.out.println(); // Move to the next line after each row
        }

		board = new Board(fields);
		state = States.PLAYING;
		System.out.println(board.toString());
		
		/*
		 * If the peer client plays first
		 */
		if (player.getName().equals(gameStr[gameStr.length - 2])) {
			opponent = new HumanPlayer(gameStr[gameStr.length - 1]);
		} else {
			String move = readLineFromServer();
			if (move.contains(Protocols.MOVE + Protocols.TILDE)) {
				opponent = new HumanPlayer(gameStr[gameStr.length - 2]);
				System.out.println(opponent.getName() + ": " + move);
				move = move.replace(Protocols.MOVE + Protocols.TILDE, "");
				//opponent move first
				opponent.makeMove(board, move);
				System.out.println(board.toString());
			} else if (move.contains(Protocols.GAMEOVER)) {
				resetGame();
				System.out.println("Server: " + move);
			} else {
				System.out.println("Server: " + move);
			}
		}
	}
	
}
