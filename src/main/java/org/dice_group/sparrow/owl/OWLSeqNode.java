package org.dice_group.sparrow.owl;

import java.util.LinkedList;
import java.util.List;

import org.dice_group.sparrow.graph.GraphNode;

public class OWLSeqNode extends OWLNode {

	public List<OWLNode> sequence = new LinkedList<OWLNode>();
	
	public OWLSeqNode(String string) {
		super(string);
	}

	public OWLSeqNode() {
		super("");
	}
	
	public void append(OWLNode node) {
		sequence.add(node);
		
		this.name+="("+node+")";
	}

}
