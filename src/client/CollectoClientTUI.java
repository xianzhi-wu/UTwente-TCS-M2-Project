package client;

import collecto.ComputerPlayer;

import exceptions.ServerUnavailableException;

import utils.Protocols;
import utils.States;
import utils.TextIO;

import utils.MessageHandler;

public class CollectoClientTUI {
	
	private CollectoClient client;
	
	public CollectoClientTUI() {
		this.client = new CollectoClient();
	}

	public void connectServer() throws ServerUnavailableException {
		System.out.print("Input server address for client: ");
		String host = TextIO.getlnString();

		System.out.print("Input a port for client: ");
		int port = TextIO.getlnInt();

		this.client.setUp(host, port);
		this.client.createConnection();
	}
	
	public void setPlayerType() {
		MessageHandler.printMessage("\n-- Collecto client --\n" + 
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

	public void handleHello() throws ServerUnavailableException {
		while (this.client.getState().equals(States.NEWIN)) {
			System.out.print("Say something to server: ");
			String sayHello = TextIO.getln();
			this.client.handleHello(Protocols.HELLO + Protocols.TILDE + sayHello);
		}
	}

	public void handleLogin() throws ServerUnavailableException {
		while (this.client.getState().equals(States.SAYHELLO)) {
			System.out.print("Enter your name to login: ");
			String name = TextIO.getln();
			this.client.handleLogin(Protocols.LOGIN + Protocols.TILDE + name);
		}
	}

	/**
	 * Start the client to play the game.
	 * @throws ServerUnavailableException 
	 */
	public void start() throws ServerUnavailableException {
		this.connectServer();
		this.setPlayerType();
		this.handleHello();
		this.handleLogin();
		this.printHelpMenu();
		this.getUserInput();
	}

	public void handleMove(String input) throws ServerUnavailableException {
		String[] command = input.split(Protocols.TILDE);
		if (this.client.getState().equals(States.PLAYING)) {
			if (command.length == 2) {
				if (this.client.getPlayer() instanceof ComputerPlayer || 
						(this.client.getBoard() != null && this.client.getBoard().isValidSingleMove(this.client.getBoard().convertMoveStr(command[1])))) {
					this.client.handleMove(input);
				} else {
					MessageHandler.printMessage(Protocols.ERROR + ": " + command[1] + " is not a valid move.");
				}
			} else if (command.length == 3) {
				if (this.client.getPlayer() instanceof ComputerPlayer || 
						(this.client.getBoard() != null && this.client.getBoard().isValidDoubleMove(this.client.getBoard().convertMoveStr(command[1]), this.client.getBoard().convertMoveStr(command[2])))) {
					this.client.handleMove(input);
				} else {
					MessageHandler.printMessage(Protocols.ERROR + ": " + command[1] + Protocols.TILDE + command[2] + " is not a valid move.");
				}
			} else {
				MessageHandler.printMessage(Protocols.ERROR + Protocols.TILDE + "Invalid input");
			}
		} else {
			MessageHandler.printMessage(Protocols.ERROR + Protocols.TILDE + "Untimely MOVE");
		}
	}

	public void handleChangeAI() {
		if (this.client.getPlayer() instanceof ComputerPlayer) {
			MessageHandler.printMessage("\n-- Change AI --\n" + 
								"1, Smart\n" + 
								"2, Naive\n");

			System.out.print("Your option: ");

			int type = TextIO.getlnInt();
			while (!(type == 1 || type == 2)) {
				type = TextIO.getlnInt();
			}
			this.client.handleChangeAI(type);
		} else {
			MessageHandler.printMessage(Protocols.ERROR + Protocols.TILDE + "Can't change AI");
		}
	}
	
	public void printHelpMenu() {
		MessageHandler.printMessage("\nPossible inputs:\n" + 
				"- HELLO~<server description>\n" +
				"- LOGIN~<username>\n" +
				"- QUEUE\n" +
				"- AI\n" +
				"- LIST\n" + 
				"- RANK\n" +
				"- HINT\n" +
				"- MOVE~<first push>[~second push]\n");
	}

	public void getUserInput() throws ServerUnavailableException {
		while (!this.client.getState().equals(States.QUITED)) {
			String input = "";

			if (this.client.getPlayer() instanceof ComputerPlayer && this.client.getState().equals(States.PLAYING)) {
				String move = this.client.getPlayer().determineMove(this.client.getBoard());
				input = Protocols.MOVE + Protocols.TILDE + move;
				MessageHandler.printMessage(this.client.getPlayer().getName() + ": " + input);
			} else {
				System.out.print(this.client.getPlayer().getName() + ": ");
				input = TextIO.getlnString();
			}

			this.handleUserInput(input);
		}
	}

	/**
	 * handle the user input.
	 * @param input
	 * @throws ServerUnavailableException 
	 */
	public void handleUserInput(String input) throws ServerUnavailableException {
		String command = input.split(Protocols.TILDE)[0];
		
		switch (command) {
			case Protocols.QUEUE:
				this.client.handleQueue(input);
				break;
			case Protocols.MOVE:
				this.client.handleMove(input);
				break;
			case Protocols.LIST:
				this.client.handleList(input);
				break;
			case Protocols.RANK:
				this.client.handleRank(input);
				break;
			case Protocols.HINT:
				this.client.handleHint();
				break;
			case Protocols.QUIT:
				this.client.handleQuit();
				break;
			case Protocols.AI:
				this.handleChangeAI();
				break;
			case Protocols.HELP:
				this.printHelpMenu();
				break;
			default:
				this.printHelpMenu();
				MessageHandler.handleError("Unkown command: " + input);
		}
	}

	public static void main(String[] args) throws ServerUnavailableException {
		CollectoClientTUI clientTUI = new CollectoClientTUI();
		clientTUI.start();
	}
	
}