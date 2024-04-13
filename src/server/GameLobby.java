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
     * Creates a new GameLobby object.
     * @requires s0 and s1 to be non-null
     * @param player01 the first player
     * @param player02 the second player
     */
    public GameLobby(CollectoClientHandler player01, CollectoClientHandler player02) {
        board = new Board();
        players = new CollectoClientHandler[NUMBER_PLAYERS];
        players[0] = player01;
        players[1] = player02;
    }
    
    /**
     * @param move a move the player wants to make
     * @param player who wants to make the move
     * @throws IOException
     */
    public synchronized void makeMove(String move, CollectoClientHandler player) 
    		throws IOException {
    	if (players[current].equals(player)) {
	    	String[] moveArr = move.split("~");
	    	boolean validMove = true;
	        if (moveArr.length == 1) {
	        	int m = board.convertMoveStr(moveArr[0]);
	        	if (board.isValidSingleMove(m)) {
	        		players[current].setColorMap(board.setMove(m));
	        	} else {
	        		validMove = false;
	        	}
	        } else if (moveArr.length == 2) {
	        	int m1 = board.convertMoveStr(moveArr[0]);
	        	int m2 = board.convertMoveStr(moveArr[1]);
	        	if (board.isValidDoubleMove(m1, m2)) {
	        		board.moveBalls(m1);
	        		players[current].setColorMap(board.setMove(m2));
	        	} else {
	        		validMove = false;
	        	}
	        }
	        if (validMove) {
		        players[0].sendMessage(Protocols.MOVE + Protocols.TILDE + move);
		        players[1].sendMessage(Protocols.MOVE + Protocols.TILDE + move);
		        current = current == 0 ? 1 : 0;
		        if (board.gameOver()) {
		    		String res = getResult();
		    		players[0].gameOver(res);
		    		players[1].gameOver(res);
		    	}
	        } else {
	        	players[current].sendMessage(Protocols.ERROR + Protocols.TILDE + Protocols.MOVE 
	        			+ Protocols.TILDE + move + " is invalid.\n"
	        			+ "Hint: " + players[current].determineMove(board));
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
		current = (int) (Math.random() * 2);
		CollectoClientHandler player01 = players[current];
		CollectoClientHandler player02 = players[current == 0 ? 1 : 0];
		String initStr = Protocols.NEWGAME + board.toProtocolString() + Protocols.TILDE 
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
		String res;
		int score01 = players[0].getScore();
		int score02 = players[1].getScore();
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
		if (players[0] != null && players[0].equals(player)) {
			String name = players[0].getName();
			players[0] = null;
			if (players[1] == null) {
			    System.out.println("Both players disconnected");
			} else {
				players[1].gameOver(Protocols.GAMEOVER + Protocols.TILDE 
						+ Protocols.DISCONNECT + Protocols.TILDE + name);
			}
		} else if (players[1] != null && players[1].equals(player)) {
			String name = players[1].getName();
			players[1] = null;
			if (players[0] == null) {
				System.out.println("Both players disconnected");
			} else {
				players[0].gameOver(Protocols.GAMEOVER + Protocols.TILDE 
						+ Protocols.DISCONNECT + Protocols.TILDE + name);
			}
		}
	}
	
}
