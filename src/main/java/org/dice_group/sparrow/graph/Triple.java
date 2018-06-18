package org.dice_group.sparrow.graph;

public class Triple {

	public GraphNode subject;
	public GraphNode object;
	public GraphNode predicate;

	public Triple(GraphNode s, GraphNode p, GraphNode o) {
		subject=s;
		object=o;
		predicate=p;
	}

	/**
	 * @return the subject
	 */
	public GraphNode getSubject() {
		return subject;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(GraphNode subject) {
		this.subject = subject;
	}

	/**
	 * @return the object
	 */
	public GraphNode getObject() {
		return object;
	}

	/**
	 * @param object
	 *            the object to set
	 */
	public void setObject(GraphNode object) {
		this.object = object;
	}

	/**
	 * @return the predicate
	 */
	public GraphNode getPredicate() {
		return predicate;
	}

	/**
	 * @param predicate
	 *            the predicate to set
	 */
	public void setPredicate(GraphNode predicate) {
		this.predicate = predicate;
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Triple) {
			Triple otherTriple = (Triple)obj;
			return otherTriple.subject.equals(this.subject) && otherTriple.predicate.equals(this.predicate) && otherTriple.object.equals(this.object);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return this.getSubject().getName()+" "+this.getPredicate().getName()+" "+this.getObject().getName();
	}

	public Object get(int i) {
		if(i==0) {
			return this.subject;
		}
		if(i==1) {
			return this.predicate;
		}
		if(i==2) {
			return this.object;
		}
		return null;
	}

}
