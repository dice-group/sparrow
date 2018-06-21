package org.dice_group.sparrow.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.dice_group.sparrow.exceptions.GraphContainsCycleException;

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
		while (nodeIt.hasNext()) {
			GraphNode node = nodeIt.next();
			if (node.getName().equals(name)) {
				return node;
			}
		}
		return null;
	}

	public static void getAllNodes(GraphNode root, HashSet<GraphNode> ret, HashMap<GraphNode, List<Triple>> visited) {
		for (Triple t : root.getRelations()) {
	
			ret.add(t.object);
			ret.add(t.predicate);
			ret.add(t.subject);
			if(!visited.containsKey(t.object) || !visited.get(t.object).contains(t)) {
				if(visited.containsKey(t.object)) {
					visited.get(t.object).add(t);
				}
				else {
					visited.put(t.object, Lists.newArrayList(t));
				}
				getAllNodes(t.object, ret, visited);
			}
			if(!visited.containsKey(t.predicate) || !visited.get(t.predicate).contains(t)) {
				if(visited.containsKey(t.predicate)) {
					visited.get(t.predicate).add(t);
				}
				else {
					visited.put(t.predicate, Lists.newArrayList(t));
				}
				getAllNodes(t.predicate, ret, visited);
			}
			if(!visited.containsKey(t.subject) || !visited.get(t.subject).contains(t)) {
				if(visited.containsKey(t.subject)) {
					visited.get(t.subject).add(t);
				}
				else {
					visited.put(t.subject, Lists.newArrayList(t));
				}
				getAllNodes(t.subject, ret, visited);
			}
		}
	}

	public static HashSet<GraphNode> getAllNodes(GraphNode root) {
		HashSet<GraphNode> ret = new HashSet<GraphNode>();
		HashMap<GraphNode,List<Triple>> visitedEdges = new HashMap<GraphNode,List<Triple>>();
		for (Triple t : root.getRelations()) {
			visitedEdges.put(root, Lists.newArrayList(t));
			ret.add(t.object);
			ret.add(t.predicate);
			ret.add(t.subject);
			getAllNodes(t.object, ret, visitedEdges);
			getAllNodes(t.predicate, ret, visitedEdges);
			getAllNodes(t.subject, ret, visitedEdges);
		}
		return ret;
	}

	public static void checkCycles(HashSet<GraphNode> graph) throws GraphContainsCycleException {
		for (GraphNode n : graph) {
			if (checkPathToNode(n, n, new LinkedList<GraphNode>())) {
				throw new GraphContainsCycleException();
			}
		}
	}

	public static boolean checkPathToNode(GraphNode source, GraphNode dest, List<GraphNode> inPath) {
		inPath.add(source);
		for (Triple t : source.getRelations()) {
			if (t.subject.equals(source)) {
				if (t.object.equals(dest)) {
					return true;
				} else if (inPath.contains(t.object)) {
					continue;
				} else {
					if (checkPathToNode(t.object, dest, inPath)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean checkBetterWay(GraphNode rootNode, GraphNode currentNode, List<GraphNode> curPath,
			List<GraphNode> path) {
		curPath.add(currentNode);
		for (Triple t : currentNode.getRelations()) {
			if (t.object.equals(currentNode)) {
				if (t.subject.equals(rootNode) || path.contains(t.subject)) {
					return true;
				} else if (curPath.contains(t.subject)) {
					continue;
				} else {
					return checkBetterWay(rootNode, t.subject, curPath, path);
				}
			}
		}
		return false;
	}

	public static boolean checkOneZigZagToNode(GraphNode source, GraphNode dest) {
		for (Triple t : source.getRelations()) {
			if (t.object.equals(source)) {
				if (checkPathToNode(t.subject, dest, new LinkedList<GraphNode>())) {
					return true;
				} else {
					return checkOneZigZagToNode(t.subject, dest);
				}
			}
		}
		return false;
	}

}
