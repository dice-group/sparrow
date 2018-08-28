package org.dice_group.sparrow.graph.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.dice_group.sparrow.exceptions.GraphContainsCycleException;

public class DirectedAcyclicGraph<V> extends GraphImpl<V, TreeGraphPattern> {

	private Set<String> rules = new HashSet<String>();


	private List<TreeGraphPattern> roots = new LinkedList<TreeGraphPattern>();


	private DirectedAcyclicGraph() {
	}
	
	/**
	 * @return the roots
	 */
	public List<TreeGraphPattern> getRoots() {
		return roots;
	}

	/**
	 * @return the rules
	 */
	public Set<String> getRules() {
		return rules;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if(roots.size()>0)
			builder.append(roots.get(0)).append("\n");
		for(int i=1;i<roots.size();i++) {
			TreeGraphPattern root = roots.get(i);
			builder.append("").append(root.toString(true)).append("\n");
		}
		return builder.toString();
	}
	
	public static <V> DirectedAcyclicGraph<V> create(GraphImpl<V, TreeGraphPattern> g, Node root)
			throws GraphContainsCycleException {
		Set<String> rules = new HashSet<String>();
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
		for(TreeGraphPattern treeNode : dag) {
			if(treeNode.isRoot()) {
				graph.roots.add(treeNode);
			}
		}
		graph.setVertices(g.vertices);
		graph.setEdges(dag);
		graph.rules=rules;
		return graph;
	}

	private static <V> void presort(GraphImpl<V, TreeGraphPattern> g, Node root, List<TreeGraphPattern> edges,
			Set<String> rules) {
		List<TreeGraphPattern> remove = new LinkedList<TreeGraphPattern>();
		List<TreeGraphPattern> inverses = new LinkedList<TreeGraphPattern>();
		for (TreeGraphPattern edge : edges) {

			// check if object has path to root
			// does not matter if path is forward or backward or mixed
			// also root not a regular path to pattern
			if (hasDirectedPath(g, root, edge.getObject())) {
				continue;
			}
			if(hasDirectedPath(g, root, edge.getSubject()) &&!edge.getSubject().equals(root)) {
				continue;
			}
			if (hasUndirectedPath(g, root, edge.getObject()))   {

				// if so inverse it
				remove.add(edge);
				TreeGraphPattern inverse = new TreeGraphPattern();
				inverse.setSubject(edge.getObject());
				inverse.setObject(edge.getSubject());
				String inverseUri = edge.getPredicate().getURI() + "Inverse";
				Node inversePredicate = NodeFactory.createURI(inverseUri);
				inverse.setPredicate(inversePredicate);
				inverses.add(inverse);
				StringBuilder r = new StringBuilder();
				r.append("<").append(inversePredicate.getURI()).append(">");
				r.append(" rdf:type owl:ObjectProperty ;\n\t  owl:inverseOf ");
				r.append("<").append(edge.getPredicate().getURI()).append(">").append(" . \n");
				rules.add(r.toString());
			}
		}
		g.edges.removeAll(remove);
		g.edges.addAll(inverses);
	}

	private static <V> boolean hasDirectedPath(GraphImpl<V, TreeGraphPattern> g, Node node, Node edge) {
		// check if there is a directed path from a node to a another node edge
		List<TreeGraphPattern> parentTriples = new LinkedList<TreeGraphPattern>();
		for (TreeGraphPattern parent : g.edges) {
			if (parent.getSubject().equals(node)) {
				parentTriples.add(parent);
			}
		}
		int tripleIndex=0;
		while(tripleIndex<parentTriples.size()) {
			for(int i=tripleIndex;i<parentTriples.size();i++) {
				if(parentTriples.get(i).getObject().equals(edge)) {
					return true;
				}
				//get all connections
				List<TreeGraphPattern> conns = getNextChildren(g, parentTriples.get(i));
				for(TreeGraphPattern con : conns) {
					if(!parentTriples.contains(con)) {
						parentTriples.add(con);
					}
				}
				tripleIndex++;
			}
		}
		return false;
	}

	private static <V> boolean hasUndirectedPath(GraphImpl<V, TreeGraphPattern> g, Node node, Node edge) {
		// check if there is a undirected path from a node to a an edge
		List<TreeGraphPattern> parentTriples = new LinkedList<TreeGraphPattern>();
		if(node.equals(edge)) {
			return true;
		}
		for (TreeGraphPattern parent : g.edges) {
			if (parent.getSubject().equals(node) || parent.getObject().equals(node)) {
				parentTriples.add(parent);
			}
		}
		int tripleIndex=0;
		while(tripleIndex<parentTriples.size()) {
			for(int i=tripleIndex;i<parentTriples.size();i++) {
				if( parentTriples.get(i).getSubject().equals(edge)) {
					return true;
				}
				//get all connections
				List<TreeGraphPattern> conns = getConnections(g, parentTriples.get(i));
				for(TreeGraphPattern con : conns) {
					if(!parentTriples.contains(con)) {
						parentTriples.add(con);
					}
				}
				tripleIndex++;
			}
		}
		return false;
	}

	private static <V> List<TreeGraphPattern> getConnections(GraphImpl<V, TreeGraphPattern> g, TreeGraphPattern edge) {
		List<TreeGraphPattern> conns = new LinkedList<TreeGraphPattern>();
		for (TreeGraphPattern con : g.edges) {
			if(con.getObject().equals(edge.getSubject())||con.getSubject().equals(edge.getObject())) {
				conns.add(con);
			}
		}
		return conns;
	}

	private static <V> List<TreeGraphPattern> getLeaves(GraphImpl<V, TreeGraphPattern> g, List<TreeGraphPattern> ignore) {
		List<TreeGraphPattern> leaves = new LinkedList<TreeGraphPattern>();
		for (TreeGraphPattern edge1 : g.edges) {
			if (!hasPath(g, edge1, leaves)) {
				leaves.add(edge1);
			}
		}
		return leaves;
	}

	private static <V> boolean containsCycle(GraphImpl<V, TreeGraphPattern> g) {
		for (TreeGraphPattern self : g.edges) {
			if (hasDirectedPath(g, self.getSubject(), self.getSubject())) {
				return true;
			}
		}
		return false;
	}

	private static <V> boolean hasPath(GraphImpl<V, TreeGraphPattern> g, TreeGraphPattern edge,
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

	private static <V> List<TreeGraphPattern> getNextChildren(GraphImpl<V, TreeGraphPattern> g, TreeGraphPattern parent) {
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
