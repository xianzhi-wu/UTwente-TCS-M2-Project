package collecto;

import utils.TextIO;
import utils.MessageHandler;

public class Game {
    
    public static final int NUMBER_PLAYERS = 2;

    /**
     * The board.
     * @invariant board is never null
     */
    private Board board;

    /**
     * The 2 players of the game.
     * @invariant the length of the array equals NUMBER_PLAYERS
     * @invariant all array items are never null
     */
    private Player[] players;

    /**
     * Index of the current player.
     * @invariant the index is always between 0 and NUMBER_PLAYERS
     */
    private int current = 0;

    /**
     * Creates a new Game object.
     * @requires p0 and p1 to be non-null
     * @param p0 the first player
     * @param p1 the second player
     */
    public Game(Player p0, Player p1) {
        board = new Board();
        players = new Player[NUMBER_PLAYERS];
        players[0] = p0;
        players[1] = p1;
        // current = 0;
    }

    /**
     * Starts the Collecto game.
     * Asks after each ended game if the user want to continue. Continues until
     * the user does not want to play anymore.
     */
    public void start() {
        boolean continueGame = true;
        while (continueGame) {
            play();
            MessageHandler.printMessage("\n> Play again? (y/n)");
            continueGame = TextIO.getBoolean();
            reset();
        }
    }

    /**
     * Resets the game.
     * The board is emptied and player[0] becomes the current player.
     */
    private void reset() {
        current = 0;
        board.reset();
        players[0].reset();
        players[1].reset();
    }

    /**
     * Plays the Collecto game. 
     * First the (still empty) board is shown. Then the game is played
     * until it is over. Players can make a move one after the other. 
     * After each move, the changed game situation is printed.
     */
    private void play() {
    	while (!board.gameOver()) {
    		update();

	    	String moveStr = players[current].determineMove(board);
	    	String[] moveArr = moveStr.split("~");

	        if (moveArr.length == 1) {
	        	players[current].setColorMap(board.setMove(Integer.parseInt(moveArr[0])));
	        } else if (moveArr.length == 2) {
	        	board.moveBalls(Integer.parseInt(moveArr[0]));
	        	players[current].setColorMap(board.setMove(Integer.parseInt(moveArr[1])));
	        }

	    	current = current == 0 ? 1 : 0;
    	}

    	update();
    	printResult();
    }

    /**
     * Prints the game situation.
     */
    private void update() {
        MessageHandler.printMessage("\nCurrent game situation:");
        for (int i = 0; i < NUMBER_PLAYERS; i++) {
            MessageHandler.printMessage(
            String.format("%s has %d point(s). %s\n", 
                          players[i].getName(), 
                          players[i].getScore(), 
                          players[i].getColorMap())
            );
        }
        MessageHandler.printMessage(board.toString());
    }

    /**
     * Prints the result of the last game.
     * @requires the game to be over
     */
    private void printResult() {
        String result = null;
    	if (players[0].getScore() > players[1].getScore()) {
            result = String.format("%s is the winner. The score is %d.\n", 
                                   players[0].getName(), 
                                   players[0].getScore());
    	} else if (players[0].getScore() < players[1].getScore()) {
    		result = String.format("%s is the winner. The score is %d.\n", 
                                   players[1].getName(), 
                                   players[1].getScore());
    	} else {
    		result = String.format("Draw. Got the same score %d\n", 
                                   players[0].getScore());
    	}
        MessageHandler.printMessage(result);
    }

}