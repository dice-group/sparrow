package org.dice_group.sparrow.owl;

import java.util.LinkedList;
import java.util.List;

import org.dice_group.sparrow.graph.GraphNode;

public class OWLSeqNode extends OWLNode {

	private List<OWLNode> child = new LinkedList<OWLNode>();
	private String initial="";
	private boolean initialIsComplex = false;
	
	public OWLSeqNode(String string) {
		super("");
		initial=string;
	}

	public OWLSeqNode() {
		super("");
	}
	
	public OWLSeqNode(OWLNode initial) {
		this(initial.toString());
		if(initial instanceof OWLSeqNode) {
			initialIsComplex = !((OWLSeqNode) initial).child.isEmpty();
		}
	}

	public OWLSeqNode putChild(OWLNode node) {
		if(!child.isEmpty() || initialIsComplex )
			child.add(OWLNode.AND_NODE);
		child.add(OWLNode.GROUP_START_NODE);
		child.add(node);
		child.add(OWLNode.GROUP_END_NODE);
		this.name=createNodeString();
		return this;
	}
	
	private String createNodeString() {
		StringBuilder builder = new StringBuilder(initial).append(" ");
		for(OWLNode children : child) {
			builder.append(children).append(" ");
		}
		return builder.toString().replaceAll("\\s+", " ");
	}

	@Override
	public String toString() {
		return createNodeString();
	}
}
