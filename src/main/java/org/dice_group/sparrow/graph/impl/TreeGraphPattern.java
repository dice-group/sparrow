package org.dice_group.sparrow.graph.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.sparql.expr.Expr;

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
	
	
	public String toString(boolean b) {
		StringBuilder ret= new StringBuilder();
		if(!b) {
			if(this.getSubject().isURI()) {
				ret.append("<").append(this.getSubject()).append("> ");
			}else {
				ret.append(this.getSubject());				
			}
		}
		if(this.getPredicate().isURI()) {
			ret.append("\t-- <"+this.getPredicate()+"> --> ");
		}
		else {
			ret.append("\t-- "+this.getPredicate()+" --> ");
		}
		if(this.getObject().isURI()) {
			ret.append("<"+this.getObject()+">");
		}
		else {
			ret.append(this.getObject());

		}
		if(!filter.isEmpty()) {
			ret.append("[");
			for(Expr expr : filter) {
				ret.append(expr).append(", ");
			}
			ret.append("]");
		}
		for(int i=0;i<children.size();i++ ) {
			TreeGraphPattern tgp = children.get(i);
			if(i!=0) {
				ret.append("\t\t\t\t");
			}
			ret.append(tgp.toString(true)).append("\n");
		}
		
		return ret.toString();
	}
	
	@Override
	public String toString() {
		return toString(false);
	}
	
}
