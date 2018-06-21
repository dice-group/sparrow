package org.dice_group.sparrow.owl;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class OWLQuery {

	public List<OWLNode> sequence = new LinkedList<OWLNode>();

	public void addOWLNode(OWLNode node) {
		sequence.add(node);
	}

	public void putOWLNode(OWLNode node, int index) {
		sequence.add(index, node);
	}

	public List<OWLNode> getOWLNodeByName(String name) {
		List<OWLNode> nodesWithName = new LinkedList<OWLNode>();
		for (OWLNode node : this.sequence) {
			if (name.equals(node.name)) {
				nodesWithName.add(node);
			}
		}
		return nodesWithName;
	}

	public OWLNode getOWLNodeByIndex(int index) {
		return sequence.get(index);
	}

	@Override
	public String toString() {
		StringBuilder owlString = new StringBuilder();
		for (OWLNode node : sequence) {
			if (node instanceof OWLSeqNode)
				owlString.append("( ");
			owlString.append(node.toString()).append(" ");
			if (node instanceof OWLSeqNode)
				owlString.append(") ");
		}
		return owlString.toString();
	}

	public OWLQuery build() {
		List<OWLNode> tmpSeq = new LinkedList<OWLNode>();
		// tmpSeq.add(sequence.get(0));
		for (int i = 0; i < sequence.size(); i++) {
			if (sequence.get(i) instanceof OWLSeqNode) {
				OWLSeqNode seqNode = (OWLSeqNode) sequence.get(i);
				seqNode = seqNode.build();
				tmpSeq.add(seqNode);
			} else if (sequence.get(i) instanceof OWLNode) {
				if (sequence.get(i).name.contains("?")) {
					String repl = sequence.get(i).name;
					repl = Pattern.compile("\\?[a-zA-Z0-9]+").matcher(repl).replaceAll("Thing");
					tmpSeq.add(new OWLNode(repl));
				} else {
					tmpSeq.add(sequence.get(i));
				}
				if (sequence.get(i).name.endsWith(OWLNode.SOME_NODE.name)) {
					if (i < sequence.size() - 1) {
						if (!sequence.get(i + 1).name.startsWith("(")
								&& !sequence.get(i + 1).name.startsWith(OWLNode.THING_NODE.name)) {
							tmpSeq.add(OWLNode.GROUP_START_NODE);
							tmpSeq.add(OWLNode.THING_NODE);
							tmpSeq.add(OWLNode.GROUP_END_NODE);
						}
					} else {
						tmpSeq.add(OWLNode.GROUP_START_NODE);
						tmpSeq.add(OWLNode.THING_NODE);
						tmpSeq.add(OWLNode.GROUP_END_NODE);
					}
				}
			}

		}
		sequence = tmpSeq;
		return this;
	}

}
