package collecto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import utils.Colors;
import utils.Protocols;

public class Board {

	public static final int DIM = 7; // Dimension of the board
	public static final int EACH = 8; // Number of each color
	public static final int MINMOVE = 0; // Minimum number of possible move
	public static final int MAXMOVE = 27; // Maximum number of possible move
	
	private Colors[][] fields; // 2D array representing the board fields

	/**
	 * initialize the board in the constructor.
	 */
	public Board() {
		this.init(); // Call the init method to initialize the board
    }

	/**
	 * Constructor with predefined board fields.
	 * @param fields The 2D array representing the predefined board fields
	 */
	public Board(Colors[][] fields) {
		// Initialize fields array
		this.fields = new Colors[DIM][DIM]; 
		for (var i = 0; i < DIM; i++) {
    		for (var j = 0; j < DIM; j++) {
    			this.fields[i][j] = fields[i][j]; // Copy the values from the given fields to the board
    		}
    	}
    }
	
	/**
	 * Get the current state of the board.
	 * @return fields The 2D array representing the board fields
	 */
	public Colors[][] getFields() {
		return this.fields;
	}
	
	/**
	 * The board is a 2D array (DIM * DIM fields).
	 * The center field of the board is EMPTY
	 * Initialize the board with valid colors using the Fisher-Yates shuffle algorithm
	 * Apply a random arrangement of colored balls while ensuring that no adjacent balls
	 * of the same color initially. The algorithm guarantees a solvable game state.
	 * @ensures the board is a valid board
	 * @returns a valid board
	 */
	public void init() {
		Colors[] colors = Arrays.stream(Colors.values())
								.filter(color -> color != Colors.EMPTY)
								.toArray(Colors[]::new);

		// Create a list containing 8*6=48 balls (8 of each color except EMPTY)
		Colors[] colorArr = new Colors[EACH * colors.length];

		// Populate the list with balls of different colors
		int fillIdx = 0;
		for (int i = 0; i < EACH; i++) {
			for (Colors color : colors) {
				colorArr[fillIdx++] = color;
			}
		}

		this.fields = new Colors[DIM][DIM];
		this.fields[DIM / 2][DIM / 2] = Colors.EMPTY; // Set the middle field as EMPTY
		int total = DIM * DIM;
		int randRange = EACH * colors.length;
		// Use a HashMap to store the count of balls that are to be filled.
		// Key: Color of the balls
		// Value: Number of balls of the same color that are to be filled
		HashMap<Colors, Integer> filledColorMap = new HashMap<>();

		// Iterate through each field to assign a color randomly while avoiding adjacent colors
		for (int i = 0; i < total; i++) {
			int row = i / DIM,
				col = i % DIM;
			
			// Skip the middle field
			if (row == DIM / 2 && col == DIM / 2) {
				continue;
			}
			
			int index = (int) (Math.random() * randRange);

			// Ensure that the color assignment adheres to the rules of the game
			if (row == 0) {
				// If in the first row and not in the first column, ensure it does not have the same color as the left neighbor 
				if (col != 0) {
					Colors left = this.fields[row][col - 1];
					while (colorArr[index] == left) {
						index = (int) (Math.random() * randRange);
					}
				}
			} else if (row > 0) {  // If not in the first row, check the top or left neighbor for color match
				// If in the first column, only check the top neighbor
				if (col == 0) {
					Colors top = this.fields[row - 1][col];

					// If in the last row, ensure the only remaining color is not the same as the color of the top neighbor. 
					// Otherwise, the initialization of the board is invalid.
					if (row == DIM - 1) {
						HashSet<Colors> set = getRemainingColors(filledColorMap, colors);
						if (set.size() == 1 && set.contains(top)) {
							break;
						}
					}

					// Ensure it does not have the same color as the top neighbor 
					while (colorArr[index] == top) {
						index = (int) (Math.random() * randRange);
					}
				} else { // If not in the first column, check the top or left neighbor
					Colors top = this.fields[row - 1][col];
    				Colors left = this.fields[row][col - 1];

					// If in the last two rows, ensure there are at least 2 different colors available
    				if (row == DIM - 1 || row == DIM - 2) {
    					HashSet<Colors> set = getRemainingColors(filledColorMap, colors);
	    				if (set.size() == 2 && set.contains(top) && set.contains(left)
							|| set.size() == 1 && (set.contains(top) || set.contains(left))) {
	    					break;
	    				}
    				}

					// Ensure it does not have the same color as the top neighbor or the left neighbor.
					while (colorArr[index]== top || colorArr[index]== left) {
    					index = (int) (Math.random() * randRange);
    				}
				}
			}

			this.fields[row][col] = colorArr[index]; // Assign the color to the current field
			filledColorMap.put(colorArr[index], filledColorMap.getOrDefault(colorArr[index], 0) + 1);
			swap(colorArr, index, randRange - 1);
			randRange--;
		}

		// If any balls remain in the list (shouldn't happen in a valid game state), reinitialize
		if (randRange != 0) {
			this.init();
		}
	}

	public HashSet<Colors> getRemainingColors(HashMap<Colors, Integer> colorsMap, Colors[] colors) {
		HashSet<Colors> set = new HashSet<>();
		for (Colors color: colors) {
			if (colorsMap.getOrDefault(color, 0) < EACH) {
				set.add(color);
			}
		}
		return set;
	}

	public void swap(Colors[] colorArr, int index1, int index2) {
		if (index1 >= 0 && index1 < colorArr.length && index2 >= 0 && index2 < colorArr.length) {
			Colors temp = colorArr[index1];
			colorArr[index1] = colorArr[index2];
			colorArr[index2] = temp;
		} else {
			throw new IllegalArgumentException("Invalid index"); 
		}
	}
	
	/**
	 * Creates a deep copy of this board.
	 * @ensures the result is a new object distinct from this object
	 * @ensures the values of all fields of the copy match the ones of this board
	 * @return a deep copy of this board
	 */
	public Board deepCopy() {
		// Create a new Board object to store the copied fields
		Board boardCopy = new Board();

		// Iterate over each field of the board and copy its value to the new board
		for (int i = 0; i < DIM; i++) {
			for (int j = 0; j < DIM; j++) {
				boardCopy.fields[i][j] = this.fields[i][j];
			}
		}

		// Return the deep copy of the board
		return boardCopy;
	}
    
    /**
     * Sets the move
     * @param move The move direction: 0 for left, 1 for right, 2 for top, 3 for bottom.
	 * @return a Map of the colors of the adjacent balls that are the same and their corresponding number.
	 */
    public HashMap<Colors, Integer> setMove(int move) {
		// Use a HashMap to store the count of adjacent balls of the same color that are to be removed.
		// Key: Color of the adjacent balls
		// Value: Number of adjacent balls of the same color that are to be removed
    	HashMap<Colors, Integer> colorMap = new HashMap<>();

		// Move the balls and return the index of the first EMPTY field indicating that you can move the balls in that direction
    	int firstEMPTYIndex = this.moveBalls(move);
    	int line = move % DIM;  // Get the index of the column or the row, depending on the direction
    	int sameColor = 0;  // The number of adjacent balls that have the same color after moving
    	boolean kept = false; // Do not replace it with EMPTY and 'keep' it for the next comparison 

    	if (firstEMPTYIndex != -1) {
	    	switch (move / DIM) {
	    		//Push to left
	    		case 0:
					// Loop over each field
	    			for (int i = firstEMPTYIndex; i < DIM; i++) {
						// If the current field is not EMPTY, compare it with the top, bottom, left and right neighbours.
	    				if (this.fields[line][i] != Colors.EMPTY) {
							// If the current field is not 'kept' in the last comparion and the top neighour has the same color, 
							// increase sameColor by one and remove the ball (set it to EMPTY)
	    					if (!kept && this.topIsSame(line, i)) {
	    						sameColor++;
	    						this.fields[line - 1][i] = Colors.EMPTY;
	    					}

							// If the bottom neighour has the same color, 
							// increase sameColor by one and remove the ball (set it to EMPTY)
	    					if (!kept && this.bottomIsSame(line, i)) {
	    						sameColor++;
	    						this.fields[line + 1][i] = Colors.EMPTY;
	    					}

							// If the left neighour has the same color, increase sameColor by one and remove the ball (set it to EMPTY)
	    					if (this.leftIsSame(line, i)) {
	    						sameColor++;
	    						this.fields[line][i - 1] = Colors.EMPTY;
	    					}

							// If the ball at the right has the same color, 
							// do not replace it with EMPTY and keep it for the next comparison.
	    					if (this.rightIsSame(line, i)) {
	    						sameColor++;
								if (!kept) {
	    							kept = true;
								}
	    					} else {
								if (kept) {
	    							kept = false;
								}
	    					}

							// If there is a match
	    					if (sameColor > 0) {
	    						if (!kept) {
	    							sameColor++;
	    	    					colorMap.put(this.fields[line][i], colorMap.getOrDefault(this.fields[line][i], 0) + sameColor);
	    	    					sameColor = 0; // reset sameColor for the next matched color
	    	    				}
	    						this.fields[line][i] = Colors.EMPTY; // Set the current field to EMPTY
	    					}
	    				} else {
	    					break;
	    				}
	    			}
	    			break;
	    		//Push to right
	    		case 1:
	    			for (int i = firstEMPTYIndex; i >= 0; i--) {
	    				if (this.fields[line][i] != Colors.EMPTY) {
	    					if (this.topIsSame(line, i)) {
	    						sameColor++;
	    						this.fields[line - 1][i] = Colors.EMPTY;
	    					}

	    					if (this.bottomIsSame(line, i)) {
	    						sameColor++;
	    						this.fields[line + 1][i] = Colors.EMPTY;
	    					}

	    					if (this.leftIsSame(line, i)) {
	    						sameColor++;
	    						if (!kept) {
	    							kept = true;
								}
	    					} else {
	    						if (kept) {
	    							kept = false;
								}
	    					}

	    					if (this.rightIsSame(line, i)) {
	    						sameColor++;
	    						this.fields[line][i + 1] = Colors.EMPTY;
	    					}

	    					if (sameColor > 0) {
	    						if (!kept) {
	    							sameColor++;
									colorMap.put(this.fields[line][i], colorMap.getOrDefault(this.fields[line][i], 0) + sameColor);
	    	    					sameColor = 0;
	    	    				}
	    						this.fields[line][i] = Colors.EMPTY;
	    					}
	    				} else {
	    					break;
	    				}
	    			}
	    			break;
	    		// Push to top
	    		case 2:
	    			for (int i = firstEMPTYIndex; i < DIM; i++) {
	    				if (this.fields[i][line] != Colors.EMPTY) {
	    					if (this.topIsSame(i, line)) {
	    						sameColor++;
	    						this.fields[i - 1][line] = Colors.EMPTY;
	    					}

	    					if (this.bottomIsSame(i, line)) {
	    						sameColor++;
	    						if (!kept) {
	    							kept = true;
								}
	    					} else {
	    						if (kept) {
	    							kept = false;
								}
	    					}

	    					if (this.leftIsSame(i, line)) {
	    						sameColor++;
	    						this.fields[i][line - 1] = Colors.EMPTY;
	    					}

	    					if (this.rightIsSame(i, line)) {
	    						sameColor++;
	    						this.fields[i][line + 1] = Colors.EMPTY;
	    					}

	    					if (sameColor > 0) {
	    						if (!kept) {
	    							sameColor++;
									colorMap.put(this.fields[i][line], colorMap.getOrDefault(this.fields[i][line], 0) + sameColor);
	    	    					sameColor = 0;
	    	    				}
	    						this.fields[i][line] = Colors.EMPTY;
	    					}
	    				} else {
	    					break;
	    				}
	    			}
	    			break;
	    		//Push to bottom
	    		case 3:
	    			for (int i = firstEMPTYIndex; i >= 0; i--) {
	    				if (this.fields[i][line] != Colors.EMPTY) {
	    					if (this.topIsSame(i, line)) {
	    						sameColor++;
	    						if (!kept) {
	    							kept = true;
								}
	    					} else {
	    						if (kept) {
	    							kept = false;
								}
	    					}

	    					if (this.bottomIsSame(i, line)) {
	    						sameColor++;
	    						this.fields[i + 1][line] = Colors.EMPTY;
	    					}

	    					if (this.leftIsSame(i, line)) {
	    						sameColor++;
	    						this.fields[i][line - 1] = Colors.EMPTY;
	    					}

	    					if (this.rightIsSame(i, line)) {
	    						sameColor++;
	    						this.fields[i][line + 1] = Colors.EMPTY;
	    					}

	    					if (sameColor > 0) {
	    						if (!kept) {
	    							sameColor++;
									colorMap.put(this.fields[i][line], colorMap.getOrDefault(this.fields[i][line], 0) + sameColor);
	    	    					sameColor = 0;
	    	    				}
	    						this.fields[i][line] = Colors.EMPTY;
	    					}
	    				} else {
	    					break;
	    				}
	    			}
	    			break;
	    	}
    	}

    	return colorMap;
    }
    
    /**
     * Check if a ball has a neighbor that has the same color
     * @param x position of the ball
     * @param y position of the ball
     * @return true or false
     */
    public boolean hasSame(int x, int y) {
    	return this.topIsSame(x, y) || this.bottomIsSame(x, y) 
    			|| this.leftIsSame(x, y) || this.rightIsSame(x, y);
    }
	
    /**
     * Reset the board by generating a new board.
     */
	public void reset() {
		this.init();
	}
	
	/**
	 * Check whether the game is over or not.
	 * @return true when there are not single move and double move,
	 * or false when there is a single move or double move
	 */
	public boolean gameOver() {
		for (int i = Board.MINMOVE; i <= Board.MAXMOVE; i++) {
			if (isValidSingleMove(i)) {
				return false;
			}
			for (int j = Board.MINMOVE; j <= Board.MAXMOVE; j++) {
				if (this.isValidDoubleMove(i, j)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * @requires MINMOVE <= move && move <= MAXMOVE
	 * @param move a move to be checked
	 * @return true or false
	 */
	public boolean isValidSingleMove(int move) {
		boolean isValid = false;

		if (!moveInRange(move)) {
			return isValid;
		}

		int moveIndex = -1;
		int direction = move / DIM;
		int line = move % DIM;

		Board boardCopy = this.deepCopy();

    	switch (direction) {
    		//Move to left
    		case 0:
    			for (int i = 0; i < DIM; i++) {
    				if (moveIndex != -1 && boardCopy.fields[line][i] != Colors.EMPTY) {
    					boardCopy.fields[line][moveIndex] = boardCopy.fields[line][i];
    					boardCopy.fields[line][i] = Colors.EMPTY;

    					if (boardCopy.hasSame(line, moveIndex)) {
    						isValid = true;
    						break;
    					}

    					moveIndex++;
    				} else if (moveIndex == -1 && boardCopy.fields[line][i] == Colors.EMPTY) {
    					moveIndex = i;
    				}
    			}
    			break;
    		//Move to right
    		case 1:
    			for (int i = DIM - 1; i >= 0; i--) {
    				if (moveIndex != -1 && boardCopy.fields[line][i] != Colors.EMPTY) {
    					boardCopy.fields[line][moveIndex] = boardCopy.fields[line][i];
    					boardCopy.fields[line][i] = Colors.EMPTY;

    					if (boardCopy.hasSame(line, moveIndex)) {
    						isValid = true;
    						break;
    					}

    					moveIndex--;
    				} else if (moveIndex == -1 && boardCopy.fields[line][i] == Colors.EMPTY) {
    					moveIndex = i;
    				}
    			}
    			break;
    		//Move to top
    		case 2:
    			for (int i = 0; i < DIM; i++) {
    				if (moveIndex != -1 && boardCopy.fields[i][line] != Colors.EMPTY) {
    					boardCopy.fields[moveIndex][line] = boardCopy.fields[i][line];
    					boardCopy.fields[i][line] = Colors.EMPTY;

    					if (boardCopy.hasSame(moveIndex, line)) {
    						isValid = true;
    						break;
    					}

    					moveIndex++;
    				} else if (moveIndex == -1 && boardCopy.fields[i][line] == Colors.EMPTY) {
    					moveIndex = i;
    				}
    			}
    			break;
    		//Move to bottom
    		case 3:
    			for (int i = DIM - 1; i >= 0; i--) {
    				if (moveIndex != -1 && boardCopy.fields[i][line] != Colors.EMPTY) {
    					boardCopy.fields[moveIndex][line] = boardCopy.fields[i][line];
    					boardCopy.fields[i][line] = Colors.EMPTY;

    					if (boardCopy.hasSame(moveIndex, line)) {
    						isValid = true;
    						break;
    					}

    					moveIndex--;
    				} else if (moveIndex == -1 && boardCopy.fields[i][line] == Colors.EMPTY) {
    					moveIndex = i;
    				}
    			}
    			break;
    	}
    	
		return isValid;
	}

	/**
	 * Check the double move.
	 * @requires MINMOVE <= move1 && move1 <= MAXMOVE
	 * * @requires MINMOVE <= move2 && move2 <= MAXMOVE
	 * @param move1 the first move must be invalid
	 * @param move2 the second move
	 * @return the validity of the double move
	 */
	public boolean isValidDoubleMove(int move1, int move2) {
    	for (int i = Board.MINMOVE; i < Board.MAXMOVE; i++) {
			if (this.isValidSingleMove(i)) {
				return false;
			}
		}

		Board boardCopy = this.deepCopy();
		boardCopy.moveBalls(move1);

		return boardCopy.isValidSingleMove(move2);
    }
	
	/**
	 * Move the balls in the specified direction
	 * @param move The move direction: 0 for left, 1 for right, 2 for top, 3 for bottom.
	 * @return The index of the first EMPTY field indicating that you can move the balls in that direction
	 * If firstMoveIndex is -1, it means that you cannot move ball in that direction.
     * For example, there is one row 0~0~1~2~3~6~4 in the board and you want to move the balls to the right, firstMoveIndex will be -1.
	 * If you want to move the balls to the left, firstMoveIndex will be 5.
	 */
	public int moveBalls(int move) {
		int firstEMPTYIndex = -1;  // The index of the first EMPTY field that you can move the ball to in a specified direction
		int emptyIndex = -1; // Used for moving all the balls in a specified direction
		int direction = move / DIM;  // Get the move direction
		int line = move % DIM; // Get the index of the column or the row, depending on the direction
		Boolean hasMove = false; // Flag indicating if there is any move

    	switch (direction) {
    		//Move to left
    		case 0:
    			for (int i = 0; i < DIM; i++) {
					// If there is an EMPTY field and there is a ball in one of the fields
    				if (emptyIndex != -1 && this.fields[line][i] != Colors.EMPTY) {
						if (!hasMove) {
							hasMove = true;
						}
    					
						// Move the ball to the EMPTY field
    					this.fields[line][emptyIndex] = this.fields[line][i];
						// Set the field to EMPTY after moving
    					this.fields[line][i] = Colors.EMPTY;
						// Increase the index of the EMPTY field by 1
    					emptyIndex++;
    				} else if (emptyIndex == -1 && this.fields[line][i] == Colors.EMPTY) {
    					firstEMPTYIndex = i;
    					emptyIndex = i;
    				}
    			}
    			break;
    		//Move to right
    		case 1:
    			for (int i = DIM - 1; i >= 0; i--) {
    				if (emptyIndex != -1 && this.fields[line][i] != Colors.EMPTY) {
    					if (!hasMove) {
							hasMove = true;
						}

    					this.fields[line][emptyIndex] = this.fields[line][i];
    					this.fields[line][i] = Colors.EMPTY;
						// Decrease the index of the EMPTY field by 1
    					emptyIndex--;
    				} else if (emptyIndex == -1 && this.fields[line][i] == Colors.EMPTY) {
    					firstEMPTYIndex = i;
    					emptyIndex = i;
    				}
    			}
    			break;
    		//Move to top
    		case 2:
    			for (int i = 0; i < DIM; i++) {
    				if (emptyIndex != -1 && this.fields[i][line] != Colors.EMPTY) {
    					if (!hasMove) {
							hasMove = true;
						}

    					this.fields[emptyIndex][line] = this.fields[i][line];
    					this.fields[i][line] = Colors.EMPTY;
    					emptyIndex++;
    				} else if (emptyIndex == -1 && this.fields[i][line] == Colors.EMPTY) {
    					firstEMPTYIndex = i;
    					emptyIndex = i;
    				}
    			}
    			break;
    		//Move to bottom
    		case 3:
    			for (int i = DIM - 1; i >= 0; i--) {
    				if (emptyIndex != -1 && this.fields[i][line] != Colors.EMPTY) {
    					if (!hasMove) {
							hasMove = true;
						}
						
    					this.fields[emptyIndex][line] = this.fields[i][line];
    					this.fields[i][line] = Colors.EMPTY;
    					emptyIndex--;
    				} else if (emptyIndex == -1 && this.fields[i][line] == Colors.EMPTY) {
    					firstEMPTYIndex = i;
    					emptyIndex = i;
    				}
    			}
    			break;
    	}
    	
    	if (!hasMove && firstEMPTYIndex != -1) {
    		firstEMPTYIndex = -1;
    	}
    	
    	return firstEMPTYIndex;
	}
	
	/*
	 * @requires (0 <= x && x <= DIM) && (0 <= y && y <= DIM)
	 * @returns true if this.fields[x][y] == this.fields[x - 1][y], else false
	 */
	public boolean topIsSame(int x, int y) {
		return x > 0 ? (this.fields[x][y] == this.fields[x - 1][y]) : false;
	} 
	
	/*
	 * @requires (0 <= x && x <= DIM) && (0 <= y && y <= DIM)
	 * @returns true if this.fields[x][y] == this.fields[x + 1][y], else false
	 */
	public boolean bottomIsSame(int x, int y) {
		return x < (DIM - 1) ? (this.fields[x][y] == this.fields[x + 1][y]) : false;
	} 
	
	/*
	 * @requires (0 <= x && x <= DIM) && (0 <= y && y <= DIM)
	 * @returns true if this.fields[x][y] == this.fields[x][y - 1], else false
	 */
	public boolean leftIsSame(int x, int y) {
		return y > 0 ? (this.fields[x][y] == this.fields[x][y - 1]) : false;
	} 
	
	/*
	 * @requires (0 <= x && x <= DIM) && (0 <= y && y <= DIM)
	 * @returns true if this.fields[x][y] == this.fields[x][y + 1], else false
	 */
	public boolean rightIsSame(int x, int y) {
		return y < DIM - 1 ? (this.fields[x][y] == this.fields[x][y + 1]) : false;
	} 
	
	/**
     * Returns a String representation of this board. 
     * @return the game situation as String
     */
    @Override
	public String toString() {
        String s = "      21 22 23 24 25 26 27\n" +
        		   "   *************************\n";

        for (int i = 0; i < DIM; i++) {
            String row = String.format("%-2s * ", i + 7);
            for (int j = 0; j < DIM; j++) {
            	row += " " + this.fields[i][j].ordinal() + " ";
            }
            s += row + " * " + i + "\n";
        }

     	s += "   *************************\n" +
     	     "      14 15 16 17 18 19 20\n";

        return s;
    }
    
    /**
     * @return a string of an initial board
     */
    public String toProtocolString() {
        String s = "";

        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
            	s += Protocols.TILDE + this.fields[i][j].ordinal();
            }
        }

        return s;
    }
    
    public boolean moveInRange(int move) {
    	return MINMOVE <= move && move <= MAXMOVE;
    }
    
    public int convertMoveStr(String moveStr) {
    	int move = -1;

    	if (moveStr.matches("[0-9]+")) {
    		move = Integer.parseInt(moveStr);
    	}

    	return move;
    }
    
    public static void main(String[] args) {
    	Board board = new Board();
    	System.out.println(board.getFields());
    	System.out.println(board.toString());
    }
    
}
