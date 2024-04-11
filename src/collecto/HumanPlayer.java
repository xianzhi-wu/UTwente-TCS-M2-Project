package collecto;

import utils.TextIO;

public class HumanPlayer extends Player {

	public HumanPlayer(String name) {
		super(name);
	}
	
	//@Override
	public String determineMove(Board board) {
		String prompt = "> " + getName() + ", what is your choice? ";
        System.out.println(prompt);

        String moveStr = TextIO.getlnWord();
        String[] moveArr = moveStr.split("~");
        
        boolean valid = false;

        if (moveArr.length == 1) {
        	valid = board.isValidSingleMove(Integer.parseInt(moveArr[0]));
        } else if (moveArr.length == 2) {
        	valid = board.isValidDoubleMove(Integer.parseInt(moveArr[0]), 
        			Integer.parseInt(moveArr[1]));
        }

        while (!valid) {
        	System.out.println("ERROR: " + moveStr + " is not a valid move.");
            System.out.println(prompt);
            moveStr = TextIO.getln();
            moveArr = moveStr.split("~");
            if (moveArr.length == 1) {
            	valid = board.isValidSingleMove(Integer.parseInt(moveArr[0]));
            } else if (moveArr.length == 2) {
            	valid = board.isValidDoubleMove(Integer.parseInt(moveArr[0]), 
            			Integer.parseInt(moveArr[1]));
            }
        }
        return moveStr;
	}

}
