package org.dice_group.sparrow.graph.impl;

import java.util.Map;

import org.dice_group.sparrow.graph.AbstractGraphNode;
import org.dice_group.sparrow.graph.OWLNode;

public class LiteralGraphNode extends AbstractGraphNode {

	private String literal;
	
	@Override
	public void createRules() {
		// TODO Auto-generated method stub

	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, OWLNode> useRule() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return literal;
	}

}
