package org.dice_group.sparrow.graph.impl;

import java.util.Map;

import org.dice_group.sparrow.graph.AbstractGraphNode;
import org.dice_group.sparrow.owl.OWLNode;

public class URIGraphNode extends AbstractGraphNode {

	private String uri;
	
	public URIGraphNode(String uri2) {
		super();
		this.uri=uri2;
	}


	@Override
	public String getName() {
		return uri;
	}

	@Override
	public boolean specializedEquals(Object obj) {
		return obj instanceof URIGraphNode;
	}

}
