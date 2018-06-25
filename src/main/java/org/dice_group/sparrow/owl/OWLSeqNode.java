package org.dice_group.sparrow.owl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.jena.sparql.expr.RegexJava;

public class OWLSeqNode implements OWLNode {

	private List<OWLNode> owlNodes = new LinkedList<OWLNode>();

	/**
	 * @return the owlNodes
	 */
	public List<OWLNode> getOwlNodes() {
		return owlNodes;
	}

	public void addNode(OWLNode owlNode) {
		if (owlNode != null)
			checkNodes(owlNode);
	}

	private void checkNodes(OWLNode newNode) {
		OWLNode node1=null;
		OWLNode node2=null;
		for (OWLNode owlNode : new LinkedList<OWLNode>(owlNodes)) {
			if (newNode.getValue().equals(owlNode.getValue())) {
				return;
			}
			if(newNode instanceof BaseOWLNode) {
				node2 = newNode;
			}
			else if (newNode instanceof GroupOWLNode) {
				node2 = ((GroupOWLNode) newNode).getParent();
			}
			else if (newNode instanceof OWLSeqNode) {
				node2 = ((OWLSeqNode) newNode).getFirstNode();
			}
			
			if (owlNode instanceof BaseOWLNode) {
				node1 = owlNode;
			} else if (owlNode instanceof GroupOWLNode) {
				node1 = ((GroupOWLNode) owlNode).getParent();
			}
			else if (owlNode instanceof OWLSeqNode) {
				node1 = ((OWLSeqNode) owlNode).contains(node2);
			}
			
			if(node2!=null && node1 !=null && node2.getValue().equals(node1.getValue())) {
				//b=b 
				combineTwoNodes(owlNode, newNode);
				break;
			}
		}
		owlNodes.add(newNode);
	}

	private OWLNode contains(OWLNode node2) {
		for(OWLNode node : owlNodes) {
			if(node.getValue().equals(node2.getValue())) {
				return node;
			}
		}
		return null;
	}

	private void remove(OWLNode node) {
		owlNodes.remove(node);
	}
	
	private void combineTwoNodes(OWLNode owlNode, OWLNode newNode) {
		if (newNode instanceof GroupOWLNode) {
			if (owlNode instanceof BaseOWLNode) {
				//exchange newNode and owlNode
				owlNodes.remove(owlNode);
				owlNodes.add(newNode);
				return;
			} else if (owlNode instanceof GroupOWLNode) {
				
				GroupOWLNode newGroup = new GroupOWLNode();
				newGroup.setParent(((GroupOWLNode) owlNode).getParent());
				OWLSeqNode child = new OWLSeqNode();
				child.addNode(((GroupOWLNode) newNode).getChild());
				child.addNode(((GroupOWLNode) owlNode).getChild());
				owlNodes.remove(owlNode);
				owlNodes.add(newGroup);
			}
			else if (owlNode instanceof OWLSeqNode) {
				OWLNode node = ((GroupOWLNode) newNode).getChild();
				((OWLSeqNode) owlNode).remove(node);
				((OWLSeqNode) owlNode).addNode(newNode);
			}
		}
		else if (newNode instanceof OWLSeqNode) {
			if (owlNode instanceof BaseOWLNode) {
				//exchange newNode and owlNode
				owlNodes.remove(owlNode);
				owlNodes.add(newNode);
				return;
			} else if (owlNode instanceof GroupOWLNode) {
				OWLNode node = ((GroupOWLNode) owlNode).getChild();
				((OWLSeqNode) newNode).remove(node);
				((OWLSeqNode) newNode).addNode(owlNode);
				owlNodes.remove(owlNode);
				owlNodes.add(newNode);
			}
			else if (owlNode instanceof OWLSeqNode) {
				for(OWLNode n : ((OWLSeqNode) newNode).getOwlNodes()) {
					((OWLSeqNode) owlNode).addNode(n);
				}
			}
		}
		
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
			if (owlNodes.get(i).getValue().trim().matches(".*\\s+.*")) {
				builder.append("(");
			}
			builder.append(owlNodes.get(i));
			if (owlNodes.get(i).getValue().trim().matches(".*\\s+.*")) {
				builder.append(")");
			}
			builder.append(" AND ");
		}
		if (!owlNodes.isEmpty()) {
			if (owlNodes.get(owlNodes.size() - 1).getValue().trim().matches(".*\\s+.*")) {
				builder.append("(");
			}
			builder.append(owlNodes.get(owlNodes.size() - 1));
			if (owlNodes.get(owlNodes.size() - 1).getValue().trim().matches(".*\\s+.*")) {
				builder.append(")");
			}
		}
		return builder.toString();
	}

	@Override
	public String build() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < owlNodes.size() - 1; i++) {
			if (owlNodes.get(i).build().trim().matches(".*\\s+.*")) {
				builder.append("(");
			}
			builder.append(owlNodes.get(i));
			if (owlNodes.get(i).build().trim().matches(".*\\s+.*")) {
				builder.append(")");
			}
			builder.append(" AND ");
		}
		if (!owlNodes.isEmpty()) {
			if (owlNodes.get(owlNodes.size() - 1).build().trim().matches(".*\\s+.*")) {
				builder.append("(");
			}
			builder.append(owlNodes.get(owlNodes.size() - 1));
			if (owlNodes.get(owlNodes.size() - 1).build().trim().matches(".*\\s+.*")) {
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
		if (owlNodes.isEmpty())
			return null;
		return owlNodes.get(owlNodes.size() - 1);
	}

	public OWLNode getFirstNode() {
		if (owlNodes.isEmpty())
			return null;
		return owlNodes.get(0);
	}

	public void exchangeLastNode(GroupOWLNode exchange) {
		owlNodes.remove(owlNodes.size() - 1);
		owlNodes.add(exchange);
	}
}
