package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import collecto.Board;
import utils.Colors;

class BoardTest {
	
	/** Test variable for a <tt>Board</tt> object. */
    private Board board;
    private Colors[][] fields = {
    		{Colors.YELLOW, Colors.PURPLE, Colors.GREEN, Colors.BLUE,Colors.PURPLE, Colors.GREEN, Colors.BLUE},
    		{Colors.PURPLE, Colors.RED, Colors.BLUE, Colors.RED, Colors.ORANGE, Colors.YELLOW, Colors.RED},
    		{Colors.RED, Colors.YELLOW, Colors.ORANGE, Colors.YELLOW,  Colors.BLUE,Colors.ORANGE,  Colors.PURPLE},
    		{Colors.GREEN, Colors.RED,Colors.GREEN, Colors.EMPTY,  Colors.YELLOW, Colors.BLUE, Colors.YELLOW},
    		{Colors.RED, Colors.YELLOW, Colors.ORANGE,  Colors.PURPLE, Colors.BLUE, Colors.YELLOW,Colors.GREEN},
    		{Colors.BLUE, Colors.ORANGE, Colors.GREEN, Colors.ORANGE, Colors.PURPLE, Colors.GREEN, Colors.PURPLE},
    		{Colors.PURPLE  ,Colors.RED, Colors.ORANGE,  Colors.GREEN, Colors.BLUE, Colors.RED, Colors.ORANGE}
    };

    
    /**
     * Sets the instance variable <tt>Board</tt> to a well-defined initial value.
     * All test methods should be preceded by a call to this method.
     */
    @BeforeEach
    public void setUp() {
        board = new Board();
    }

    @Test
    public void testIsValidBoard() {
    	assertFalse(board.gameOver());
    }
    
    @Test
    public void testIsValidSingleMove() {
    	board = new Board(fields);
    	assertTrue(board.isValidSingleMove(3));
    }
    
    @Test
    public void testIsValidDoubleMove() {
    	board = new Board(fields);
    	assertFalse(board.isValidDoubleMove(3, 4));
    }
    
    @Test
    public void testTopIsSame() {
    	board = new Board(fields);
    	assertFalse(board.topIsSame(3, 4));
    }
    
    @Test
    public void testBottomIsSame() {
    	board = new Board(fields);
    	assertFalse(board.bottomIsSame(3, 4));
    }
    
    @Test
    public void testLeftIsSame() {
    	board = new Board(fields);
    	assertFalse(board.leftIsSame(3, 4));
    }
    
    @Test
    public void testRightIsSame() {
    	board = new Board(fields);
    	assertFalse(board.rightIsSame(3, 4));
    }
    
    @Test
    public void testMoveBalls() {
    	board = new Board(fields);
    	assertEquals(3, board.moveBalls(3));
    }
    
    @Test
    public void testSetMove() {
    	board = new Board(fields);
    	Map<Colors, Integer> ballMap = new HashMap<>();
    	ballMap.put(Colors.YELLOW, 2);
    	ballMap.put(Colors.BLUE, 3);
    	assertEquals(ballMap, board.setMove(3));
    }
	
}
