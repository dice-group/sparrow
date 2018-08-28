package org.dice_group.sparrow.graph;

import java.util.List;

public interface Graph<V, E> {

	/**
	 * @param e
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addNode(V e);



	/**
	 * @param e
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addEdge(E e) ;

	
	/**
	 * @return the vertices
	 */
	public List<V> getVertices();
	/**
	 * @param vertices the vertices to set
	 */
	public void setVertices(List<V> vertices) ;

	/**
	 * @return the edges
	 */
	public List<E> getEdges() ;

	/**
	 * @param edges the edges to set
	 */
	public void setEdges(List<E> edges);
	
}
