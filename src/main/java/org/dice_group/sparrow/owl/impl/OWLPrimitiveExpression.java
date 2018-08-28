package org.dice_group.sparrow.owl.impl;

import org.dice_group.sparrow.owl.OWLClassExpression;

public class OWLPrimitiveExpression implements OWLClassExpression {

	public static OWLClassExpression OWLThing = new OWLPrimitiveExpression("Thing");
	
	private String primitive;

	public OWLPrimitiveExpression(String string) {
		this.primitive=string;
	}

	public OWLPrimitiveExpression() {
	}

	/**
	 * @return the primitive
	 */
	public String getPrimitive() {
		return primitive;
	}

	/**
	 * @param primitive the primitive to set
	 */
	public void setPrimitive(String primitive) {
		this.primitive = primitive;
	}
	
	@Override
	public String toString() {
		return this.primitive;
	}
	
}
