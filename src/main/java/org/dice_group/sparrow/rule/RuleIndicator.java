package org.dice_group.sparrow.rule;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.dice_group.sparrow.exceptions.GraphContainsCycleException;
import org.dice_group.sparrow.exceptions.RootNodeNotVarException;
import org.dice_group.sparrow.exceptions.RuleHasNotNObjectsException;
import org.dice_group.sparrow.exceptions.RuleNotAvailableException;
import org.dice_group.sparrow.graph.GraphNode;
import org.dice_group.sparrow.graph.GraphUtils;
import org.dice_group.sparrow.graph.Triple;
import org.dice_group.sparrow.graph.impl.VarGraphNode;
import org.dice_group.sparrow.owl.GroupOWLNode;
import org.dice_group.sparrow.owl.OWLNode;
import org.dice_group.sparrow.owl.OWLSeqNode;

public class RuleIndicator {

	private Set<Triple> alreadyUsed = new HashSet<Triple>();
	private Map<GraphNode, OWLNode> executedMapping = new HashMap<GraphNode, OWLNode>();

	private GraphNode rootNode;
	private Rules rules = new Rules();

	public RuleIndicator(String ruleFile, boolean dismissURIQuotes) throws IOException, RuleHasNotNObjectsException {
		try (BufferedReader reader = new BufferedReader(new FileReader(ruleFile))) {
			String ruleString;
			while ((ruleString = reader.readLine()) != null) {
				if(!ruleString.isEmpty())
					try {
						rules.loadRule(ruleString, dismissURIQuotes);
					}
					catch(Exception e) {
						System.out.println("Rule could not be loaded "+ruleString);
					}
			}
		}
	}

	public String injectRules(GraphNode rootNode)
			throws RootNodeNotVarException, RuleNotAvailableException, GraphContainsCycleException {
		this.rootNode = rootNode;
		HashSet<GraphNode> graph = GraphUtils.getAllNodes(rootNode);
		GraphUtils.checkCycles(graph);
		if (rootNode instanceof VarGraphNode) {
			OWLSeqNode query = new OWLSeqNode();

			for (Triple relation : rootNode.getRelations()) {
				// combine relations TODO direct injectRule
				if(!alreadyUsed.contains(relation)) {
					query.addNode(injectRule(Lists.newArrayList(rootNode), relation));

				}
			}
			System.out.println(query.toString());
			String queryStr = query.build();
			queryStr = Pattern.compile("\\?[a-zA-Z0-9]+").matcher(queryStr).replaceAll("Thing");
			return queryStr;
		} else {
			throw new RootNodeNotVarException(rootNode.toString());
		}
	}

	private OWLNode injectRule(List<GraphNode> path, Triple relation) throws RuleNotAvailableException {
		// for each node inject their relations
		// check if inverse ->
		GraphNode lastNode = path.get(path.size() - 1); 
		int direction = relation.getIndex(lastNode);
		if (lastNode != rootNode && lastNode.equals(relation.object)
				&& (GraphUtils.checkBetterWay(rootNode, lastNode, new LinkedList<GraphNode>(), path)
						|| GraphUtils.checkOneZigZagToNode(lastNode, rootNode))) {
			// there is a better way
			return null;
		}
		OWLNode initial = rules.execute(relation, direction);
		System.out.println(initial);
		GroupOWLNode group = new GroupOWLNode();
		group.setParent(initial);
		if (alreadyUsed.contains(relation) && !(lastNode instanceof VarGraphNode) ) {
			return null;
		}
		alreadyUsed.add(relation);
		for (int i = 0; i < 3; i++) {
			if(i==1 && !(relation.get(i) instanceof VarGraphNode)) {
				continue;
			}
			if (!path.contains(relation.get(i))) {
				path.add((GraphNode) relation.get(i));
				OWLSeqNode child = new OWLSeqNode();
				for (Triple subRelation : ((GraphNode) relation.get(i)).getRelations()) {
					if(!alreadyUsed.contains(subRelation) ||(relation.get(i) instanceof VarGraphNode && !subRelation.equals(relation))) {
						
						
						child.addNode(injectRule(path, subRelation));
						
					}
				}
				group.setChild(child);
				path.remove(path.size()-1);
			}
			
		}
		

		return group;
	}

}
