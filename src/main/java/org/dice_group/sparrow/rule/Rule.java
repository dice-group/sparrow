package org.dice_group.sparrow.rule;

import org.dice_group.sparrow.graph.GraphNode;
import org.dice_group.sparrow.graph.Triple;
import org.dice_group.sparrow.graph.impl.URIGraphNode;
import org.dice_group.sparrow.graph.impl.VarGraphNode;
import org.dice_group.sparrow.owl.BaseOWLNode;
import org.dice_group.sparrow.owl.OWLNode;
import org.dice_group.sparrow.owl.OWLParser;

public class Rule {

	private Object[] input;
	private String output;
	
	private int direction=0;

	private boolean dismissURIQuotes=true;

	public Rule(Object[] input, String output, boolean dismissURIQuotes, int direction) {
		this.input = input;
		this.output = output;
		this.dismissURIQuotes = dismissURIQuotes;
		this.direction=direction;
	}

	public boolean fits(Triple relation, int direction) {
		if(direction!=this.direction) {
			return false;
		}
		boolean fits = true;
		for (int i = 0; i < 3; i++) {
			if (this.input[i].getClass() == String.class) {
				fits &= ((GraphNode)relation.get(i)).getName().equals(input[i]);
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
	
			String replacer = ((GraphNode)relation.get(i)).useRule();
//			if(relation.get(i) instanceof VarGraphNode && i==2) {
//				replacer = "SOME";
//			}
//			else if(relation.get(i) instanceof VarGraphNode && i==0) {
//				replacer = "Thing";
//			}
			if(ruleObj instanceof VarGraphNode) {
				exchange = ((GraphNode)ruleObj).getName().substring(1);
			}
			else if(ruleObj instanceof GraphNode) {
				exchange = ((GraphNode)ruleObj).getName();
			}
			if(dismissURIQuotes) {
				if(relation.get(i) instanceof URIGraphNode) {
					replacer = replacer.replace("<", "").replace(">","");
				}
			}
			owlString = owlString.replace(exchange, replacer);
			
		}
		return OWLParser.parse(owlString);
	}

}
