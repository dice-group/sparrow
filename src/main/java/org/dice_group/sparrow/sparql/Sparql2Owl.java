package org.dice_group.sparrow.sparql;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.syntax.ElementWalker;
import org.dice_group.sparrow.exceptions.GraphContainsCycleException;
import org.dice_group.sparrow.exceptions.RootNodeNotVarException;
import org.dice_group.sparrow.exceptions.RuleHasNotNObjectsException;
import org.dice_group.sparrow.exceptions.RuleNotAvailableException;
import org.dice_group.sparrow.graph.GraphNode;
import org.dice_group.sparrow.graph.impl.VarGraphNode;
import org.dice_group.sparrow.rule.RuleIndicator;

public class Sparql2Owl {

	private  String ruleFile;
	private boolean dismissURIQuotes;

	public Sparql2Owl(String ruleFile, boolean dismissURIQuotes) {
		this.ruleFile=ruleFile;
		this.dismissURIQuotes=dismissURIQuotes;
	}
	
	
	public String convertSparqlQuery(String query, String varName) throws RootNodeNotVarException, IOException, RuleHasNotNObjectsException, RuleNotAvailableException, GraphContainsCycleException {
		return convertSparqlQuery(QueryFactory.create(query), varName);
	}

	public String convertSparqlQuery(Query query, String varName) throws RootNodeNotVarException, IOException, RuleHasNotNObjectsException, RuleNotAvailableException, GraphContainsCycleException {
		//0.
//		System.out.println(query);
		query.getPrefixMapping().clearNsPrefixMap();
//		System.out.println(query);
		// 1. walk query and create graph
		SparqlElementVisitor elVisitor = new SparqlElementVisitor();
		elVisitor.setElementWhere(query.getQueryPattern());
		ElementWalker.walk(query.getQueryPattern(), elVisitor);
		Set<GraphNode> nodeList = elVisitor.getNodes();
		
		// 2. get graphNode with varName
		GraphNode rootNode=null;
		Iterator<GraphNode> nodeIt = nodeList.iterator();
		while(nodeIt.hasNext()) {
			GraphNode node = nodeIt.next();
			if(node instanceof VarGraphNode && node.getName().equals("?"+varName)) {
				rootNode = node;
				break;
			}
		}
		System.out.println("Found root node: "+rootNode);

		// 3. use rules of nodes and graph structure
		RuleIndicator ruleIndicator = new RuleIndicator(ruleFile, dismissURIQuotes);
		return ruleIndicator.injectRules(rootNode).toString();
		
	}


	public String convertSparqlQuery(String query) {
		try {
			Query q = QueryFactory.create(query);
			return this.convertSparqlQuery(q, q.getProjectVars().get(0).getName());
		}catch(Exception e) {
			System.out.println("Query could not be converted ");
			System.out.println("ERROR Query: "+query);
			System.out.println(e.getMessage());
		}
		return "";
	}

}
