package collecto;

public class ComputerPlayer extends Player {
	
	private String name;
    private Strategy strategy;
	
	public ComputerPlayer(String name, int type) {
		super();
		if (type == 1) {
			this.strategy = new SmartStrategy(this);
		} else if (type == 2) {
			this.strategy = new NaiveStrategy();
		}
		this.name = name + "-AI";
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}
	
	public Strategy getStrategy() {
		return this.strategy;
	}

	@Override
	public String determineMove(Board board) {
		return this.strategy.determineMove(board);
	}
	
}
