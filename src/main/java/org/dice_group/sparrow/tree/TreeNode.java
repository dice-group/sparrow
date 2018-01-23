package org.dice_group.sparrow.tree;

import java.util.Map;

public interface TreeNode {

	public void createRules();
	
	public void execute();	
	
	public Map<String, OWLNode> useRule();
	
	
}
