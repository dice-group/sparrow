package org.dice_group.sparrow.rule;

import org.dice_group.sparrow.graph.GraphNode;
import org.dice_group.sparrow.graph.Triple;
import org.dice_group.sparrow.graph.impl.URIGraphNode;
import org.dice_group.sparrow.owl.OWLNode;

public class Rule {

	private Object[] input;
	private String output;

	private boolean dismissURIQuotes=true;

	public Rule(Object[] input, String output, boolean dismissURIQuotes) {
		this.input = input;
		this.output = output;
		this.dismissURIQuotes = dismissURIQuotes;
	}

	public boolean fits(Triple relation) {
		boolean fits = true;
		for (int i = 0; i < 3; i++) {
			if (this.input[i].getClass() == String.class) {
				fits &= relation.get(i).equals(input[i]);
			} else {
				// if class of relation and input are the same everythings ok.
				fits &= relation.get(i).getClass().equals(this.input[i].getClass());
			}
		}
		return fits;
	}

	public OWLNode execute(Triple relation) {
		String owlString = output;
		for(int i=0;i<3;i++) {
			Object ruleObj = input[i];
			String exchange = ruleObj.toString();
			if(ruleObj instanceof GraphNode) {
				exchange = ((GraphNode)ruleObj).getName();
			}
			String replacer = ((GraphNode)relation.get(i)).useRule();
			if(dismissURIQuotes) {
				if(relation.get(i) instanceof URIGraphNode) {
					replacer = replacer.replace("<", "").replace(">","");
				}
			}
			owlString = owlString.replace(exchange, replacer);
			
		}
		return new OWLNode(owlString);
	}

}
