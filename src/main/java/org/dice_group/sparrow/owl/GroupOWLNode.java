package org.dice_group.sparrow.owl;

public class GroupOWLNode implements OWLNode {

	private OWLNode child;
	private OWLNode parent;

	public void setChild(OWLNode owlNode) {

		this.child = owlNode;
	}

	@Override
	public void setValue(String base) {
		this.setParent(new BaseOWLNode(base));
	}

	public void setParent(OWLNode owlNode) {
		this.parent = owlNode;
	}

	@Override
	public String getValue() {
		if (child == null || child.getValue().isEmpty()) {
			return parent.getValue();
		}
		return parent.getValue() + "(" + child.getValue() + ")";
	}

	@Override
	public String toString() {
		return getValue();
	}

	@Override
	public String build() {
		if(child==null||(child instanceof OWLSeqNode && ((OWLSeqNode)child).getFirstNode()==null)) {
			return parent.build();
		}
		if(child!=null) {
			child.build();
		}
		if(parent!=null) {
			parent.build();
		}
		OWLNode leftNode = getFirstBaseNode(child);
		OWLNode rightNode = getLastBaseNode(parent);
		String rightStr = rightNode.getValue();
		String[] rightParts = rightStr.split("\\s+");
		String childStart = leftNode.getValue();
		if(childStart.matches(".*\\s+.*")) {
			childStart = childStart.split("\\s+")[0];
		}

		if (rightParts.length > 1 && rightParts[rightParts.length - 2].toLowerCase().equals("some")
				&& rightParts[rightParts.length - 1].trim().equals(childStart.trim())) {
			//TODO
			GroupOWLNode exchange = new GroupOWLNode();
			StringBuilder newParStr = new StringBuilder();
			for (int i = 0; i < rightParts.length - 2; i++) {
				newParStr.append(rightParts[i]);
			}
			newParStr.append(" SOME ");
			BaseOWLNode newParent = new BaseOWLNode(newParStr.toString());
			exchange.setParent(newParent);
			exchange.setChild(child);
			if(!rightNode.equals(parent)) {
				exchange(exchange, rightNode, parent);
			}else {
				parent=exchange;
			}
			child=null;
		}
		else if(rightNode.getValue().trim().endsWith(")") && leftNode.getValue().trim().startsWith("(")) {
			GroupOWLNode newChild = new GroupOWLNode();
			newChild.setChild(child);
			newChild.setParent(new BaseOWLNode("SOME"));
			child = newChild;
		}
	return getValue();

	}

	private void exchange(GroupOWLNode exchange, OWLNode rightNode, OWLNode parent2) {
		if(parent2 instanceof OWLSeqNode) {
			if(((OWLSeqNode) parent2).getLastNode().equals(rightNode)) {
				((OWLSeqNode) parent2).exchangeLastNode(exchange);
			}
			else {
				exchange(exchange, rightNode, ((OWLSeqNode) parent2).getLastNode());
			}
		}
		else {
			if(((GroupOWLNode)parent2).child.equals(rightNode)) {
				((GroupOWLNode)parent2).setChild(exchange);
			}
			else {
				exchange(exchange, rightNode, ((GroupOWLNode)parent2).child);
			}
		}
	}

	private OWLNode getLastBaseNode(OWLNode parent2) {
		if(parent2 instanceof BaseOWLNode) {
			return parent2;
		}
		else if(parent2 instanceof OWLSeqNode) {
			
			return getLastBaseNode(((OWLSeqNode) parent2).getLastNode());
		
		}
		else if(parent2 instanceof GroupOWLNode) {
			GroupOWLNode parGroup = (GroupOWLNode)parent2;
			if(parGroup.child==null) {
				return getLastBaseNode(parGroup.parent);
			}
			else {
				OWLNode last = getLastBaseNode(parGroup.child);
				if(last==null) {
					return getLastBaseNode(parGroup.parent);
				}
				return last;
			}
		}
		return null;
		
	}

	private OWLNode getFirstBaseNode(OWLNode child2) {
		if(child2 instanceof BaseOWLNode) {
			return child2;
		}
		else if(child2 instanceof OWLSeqNode) {
			return getFirstBaseNode(((OWLSeqNode) child2).getFirstNode());
		}
		return getFirstBaseNode(((GroupOWLNode) child2).parent);
	}

	public OWLNode getParent() {
		return this.parent;
	}

	public OWLNode getChild() {
		return child;
	}

}
