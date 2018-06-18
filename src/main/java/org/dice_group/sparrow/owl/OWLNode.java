package org.dice_group.sparrow.owl;

public class OWLNode {

	public static final OWLNode AND_NODE= new OWLNode("AND");
	//TODO make to Group node and every common node (e.g not AND, OR, etc) is subsided by a group node
	public static final OWLNode GROUP_START_NODE = new OWLNode("(");
	public static final OWLNode GROUP_END_NODE = new OWLNode(")");
	
	protected String name;


	public OWLNode(String string) {
		this.name=string;
	}

	
	@Override
	public String toString() {
		return name;
	}
}
