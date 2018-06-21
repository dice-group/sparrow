package org.dice_group.sparrow.graph.impl;

import java.util.Map;

import org.dice_group.sparrow.graph.AbstractGraphNode;
import org.dice_group.sparrow.owl.OWLNode;

public class VarGraphNode extends AbstractGraphNode {

	private String varName;
	
	public VarGraphNode(String varName) {
		super();
		this.varName="?"+varName;
	}


	@Override
	public String getName() {
		return varName;
	}

	@Override
	public boolean specializedEquals(Object obj) {
		return obj instanceof VarGraphNode;
	}

}
