package org.dice_group.sparrow.owl.impl;

import org.dice_group.sparrow.owl.OWLClassExpression;
import org.dice_group.sparrow.owl.OWLCombinationExpression;

public class OWLDisjunction extends OWLCombinationExpression implements OWLClassExpression {

	public OWLDisjunction() {
		super("OR");
	}

}
