package org.dice_group.sparrow.rule;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.dice_group.sparrow.exceptions.RuleHasNotNObjectsException;
import org.dice_group.sparrow.exceptions.RuleNotAvailableException;
import org.dice_group.sparrow.graph.impl.TreeGraphPattern;
import org.dice_group.sparrow.owl.OWLClassExpression;

public class Rules {

	private List<Rule> rules = new LinkedList<Rule>();

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
	 * exa (dismissURIQuotes=true): VAR rdf:type URI -> class URI URI_1 rdf:label
	 * LITERAL_1 -> URI_1 AND name LITERAL_1
	 * 
	 * ?s rdf:type <http://abc.de> -> class http://abc.de <http://test.com>
	 * rdf:label "TEST" -> http://test.com AND name "TEST"
	 * 
	 * @param ruleString
	 * @param dismissURIQUotes
	 * @throws RuleHasNotNObjectsException 
	 */
	public void loadRule(String ruleString) throws RuleHasNotNObjectsException {

		String[] input = new String[3];
		String[] tmpRule = ruleString.trim().split("->");
		String[] inputRule = tmpRule[0].trim().split("\\s+");
		String outputRule = tmpRule[1].trim();

		if (inputRule.length > 3 && inputRule.length < 3) {
			throw new RuleHasNotNObjectsException(3, "INPUT: " + ruleString);
		}
		for (int i = 0; i < 3; i++) {

			// assuming string:
			input[i] = inputRule[i];

		}

		rules.add(new Rule(input, outputRule));
	}

	public OWLClassExpression execute(TreeGraphPattern tgp, OWLClassExpression sub) throws RuleNotAvailableException {

		for (Rule r : rules) {
			if (r.fits(tgp)) {
				return r.execute(tgp, sub);
			}
		}
		throw new RuleNotAvailableException();
	}
}
