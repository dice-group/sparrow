package org.dice_group.sparrow.sparql;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitor;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.RecursiveElementVisitor;
import org.dice_group.sparrow.annotation.SparqlAnnotation;
import org.dice_group.sparrow.annotation.impl.GroupAnnotation;
import org.dice_group.sparrow.graph.GraphNode;
import org.dice_group.sparrow.graph.GraphNodeFactory;
import org.dice_group.sparrow.graph.GraphUtils;
import org.dice_group.sparrow.graph.Triple;

public class SparqlElementVisitor extends RecursiveElementVisitor {

	public SparqlElementVisitor(ElementVisitor visitor) {
		super(visitor);
	}

	public SparqlElementVisitor() {
		super(new ElementVisitorBase());
	}

	private boolean started = false;
	private Element where;
	private int groupIndex=0;
	private GroupAnnotation currentGroup = new GroupAnnotation(groupIndex++);


	private HashSet<GraphNode> nodes = new HashSet<GraphNode>();

	public Set<GraphNode> getNodes(){
		return nodes;
	}
	
	// TODO if OPTIONAL/UNION GROUP ADD according annotation

	/**
	 * Sets the complete where clause element
	 * 
	 * @param el
	 */
	public void setElementWhere(Element el) {
		this.where = el;
	}

	public void endElement(ElementFilter el) {
		// TODO create filter step

		// TODO add filter merger
	}

	public void startElement(ElementGroup el) {
		if (!started && el.equals(where)) {
			// root element found
			started = true;

		} else if (started) {
			if(currentGroup==null) {
				currentGroup = new GroupAnnotation(groupIndex++);
			}
			else {
				GroupAnnotation subAnnotation = new GroupAnnotation(groupIndex++);
				currentGroup.getAnnotations().add(subAnnotation);
				subAnnotation.setParent(currentGroup);
				currentGroup = subAnnotation;
			}
		}
	}

	public void endElement(ElementGroup el) {
		// set back to parent
		if (started && currentGroup!=null) {
			currentGroup = this.currentGroup.getParent();
		}
	}

	public void endElement(ElementPathBlock el) {
		
		if (started) {
			for (TriplePath path : el.getPattern().getList()) {
				if (path.getPredicate() != null) {
					// plain predicate
					GraphNode s = addNode(path.getSubject());
					GraphNode p = addNode(path.getPredicate());
					GraphNode o = addNode(path.getObject());
					s.addAnnotation(currentGroup);
					p.addAnnotation(currentGroup);
					o.addAnnotation(currentGroup);
					Triple relation = new Triple(s, p, o);
					s.addRelation(relation);
					p.addRelation(relation);
					o.addRelation(relation);
					
				}
			}

		}
		
	}
	
	private GraphNode addNode(Node rdf) {
		GraphNode node = GraphNodeFactory.create(rdf);
		GraphNode tmp = GraphUtils.getNodeWithName(node.getName(), nodes);
		if(tmp!=null) {
			node=tmp;
		}else {
			nodes.add(node);
		}
		return node;
	}

}
