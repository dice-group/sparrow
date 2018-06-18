package org.dice_group.sparrow.graph.impl;

import java.util.Map;

import org.dice_group.sparrow.graph.AbstractGraphNode;
import org.dice_group.sparrow.owl.OWLNode;

public class LiteralGraphNode extends AbstractGraphNode {

	private String literal;
	
	public LiteralGraphNode(String string) {
		super();
		this.literal=string;
	}



	@Override
	public String getName() {
		return literal;
	}

	@Override
	public boolean specializedEquals(Object obj) {
		return true;
	}

}
