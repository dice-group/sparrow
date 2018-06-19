package org.dice_group.sparrow.graph;

import static org.junit.Assert.assertEquals;

import org.apache.jena.datatypes.BaseDatatype;
import org.apache.jena.datatypes.xsd.impl.RDFhtml;
import org.apache.jena.graph.BlankNodeId;
import org.apache.jena.graph.NodeFactory;
import org.dice_group.sparrow.graph.impl.BNodeGraphNode;
import org.dice_group.sparrow.graph.impl.LiteralGraphNode;
import org.dice_group.sparrow.graph.impl.URIGraphNode;
import org.dice_group.sparrow.graph.impl.VarGraphNode;
import org.junit.Test;

public class GraphNodeFactoryTest {

	
	@Test
	public void test() {
		assertEquals(new URIGraphNode("http://test.com"), GraphNodeFactory.create(NodeFactory.createURI("http://test.com")));
		assertEquals(new VarGraphNode("varA"), GraphNodeFactory.create(NodeFactory.createVariable("varA")));
		assertEquals(new LiteralGraphNode("\"test\""), GraphNodeFactory.create(NodeFactory.createLiteral("test")));
		assertEquals(new LiteralGraphNode("\"test\"@en"), GraphNodeFactory.create(NodeFactory.createLiteral("test", "en")));
		assertEquals(new LiteralGraphNode("\"test\"^^http://test.com"), GraphNodeFactory.create(NodeFactory.createLiteral("test", new BaseDatatype("http://test.com"))));
		assertEquals(new BNodeGraphNode("_:abc"), GraphNodeFactory.create(NodeFactory.createBlankNode(BlankNodeId.create("_:abc"))));
		

	}
}
