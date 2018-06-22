package org.dice_group.sparrow.owl;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class OWLParser {

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
			int endIndex = nodeString.indexOf("(");
			restString = new StringBuilder(restString.substring(endIndex));
			BaseOWLNode parent = new BaseOWLNode(nodeString.substring(0, endIndex));
			ret.setParent(parent);
			ret.setChild(parseComponent(restString.toString(), restString));
			// remove last ) Group Node
			restString.deleteCharAt(0);
		}
		return ret;
	}

	private static OWLNode parseSeq(String substring, StringBuilder restString) {
		OWLSeqNode seqNode = new OWLSeqNode();
		int endIndex = -1;
		String tmpSubString = "";
		if (substring.startsWith("(")) {
			endIndex = findGroupEndIndex(substring);
			tmpSubString = substring.substring(1, endIndex);
		} else {
			endIndex = findGroupEndIndex("(" + substring);
			if (endIndex > -1) {
				tmpSubString = substring.substring(0, endIndex);
			}
			else {
				tmpSubString = substring;
			}
		}

		//TODO not split but get components
		String[] nodes = getSeqComponents(tmpSubString);
//		String[] nodes = tmpSubString.split("AND");

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
		//ignore ANDs in '' or "" and in ()
		int bracketIndex = owl.indexOf("(");
		int andIndex = owl.indexOf("AND");
		if(bracketIndex<andIndex&&bracketIndex>-1) {
			int end = findGroupEndIndex(owl);
			return getNextAnd(owl.substring(end));
		}
		else {
			//check if and is in '' or ""
			//count previous ' and " if even good to go,
			if(andIndex>-1) {
				if(StringUtils.countMatches(owl.substring(0, andIndex), "\"")%2!=0||
						StringUtils.countMatches(owl.substring(0, andIndex), "'")%2!=0) {
					//AND is escaped go to and afterwards
					return getNextAnd(owl.substring(andIndex+3));
				}
				return andIndex;				
			}
			else {
				return -1;
			}
		}
	}
	
	private static String[] getSeqComponents(String tmpSubString) {
		List<String> comp = new LinkedList<String>();
		String sub = tmpSubString;
		StringBuilder current = new StringBuilder();
		int next;
		while((next = getNextAnd(sub))>-1) {
			comp.add(sub.substring(0, next));
			sub = sub.substring(next+3);
		}
		if(!sub.isEmpty()) {
			while(StringUtils.countMatches(sub, "(")<
					StringUtils.countMatches(sub, ")")) {
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
			if ((andIndex < bracketIndex && andIndex>-1) || (andIndex>-1 && bracketIndex==-1)) {
				// next is seq
				return parseSeq(substring.trim(), new StringBuilder(substring.trim()));
			} else if ((andIndex > bracketIndex && bracketIndex>-1) || (andIndex==-1 && bracketIndex>-1)) {
				// next is group
				return parseGroup(substring.trim(), new StringBuilder(substring.trim()));
			} else {
				// both have to be -1
				restString = new StringBuilder();
				return new BaseOWLNode(substring);
			}
		}
	}

	public static OWLNode parse(String nodeString) {
		return parseComponent(nodeString, new StringBuilder(nodeString));
	}

	public static boolean isNextGroup(String substring) {
		int andIndex = substring.indexOf("AND");
		int bracketIndex = substring.indexOf("(");
		if ((andIndex > bracketIndex && bracketIndex>-1) || (andIndex==-1 && bracketIndex>-1)) {
			// next is group
			return true;
		}
		return false;
	}

	public static boolean isNextSeq(String substring) {
		int andIndex = substring.indexOf("AND");
		int bracketIndex = substring.indexOf("(");
		if ((andIndex < bracketIndex && andIndex>-1) || (andIndex>-1 && bracketIndex==-1)) {
			// next is group
			return true;
		}
		return false;
	}

}
