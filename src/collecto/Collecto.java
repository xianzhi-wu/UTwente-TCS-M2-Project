package collecto;

public class Collecto {
	public static void main(String[] args) {
		Player cp1 = new ComputerPlayer("Smart", 1);
    	Player cp2 = new ComputerPlayer("Naive", 2);
    	Game game = new Game(cp1, cp2);
    	game.start();
    }
}