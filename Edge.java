package is.cs.ltsa_aco;

public class Edge {
	private double phrm; //pheromone val
	private boolean isGoalPh; //check goalPheromone
	
	public Edge(){
		this.phrm  = 0.0;
		
	}
	public Edge(double val){
		this.phrm = val;
	}
	
	public double getPhrm(){
		return phrm;
	}
	public void setPhrm(double val){
		this.phrm = val;
	}
	public void chIsGoalPh(){
		isGoalPh = !isGoalPh;
	}
	public boolean getIsGph(){
		return isGoalPh;
	}
	
	@Override
	public String toString(){
		return "\n";
	}

}
