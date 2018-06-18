package org.dice_group.sparrow.graph;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.dice_group.sparrow.annotation.SparqlAnnotation;
import org.dice_group.sparrow.owl.OWLNode;

public interface GraphNode {
	
	public void addAnnotation(SparqlAnnotation annotation);
	
	public HashSet<SparqlAnnotation> getAnnotations();
	

	public String useRule();
	
	
	public String getName();

	public List<Triple> getRelations();

	public void setRelations(List<Triple> relations);
	public void addRelation(Triple relation);

	
}
