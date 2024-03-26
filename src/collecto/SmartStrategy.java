package collecto;

import java.util.HashMap;
import java.util.Map;

import utils.Colors;

public class SmartStrategy implements Strategy {
	
	private Player player;
	
	public SmartStrategy(Player player) {
		this.player = player;
	}
	
	@Override
	public String getName() {
		return "Smart";
	}
	/**
	 * AI player
	 * @param name the name of the AI player
	 */
	
	/**
	 * determine a best move comparing with the predictive result of the opponent.
	 * @param the board
	 */
	public String determineMove(Board board) {
		
		Map<String, Map<Colors, Integer>> possibleMoves = this.getPossibleMoves(board);
		
		String move = "-1";
		int totalBall = 0;
		int curScore = player.getScore();
		int moreThan6 = 0;
		int oppIncScore = 0;
		
		Map<Colors, Integer> curBallMap;
		for (String m : possibleMoves.keySet()) {
			int total = 0;
			int mt6 = 0;
			curBallMap = new HashMap<>(player.getBallMap());
			for (Colors color : possibleMoves.get(m).keySet()) {
				int amount = possibleMoves.get(m).get(color);
				total += amount;
	    		int current = curBallMap.getOrDefault(color, 0);
	    		if (current == 6 && current + amount == 8) {
	    			mt6++;
	    		}
	    		curBallMap.put(color, current + amount);
	    	}
			int score = 0;
			for (Integer val : curBallMap.values()) {
	    		score += val / 3;
	    	}
			
			Map<String, Integer> oppScoreInfo = this.getOppScoreInfo(board, m);
			if (totalBall == 0 
					|| total > totalBall && score > curScore && oppScoreInfo.get("incScores") == 0
					|| total > totalBall && score == curScore && oppScoreInfo.get("incScores") == 0
					|| total > totalBall && score - curScore > oppScoreInfo.get("incScores")
					|| total > totalBall && score > curScore 
					|| total == totalBall && score == curScore 
								&& oppIncScore > oppScoreInfo.get("incScores")
					|| total == totalBall && score == curScore
							 && oppIncScore == oppScoreInfo.get("incScores") && mt6 < moreThan6 
					|| total == totalBall && score == curScore 
							 && oppIncScore == oppScoreInfo.get("incScores") && mt6 == moreThan6
					|| total > totalBall) {
				move = m;
				curScore = score;
				totalBall = total;
				moreThan6 = mt6;
			    oppIncScore = oppScoreInfo.get("incScores");
			}
		}
		return move;
		
	}
	
	/**
	 * @param board
	 * @param move
	 */
	public Map<String, Integer> getOppScoreInfo(Board board, String move) {
		//Board boardCopy = board.deepCopy();
		Map<String, Map<Colors, Integer>> oppPossibleMoves = this.predictMovesForOpp(board, move);
		Map<String, Integer> scoreInfo = new HashMap<>();
		if (oppPossibleMoves.isEmpty()) {
			scoreInfo.put("incScores", 0);
			scoreInfo.put("totalBall", 0);
			scoreInfo.put("moreThan6", 0);
		} else {
			Map<Colors, Integer> oppBallMap = this.getOppBallMap(board);
			int totalBall = 0;
			int curScore = this.calScore(oppBallMap);
			int moreThan6 = 0;
			int increasedScores = 0;
			Map<Colors, Integer> curBallMap;
			for (String key : oppPossibleMoves.keySet()) {
				int total = 0;
				int mt6 = 0;
				curBallMap = new HashMap<>(oppBallMap);
				for (Colors color : oppPossibleMoves.get(key).keySet()) {
					int amount = oppPossibleMoves.get(key).get(color);
					total += amount;
		    		int current = curBallMap.getOrDefault(color, 0);
		    		if (current == 6 && current + amount == 8) {
		    			mt6++;
		    		}
		    		curBallMap.put(color, current + amount);
		    	}
				int score = 0;
				for (Integer val : curBallMap.values()) {
		    		score += val / 3;
		    	}
				
				if (increasedScores == 0 || increasedScores < score - curScore 
						|| (increasedScores == score - curScore && mt6 < moreThan6)
						|| (increasedScores == score - curScore && 
						 				mt6 == moreThan6 && total < totalBall)) {
					increasedScores = score - curScore;
					scoreInfo.put("incScores", increasedScores);
					totalBall = total;
					scoreInfo.put("totalBall", totalBall);
					moreThan6 = mt6;
					scoreInfo.put("moreThan6", moreThan6);
				}
			}
		}
		return scoreInfo;
	}
	
	/**
	 * Assume after AI moves, predict how moves the opponent can get.
	 * @param board
	 * @param move
	 * @return a hash map containing all possible moves and the possible result
	 */
	private Map<String, Map<Colors, Integer>> predictMovesForOpp(Board board, String move) {
		Board boardCopy = board.deepCopy();
		String[] moveArr = move.split("~");
        if (moveArr.length == 1) {
        	boardCopy.setMove(Integer.parseInt(moveArr[0]));
        } else if (moveArr.length == 2) {
        	boardCopy.moveBalls(Integer.parseInt(moveArr[0]));
        	boardCopy.setMove(Integer.parseInt(moveArr[1]));
        }
		Map<String, Map<Colors, Integer>> predictiveMoves = this.getPossibleMoves(boardCopy);
		return predictiveMoves;
	}
	
	/**
	 * Get how many balls of each color the opponent gets.
	 * @param board
	 * @return a map 
	 */
	private Map<Colors, Integer> getOppBallMap(Board board) {
		
		Map<Colors, Integer> oppBallMap = new HashMap<>();
		Map<Colors, Integer> curBallMap = new HashMap<Colors, Integer>(player.getBallMap());
		Colors[][] fields = board.getFields();
		for (int i = 0; i < Board.DIM; i++) {
			for (int j = 0; j < Board.DIM; j++) {
				Colors color = fields[i][j];
				if (color != Colors.EMPTY) {
					int current = curBallMap.getOrDefault(fields[i][j], 0);
		    		curBallMap.put(color, current + 1);
				}
			}
		}
		for (Colors color : Colors.values()) {
			if (color != Colors.EMPTY) {
				int current = curBallMap.getOrDefault(color, 0);
				if (Board.EACH - current > 0) {
					oppBallMap.put(color, Board.EACH - current);
				}
			}
		}
		return oppBallMap;
		
	}
	
	/**
	 * Calculate the score, get 1 point for every 3 balls of each color.
	 * @param ballMap
	 * @return the score
	 */
	private int calScore(Map<Colors, Integer> ballMap) {
		int score = 0;
    	for (Integer val : ballMap.values()) {
    		score += val / 3;
    	}
    	return score;
	}

}
