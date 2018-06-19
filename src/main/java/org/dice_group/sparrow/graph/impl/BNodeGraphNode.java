package org.dice_group.sparrow.graph.impl;

import java.util.Map;

import org.dice_group.sparrow.graph.AbstractGraphNode;
import org.dice_group.sparrow.owl.OWLNode;

public class BNodeGraphNode extends AbstractGraphNode {

	private String bnode;
	
	public BNodeGraphNode(String blankNodeLabel) {
		super();
		this.bnode=blankNodeLabel;
	}




	@Override
	public String getName() {
		return bnode;
	}

	@Override
	public boolean specializedEquals(Object obj) {
		// TODO Auto-generated method stub
		return obj instanceof BNodeGraphNode;
	}

}
