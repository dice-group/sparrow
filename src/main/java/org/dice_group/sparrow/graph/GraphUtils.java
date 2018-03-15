package org.dice_group.sparrow.graph;

public class GraphUtils {


	/**
	 * Searches a Graph based upon the TreeNode node and checks if the Graph has
	 * a 
	 * 
	 * 
	 * @param name unique name of the treeNode (example a Var)
	 * @param node
	 * @return
	 */
	public static GraphNode getNodeWithName(String name, GraphNode node) {
		for(GraphNode subNode : node.getParents()) {
			if(name.equals(subNode.getName())) {
				//found the treeNode
				return subNode;
			}
		}
		for(GraphNode subNode : node.getChildren()) {
			if(name.equals(subNode.getName())) {
				//found the treeNode
				return subNode;
			}
		}
		return null;
	}
}
