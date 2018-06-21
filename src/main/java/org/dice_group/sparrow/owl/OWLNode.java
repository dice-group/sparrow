package org.dice_group.sparrow.owl;

public class OWLNode {

	public static final OWLNode AND_NODE= new OWLNode("AND");
	public static final OWLNode GROUP_START_NODE = new OWLNode("(");
	public static final OWLNode GROUP_END_NODE = new OWLNode(")");
	public static final OWLNode SOME_NODE = new OWLNode("SOME");
	public static final OWLNode VALUE_NODE = new OWLNode("VALUE");
	public static final OWLNode THING_NODE = new OWLNode("Thing");
	
	protected String name;


	public OWLNode(String string) {
		this.name=string;
	}

	
	@Override
	public String toString() {
		return name;
	}
}
