package org.dice_group.sparrow.graph;

import java.util.LinkedList;
import java.util.List;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.dice_group.sparrow.exceptions.GraphContainsCycleException;

public class DirectedAcyclicGraph<V> extends Graph<V, TreeGraphPattern> {

	private DirectedAcyclicGraph() {
	}

	public static <V> DirectedAcyclicGraph<V> create(Graph<V, TreeGraphPattern> g, Node root)
			throws GraphContainsCycleException {
		StringBuilder rules = new StringBuilder();
		if (containsCycle(g)) {
			throw new GraphContainsCycleException();
		}
		presort(g, root, g.edges, rules);
		List<TreeGraphPattern> dag = new LinkedList<TreeGraphPattern>(g.edges);
		DirectedAcyclicGraph<V> graph = new DirectedAcyclicGraph<V>();
		// sort edges into a topology list
//		while (dag.size() != g.edges.size()) {
//			List<TreeGraphPattern> tmp = getLeaves(g, dag);
//			dag.addAll(tmp);
//		}
		// Create Tree
		for (TreeGraphPattern treeNode : dag) {
			List<TreeGraphPattern> children = getNextChildren(g, treeNode);
			treeNode.setChildren(children);
			for (TreeGraphPattern child : children) {
				child.addParent(treeNode);
			}
		}
		graph.setVertices(g.vertices);
		graph.setEdges(dag);
		return graph;
	}

	private static <V> void presort(Graph<V, TreeGraphPattern> g, Node root, List<TreeGraphPattern> edges,
			StringBuilder rules) {
		List<TreeGraphPattern> remove = new LinkedList<TreeGraphPattern>();
		List<TreeGraphPattern> inverses = new LinkedList<TreeGraphPattern>();
		for (TreeGraphPattern edge : edges) {

			// check if object has path to root
			// does not matter if path is forward or backward or mixed
			// also root not a regular path to pattern
			if (hasDirectedPath(g, root, edge.getObject())) {
				continue;
			}
			if (hasUndirectedPath(g, root, edge.getObject())) {

				// if so inverse it
				remove.add(edge);
				TreeGraphPattern inverse = new TreeGraphPattern();
				inverse.setSubject(edge.getObject());
				inverse.setObject(edge.getSubject());
				String inverseUri = edge.getPredicate().getURI() + "Inverse";
				Node inversePredicate = NodeFactory.createURI(inverseUri);
				inverse.setPredicate(inversePredicate);
				inverses.add(inverse);
				// TODO add inverse to inverse Rules
			}
		}
		g.edges.removeAll(remove);
		g.edges.addAll(inverses);
	}

	private static <V> boolean hasDirectedPath(Graph<V, TreeGraphPattern> g, Node node, Node edge) {
		// check if there is a directed path from a node to a another node edge
		int getChildIndex = 0;
		List<TreeGraphPattern> parentTriples = new LinkedList<TreeGraphPattern>();
		for (TreeGraphPattern parent : g.edges) {
			if (parent.getSubject().equals(node)) {
				parentTriples.add(parent);
			}
		}
		List<TreeGraphPattern> childTriples = new LinkedList<TreeGraphPattern>();
		for (TreeGraphPattern parent : g.edges) {
			if (parent.getObject().equals(edge)) {
				parentTriples.add(parent);
			}
		}
		while (Sets.intersection(Sets.newHashSet(parentTriples), Sets.newHashSet(childTriples)).isEmpty()) {
			if(parentTriples.size() + childTriples.size() == g.edges.size()) {
				return false;
			}
			int i = parentTriples.size();

			for (TreeGraphPattern p : parentTriples.subList(getChildIndex, parentTriples.size())) {
				for(TreeGraphPattern child : getNextChildren(g, p)) {
					if(!parentTriples.contains(child)) {
						parentTriples.add(child);
					}
				}
				
			}
			if(i==parentTriples.size()) {
				//nothing changed, no more children
				return false;
			}
			getChildIndex = i;
		}

		return true;
	}

	private static <V> boolean hasUndirectedPath(Graph<V, TreeGraphPattern> g, Node node, Node edge) {
		// TODO check if there is a undirected path from a node to a an edge

		return false;
	}

	private static <V> List<TreeGraphPattern> getLeaves(Graph<V, TreeGraphPattern> g, List<TreeGraphPattern> ignore) {
		List<TreeGraphPattern> leaves = new LinkedList<TreeGraphPattern>();
		for (TreeGraphPattern edge1 : g.edges) {
			if (!hasPath(g, edge1, leaves)) {
				leaves.add(edge1);
			}
		}
		return leaves;
	}

	private static <V> boolean containsCycle(Graph<V, TreeGraphPattern> g) {
		for (TreeGraphPattern self : g.edges) {
			if (hasDirectedPath(g, self.getSubject(), self.getSubject())) {
				return true;
			}
		}
		return false;
	}

	private static <V> boolean hasPath(Graph<V, TreeGraphPattern> g, TreeGraphPattern edge,
			List<TreeGraphPattern> ignore) {
		for (TreeGraphPattern edge2 : g.edges) {
			if (!edge.equals(edge2) && !ignore.contains(edge2)) {
				if (edge2.getSubject().equals(edge.getObject())) {
					return true;
				}
			}
		}
		return false;
	}

	private static <V> List<TreeGraphPattern> getNextChildren(Graph<V, TreeGraphPattern> g, TreeGraphPattern parent) {
		List<TreeGraphPattern> children = new LinkedList<TreeGraphPattern>();
		for (TreeGraphPattern pattern : g.edges) {
			if (!parent.equals(pattern)) {
				if (pattern.getSubject().equals(parent.getObject())) {
					children.add(pattern);
				}
			}
		}
		return children;
	}

}
