package org.dice_group.sparrow.sparql;

import java.io.IOException;
import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.syntax.ElementWalker;
import org.dice_group.sparrow.exceptions.GraphContainsCycleException;
import org.dice_group.sparrow.exceptions.RuleHasNotNObjectsException;
import org.dice_group.sparrow.exceptions.TooManyProjectVarsException;
import org.dice_group.sparrow.graph.DirectedAcyclicGraph;
import org.dice_group.sparrow.graph.Graph;
import org.dice_group.sparrow.graph.GraphPattern;
import org.dice_group.sparrow.graph.TreeGraphPattern;
import org.dice_group.sparrow.rule.RuleIndicator;

public class Sparql2Owl {

	private  String ruleFile;
	private boolean dismissURIQuotes;

	public Sparql2Owl(String ruleFile, boolean dismissURIQuotes) {
		this.ruleFile=ruleFile;
		this.dismissURIQuotes=dismissURIQuotes;
	}
	
	public String convert(Query query, String varName) throws TooManyProjectVarsException, IOException, RuleHasNotNObjectsException, GraphContainsCycleException {
		query.getPrefixMapping().clearNsPrefixMap();
		ListElementVisitor visitor = new ListElementVisitor();
		ElementWalker.walk(query.getQueryPattern(), visitor);
		StringBuilder owl = new StringBuilder();
		for(int i=0;i<visitor.graphs.size();i++) {
			Graph<Node, TreeGraphPattern> graph = visitor.graphs.get(i);
			//convert graph to dag
			if(query.getProjectVars().size()>1) {
				throw new TooManyProjectVarsException(query.getProjectVars().size()+" ");
			}
			Node rootNode = query.getProjectVars().get(0).asNode();
			DirectedAcyclicGraph<Node> dag = DirectedAcyclicGraph.create(graph, rootNode);
			StringBuilder singleOWL = new StringBuilder();
			for(GraphPattern p : dag.getEdges()) {
				//TODO p -- rule --> owl
				RuleIndicator ruleIndicator = new RuleIndicator(ruleFile, dismissURIQuotes);
				ruleIndicator.executeDAG(dag);
			}
			owl.append(singleOWL);
			if(i<visitor.graphs.size()-1) {
				owl.append(" OR ");
			}
		}
		return owl.toString();
	}
	
	


}
