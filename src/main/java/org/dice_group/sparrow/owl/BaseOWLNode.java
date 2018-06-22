package org.dice_group.sparrow.owl;

public class BaseOWLNode implements OWLNode {

	private String base;

	public BaseOWLNode(String string) {
		this.setValue(string);
	}

	@Override
	public void setValue(String base) {
		this.base = base;
	}

	@Override
	public String build() {
		return this.base;
	}

	@Override
	public String getValue() {
		return base;
	}
	
	@Override
	public String toString() {
		return base;
	}

}
