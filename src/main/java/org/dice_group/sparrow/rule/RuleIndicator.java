package org.dice_group.sparrow.rule;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.dice_group.sparrow.exceptions.RootNodeNotVarException;
import org.dice_group.sparrow.exceptions.RuleHasNotNObjectsException;
import org.dice_group.sparrow.exceptions.RuleNotAvailableException;
import org.dice_group.sparrow.graph.GraphNode;
import org.dice_group.sparrow.graph.Triple;
import org.dice_group.sparrow.graph.impl.VarGraphNode;
import org.dice_group.sparrow.owl.OWLNode;
import org.dice_group.sparrow.owl.OWLQuery;
import org.dice_group.sparrow.owl.OWLSeqNode;

public class RuleIndicator {

	private Set<Triple> alreadyUsed = new HashSet<Triple>();
	private Map<GraphNode, OWLNode> executedMapping = new HashMap<GraphNode, OWLNode>();

	private Rules rules;

	public RuleIndicator(String ruleFile, boolean dismissURIQuotes) throws IOException, RuleHasNotNObjectsException {
		try (BufferedReader reader = new BufferedReader(new FileReader(ruleFile))) {
			String ruleString;
			while ((ruleString = reader.readLine()) != null) {
				rules.loadRule(ruleString, dismissURIQuotes);
			}
		}
	}

	public OWLQuery injectRules(GraphNode rootNode) throws RootNodeNotVarException, RuleNotAvailableException {
		if (rootNode instanceof VarGraphNode) {
			OWLQuery query = new OWLQuery();

			int i = 0;
			for (Triple relation : rootNode.getRelations()) {
				// combine relations TODO direct injectRule
				if (i++ > 0)
					query.addOWLNode(OWLNode.AND_NODE);
				query.addOWLNode(OWLNode.GROUP_START_NODE);
				query.addOWLNode(injectRule(Lists.newArrayList(rootNode), relation));
				query.addOWLNode(OWLNode.GROUP_END_NODE);

			}
			return query;
		} else {
			throw new RootNodeNotVarException(rootNode.toString());
		}
	}

	private OWLNode injectRule(List<GraphNode> path, Triple relation) throws RuleNotAvailableException {
		// for each node inject their relations
		OWLNode initial = rules.execute(relation);
		OWLSeqNode seqNode = new OWLSeqNode(initial);
		if (alreadyUsed.contains(relation)) {
			return new OWLNode("");
		}
		for (int i = 0; i < 3; i++) {
			if (!path.contains(relation.get(i))) {
				path.add((GraphNode) relation.get(i));
				for (Triple subRelation : ((GraphNode)relation.get(i)).getRelations()) {
						seqNode.putChild(injectRule(path, subRelation));
				}
			}
		}

		alreadyUsed.add(relation);
		return seqNode;
	}

}
