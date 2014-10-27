package is.cs.ltsa_aco;

import edu.uci.ics.jung.graph.Graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class MyACO extends ACO{
	private Vertex goalVer;
	private double goalPh;
		
	public MyACO(Graph<Vertex, Edge> graph, Vertex init, Vertex goal) {
		super(graph, init);
		this.goalVer = goal; 
		initGoalPh();
		//このあとMainクラスでSearchを呼ぶ
	}

	public void initGoalPh(){
		Random rd = new Random();
		double goalPh = rd.nextDouble() * 10;
		System.out.println("goalPh = "+goalPh);
	}
	
	public void scatterGoalPh(){
		Collection<Edge> edges = graph.getInEdges(goalVer);
		Iterator<Edge> it = edges.iterator();
		Edge e = null;
		while(it.hasNext()){
			e = it.next();
			e.setPhrm(e.getPhrm() + goalPh);
			e.chIsGoalPh();
		}
	}
	
	@Override
	public void search(){
		long startTime = System.nanoTime();
		long usedMaxMem = -1;
		long tmpMem = -1;
		
		while(step < MAX_STEPS){
			System.out.println("In MyACO's search(): step = " + step);
			constAntSolutions();
			if(hasGoal())
				break;
			evaporatePheromone();
			updatePheromone();
			scatterGoalPh();
			//メモリ使用量を計算
			tmpMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			if( tmpMem > usedMaxMem)
				usedMaxMem = tmpMem;
			for(Ant a : ant){
				a.clearPath();
				a.setNowVer(initVer);
			}
			LAMBDA_ANT += LAMBDA_ANT;
			step++;
		}
		if(step >= MAX_STEPS)
			System.out.println("reached MAX_STEPS");
		long endTime = System.nanoTime();
		
		//====結果出力====
		System.out.println("\n===Answer by MyACO===");
		System.out.println("bestPathSize = "+ bestPath.size());
		System.out.print("bestPath: ");
		
		Edge e = null;
		Vertex v = null;
		v = graph.getSource(bestPath.get(0));
		System.out.print(v.toString()+"->");
		for(int i=0; i<bestPath.size(); i++){
			e = bestPath.get(i);
			v = graph.getDest(e);
			if(i == bestPath.size() - 1)
				System.out.print(v.toString());
			else
				System.out.print(v.toString()+"->");
		}
		System.out.println("\nTime: " + (endTime - startTime) + " ns");
		System.out.println("Memory: "+ usedMaxMem + " byte");
	}
	
	@Override
	public void evaporatePheromone(){
		System.out.println("  evaporatePheromone() : MyACO");
		System.out.println("  tauMax = "+tauMax+", tauMin = "+tauMin);
		System.out.println("  bestPathSize = "+bestPath.size());
		//全てのEdgeに対して行う
		Collection<Edge> edges = graph.getEdges();
		Edge tmp = null;
		double pVal = 0.0;
		
		Iterator<Edge> it = edges.iterator();
		while(it.hasNext()){
			tmp = it.next();
			if(tmp.getIsGph())
				continue;
			pVal = tmp.getPhrm() * (1.0-RHO);
			if(pVal < tauMin)
				tmp.setPhrm(tauMin);
			else if(pVal > tauMax)
				tmp.setPhrm(tauMax);
			else
				tmp.setPhrm(pVal);
		}
	}
	public void updatePheromone(){
		System.out.println("  updatePheromone() : MyACO");
		Edge tmp = null;
		double addVal = 1.0 / fitnessFunc(bestPath);
		double pVal = 0.0;
		//System.out.println("addVal = "+addVal);
		for(int i=0; i<bestPath.size(); i++){
			tmp = bestPath.get(i);
			if(tmp.getIsGph())
				continue;
			pVal = tmp.getPhrm() + addVal;
			
			if(pVal > tauMax)
				tmp.setPhrm(tauMax);
			else if(pVal < tauMin)
				tmp.setPhrm(tauMin);
			else
				tmp.setPhrm(pVal);
		}
	}
}
