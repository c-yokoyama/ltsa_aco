package is.cs.ltsa_aco;

import java.util.*;

public class Ant {
	private ArrayList<Edge> path; //通った経路
	private Vertex nowVer; //現在いるVertex
	
	public Ant(){
		path = new ArrayList<Edge>(1);
	}
	public boolean checkAlreadyTraversed(Edge e){
		return path.contains(e);
	}
	
	public void addEdge(Edge e){
		path.add(e);
	}
	public void setNowVer(Vertex v){
		nowVer = v;
	}
	public Vertex getNowVer(){
		return nowVer;
	}
	public ArrayList<Edge> getAntPath(){
		return path;
	}
	public Iterator<Edge> pathIterator(){
		return path.iterator();
	}
	public void clearPath(){
		path.clear();
	}
}
