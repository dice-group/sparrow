package org.dice_group.sparrow.graph;

import java.util.LinkedList;
import java.util.List;

public class Graph<V, E> {

	protected List<V> vertices = new LinkedList<V>();
	protected List<E> edges = new LinkedList<E>();

	/**
	 * @param e
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addNode(V e) {
		return vertices.add(e);
	}




	/**
	 * @param e
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addEdge(E e) {
		return edges.add(e);
	}

	
	/**
	 * @return the vertices
	 */
	public List<V> getVertices() {
		return vertices;
	}

	/**
	 * @param vertices the vertices to set
	 */
	public void setVertices(List<V> vertices) {
		this.vertices = vertices;
	}

	/**
	 * @return the edges
	 */
	public List<E> getEdges() {
		return edges;
	}

	/**
	 * @param edges the edges to set
	 */
	public void setEdges(List<E> edges) {
		this.edges = edges;
	}
	
}
