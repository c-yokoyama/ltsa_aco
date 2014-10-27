package is.cs.ltsa_aco;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;

public class GraphConstructor {
	private Graph<Vertex, Edge> graph;
	private Vertex[] verx;
	private ArrayList<Edge>edge;
	private Vertex initVer;
	private Vertex goalVer;
	
	public GraphConstructor(String filePath){
		this.graph = new DirectedSparseMultigraph<Vertex, Edge>();
		if(!constModel(filePath)){
			System.err.println("Error occurred in constModel()");
			System.exit(1);
		}
	}
	public Vertex getInitVertex(){
		return initVer;
	}
	public Vertex getGoalVer(){
		return goalVer;
	}
	public Graph<Vertex, Edge> getFinishedGraph(){
		return graph;
	}
	
	public boolean constModel(String filePath){
		try {
			FileReader fr = new FileReader(new File(filePath));
			BufferedReader br = new BufferedReader(fr);
			String fstLine = br.readLine();
			if( !fstLine.contains("des(") ){
				br.close();
				return false;
			}
			
			//()の内側のみ取り出して","で区切る
			//tmp[0] 初期状態　,tmp[1] 遷移数(行数),　tmp[2] 状態数
			String[] tmp = fstLine.substring(4, fstLine.length()-1).split(",");
			int edgeNum = Integer.parseInt(tmp[1]);
			verx = new Vertex[Integer.parseInt(tmp[2])];
			edge = new ArrayList<Edge>();
		
			for(int i=0; i<verx.length; i++){
				verx[i] = new Vertex(i); 
				graph.addVertex(verx[i]);
			}
			initVer = verx[Integer.parseInt(tmp[0])];
			initVer.setInit();
			goalVer = new Vertex(-1);
			goalVer.setGoal();
			
			
			String str = null;
			Edge e = null;
			for(int i=0; i<edgeNum; i++){
				str = br.readLine();
				tmp = str.substring(1, str.length()-1).split(",");
				e = new Edge();
				if("-1".equals(tmp[2]))
					graph.addEdge(e, verx[Integer.parseInt(tmp[0])], goalVer);
				else
					graph.addEdge(e, verx[Integer.parseInt(tmp[0])], verx[Integer.parseInt(tmp[2])] );
				edge.add(e);
			}
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		//System.out.println("Graph: "+graph.toString());
		return true;
	}
}
