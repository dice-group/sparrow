package org.dice_group.sparrow.sparql;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementUnion;
import org.apache.jena.sparql.syntax.ElementVisitor;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.RecursiveElementVisitor;
import org.dice_group.sparrow.graph.impl.GraphImpl;
import org.dice_group.sparrow.graph.impl.GraphPattern;
import org.dice_group.sparrow.graph.impl.TreeGraphPattern;

public class ListElementVisitor extends RecursiveElementVisitor {

	public ListElementVisitor(ElementVisitor visitor) {
		super(visitor);
	}

	public ListElementVisitor() {
		super(new ElementVisitorBase());
	}

	private boolean started = false;
	private Element where;

	public List<GraphImpl<Node, TreeGraphPattern>> graphs = new LinkedList<GraphImpl<Node, TreeGraphPattern>>();
	
	public void startElement(ElementUnion el) {
		graphs.add(new GraphImpl<Node, TreeGraphPattern>());
		//start new GraphPattern List
	}

	public void endElement(ElementUnion el) {
	}

	/**
	 * Sets the complete where clause element
	 * 
	 * @param el
	 */
	public void setElementWhere(Element el) {
		this.where = el;
	}

	public void endElement(ElementFilter el) {
		if(started) {
		for(GraphPattern pattern : graphs.get(graphs.size()-1).getEdges()) {
			pattern.addIfFit(el.getExpr());
		}
		}
	}

	public void startElement(ElementGroup el) {
		if (!started && el.equals(where)) {
			// root element found
			started = true;
			graphs.add(new GraphImpl<Node, TreeGraphPattern>());

		} else if (started) {

		}
	}


	public void endElement(ElementPathBlock el) {

		if (started) {
			for (TriplePath path : el.getPattern().getList()) {
				if (path.getPredicate() != null) {
					TreeGraphPattern pattern  = new TreeGraphPattern();
					pattern.setPattern(path);
					graphs.get(graphs.size()-1).addEdge(pattern);
					graphs.get(graphs.size()-1).addNode(path.getSubject());
					graphs.get(graphs.size()-1).addNode(path.getPredicate());
					graphs.get(graphs.size()-1).addNode(path.getObject());
				}
			}

		}

	}



}