package org.dice_group.sparrow.graph;

import org.apache.jena.graph.Node;
import org.dice_group.sparrow.graph.impl.BNodeGraphNode;
import org.dice_group.sparrow.graph.impl.LiteralGraphNode;
import org.dice_group.sparrow.graph.impl.URIGraphNode;
import org.dice_group.sparrow.graph.impl.VarGraphNode;

public class GraphNodeFactory {

	public static GraphNode create(Node node) {
		if(node.isURI()) {
			return createURINode(node);
		}
		if(node.isBlank()) {
			return createBNode(node);
		}
		if(node.isLiteral()) {
			return createLiteralNode(node);
		}
		else {
			return createVarNode(node);
		}
	}

	private static GraphNode createVarNode(Node node) {
		VarGraphNode ret = new VarGraphNode(node.getName()); 
		
		return ret;
	}

	private static GraphNode createLiteralNode(Node node) {
		LiteralGraphNode ret = new LiteralGraphNode(node.getLiteral().toString(true));
		return ret;
	}

	private static GraphNode createBNode(Node node) {
		BNodeGraphNode ret = new BNodeGraphNode(node.getBlankNodeLabel());
		return ret;
	}

	private static GraphNode createURINode(Node node) {
		URIGraphNode ret = new URIGraphNode(node.getURI());
		return ret;
	}

}
