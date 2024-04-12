package server;

import java.io.IOException;

import collecto.Board;
import utils.Protocols;

public class GameLobby {
	
	public static final int NUMBER_PLAYERS = 2;

    /**
     * @invariant board is never null
     */
    private Board board;

    /**
     * The 2 players of the game.
     * @invariant the length of the array equals NUMBER_PLAYERS
     * @invariant all array items are never null
     */
    private CollectoClientHandler[] players;

    /**
     * Index of the current player.
     * @invariant the index is always between 0 and NUMBER_PLAYERS
     */
    private int current;

    /**
     * Creates a new GameRoom object.
     * @requires s0 and s1 to be non-null
     * @param player01 the first player
     * @param player02 the second player
     */
    public GameLobby(CollectoClientHandler player01, CollectoClientHandler player02) {
        this.board = new Board();
        this.players = new CollectoClientHandler[NUMBER_PLAYERS];
        this.players[0] = player01;
        this.players[1] = player02;
    }
    
    /**
     * @param move a move the player wants to make
     * @param player who wants to make the move
     * @throws IOException
     */
    public synchronized void makeMove(String move, CollectoClientHandler player) throws IOException {
    	if (this.players[this.current].equals(player)) {
	    	String[] moveArr = move.split("~");
	    	boolean validMove = true;

	        if (moveArr.length == 1) {
	        	int m = this.board.convertMoveStr(moveArr[0]);
	        	if (this.board.isValidSingleMove(m)) {
	        		this.players[this.current].setColorMap(this.board.setMove(m));
	        	} else {
	        		validMove = false;
	        	}
	        } else if (moveArr.length == 2) {
	        	int m1 = this.board.convertMoveStr(moveArr[0]);
	        	int m2 = this.board.convertMoveStr(moveArr[1]);
	        	if (this.board.isValidDoubleMove(m1, m2)) {
	        		this.board.moveBalls(m1);
	        		this.players[this.current].setColorMap(this.board.setMove(m2));
	        	} else {
	        		validMove = false;
	        	}
	        }

	        if (validMove) {
		        this.players[0].sendMessage(Protocols.MOVE + Protocols.TILDE + move);
		        this.players[1].sendMessage(Protocols.MOVE + Protocols.TILDE + move);
		        this.current = this.current == 0 ? 1 : 0;
		        if (this.board.gameOver()) {
		    		String res = getResult();
		    		this.players[0].gameOver(res);
		    		this.players[1].gameOver(res);
		    	}
	        } else {
	        	this.players[this.current].sendMessage(Protocols.ERROR + Protocols.TILDE + Protocols.MOVE 
	        			+ Protocols.TILDE + move + " is invalid.\n"
	        			+ "Hint: " + this.players[this.current].determineMove(this.board));
	        }
    	} else {
    		player.sendMessage(Protocols.ERROR + Protocols.TILDE + "Not your turn");
    	}
    }
    
    /**
     * Start the game, send the board to both players.
     * who player first is random
     */
	public synchronized void startGame() {
		this.current = (int) (Math.random() * 2);
		CollectoClientHandler player01 = this.players[this.current];
		CollectoClientHandler player02 = this.players[this.current == 0 ? 1 : 0];
		String initStr = Protocols.NEWGAME + this.board.toProtocolString() + Protocols.TILDE 
				+ player01.getName() + Protocols.TILDE + player02.getName();
		System.out.println(initStr);
		player01.startGame(this, initStr);
		player02.startGame(this, initStr);
	}
	
	/**
	 * When the game is over.
	 * @return the result
	 */
	public String getResult() {
		int score01 = this.players[0].getScore();
		int score02 = this.players[1].getScore();

		String res;
		if (score01 > score02) {
			res = Protocols.GAMEOVER + Protocols.TILDE + Protocols.VICTORY 
					+ Protocols.TILDE + players[0].getName();
		} else if (score01 < score02) {
			res = Protocols.GAMEOVER + Protocols.TILDE + Protocols.VICTORY 
					+ Protocols.TILDE + players[1].getName();
		} else {
			res = Protocols.GAMEOVER + Protocols.TILDE + Protocols.DRAW;
		}
		
		return res;
	}
	
	/**
	 * When the player is disconnected, remove the player.
	 * @param player
	 * @throws IOException
	 */
	public synchronized void removePlayer(CollectoClientHandler player) throws IOException {
		if (this.players[0] != null && this.players[0].equals(player)) {
			String name = this.players[0].getName();
			this.players[0] = null;
			if (this.players[1] == null) {
			    System.out.println("Both players disconnected");
			} else {
				this.players[1].gameOver(Protocols.GAMEOVER + Protocols.TILDE 
						+ Protocols.DISCONNECT + Protocols.TILDE + name);
			}
		} else if (this.players[1] != null && this.players[1].equals(player)) {
			String name = this.players[1].getName();
			this.players[1] = null;
			if (this.players[0] == null) {
				System.out.println("Both players disconnected");
			} else {
				this.players[0].gameOver(Protocols.GAMEOVER + Protocols.TILDE 
						+ Protocols.DISCONNECT + Protocols.TILDE + name);
			}
		}
	}
	
}
