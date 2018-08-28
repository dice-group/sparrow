package org.dice_group.sparrow.owl;

import java.util.LinkedList;
import java.util.List;

public abstract class OWLCombinationExpression implements OWLClassExpression {
	
	protected List<OWLClassExpression> combination = new LinkedList<OWLClassExpression>();
	private String combinationSymbol;

	public OWLCombinationExpression(String combinationSymbol) {
		this.combinationSymbol = combinationSymbol;
	}
	
	public void addOwlClassExpression(OWLClassExpression owlClE) {
		this.combination.add(owlClE);
	}
	

	public boolean isEmpty() {
		return this.combination.isEmpty();
	}
	
	@Override
	public String toString() {
		StringBuilder build = new StringBuilder();
		for(int i=0;i<combination.size()-1;i++) {
			build.append("(").append(combination.get(i).toString()).append(")\t").append(combinationSymbol).append("\t\n");
		}
		build.append(combination.get(combination.size()-1));
		return build.toString();
	}
}
