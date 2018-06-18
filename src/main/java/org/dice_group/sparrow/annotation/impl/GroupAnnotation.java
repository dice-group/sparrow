package org.dice_group.sparrow.annotation.impl;

import org.dice_group.sparrow.annotation.AbstractSparqlAnnotation;
import org.dice_group.sparrow.annotation.SparqlAnnotation;

public class GroupAnnotation  extends AbstractSparqlAnnotation {
	
	private GroupAnnotation parent;
	
	
	public GroupAnnotation() {
	}
	
	
	public GroupAnnotation(int id) {
		ID=id;
	}

	/**
	 * @return the iD
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @param iD the iD to set
	 */
	public void setID(int iD) {
		ID = iD;
	}


	/**
	 * @return the parent
	 */
	public GroupAnnotation getParent() {
		return parent;
	}


	/**
	 * @param parent the parent to set
	 */
	public void setParent(GroupAnnotation parent) {
		this.parent = parent;
	}

}
