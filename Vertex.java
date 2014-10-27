package is.cs.ltsa_aco;

public class Vertex {
	private int num;
	private boolean isInit = false;
	private	boolean isAccept = false;
	
	public Vertex(int num){
		this.num = num;
	}
	
	public boolean isInit(){
		return isInit;
	}
	public void setInit(){
		isInit = true;
	}
	public boolean isGoal(){
		return isAccept;
	}
	public void setGoal(){
		isAccept = true;
	}
	public int getNum(){
		return num;
	}
	
	@Override
	public String toString(){
		return "V("+num+")";
	}

}
