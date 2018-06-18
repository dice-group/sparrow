package org.dice_group.sparrow.annotation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class AbstractSparqlAnnotation implements SparqlAnnotation {

	protected HashSet<SparqlAnnotation> annotations = new HashSet<SparqlAnnotation>();
	protected int ID=0;

	/**
	 * @return the annotations
	 */
	public HashSet<SparqlAnnotation> getAnnotations() {
		return annotations;
	}

	/**
	 * @param annotations
	 *            the annotations to set
	 */
	public void setAnnotations(HashSet<SparqlAnnotation> annotations) {
		this.annotations = annotations;
	}
	
	@Override
	public int hashCode() {
		return ID;
	}
	
	@Override
	public String toString() {
		return ID+":"+this.getClass().getName();
	}
}
