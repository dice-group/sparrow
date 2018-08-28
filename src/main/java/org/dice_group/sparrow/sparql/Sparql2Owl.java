package org.dice_group.sparrow.sparql;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.syntax.ElementWalker;
import org.dice_group.sparrow.exceptions.GraphContainsCycleException;
import org.dice_group.sparrow.exceptions.RuleHasNotNObjectsException;
import org.dice_group.sparrow.exceptions.RuleNotAvailableException;
import org.dice_group.sparrow.exceptions.TooManyProjectVarsException;
import org.dice_group.sparrow.graph.impl.DirectedAcyclicGraph;
import org.dice_group.sparrow.graph.impl.GraphImpl;
import org.dice_group.sparrow.graph.impl.TreeGraphPattern;
import org.dice_group.sparrow.owl.OWLClassExpression;
import org.dice_group.sparrow.owl.impl.OWLDisjunction;
import org.dice_group.sparrow.rule.RuleIndicator;

public class Sparql2Owl {

	private String ruleFile;
	private Set<String> rules = new HashSet<String>();

	public Sparql2Owl(String ruleFile) {
		this.ruleFile = ruleFile;
	}

	public String convert(Query query, String varName) {
		try {
			query.getPrefixMapping().clearNsPrefixMap();
			ListElementVisitor visitor = new ListElementVisitor();
			visitor.setElementWhere(query.getQueryPattern());
			ElementWalker.walk(query.getQueryPattern(), visitor);
			OWLDisjunction disjunction = new OWLDisjunction();
			for (int i = 0; i < visitor.graphs.size(); i++) {
				GraphImpl<Node, TreeGraphPattern> graph = visitor.graphs.get(i);
				// convert graph to dag
				if (query.getProjectVars().size() > 1) {
					throw new TooManyProjectVarsException(query.getProjectVars().size() + " ");
				}
				Node rootNode = query.getProjectVars().get(0).asNode();
				DirectedAcyclicGraph<Node> dag = DirectedAcyclicGraph.create(graph, rootNode);

				// p -- rule --> owl
				RuleIndicator ruleIndicator = new RuleIndicator(ruleFile);
				OWLClassExpression owlClE = ruleIndicator.executeDAG(dag);
				disjunction.addOwlClassExpression(owlClE);
				rules.addAll(dag.getRules());
			}
			System.out.println("GOT "+disjunction);
			return disjunction.toString();
		} catch (Exception e) {
			return "";
		}
	}

	public Set<String> getRules() {
		return rules;
	}

}
