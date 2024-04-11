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
	 * Has a higher preference for taking most balls
	 * @param the board
	 */
	public String determineMove(Board board) {
		Map<String, Map<Colors, Integer>> possibleMoves = this.getPossibleMoves(board);
		String move = "-1";
		int totalBall = 0;

		for (String m : possibleMoves.keySet()) {
			int total = 0;
			for (Colors color : possibleMoves.get(m).keySet()) {
				int amount = possibleMoves.get(m).get(color);
				total += amount;
	    	}
			if (totalBall == 0 || total > totalBall) {
				move = m;
				totalBall = total;
			}
		}
		
		return move;
	}

}
