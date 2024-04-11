package collecto;

import java.util.Map;

import utils.Colors;

public class NaiveStrategy implements Strategy {

	@Override
	public String getName() {
		return "Naive";
	}

	@Override
	/**
	 * Has a higher preference for getting the most balls
	 * @param the board
	 */
	public String determineMove(Board board) {
		Map<String, Map<Colors, Integer>> possibleMoves = this.getPossibleMoves(board);
		String move = "-1";
		int maxBalls = 0;

		for (String m : possibleMoves.keySet()) {
			int num = 0;
			
			for (Colors color : possibleMoves.get(m).keySet()) {
				num += possibleMoves.get(m).get(color);
	    	}

			if (maxBalls == 0 || num > maxBalls) {
				move = m;
				maxBalls = num;
			}
		}
		
		return move;
	}

}
