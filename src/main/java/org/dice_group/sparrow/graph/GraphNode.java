package org.dice_group.sparrow.graph;

import java.util.List;
import java.util.Map;

public interface GraphNode {
	
	public void createRules();
	
	public void execute();	
	
	public Map<String, OWLNode> useRule();
	
	public List<GraphNode> getChildren();
	
	public List<GraphNode> getParents();
	
	public String getName();
}
