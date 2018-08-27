package org.dice_group.sparrow.rule;

import java.util.HashSet;
import java.util.Set;

import org.dice_group.sparrow.exceptions.RuleHasNotNObjectsException;
import org.dice_group.sparrow.exceptions.RuleNotAvailableException;
import org.dice_group.sparrow.graph.Triple;
import org.dice_group.sparrow.graph.impl.BNodeGraphNode;
import org.dice_group.sparrow.graph.impl.LiteralGraphNode;
import org.dice_group.sparrow.graph.impl.URIGraphNode;
import org.dice_group.sparrow.graph.impl.VarGraphNode;
import org.dice_group.sparrow.owl.OWLNode;

public class Rules {

	private Set<Rule> rules = new HashSet<Rule>();

	/**
	 * SIMPLE FORM: %% = VAR BNODE = BNODE URI = URINode LITERAL = LiteralNode
	 * String = String
	 * 
	 * FILTER:
	 * 
	 * 
	 * RULE FORMAT: FORM FORM FORM -> OWL_FORM OWL_FORM FORM filter(FILTER) ->
	 * 
	 * 
	 * exa (dismissURIQuotes=true): 
	 * VAR rdf:type URI -> class URI 
	 * URI_1 rdf:label LITERAL_1 -> URI_1 AND name LITERAL_1
	 * 
	 * ?s rdf:type <http://abc.de> -> class http://abc.de <http://test.com>
	 * rdf:label "TEST" -> http://test.com AND name "TEST"
	 * 
	 * @param ruleString
	 * @param dismissURIQUotes 
	 * @throws RuleHasNot3ObjectsException 
	 * @throws RuleHasToManyObjectsException
	 */
	public void loadRule(String ruleString, boolean dismissURIQUotes) throws RuleHasNotNObjectsException {
		
		Object[] input = new Object[3];
		String[] tmpRule = ruleString.trim().split("->");
		int direction = Integer.parseInt(tmpRule[0].trim());
		String[] inputRule = tmpRule[1].trim().split("\\s+");
		String outputRule = tmpRule[2].trim();

		if (inputRule.length > 3 && inputRule.length < 3) {
			throw new RuleHasNotNObjectsException(3, "INPUT: "+ruleString);
		}
		for (int i = 0; i < 3; i++) {
			String ruleObj;
			if(inputRule[i].indexOf("_")>0) {
				ruleObj = inputRule[i].substring(0, inputRule[i].indexOf("_"));
			}
			else {
				ruleObj = inputRule[i];
			}
			
			switch (ruleObj.toUpperCase()) {
			case "%%":
			case "VAR":
			case "VARNODE":
				input[i] = new VarGraphNode(inputRule[i]);
				break;
			case "BNODE":
				input[i] = new BNodeGraphNode(inputRule[i]);
				break;
			case "URI":
			case "URINODE":
				input[i] = new URIGraphNode(inputRule[i]);
				break;
			case "LITERAL":
				input[i] = new LiteralGraphNode(inputRule[i]);
				break;
			default:
				//assuming string:
				input[i] = inputRule[i];
				break;
			}
		}
		
		
		rules.add(new Rule(input, outputRule, dismissURIQUotes, direction));
	}

	public OWLNode execute(Triple relation, int direction) throws RuleNotAvailableException {
		// Find correct Rule and execute
		for(Rule r : rules) {
			if(r.fits(relation, direction)) {
				return r.execute(relation);
			}
		}
		throw new RuleNotAvailableException(relation.toString()+" with direction "+direction);
	}
	
	
}
