package collecto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.Colors;
import utils.Protocols;

public class Board {
	
	public static final int DIM = 7;
	public static final int EACH = 8;
	public static final int MINMOVE = 0;
	public static final int MAXMOVE = 27;
	private Colors[][] fields;
	
	/**
	 * initialize the board in the constructor.
	 */
	public Board() {
		this.init();
    }
	
	public Board(Colors[][] fields) {
		for (var i = 0; i < DIM; i++) {
    		for (var j = 0; j < DIM; j++) {
    			this.fields[i][j] = fields[i][j];
    		}
    	}
    }
	
	/**
	 * @return fields
	 */
	public Colors[][] getFields() {
		return this.fields;
	}
	
	/**
	 * The board is a 2D array (DIM * DIM fields).
	 * the most middle field is EMPTY
	 * Apply Fisher-Yates algorithm to initialize the board
	 * @ensures the board is a valid board
	 * @returns the valid board
	 */
	public void init() {
		//Put 48 balls in an array list
		List<Colors> arr = new ArrayList<>();
		for (int i = 0; i < EACH; i++) {
			for (Colors color : Colors.values()) {
				if (color == Colors.EMPTY) {
					continue;
				}
				arr.add(color);
			}
		}
		fields = new Colors[DIM][DIM];
		fields[DIM / 2][DIM / 2] = Colors.EMPTY;
		int total = DIM * DIM;
		for (int i = 0; i < total; i++) {
			int row = i / DIM,
				col = i % DIM;
			if (row == DIM / 2 && col == DIM / 2) {
				continue;
			}
			int index = (int) (Math.random() * arr.size());
			if (row == 0) {
				if (col != 0) {
					Colors left = fields[row][col - 1];
					while (arr.get(index) == left) {
						index = (int) (Math.random() * arr.size());
					}
				}
			} else if (row > 0) {
				if (col == 0) {
					Colors top = fields[row - 1][col];
					if (row == DIM - 1) {
						Set<Colors> set = new HashSet<>(arr);
						if (set.size() == 1 && set.contains(top)) {
							break;
						}
					}
					while (arr.get(index) == top) {
						index = (int) (Math.random() * arr.size());
					}
				} else {
					Colors top = fields[row - 1][col];
    				Colors left = fields[row][col - 1];
    				if (row == DIM - 1 || row == DIM - 2) {
    					Set<Colors> set = new HashSet<>(arr);
	    				if (set.size() == 2 && set.contains(top) && set.contains(left)
	    						|| set.size() == 1 && (set.contains(top) || set.contains(left))) {
	    					break;
	    				}
    				}
					while (arr.get(index) == top || arr.get(index) == left) {
    					index = (int) (Math.random() * arr.size());
    				}
				}
			}
			fields[row][col] = arr.get(index);
			arr.remove(index);
		}
		if (arr.size() != 0) {
			this.init();
		}
	}
	
	/**
     * Creates a deep copy of this fields.
     * @ensures the result is a new object, so not this object
     * @ensures the values of all fields of the copy match the ones of this Board
     */
    public Board deepCopy() {
    	Board board = new Board();
    	for (var i = 0; i < DIM; i++) {
    		for (var j = 0; j < DIM; j++) {
    			board.fields[i][j] = fields[i][j];
    		}
    	}
        return board;
    }
    
    /**
     * Sets the move and the move results in adjacent colors.
     * @param move
     * @return a map with the colors and the amount
     */
    public Map<Colors, Integer> setMove(int move) {
    	Map<Colors, Integer> ballMap = new HashMap<>();
    	int firstMoveIndex = this.moveBalls(move),
    		line = move % DIM,
    		sameBall = 0;
    	boolean keep;
    	if (firstMoveIndex != -1) {
	    	switch (move / DIM) {
	    		//Push to left
	    		case 0:
	    			for (int i = firstMoveIndex; i < DIM; i++) {
	    				if (this.fields[line][i] != Colors.EMPTY) {
	    					if (this.topIsSame(line, i)) {
	    						sameBall++;
	    						this.fields[line - 1][i] = Colors.EMPTY;
	    					}
	    					if (this.bottomIsSame(line, i)) {
	    						sameBall++;
	    						this.fields[line + 1][i] = Colors.EMPTY;
	    					}
	    					if (this.leftIsSame(line, i)) {
	    						sameBall++;
	    						this.fields[line][i - 1] = Colors.EMPTY;
	    					}
	    					if (this.rightIsSame(line, i)) {
	    						sameBall++;
	    						keep = true;
	    					} else {
	    						keep = false;
	    					}
	    					if (sameBall > 0) {
	    						if (!keep) {
	    							sameBall++;
	    	    					ballMap.put(this.fields[line][i], sameBall);
	    	    					sameBall = 0;
	    	    				}
	    						this.fields[line][i] = Colors.EMPTY;
	    					}
	    				} else {
	    					break;
	    				}
	    			}
	    			break;
	    		//Push to right
	    		case 1:
	    			for (int i = firstMoveIndex; i >= 0; i--) {
	    				if (this.fields[line][i] != Colors.EMPTY) {
	    					if (this.topIsSame(line, i)) {
	    						sameBall++;
	    						this.fields[line - 1][i] = Colors.EMPTY;
	    					}
	    					if (this.bottomIsSame(line, i)) {
	    						sameBall++;
	    						this.fields[line + 1][i] = Colors.EMPTY;
	    					}
	    					if (this.leftIsSame(line, i)) {
	    						sameBall++;
	    						keep = true;
	    					} else {
	    						keep = false;
	    					}
	    					if (this.rightIsSame(line, i)) {
	    						sameBall++;
	    						this.fields[line][i + 1] = Colors.EMPTY;
	    					}
	    					if (sameBall > 0) {
	    						if (!keep) {
	    							sameBall++;
	    	    					ballMap.put(this.fields[line][i], sameBall);
	    	    					sameBall = 0;
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
	    			for (int i = firstMoveIndex; i < DIM; i++) {
	    				if (this.fields[i][line] != Colors.EMPTY) {
	    					if (this.topIsSame(i, line)) {
	    						sameBall++;
	    						this.fields[i - 1][line] = Colors.EMPTY;
	    					}
	    					if (this.bottomIsSame(i, line)) {
	    						sameBall++;
	    						keep = true;
	    					} else {
	    						keep = false;
	    					}
	    					if (this.leftIsSame(i, line)) {
	    						sameBall++;
	    						this.fields[i][line - 1] = Colors.EMPTY;
	    					}
	    					if (this.rightIsSame(i, line)) {
	    						sameBall++;
	    						this.fields[i][line + 1] = Colors.EMPTY;
	    					}
	    					if (sameBall > 0) {
	    						if (!keep) {
	    							sameBall++;
	    	    					ballMap.put(this.fields[i][line], sameBall);
	    	    					sameBall = 0;
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
	    			for (int i = firstMoveIndex; i >= 0; i--) {
	    				if (this.fields[i][line] != Colors.EMPTY) {
	    					if (this.topIsSame(i, line)) {
	    						sameBall++;
	    						keep = true;
	    					} else {
	    						keep = false;
	    					}
	    					if (this.bottomIsSame(i, line)) {
	    						sameBall++;
	    						this.fields[i + 1][line] = Colors.EMPTY;
	    					}
	    					if (this.leftIsSame(i, line)) {
	    						sameBall++;
	    						this.fields[i][line - 1] = Colors.EMPTY;
	    					}
	    					if (this.rightIsSame(i, line)) {
	    						sameBall++;
	    						this.fields[i][line + 1] = Colors.EMPTY;
	    					}
	    					if (sameBall > 0) {
	    						if (!keep) {
	    							sameBall++;
	    	    					ballMap.put(this.fields[i][line], sameBall);
	    	    					sameBall = 0;
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
    	return ballMap;
    }
    
    /**
     * Check adjacent color.
     * @param x the row
     * @param y the column
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
		boolean res = true;
		for (int i = Board.MINMOVE; i < Board.MAXMOVE; i++) {
			if (isValidSingleMove(i)) {
				res = false;
				break;
			}
		}
		label: for (int i = Board.MINMOVE; i <= Board.MAXMOVE; i++) {
				for (int j = Board.MINMOVE; j <= Board.MAXMOVE; j++) {
					if (this.isValidDoubleMove(i, j)) {
						res = false;
						break label;
					}
				}
			}
		return res;
	}
	
	/**
	 * @requires MINMOVE <= move && move <= MAXMOVE
	 * @param move a move to be checked
	 * @return true or false
	 */
	public boolean isValidSingleMove(int move) {
		boolean hasSame = false;
		if (!moveInRange(move)) {
			return hasSame;
		}
		int moveIndex = -1,
			direction = move / DIM,
			line = move % DIM;
		Board boardCopy = this.deepCopy();
    	switch (direction) {
    		//Move to left
    		case 0:
    			for (int i = 0; i < DIM; i++) {
    				if (moveIndex != -1 && boardCopy.fields[line][i] != Colors.EMPTY) {
    					boardCopy.fields[line][moveIndex] = boardCopy.fields[line][i];
    					boardCopy.fields[line][i] = Colors.EMPTY;
    					if (boardCopy.hasSame(line, moveIndex)) {
    						hasSame = true;
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
    						hasSame = true;
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
    						hasSame = true;
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
    						hasSame = true;
    						break;
    					}
    					moveIndex--;
    				} else if (moveIndex == -1 && boardCopy.fields[i][line] == Colors.EMPTY) {
    					moveIndex = i;
    				}
    			}
    			break;
    	}
    	
		return hasSame;
	}
	/**
	 * Check the double move.
	 * @requires MINMOVE <= move1 && move1 <= MAXMOVE
	 * * @requires MINMOVE <= move2 && move2 <= MAXMOVE
	 * @param move1 the first move must be invalid
	 * @param move2 the second
	 * @return the validity of the double move
	 */
	public boolean isValidDoubleMove(int move1, int move2) {
    	boolean res = true;
    	for (int i = Board.MINMOVE; i < Board.MAXMOVE; i++) {
			if (this.isValidSingleMove(i)) {
				res = false;
				break;
			}
		}
    	if (res) {
			Board boardCopy = this.deepCopy();
			boardCopy.moveBalls(move1);
			res = boardCopy.isValidSingleMove(move2);
    	}
    	return res;
    }
	
	/**
	 * Move the balls.
	 * @param move
	 * @return the index of the first empty field
	 * firstMoveIndex == -1 means that you cannot move ball in the direction.
     * For example, 1~2~3~6~4~0~0, firstMoveIndex will be -1
     * 1~2~0~6~4~0~0, firstMoveIndex will be 2
	 */
	public int moveBalls(int move) {
		int firstMoveIndex = -1,
			emptyIndex = -1,
			direction = move / DIM,
			line = move % DIM;
		Boolean hasMove = false;
    	switch (direction) {
    		//Move to left
    		case 0:
    			for (int i = 0; i < DIM; i++) {
    				if (emptyIndex != -1 && this.fields[line][i] != Colors.EMPTY) {
    					hasMove = true;
    					this.fields[line][emptyIndex] = this.fields[line][i];
    					this.fields[line][i] = Colors.EMPTY;
    					emptyIndex++;
    				} else if (emptyIndex == -1 && this.fields[line][i] == Colors.EMPTY) {
    					firstMoveIndex = i;
    					emptyIndex = i;
    				}
    			}
    			break;
    		//Move to right
    		case 1:
    			for (int i = DIM - 1; i >= 0; i--) {
    				if (emptyIndex != -1 && this.fields[line][i] != Colors.EMPTY) {
    					hasMove = true;
    					this.fields[line][emptyIndex] = this.fields[line][i];
    					this.fields[line][i] = Colors.EMPTY;
    					emptyIndex--;
    				} else if (emptyIndex == -1 && this.fields[line][i] == Colors.EMPTY) {
    					firstMoveIndex = i;
    					emptyIndex = i;
    				}
    			}
    			break;
    		//Move to top
    		case 2:
    			for (int i = 0; i < DIM; i++) {
    				if (emptyIndex != -1 && this.fields[i][line] != Colors.EMPTY) {
    					hasMove = true;
    					this.fields[emptyIndex][line] = this.fields[i][line];
    					this.fields[i][line] = Colors.EMPTY;
    					emptyIndex++;
    				} else if (emptyIndex == -1 && this.fields[i][line] == Colors.EMPTY) {
    					firstMoveIndex = i;
    					emptyIndex = i;
    				}
    			}
    			break;
    		//Move to bottom
    		case 3:
    			for (int i = DIM - 1; i >= 0; i--) {
    				if (emptyIndex != -1 && this.fields[i][line] != Colors.EMPTY) {
    					hasMove = true;
    					this.fields[emptyIndex][line] = this.fields[i][line];
    					this.fields[i][line] = Colors.EMPTY;
    					emptyIndex--;
    				} else if (emptyIndex == -1 && this.fields[i][line] == Colors.EMPTY) {
    					firstMoveIndex = i;
    					emptyIndex = i;
    				}
    			}
    			break;
    	}
    	
    	if (!hasMove && firstMoveIndex != -1) {
    		firstMoveIndex = -1;
    	}
    	
    	return firstMoveIndex;
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
