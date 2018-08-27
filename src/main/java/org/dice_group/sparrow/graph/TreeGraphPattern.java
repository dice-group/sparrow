package org.dice_group.sparrow.graph;

import java.util.LinkedList;
import java.util.List;

public class TreeGraphPattern extends GraphPattern {

	private List<TreeGraphPattern> children = new LinkedList<TreeGraphPattern>();
	private List<TreeGraphPattern> parents = new LinkedList<TreeGraphPattern>();
	/**
	 * @return the children
	 */
	public List<TreeGraphPattern> getChildren() {
		return children;
	}
	/**
	 * @param children the children to set
	 */
	public void setChildren(List<TreeGraphPattern> children) {
		this.children = children;
	}
	/**
	 * @return the parents
	 */
	public List<TreeGraphPattern> getParents() {
		return parents;
	}
	/**
	 * @param parents the parents to set
	 */
	public void setParents(List<TreeGraphPattern> parents) {
		this.parents = parents;
	}
	/**
	 * @param arg0
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addChild(TreeGraphPattern arg0) {
		return children.add(arg0);
	}
	/**
	 * @param arg0
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addParent(TreeGraphPattern arg0) {
		return parents.add(arg0);
	}
	
	public boolean isLeaf() {
		return children.isEmpty();
	}
	
	public boolean isRoot() {
		return parents.isEmpty();
	}
	
	
}
