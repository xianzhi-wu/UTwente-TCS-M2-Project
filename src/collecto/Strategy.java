package collecto;

import java.util.HashMap;
import java.util.Map;

import utils.Colors;

public interface Strategy {
	
	public String getName();
	public String determineMove(Board board);
	
	/**
	 * Get all the possible Moves.
	 * @param board
	 * @return
	 */
	default Map<String, Map<Colors, Integer>> getPossibleMoves(Board board) {
		Board boardCopy;
		Map<String, Map<Colors, Integer>> possibleMoves = new HashMap<>();
		
		for (int i = Board.MINMOVE; i <= Board.MAXMOVE; i++) {
			if (board.isValidSingleMove(i)) {
				boardCopy = board.deepCopy();
				possibleMoves.put("" + i, boardCopy.setMove(i));
			} else {
				continue;
			}
		} 
	
		if (possibleMoves.isEmpty()) {
			for (int i = Board.MINMOVE; i <= Board.MAXMOVE; i++) {
				for (int j = Board.MINMOVE; j <= Board.MAXMOVE; j++) {
					if (board.isValidDoubleMove(i, j)) {
						boardCopy = board.deepCopy();
						boardCopy.moveBalls(i);
						possibleMoves.put(i + "~" + j, boardCopy.setMove(j));
					}
				}
			}
		}
		
		return possibleMoves;
	}
}
