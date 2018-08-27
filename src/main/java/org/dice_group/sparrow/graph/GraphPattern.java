package org.dice_group.sparrow.graph;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.Expr;
import org.dice_group.sparrow.rule.Rule;

public class GraphPattern {

	private Node subject;
	/**
	 * @return the subject
	 */
	public Node getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(Node subject) {
		this.subject = subject;
	}

	/**
	 * @return the predicate
	 */
	public Node getPredicate() {
		return predicate;
	}

	/**
	 * @param predicate the predicate to set
	 */
	public void setPredicate(Node predicate) {
		this.predicate = predicate;
	}

	/**
	 * @return the object
	 */
	public Node getObject() {
		return object;
	}

	/**
	 * @param object the object to set
	 */
	public void setObject(Node object) {
		this.object = object;
	}

	private Node predicate;
	private Node object;

	private List<Expr> filter = new LinkedList<Expr>();

	public boolean addIfFit(Expr expr) {
		for (Var v : expr.getVarsMentioned()) {
			if (subject.isVariable() && v.getVarName().equals(subject.toString())) {
				filter.add(expr);
				return true;
			}
			if (predicate.isVariable() && v.getVarName().equals(subject.toString())) {
				filter.add(expr);
				return true;
			}
			if (object.isVariable() && v.getVarName().equals(subject.toString())) {
				filter.add(expr);
				return true;
			}
		}
		return false;
	}

	public void setPattern(TriplePath path) {
		subject = path.getSubject();
		predicate = path.getPredicate();
		object = path.getObject();
	}
	
	public boolean fitRule(Rule r) {
		return true;
	}

}
