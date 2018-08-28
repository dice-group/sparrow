package org.dice_group.sparrow.rule;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.dice_group.sparrow.exceptions.RuleHasNotNObjectsException;
import org.dice_group.sparrow.exceptions.RuleNotAvailableException;
import org.dice_group.sparrow.graph.impl.DirectedAcyclicGraph;
import org.dice_group.sparrow.graph.impl.TreeGraphPattern;
import org.dice_group.sparrow.owl.OWLClassExpression;
import org.dice_group.sparrow.owl.impl.OWLConjunction;

public class RuleIndicator {

	private Map<TreeGraphPattern, OWLClassExpression> cache = new HashMap<TreeGraphPattern, OWLClassExpression>();

	private Rules rules = new Rules();

	/**
	 * @return the rules
	 */
	public Rules getRules() {
		return rules;
	}

	/**
	 * @param rules the rules to set
	 */
	public void setRules(Rules rules) {
		this.rules = rules;
	}

	public RuleIndicator(String ruleFile) throws IOException, RuleHasNotNObjectsException {
		try (BufferedReader reader = new BufferedReader(new FileReader(ruleFile))) {
			String ruleString;
			while ((ruleString = reader.readLine()) != null) {
				if (!ruleString.isEmpty())
					try {
						rules.loadRule(ruleString);
					} catch (Exception e) {
						// System.out.println("Rule could not be loaded "+ruleString);
					}
			}
		}
	}


	public OWLClassExpression executeDAG(DirectedAcyclicGraph<Node> dag) throws RuleNotAvailableException {
		//recursive and cache
		OWLConjunction ret = new OWLConjunction();
		for(TreeGraphPattern root: dag.getRoots()) {
			ret.addOwlClassExpression(executeTGP(root));
		}
		return ret;
	}
	
	public OWLClassExpression executeTGP(TreeGraphPattern tgp) throws RuleNotAvailableException {
		//execute one TGP tree
		OWLConjunction conjunction = new OWLConjunction();
		for(TreeGraphPattern child : tgp.getChildren()) {
			if(child.isLeaf()) {
				// simply convert
				OWLClassExpression leaf = rules.execute(child, null);
				cache.put(child, leaf);
				//add to cache
				conjunction.addOwlClassExpression(leaf);
			}
			else if(cache.keySet().contains(child)) {
				// get from cache
				conjunction.addOwlClassExpression(cache.get(child));
			}
			else {
				//recursive conversion
				OWLClassExpression owlClE = executeTGP(child);
				conjunction.addOwlClassExpression(owlClE);
				//add to cache
				cache.put(child, owlClE);
				
			}
		}
		//rules -> tgp, conjunction
		if(conjunction.isEmpty()){
			conjunction =null;
		}
		OWLClassExpression ret =  this.rules.execute(tgp, conjunction);
		cache.put(tgp, ret);
		return ret;

	}

}
