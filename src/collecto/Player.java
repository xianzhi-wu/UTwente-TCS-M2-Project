package collecto;

import java.util.HashMap;

import utils.Colors;
import utils.Protocols;

public abstract class Player {

    private String name;
    private HashMap<Colors, Integer> colorMap;
    
    public Player() {
    	this.colorMap = new HashMap<>();
    }

    /**
     * Creates a new Player object.
     * @requires 'name' is not null
     * @ensures the name of this player will be 'name'
     */
    public Player(String name) {
        this.name = name;
        this.colorMap = new HashMap<>();
    }

    /**
     * Returns the name of the player.
     */
    public String getName() {
        return this.name;
    }

    /*
     * Set the name of the player
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Returns the score of the player.
     */
    public int getScore() {
    	int score = 0;

    	for (Integer val : this.colorMap.values()) {
    		score += val / 3;
    	}

    	return score;
    }
    
    /**
     * Determines the field for the next move
     * @requires board has valid move
     * @param board the current game board
     * @return the player's choice
     */
    public abstract String determineMove(Board board);

    /**
     * Makes a move on the board
     * @requires board has at least a valid move
     * @param board the current board
     */
    public void makeMove(Board board, String move) {
    	String[] moveArr = move.split(Protocols.TILDE);

		if (moveArr.length == 1) {
			this.setColorMap(board.setMove(Integer.parseInt(moveArr[0])));
		} else if (moveArr.length == 2) {
			board.moveBalls(Integer.parseInt(moveArr[0]));
			this.setColorMap(board.setMove(Integer.parseInt(moveArr[1])));
		}
    };
    
    /**
     * Add the color after each valid move
     * @param map
     */
    public void setColorMap(HashMap<Colors, Integer> map) {
    	for (Colors color : map.keySet()) {
    		int current = this.colorMap.getOrDefault(color, 0);
    		this.colorMap.put(color, current + map.get(color));
    	}
    }
    
    /**
     * Return the balls that the player gets.
     * @return a map containing balls(color) and amount
     */
    public HashMap<Colors, Integer> getColorMap() {
    	return this.colorMap;
    }
    
    /**
     * reset the result.
     */
    public void reset() {
    	this.colorMap = new HashMap<>();
    }
    
}
