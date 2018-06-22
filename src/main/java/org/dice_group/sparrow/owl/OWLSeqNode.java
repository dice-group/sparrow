package org.dice_group.sparrow.owl;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.jena.sparql.expr.RegexJava;

public class OWLSeqNode implements OWLNode {

	private List<OWLNode> owlNodes = new LinkedList<OWLNode>();

	public void addNode(OWLNode owlNode) {
		if(owlNode!=null)
			this.owlNodes.add(owlNode);
	}

	@Override
	public void setValue(String base) {
		this.owlNodes = new LinkedList<OWLNode>();
		owlNodes.add(new BaseOWLNode(base));
	}

	@Override
	public String getValue() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < owlNodes.size() - 1; i++) {
			if(owlNodes.get(i).getValue().trim().matches(".*\\s+.*")) {
				builder.append("(");
			}
			builder.append(owlNodes.get(i));
			if(owlNodes.get(i).getValue().trim().matches(".*\\s+.*")) {
				builder.append(")");
			}
			builder.append(" AND ");
		}
		if (!owlNodes.isEmpty()) {
			if(owlNodes.get(owlNodes.size() - 1).getValue().trim().matches(".*\\s+.*")) {
				builder.append("(");
			}
			builder.append(owlNodes.get(owlNodes.size() - 1));
			if(owlNodes.get(owlNodes.size() - 1).getValue().trim().matches(".*\\s+.*")) {
				builder.append(")");
			}
		}
		return builder.toString();
	}

	@Override
	public String build() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < owlNodes.size() - 1; i++) {
			if(owlNodes.get(i).build().trim().matches(".*\\s+.*")) {
				builder.append("(");
			}
			builder.append(owlNodes.get(i));
			if(owlNodes.get(i).build().trim().matches(".*\\s+.*")) {
				builder.append(")");
			}
			builder.append(" AND ");
		}
		if (!owlNodes.isEmpty()) {
			if(owlNodes.get(owlNodes.size() - 1).build().trim().matches(".*\\s+.*")) {
				builder.append("(");
			}
			builder.append(owlNodes.get(owlNodes.size() - 1));
			if(owlNodes.get(owlNodes.size() - 1).build().trim().matches(".*\\s+.*")) {
				builder.append(")");
			}
		}
		return builder.toString();
	}

	@Override
	public String toString() {
		return getValue();
	}

	public OWLNode getLastNode() {
		if(owlNodes.isEmpty())
			return null;
		return owlNodes.get(owlNodes.size()-1);
	}

	public OWLNode getFirstNode() {
		if(owlNodes.isEmpty())
			return null;
		return owlNodes.get(0);
	}

	public void exchangeLastNode(GroupOWLNode exchange) {
		owlNodes.remove(owlNodes.size()-1);
		owlNodes.add(exchange);
	}
}
