package org.dice_group.sparrow.owl;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.jena.sparql.expr.RegexJava;

public class OWLSeqNode extends OWLNode {

	private List<OWLNode> child = new LinkedList<OWLNode>();
	private String initial = "";
	private boolean initialIsComplex = false;

	public OWLSeqNode(String string) {
		super("");
		initial = string;
	}

	public OWLSeqNode() {
		super("");
	}

	public OWLSeqNode(OWLNode initial) {
		this(initial.toString());
		if (initial instanceof OWLSeqNode) {
			initialIsComplex = !((OWLSeqNode) initial).child.isEmpty();
		}
	}

	public OWLSeqNode putChild(OWLNode node) {
		if (!this.initial.isEmpty()||!child.isEmpty())
			child.add(OWLNode.AND_NODE);
		// child.add(OWLNode.SOME_NODE);
		child.add(OWLNode.GROUP_START_NODE);
		child.add(node);
		child.add(OWLNode.GROUP_END_NODE);
		this.name = createNodeString();
		return this;
	}

	private String createNodeString() {
		StringBuilder builder = new StringBuilder(initial).append(" ");
		for (OWLNode children : child) {
			builder.append(children).append(" ");
		}
		return builder.toString().replaceAll("\\s+", " ");
	}

	@Override
	public String toString() {
		return createNodeString();
	}

	public OWLSeqNode build() {
		List<OWLNode> tmpSeq = new LinkedList<OWLNode>();
		OWLNode currentNode = new OWLNode(initial);
		for (int i = -1; i < child.size(); i++) {
			if(i>=0) {
				currentNode = child.get(i);
			}
			if (currentNode instanceof OWLSeqNode) {
				OWLSeqNode seqNode = (OWLSeqNode) child.get(i);
				seqNode = seqNode.build();
				tmpSeq.add(seqNode);
			}
			else if (currentNode instanceof OWLNode) {
				if (currentNode.name.contains("?")) {
					String repl = currentNode.name;
					repl = Pattern.compile("\\?[a-zA-Z0-9]+").matcher(repl).replaceAll("Thing");
					
//					repl.replaceAll("\\?[a-zA-Z0-9]+", "Thing");
					tmpSeq.add(new OWLNode(repl));
				} else {
					tmpSeq.add(currentNode);
				}
				if (currentNode.name.endsWith(OWLNode.SOME_NODE.name)) {
					if (i < child.size() - 1) {
						if (!child.get(i + 1).name.startsWith("(")
								&& !child.get(i + 1).name.startsWith(OWLNode.THING_NODE.name)) {
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
		initial="";
		child = tmpSeq;
		return this;
	}
}
