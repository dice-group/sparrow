package org.dice_group.sparrow.graph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dice_group.sparrow.annotation.SparqlAnnotation;

public abstract class AbstractGraphNode implements GraphNode {

	private Set<SparqlAnnotation> annotations = new HashSet<SparqlAnnotation>();
	private List<GraphNode> children = new LinkedList<GraphNode>();
	private List<GraphNode> parents = new LinkedList<GraphNode>();
	
	@Override
	public List<GraphNode> getParents(){
		return this.parents;
	}
	
	@Override
	public List<GraphNode> getChildren(){
		return this.children;
	}

	/**
	 * @return the annotations
	 */
	public Set<SparqlAnnotation> getAnnotations() {
		return annotations;
	}

	/**
	 * @param annotations the annotations to set
	 */
	public void setAnnotations(Set<SparqlAnnotation> annotations) {
		this.annotations = annotations;
	}
}
