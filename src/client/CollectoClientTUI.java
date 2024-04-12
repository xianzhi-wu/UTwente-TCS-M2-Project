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
	
	/**
	 * Start the client to play the game.
	 * @throws ServerUnavailableException
	 * @throws ProtocolException
	 */
	public void start() throws ServerUnavailableException, ProtocolException {
		
		System.out.println("\n-- Collecto client --\n" + 
							"1, AI player 01(Smart)\n" + 
							"2, AI player 02(Naive)\n" +
							"3, Human player\n");
							
		System.out.print("Your option: ");
		
		int type = TextIO.getlnInt();
		if (type != 1 && type != 2) {
			type = 3;
		}
		client.setPlayerType(type);
		
		while (client.getState().equals(States.NEWIN)) {
			System.out.print("Say something to server: ");
			String sayHello = TextIO.getln();
			client.handleHello(Protocols.HELLO + Protocols.TILDE + sayHello);
		}
		
		while (client.getState().equals(States.SAYHELLO)) {
			System.out.print("Enter your name to login: ");
			String name = TextIO.getln();
			client.handleLogin(Protocols.LOGIN + Protocols.TILDE + name);
		}
		
		player = client.getPlayer();
		boolean exit = false;
		while (!exit) {
			String input = "";
			if (player instanceof ComputerPlayer) {
				if (client.getState().equals(States.PLAYING)) {
					String move = player.determineMove(client.getBoard());
					if (!move.equals("-1")) {
						input = "MOVE~" + move;
						System.out.println(player.getName() + "(" + ((ComputerPlayer) player).getStrategy().getName() + ")" + ": " + input);
					} else {
						continue;
					}
				} else {
					System.out.print(player.getName() + "(" + ((ComputerPlayer) player).getStrategy().getName() + ")" + ": ");
					input = TextIO.getlnString();
				}
			} else {
				System.out.print(player.getName() + ": ");
				input = TextIO.getlnString();
			}
			
			if (input.equals(Protocols.QUIT)) {
				exit = true;
				client.handleQuit();
			} else {
				handleUserInput(input);
			}
		}
		
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

	/**
	 * handle the user input.
	 * @param input
	 * @throws ServerUnavailableException
	 * @throws ProtocolException
	 */
	public void handleUserInput(String input) throws ServerUnavailableException, ProtocolException {
		
		String[] splitted = input.split("~"); 
		String commandString = splitted[0];
		
		switch (commandString) {
			case Protocols.HELLO:
				if (splitted.length > 1) {
					client.handleHello(input);
				} else {
					System.out.println(Protocols.ERROR + Protocols.TILDE + "Invalid input");
				}
				break;
			case Protocols.LOGIN:
				if (client.getState().equals(States.HANDSHANK) || client.getState().equals(States.GAMEOVER)) {
					System.out.println(Protocols.ALREADYLOGGEDIN);
				} else {
					if (splitted.length == 2) {
						client.handleLogin(input);
					} else {
						System.out.println(Protocols.ERROR + Protocols.TILDE + "Invalid input");
					}
				}
				break;
			case Protocols.QUEUE:
				if (splitted.length == 1) {
					client.handleQueue(input);
				} else {
					System.out.println(Protocols.ERROR + Protocols.TILDE + "Invalid input");
				}
				break;
			case Protocols.MOVE:
				if (client.getState().equals(States.PLAYING)) {
					board = client.getBoard();
					if (splitted.length == 2) {
						if (player instanceof ComputerPlayer || 
								(board != null && board.isValidSingleMove(board.convertMoveStr(splitted[1])))) {
							client.handleMove(input);
						} else {
							System.out.println(Protocols.ERROR + ": " + splitted[1] + " is not a valid move.");
						}
					} else if (splitted.length == 3) {
						if (player instanceof ComputerPlayer || 
								(board != null && board.isValidDoubleMove(board.convertMoveStr(splitted[1]), board.convertMoveStr(splitted[2])))) {
							client.handleMove(input);
						} else {
							System.out.println(Protocols.ERROR + ": " + splitted[1] + Protocols.TILDE + splitted[2] + " is not a valid move.");
						}
					} else {
						System.out.println(Protocols.ERROR + Protocols.TILDE + "Invalid input");
					}
				} else {
					System.out.println(Protocols.ERROR + Protocols.TILDE + "Untimely MOVE");
				}
				break;
			case Protocols.LIST:
				client.handleList(input);
				break;
			case Protocols.RANK:
				client.handleRank(input);
				break;
			case Protocols.HELP:
				printHelpMenu();
				break;
			case Protocols.HINT:
				if (client.getState().equals(States.PLAYING)) {
					board = client.getBoard();
					System.out.println("Hint for move: " + new NaiveStrategy().determineMove(board));
				} else {
					System.out.println(Protocols.ERROR + Protocols.TILDE + "Untimely HINT");
				}
				break;
			case "AI":
				if (player instanceof ComputerPlayer) {
					System.out.println("\n-- Change AI --\n" + 
							"1, Smart\n" + 
							"2, Naive\n");
					System.out.print("Your option: ");
					int type = TextIO.getlnInt();
					while (!(type == 1 || type == 2)) {
						type = TextIO.getlnInt();
					}
					client.handleAI(type);
				} else {
					System.out.println(Protocols.ERROR + Protocols.TILDE + "Can't change AI");
				}
				break;
			default:
				System.out.println("Unkown command: " + input);
				printHelpMenu();
		}
		
	}
	
	public void printHelpMenu() {
		System.out.println("\nPossible inputs:\n" + 
				"- HELLO~<server description>[~extension]*\n" +
				"- LOGIN~<username>\n" +
				"- LIST\n" + 
				"- QUEUE\n" +
				"- RANK\n" +
				"- HINT\n" +
				"- MOVE~<first push>[~second push]\n");
	}

	public static void main(String[] args) throws ServerUnavailableException, ProtocolException {
		CollectoClientTUI clientTUI = new CollectoClientTUI();
		clientTUI.setUp();
		if (clientTUI.createConnection()) {
			clientTUI.start();
		} else {
			System.out.println("Failed to connect to server");
		}
	}
}