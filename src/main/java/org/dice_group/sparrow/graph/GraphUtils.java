package org.dice_group.sparrow.graph;

import java.util.HashSet;
import java.util.Iterator;

public class GraphUtils {

	/**
	 * Searches a Graph based upon the TreeNode node and checks if the Graph has a
	 * 
	 * 
	 * @param name
	 *            unique name of the treeNode (example a Var)
	 * @param graph
	 * @return the GraphNode or null
	 */
	public static GraphNode getNodeWithName(String name, Triple[] graph) {
		for (Triple triple : graph) {
			if (name.equals(triple.getSubject().getName())) {
				return triple.getSubject();
			}
			if (name.equals(triple.getObject().getName())) {
				return triple.getObject();
			}
			if (name.equals(triple.getPredicate().getName())) {
				return triple.getPredicate();
			}
		}
		return null;
	}
	
	/**
	 * Searches a Graph based upon the TreeNode node and checks if the Graph has a
	 * 
	 * 
	 * @param name
	 *            unique name of the treeNode (example a Var)
	 * @param graph
	 * @return the GraphNode or null
	 */
	public static GraphNode getNodeWithName(String name, HashSet<GraphNode> graph) {
		Iterator<GraphNode> nodeIt = graph.iterator();
		while(nodeIt.hasNext()) {
			GraphNode node =nodeIt.next();
			if(node.getName().equals(name)) {
				return node;
			}
		}
		return null;
	}
}
