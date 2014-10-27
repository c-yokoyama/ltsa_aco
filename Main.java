package is.cs.ltsa_aco;

import java.awt.*;
import javax.swing.JFrame;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import org.apache.commons.collections15.Transformer;

public class Main {
	private final static int ACO = 0;
	private final static int MyACO = 1;
	private Graph<Vertex, Edge> graph;
	private GraphConstructor gCon;
	private ACO aco;
	private MyACO myAco;
	
	/*===Select Algorithm===*/
	private int mode = MyACO;
	
	private final static String filePath = "C:\\Users\\cyoko_000\\Desktop\\Aldebaran\\SAFE.aut";
	//private final static String filePath = "/Users/c-yokoyama/Desktop/SAFE2.aut";
	
	public Main(){
		gCon = new GraphConstructor(filePath);
		graph = gCon.getFinishedGraph();
		//setLayout(); //Visualization
		
		if(mode == ACO){
			aco = new ACO(graph, gCon.getInitVertex());
			aco.search();
		}else if(mode == MyACO){
			myAco = new MyACO(graph, gCon.getInitVertex(), gCon.getGoalVer());
			myAco.search();
		}
	}
	
	public void setLayout(){
		Dimension viewArea = new Dimension(1280, 800);
		Layout<Vertex, Edge> layout = new ISOMLayout<Vertex, Edge>(graph);
		
		BasicVisualizationServer<Vertex,Edge> panel = 
	                 new BasicVisualizationServer<Vertex,Edge>(layout, viewArea);
		
		 Transformer<Vertex,Paint> nodeFillColor = new Transformer<Vertex, Paint>() {
			 @Override
			 public Paint transform(Vertex v) {
				 if(v.isInit())
					 return Color.RED;
				 else if(v.isGoal())
					 return Color.BLUE;
				 else 
					 return Color.CYAN;
			 }
		 };
		 panel.getRenderContext().setVertexFillPaintTransformer(nodeFillColor);

		JFrame frame = new JFrame("Finding Safety Errors with ACO or myACO");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
	}
	public static void main(String[] args){
		new Main();
	}
}
