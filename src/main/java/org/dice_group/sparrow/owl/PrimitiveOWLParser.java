package org.dice_group.sparrow.owl;

import org.dice_group.sparrow.owl.impl.OWLConjunction;
import org.dice_group.sparrow.owl.impl.OWLDisjunction;
import org.dice_group.sparrow.owl.impl.OWLPrimitiveExpression;
import org.dice_group.sparrow.owl.impl.OWLRuleExpression;

public class PrimitiveOWLParser {

	public static OWLClassExpression parse(String output) {
		//parse output and set if is Primitive, Rule, Conj or DIsj
		if(output.toUpperCase().contains(" AND ")) {
			return new OWLConjunction();
		}
		if (output.toUpperCase().contains(" OR ")) {
			return new OWLDisjunction();
		}
		if(output.matches("[^(]+\\([^)]+\\)\\s*")) {
			return new OWLRuleExpression();
		}
		return new OWLPrimitiveExpression();
	}

}
