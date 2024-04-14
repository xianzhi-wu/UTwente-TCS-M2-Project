package client;

import collecto.Board;
import collecto.ComputerPlayer;
import collecto.NaiveStrategy;
import collecto.Player;

import exceptions.ProtocolException;
import exceptions.ServerUnavailableException;

import utils.Protocols;
import utils.States;
import utils.TextIO;

public class CollectoClientTUI {
	
	private CollectoClient client;
	
	private Player player;
	private Board board;
	
	public CollectoClientTUI() {
		this.client = new CollectoClient();
	}

	public void setUp() {
		System.out.print("Input server address for client: ");
		String host = TextIO.getlnString();

		System.out.print("Input a port for client: ");
		int port = TextIO.getlnInt();

		client.setUp(host, port);
	}
	
	public boolean createConnection() {
		return client.createConnection();
	}
	
	public void setPlayerType() {
		System.out.println("\n-- Collecto client --\n" + 
							"1, AI player 01(Smart)\n" + 
							"2, AI player 02(Naive)\n" +
							"3, Human player\n");
							
		System.out.print("Your option: ");
		
		int type = TextIO.getlnInt();
		if (type != 1 && type != 2) {
			type = 3;
		}
		this.client.setPlayerType(type);
	}

	public void handleHello() throws ServerUnavailableException, ProtocolException {
		while (this.client.getState().equals(States.NEWIN)) {
			System.out.print("Say something to server: ");
			String sayHello = TextIO.getln();
			this.client.handleHello(Protocols.HELLO + Protocols.TILDE + sayHello);
		}
	}

	public void handleLogin() throws ServerUnavailableException, ProtocolException {
		while (this.client.getState().equals(States.SAYHELLO)) {
			System.out.print("Enter your name to login: ");
			String name = TextIO.getln();
			this.client.handleLogin(Protocols.LOGIN + Protocols.TILDE + name);
		}
	}

	/**
	 * Start the client to play the game.
	 * @throws ServerUnavailableException
	 * @throws ProtocolException
	 */
	public void start() throws ServerUnavailableException, ProtocolException {
		this.setUp();
		if (this.createConnection()) {
			this.setPlayerType();
			this.handleHello();
			this.handleLogin();
			this.printHelpMenu();
			this.getUserInput();
		}
	}

	public void handleQueue(String input) throws ServerUnavailableException, ProtocolException {
		this.client.handleQueue(input);
	}

	public void handleQuit() throws ServerUnavailableException, ProtocolException {
		this.client.handleQuit();
	}

	public void handleMove(String input) throws ServerUnavailableException, ProtocolException {
		String[] command = input.split(Protocols.TILDE);
		if (this.client.getState().equals(States.PLAYING)) {
			this.board = this.client.getBoard();
			if (command.length == 2) {
				if (this.player instanceof ComputerPlayer || 
						(this.board != null && this.board.isValidSingleMove(this.board.convertMoveStr(command[1])))) {
					this.client.handleMove(input);
				} else {
					System.out.println(Protocols.ERROR + ": " + command[1] + " is not a valid move.");
				}
			} else if (command.length == 3) {
				if (this.player instanceof ComputerPlayer || 
						(this.board != null && this.board.isValidDoubleMove(this.board.convertMoveStr(command[1]), board.convertMoveStr(command[2])))) {
					this.client.handleMove(input);
				} else {
					System.out.println(Protocols.ERROR + ": " + command[1] + Protocols.TILDE + command[2] + " is not a valid move.");
				}
			} else {
				System.out.println(Protocols.ERROR + Protocols.TILDE + "Invalid input");
			}
		} else {
			System.out.println(Protocols.ERROR + Protocols.TILDE + "Untimely MOVE");
		}
	}

	public void handleChangeAI() throws ServerUnavailableException, ProtocolException {
		if (this.player instanceof ComputerPlayer) {
			System.out.println("\n-- Change AI --\n" + 
								"1, Smart\n" + 
								"2, Naive\n");

			System.out.print("Your option: ");

			int type = TextIO.getlnInt();
			while (!(type == 1 || type == 2)) {
				type = TextIO.getlnInt();
			}
			this.client.handleChangeAI(type);
		} else {
			System.out.println(Protocols.ERROR + Protocols.TILDE + "Can't change AI");
		}
	}

	public void hint() {
		if (this.client.getState().equals(States.PLAYING)) {
			this.board = this.client.getBoard();
			System.out.println("Hint for move: " + new NaiveStrategy().determineMove(this.board));
		} else {
			System.out.println(Protocols.ERROR + Protocols.TILDE + "Untimely HINT");
		}
	}
	
	public void printHelpMenu() {
		System.out.println("\nPossible inputs:\n" + 
				"- HELLO~<server description>\n" +
				"- LOGIN~<username>\n" +
				"- QUEUE\n" +
				"- AI\n" +
				"- LIST\n" + 
				"- RANK\n" +
				"- HINT\n" +
				"- MOVE~<first push>[~second push]\n");
	}
	
	public void handleList(String input) throws ServerUnavailableException, ProtocolException {
		this.client.handleList(input);
	}

	public void handleRank(String input) throws ServerUnavailableException, ProtocolException {
		this.client.handleRank(input);
	}

	public void getUserInput() throws ServerUnavailableException, ProtocolException {
		while (!this.client.getState().equals(States.QUITED)) {
			this.player = this.client.getPlayer();
			String input = "";

			if (this.player instanceof ComputerPlayer && this.client.getState().equals(States.PLAYING)) {
				String move = this.player.determineMove(this.client.getBoard());
				input = Protocols.MOVE + Protocols.TILDE + move;
				System.out.println(this.player.getName() + ": " + input);
			} else {
				System.out.print(this.player.getName() + ": ");
				input = TextIO.getlnString();
			}

			this.handleUserInput(input);
		}
	}

	/**
	 * handle the user input.
	 * @param input
	 * @throws ServerUnavailableException
	 * @throws ProtocolException
	 */
	public void handleUserInput(String input) throws ServerUnavailableException, ProtocolException {
		String command = input.split(Protocols.TILDE)[0];
		
		switch (command) {
			case Protocols.QUEUE:
				this.handleQueue(input);
				break;
			case Protocols.MOVE:
				this.handleMove(input);
				break;
			case Protocols.LIST:
				this.handleList(input);
				break;
			case Protocols.RANK:
				this.handleRank(input);
				break;
			case Protocols.HELP:
				this.printHelpMenu();
				break;
			case Protocols.HINT:
				this.hint();
				break;
			case Protocols.QUIT:
				this.handleQuit();
				break;
			case Protocols.AI:
				this.handleChangeAI();
				break;
			default:
				System.out.println("Unkown command: " + input);
				this.printHelpMenu();
		}
	}

	public static void main(String[] args) throws ServerUnavailableException, ProtocolException {
		CollectoClientTUI clientTUI = new CollectoClientTUI();
		clientTUI.start();
	}
	
}