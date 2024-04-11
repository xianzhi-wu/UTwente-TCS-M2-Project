package collecto;

public class Collecto {
	public static void main(String[] args) {
		
		Player cp1 = new ComputerPlayer("xianz", 1);
    	Player cp2 = new ComputerPlayer("test", 2);
    	Game game = new Game(cp1, cp2);

		//Player hp = new HumanPlayer("human");
    	//Player cp = new ComputerPlayer("Naive", 2);
    	//Game game = new Game(hp, cp);

    	game.start();
    }
}