package is.cs.ltsa_aco;

import java.util.*;

import edu.uci.ics.jung.graph.Graph;

public class ACO {
	protected final static int MAX_STEPS = 100;
	protected final static int COLSIZE = 10;
	protected final static double ALPHA = 1.0;
	protected final static double BETA = 2.0;
	protected final static double ETA = 1.0;
	protected final static double RHO = 0.2;
	protected final static int PP = 70;
	protected final static int PC = 70;
	protected final static double A = 5.0;
	protected int LAMBDA_ANT = 5;
	
	protected Graph<Vertex, Edge> graph; 
	protected Ant[] ant;
	protected ArrayList<Edge> bestPath;
	protected int step;
	protected Vertex initVer;
	protected double tauMax;
	protected double tauMin;
	protected boolean[] checkVer;
	
	public ACO(Graph<Vertex, Edge> graph, Vertex init){
		this.graph = graph;
		ant = new Ant[COLSIZE];
		checkVer = new boolean[graph.getVertexCount() - 1];//-1はV(-1)の分
		this.initVer = init;
		initPheromone();
		for(int i=0; i<COLSIZE; i++){
			ant[i] = new Ant();
			ant[i].setNowVer(initVer);
		}
		//この後MainクラスでSearchが呼ばれる
	}
	
	public void initPheromone(){
		//フェロモンの初期値はランダムで0.1-1.0のどれか
		Random rd = new Random();
		double pVal = rd.nextDouble();
		Collection<Edge> edge = graph.getEdges();
		Iterator<Edge> it = edge.iterator();
		
		System.out.println("initPheromone(): pVal = " + pVal);
		while(it.hasNext()){
			it.next().setPhrm(pVal);
		}
	}
	public boolean hasGoal(){
		Vertex v = null;
		for(int i=0; i < COLSIZE; i++){
			v = ant[i].getNowVer();
			if(v.isGoal())
				return true;
		}
		return false;
	}
	
	public void search(){
		long startTime = System.nanoTime();
		long usedMaxMem = -1;
		long tmpMem = -1;
		
		while(step < MAX_STEPS){
			System.out.println("In search(): step = " + step);
			constAntSolutions();
			if(hasGoal()){
				break;
			}
			evaporatePheromone();
			updatePheromone();
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
		System.out.println("\n===Answer by ACO===");
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
	
	public void constAntSolutions(){
		System.out.println("  constAntSolutions()");
		Collection<Edge> edges = null;
		Edge next = null;
		double p = -1.0, tmpP = -1.0;
		int antStep = 0;
		
		for(int i=0; i < COLSIZE; i++){
			antStep = 0;
			while( !ant[i].getNowVer().isGoal() && antStep < LAMBDA_ANT) {
				edges = graph.getOutEdges(ant[i].getNowVer());
				double sum = 0; //Pの分母
				
				//先に分母を計算
				for(Edge e : edges)
					sum += Math.pow(e.getPhrm(), ALPHA) * Math.pow(ETA, BETA);
				Vertex s = null, d = null;
				for(Edge e : edges){
					s = graph.getSource(e);
					d = graph.getDest(e);
					if(ant[i].checkAlreadyTraversed(e) || s.getNum() == d.getNum() ) 
						continue;
					
					//ETAがヒューリスティック値
					tmpP = ( Math.pow(e.getPhrm(), ALPHA) * Math.pow(ETA, BETA)  ) / sum;
					
					if( tmpP >= p ){
						next = e;
						p = tmpP;
					}
				}
				//nowVerから辿れるエッジがなくなった場合
				if(next == null)
					break;
				ant[i].addEdge(next);
				ant[i].setNowVer(graph.getDest(next));
			
				//localPheromonUpdate
				next.setPhrm(next.getPhrm() * 0.5); 
				
				p = -1.0;
				next = null;
				antStep++;
			}
			//==ここまでで蟻1匹の探索が終了==
			
			//selectBestPath
			if(bestPath == null){
				bestPath = new ArrayList<Edge>(ant[i].getAntPath());
			}//適合度関数の値は小さいほうが高評価
			else if( fitnessFunc(ant[i].getAntPath()) < fitnessFunc(bestPath) ){
				bestPath = new ArrayList<Edge>(ant[i].getAntPath());
			}
			//フェロモンのIntervalを更新
			tauMax = 1.0 / (RHO * fitnessFunc(bestPath) );
			tauMin = tauMax / A;
		}
	}
	
	public void evaporatePheromone(){
		System.out.println("  evaporatePheromone()");
		System.out.println("  tauMax = "+tauMax+", tauMin = "+tauMin);
		System.out.println("  bestPathSize = "+bestPath.size());
		//全てのEdgeに対して行う
		Collection<Edge> edges = graph.getEdges();
		Edge tmp = null;
		double pVal = 0.0;
		
		Iterator<Edge> it = edges.iterator();
		while(it.hasNext()){
			tmp = it.next();
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
		System.out.println("  updatePheromone()");
		Edge tmp = null;
		double addVal = 1.0 / fitnessFunc(bestPath);
		double pVal = 0.0;
		//System.out.println("addVal = "+addVal);
		for(int i=0; i<bestPath.size(); i++){
			tmp = bestPath.get(i);
			pVal = tmp.getPhrm() + addVal;
			
			if(pVal > tauMax)
				tmp.setPhrm(tauMax);
			else if(pVal < tauMin)
				tmp.setPhrm(tauMin);
			else
				tmp.setPhrm(pVal);
		}
	}

	public int fitnessFunc(ArrayList<Edge> path){
		int ans = 0;
		/*pathが持ってる一番最後のEdgeのDest側のVertex
			つまりNowver*/
		Vertex v = graph.getDest( path.get(path.size()-1) );
		
		if(v.isGoal()){
			ans = path.size();
		}else{
			Iterator<Edge> it = path.iterator();
			int circlePena = 0;
			checkVer[v.getNum()] = true;
			while(it.hasNext()){
				v = graph.getSource(it.next());
				if(checkVer[v.getNum()])
					circlePena++;
				else 
					checkVer[v.getNum()] = true;
			}
			//ヒューリスティック値が未実装
			ans = path.size() + PP + (PC*circlePena) / path.size() ;
		}
		return ans;
	}
}

