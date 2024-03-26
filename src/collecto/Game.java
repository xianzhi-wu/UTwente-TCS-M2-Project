package collecto;

import utils.TextIO;

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
    private int current;

    /**
     * Creates a new Game object.
     * @requires s0 and s1 to be non-null
     * @param s0 the first player
     * @param s1 the second player
     */
    public Game(Player s0, Player s1) {
        board = new Board();
        players = new Player[NUMBER_PLAYERS];
        players[0] = s0;
        players[1] = s1;
        current = 0;
    }

    /**
     * Starts the Collecto game.
     * Asks after each ended game if the user want to continue. Continues until
     * the user does not want to play anymore.
     */
    public void start() {
        boolean continueGame = true;
        while (continueGame) {
            reset();
            play();
            System.out.println("\n> Play another time? (y/n)?");
            continueGame = TextIO.getBoolean();
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
	        	players[current].addBall(board.setMove(Integer.parseInt(moveArr[0])));
	        } else if (moveArr.length == 2) {
	        	board.moveBalls(Integer.parseInt(moveArr[0]));
	        	players[current].addBall(board.setMove(Integer.parseInt(moveArr[1])));
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
        System.out.println("\nCurrent game situation:");
    	System.out.printf("%s has %d point(s). %s\n", players[0].getName(), 
    			players[0].getScore(), players[0].getBallMap());
    	System.out.printf("%s has %d point(s). %s\n\n", players[1].getName(), 
    			players[1].getScore(), players[1].getBallMap());
    	System.out.println(board.toString());
    }

    /**
     * Prints the result of the last game.
     * @requires the game to be over
     */
    private void printResult() {
    	if (players[0].getScore() > players[1].getScore()) {
    		System.out.printf("%s is the winner. The score is %d.\n", players[0]
    				.getName(), players[0].getScore());
    	} else if (players[0].getScore() < players[1].getScore()) {
    		System.out.printf("%s is the winner. The score is %d.\n", players[1]
    				.getName(), players[1].getScore());
    	} else {
    		System.out.printf("Draw. Got the same score %d\n", players[0].getScore());
    	}
    }
}