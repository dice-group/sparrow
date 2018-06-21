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

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.dice_group.sparrow.exceptions.GraphContainsCycleException;
import org.dice_group.sparrow.exceptions.RootNodeNotVarException;
import org.dice_group.sparrow.exceptions.RuleHasNotNObjectsException;
import org.dice_group.sparrow.exceptions.RuleNotAvailableException;
import org.dice_group.sparrow.graph.GraphNode;
import org.dice_group.sparrow.graph.GraphUtils;
import org.dice_group.sparrow.graph.Triple;
import org.dice_group.sparrow.graph.impl.VarGraphNode;
import org.dice_group.sparrow.owl.OWLNode;
import org.dice_group.sparrow.owl.OWLQuery;
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
				rules.loadRule(ruleString, dismissURIQuotes);
			}
		}
	}

	public OWLQuery injectRules(GraphNode rootNode)
			throws RootNodeNotVarException, RuleNotAvailableException, GraphContainsCycleException {
		this.rootNode = rootNode;
		HashSet<GraphNode> graph = GraphUtils.getAllNodes(rootNode);
		GraphUtils.checkCycles(graph);
		if (rootNode instanceof VarGraphNode) {
			OWLQuery query = new OWLQuery();

			int i = 0;
			for (Triple relation : rootNode.getRelations()) {
				// combine relations TODO direct injectRule
				if (i++ > 0)
					query.addOWLNode(OWLNode.AND_NODE);
				if(rootNode.getRelations().size()>1)
					query.addOWLNode(OWLNode.GROUP_START_NODE);
				query.addOWLNode(injectRule(Lists.newArrayList(rootNode), relation));
				if(rootNode.getRelations().size()>1)
					query.addOWLNode(OWLNode.GROUP_END_NODE);

			}
			query.build();
			return query;
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
			return new OWLNode("");
		}
		OWLNode initial = rules.execute(relation, direction);
		OWLSeqNode seqNode = new OWLSeqNode(initial);
		if (alreadyUsed.contains(relation)) {
			return new OWLNode("");
		}
		alreadyUsed.add(relation);
		for (int i = 0; i < 3; i++) {
			if (!path.contains(relation.get(i))) {
				path.add((GraphNode) relation.get(i));
				for (Triple subRelation : ((GraphNode) relation.get(i)).getRelations()) {
					if(!alreadyUsed.contains(subRelation))
						seqNode.putChild(injectRule(path, subRelation));
				}
			}
		}

		return seqNode;
	}

}
