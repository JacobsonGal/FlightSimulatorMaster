package model.server.network;

public interface Solver<Problem,Solution> {
	public Solution Solve(Problem p);
}
