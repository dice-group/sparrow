package org.dice_group.sparrow.owl.impl;

import org.dice_group.sparrow.owl.OWLClassExpression;

public class OWLRuleExpression implements OWLClassExpression {

	private OWLClassExpression ruledClass;
	private OWLPrimitiveExpression rule;
	
	public void setRule(OWLPrimitiveExpression rule) {
		this.rule=rule;
	}
	public void setRuledClass(OWLClassExpression ruledClass) {
		this.ruledClass=ruledClass;
	}
	
	@Override
	public String toString() {
		StringBuilder build = new StringBuilder();
		build.append(rule).append(" SOME ( ").append(ruledClass.toString()).append(" ) ");
		
		return build.toString();
	}
	
}
