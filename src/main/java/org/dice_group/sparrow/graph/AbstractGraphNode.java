package org.dice_group.sparrow.graph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dice_group.sparrow.annotation.SparqlAnnotation;

public abstract class AbstractGraphNode implements GraphNode {

	private HashSet<SparqlAnnotation> annotations = new HashSet<SparqlAnnotation>();
	
	private List<Triple> relations = new LinkedList<Triple>();

	
	@Override
	public void addAnnotation(SparqlAnnotation annotation) {
		this.annotations.add(annotation);
	}
	
	public HashSet<SparqlAnnotation> getAnnotations(){
		return this.annotations;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof GraphNode) {
			if(((GraphNode) obj).getName().equals(this.getName())){
				return specializedEquals(obj);
			}
		}
		return false;
	}

	public abstract boolean specializedEquals(Object obj);

	/**
	 * @return the relations
	 */
	@Override
	public List<Triple> getRelations() {
		return relations;
	}

	/**
	 * @param relations the relations to set
	 */
	@Override
	public void setRelations(List<Triple> relations) {
		this.relations = relations;
	}
	
	@Override
	public void addRelation(Triple relation) {
		this.relations.add(relation);
	}
	
	@Override
	public int hashCode() {
		return this.getName().hashCode();
	}
	
	@Override
	public String toString() {
		return this.getName()+"["+this.getRelations()+"]{"+this.getAnnotations()+"}";
	}
	
	@Override
	public String useRule() {
		return getName();
	}
}
