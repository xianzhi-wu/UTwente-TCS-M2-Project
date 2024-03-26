package collecto;

public class Collecto {
	public static void main(String[] args) {
		Player hp1 = new ComputerPlayer("Smart", 1);
    	Player hp2 = new ComputerPlayer("Naive", 2);
    	Game game = new Game(hp1, hp2);
    	game.start();
    }
}
