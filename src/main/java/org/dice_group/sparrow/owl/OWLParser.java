package org.dice_group.sparrow.owl;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class OWLParser {

	public static OWLNode parseIt(String expr) {
		List<PosOwl> parentRoute = new LinkedList<PosOwl>();
		PosOwl exprNode = new PosOwl();
		exprNode.setPos(0);
		exprNode.setNode(new BaseOWLNode(expr));
		do {
			if(exprNode.isFinished()) {
				if(parentRoute.isEmpty()) {
					return exprNode.getNode();
				}
				exprNode = parentWork(parentRoute, exprNode);
			}
			if (isSeq(exprNode)) {
				exprNode = seqWork(parentRoute, exprNode);
			} else if (isGroup(exprNode)) {
				exprNode = groupWork(parentRoute, exprNode);
			}
			else {
				// return and set nodes
				if(parentRoute.isEmpty()) {
					//done with parsing
					return exprNode.getNode();
				}
				exprNode = parentWork(parentRoute, exprNode);

			}
		}while (!parentRoute.isEmpty())	;
		return exprNode.getNode();
	}
	
	private static boolean isGroup(PosOwl exprNode) {
		// TODO Auto-generated method stub
		return false;
	}

	private static boolean isSeq(PosOwl exprNode) {
		// TODO Auto-generated method stub
		return false;
	}

	private static PosOwl groupWork(List<PosOwl> parentRoute, PosOwl exprNode) {
		GroupOWLNode node = new GroupOWLNode();
		String[] cmps = getGroupComps(exprNode);
		node.setChild(new BaseOWLNode(cmps[1]));
		node.setParent(new BaseOWLNode(cmps[0]));
		PosOwl parent = new PosOwl();
		parent.setNode(node);
		parentRoute.add(parent);
		PosOwl groupParent = new PosOwl();
		groupParent.setPos(0);
		groupParent.setNode(node.getParent());
		return groupParent;
	}
	
	private static String[] getGroupComps(PosOwl exprNode) {
		// TODO Auto-generated method stub
		return null;
	}

	private static PosOwl seqWork(List<PosOwl> parentRoute, PosOwl exprNode) {
		OWLSeqNode node = new OWLSeqNode();
		String[] cmps = getSeqComps(exprNode);
		for (String cmp : cmps) {
			BaseOWLNode base = new BaseOWLNode(cmp);
			node.addNode(base);
		}
		PosOwl parent = new PosOwl();
		parent.setNode(node);
		parentRoute.add(parent);
		PosOwl firstSeq = new PosOwl();
		firstSeq.setPos(0);
		firstSeq.setNode(node.getFirstNode());
		return firstSeq;
	}
	
	private static String[] getSeqComps(PosOwl exprNode) {
		// TODO Auto-generated method stub
		return null;
	}

	private static PosOwl parentWork(List<PosOwl> parentRoute, PosOwl exprNode) {
		PosOwl parent = parentRoute.get(parentRoute.size()-1);
		if(parent.getNode() instanceof GroupOWLNode) {
			if(exprNode.getPos()==0) {
				((GroupOWLNode)parent.getNode()).setParent(exprNode.getNode());
				PosOwl child = new PosOwl();
				child.setPos(1);
				child.setNode(((GroupOWLNode)parent.getNode()).getChild());
				return child;
			}else {
				((GroupOWLNode)parent.getNode()).setChild(exprNode.getNode());
				//done with group node
				parentRoute.remove(parentRoute.size()-1);
				parent.setFinished(true);
				return parent;
			}
		}
		else if(parent.getNode() instanceof OWLSeqNode) {
			((OWLSeqNode) parent.getNode()).getOwlNodes().set(exprNode.getPos(), exprNode.getNode());
			if(((OWLSeqNode) parent.getNode()).getOwlNodes().size()-1==exprNode.getPos()) {
				//finished, set finished and set back to seq node
				parent.setFinished(true);
				parentRoute.remove(parentRoute.size()-1);
				return parent;
			}else {
				//get next sequence component
				PosOwl nextSeq = new PosOwl();
				nextSeq.setNode(((OWLSeqNode) parent.getNode()).getOwlNodes().get(exprNode.getPos()+1));
				nextSeq.setPos(exprNode.getPos()+1);
				return nextSeq;
				
			}
		}
		return null;
	}

	private static int findGroupEndIndex(String str) {
		int i = 0;
		int index = -1;
		char[] chars = str.toCharArray();

		for (int k = 0; k < chars.length; k++) {
			if (chars[k] == '(') {
				i++;
			} else if (chars[k] == ')') {
				i--;
				if (i == 0) {
					index = k;
					break;
				}
			}
		}
		return index;
	}

	private static int findSeqEndIndex(String str) {
		int i = 0;
		int index = str.length() - 1;
		char[] chars = str.toCharArray();

		for (int k = 0; k < chars.length; k++) {
			if (chars[k] == '(') {
				i++;
			} else if (chars[k] == ')') {
				i--;
				if (i == -1) {
					index = k;
					break;
				}
			}
		}
		return index;
	}

	private static OWLNode parseGroup(String nodeString, StringBuilder restString) {
		GroupOWLNode ret = new GroupOWLNode();
		if (nodeString.startsWith("(")) {
			// assume it is a parse Node
			int endIndex = findGroupEndIndex(nodeString);
			restString = new StringBuilder(restString.substring(endIndex + 1));
			ret.setParent(parseComponent(nodeString.substring(endIndex), restString));
			ret.setChild(parseComponent(nodeString.substring(endIndex + 1), restString));

		} else {
			// BaseNode
			int endIndex = indexOfIgnoreInv(nodeString);
			if (endIndex == -1) {
				// actual baseNode
				restString = new StringBuilder();
				return new BaseOWLNode(nodeString);
			}
			restString = new StringBuilder(restString.substring(endIndex + 1));
			BaseOWLNode parent = new BaseOWLNode(nodeString.substring(0, endIndex));

			ret.setParent(parent);
			ret.setChild(parseComponent(restString.toString(), restString));
			// remove last ) Group Node
			// restString.deleteCharAt(0);
		}
		return ret;
	}

	private static int indexOfIgnoreInv(String str) {
		return str.replace("^(-1)", "Inver").indexOf("(");
	}

	private static OWLNode parseSeq(String substring, StringBuilder restString) {
		OWLSeqNode seqNode = new OWLSeqNode();
		int endIndex = -1;
		String tmpSubString = "";
		if (substring.startsWith("(")) {
			endIndex = findSeqEndIndex(substring);
			tmpSubString = substring.substring(1, endIndex);
		} else {
			endIndex = findSeqEndIndex("(" + substring);
			if (endIndex > -1) {
				tmpSubString = substring.substring(0, endIndex);
			} else {
				tmpSubString = substring;
			}
		}

		// TODO not split but get components
		String[] nodes = getSeqComponents(tmpSubString);
		// String[] nodes = tmpSubString.split("AND");

		for (String node : nodes) {
			seqNode.addNode(parseComponent(node.trim(), new StringBuilder(node)));
		}
		if (endIndex > -1) {
			restString = new StringBuilder(restString.substring(endIndex));

		} else {
			restString = new StringBuilder();
		}
		return seqNode;
	}

	private static int getNextAnd(String owl) {
		// ignore ANDs in '' or "" and in ()
		int bracketIndex = owl.indexOf("(");
		int andIndex = owl.indexOf("AND");
		if (bracketIndex < andIndex && bracketIndex > -1) {
			int end = findGroupEndIndex(owl);
			return getNextAnd(owl.substring(end));
		} else {
			// check if and is in '' or ""
			// count previous ' and " if even good to go,
			if (andIndex > -1) {
				if (StringUtils.countMatches(owl.substring(0, andIndex), "\"") % 2 != 0
						|| StringUtils.countMatches(owl.substring(0, andIndex), "'") % 2 != 0) {
					// AND is escaped go to and afterwards
					return getNextAnd(owl.substring(andIndex + 3));
				}
				return andIndex;
			} else {
				return -1;
			}
		}
	}

	private static String[] getSeqComponents(String tmpSubString) {
		List<String> comp = new LinkedList<String>();
		String sub = tmpSubString;
		StringBuilder current = new StringBuilder();
		int next;
		while ((next = getNextAnd(sub)) > -1) {
			comp.add(sub.substring(0, next));
			sub = sub.substring(next + 3);
		}
		if (!sub.isEmpty()) {
			while (StringUtils.countMatches(sub, "(") < StringUtils.countMatches(sub, ")")) {
				sub = sub.substring(0, sub.lastIndexOf(")"));
			}
			comp.add(sub);
		}
		return comp.toArray(new String[comp.size()]);
	}

	private static OWLNode parseComponent(String substring, StringBuilder restString) {
		if (substring.startsWith("(")) {
			if (isNextGroup(substring.substring(1).trim())) {
				return parseGroup(substring.trim().substring(1), new StringBuilder(substring.trim().substring(1)));
			} else if (isNextSeq(substring.substring(1).trim())) {
				return parseSeq(substring.trim().substring(1), new StringBuilder(substring.trim().substring(1)));
			} else {
				return new BaseOWLNode(substring.substring(1, findGroupEndIndex(substring)));
			}
		} else {
			int andIndex = substring.indexOf("AND");
			int bracketIndex = substring.indexOf("(");
			if ((andIndex < bracketIndex && andIndex > -1) || (andIndex > -1 && bracketIndex == -1)) {
				// next is seq
				return parseSeq(substring.trim(), new StringBuilder(substring.trim()));
			} else if ((andIndex > bracketIndex && bracketIndex > -1) || (andIndex == -1 && bracketIndex > -1)) {
				// next is group
				return parseGroup(substring.trim(), new StringBuilder(substring.trim()));
			} else {
				// both have to be -1
				restString = new StringBuilder();
				if (substring.trim().endsWith(")")) {
					substring = substring.trim();
					substring = substring.substring(0, substring.length() - 1);
				}
				return new BaseOWLNode(substring);
			}
		}
	}

	public static OWLNode parse(String nodeString) {
		try {
			return parseComponent(nodeString, new StringBuilder(nodeString));
		} catch (StackOverflowError e) {
			System.out.println(nodeString);
			return null;
		}
	}

	public static boolean isNextGroup(String substring) {
		int andIndex = substring.indexOf("AND");
		int bracketIndex = substring.indexOf("(");
		if ((andIndex > bracketIndex && bracketIndex > -1) || (andIndex == -1 && bracketIndex > -1)) {
			// next is group
			return true;
		}
		return false;
	}

	public static boolean isNextSeq(String substring) {
		int andIndex = substring.indexOf("AND");
		int bracketIndex = substring.indexOf("(");
		if ((andIndex < bracketIndex && andIndex > -1) || (andIndex > -1 && bracketIndex == -1)) {
			// next is group
			return true;
		}
		return false;
	}

}
