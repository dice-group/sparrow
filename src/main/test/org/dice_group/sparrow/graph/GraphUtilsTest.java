package org.dice_group.sparrow.graph;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;

import org.dice_group.sparrow.graph.impl.URIGraphNode;
import org.dice_group.sparrow.graph.impl.VarGraphNode;
import org.junit.Before;
import org.junit.Test;

public class GraphUtilsTest {

	private HashSet<GraphNode> graph = new HashSet<GraphNode>();
	private Triple[] triples;
	
	private GraphNode expected;
	
	@Before
	public void createGraph() {
		GraphNode node = new URIGraphNode("test");
		graph.add(node);
		GraphNode node1 = new URIGraphNode("test1");
		graph.add(node1);
		GraphNode node2 = new URIGraphNode("test2");
		graph.add(node2);
		GraphNode node3 = new URIGraphNode("test3");
		graph.add(node3);
		expected = new URIGraphNode("abc");
		graph.add(expected);
		GraphNode node4 = new URIGraphNode("test4");
		graph.add(node4);
		
		//build relations
		Triple t1 = new Triple(node, node2, node3);
		Triple t2 = new Triple(node4, node2, node);
		Triple t3 = new Triple(node4, node2, node3);
		Triple t4 = new Triple(node1, node3, node4);
		Triple t5 = new Triple(node1, node, expected);
		triples = new Triple[] {t1,t2,t3,t4,t5};
	}
	
	@Test
	public void test() {
		assertEquals(expected, GraphUtils.getNodeWithName("abc", graph));
		assertEquals(expected, GraphUtils.getNodeWithName("abc", triples));
		assertEquals(new URIGraphNode("test1"), GraphUtils.getNodeWithName("test1", graph));
		assertEquals(new URIGraphNode("test1"), GraphUtils.getNodeWithName("test1", triples));
		assertEquals(new URIGraphNode("test2"), GraphUtils.getNodeWithName("test2", graph));
		assertEquals(new URIGraphNode("test2"), GraphUtils.getNodeWithName("test2", triples));
		assertEquals(null, GraphUtils.getNodeWithName("notInList", graph));
		assertEquals(null, GraphUtils.getNodeWithName("notInList", triples));
	}
	
	@Test
	public void simpleTripleTest() {
		Triple t = new Triple(null, null, null);
		t.setSubject(new VarGraphNode("a"));
		t.setPredicate(new VarGraphNode("b"));
		t.setObject(new VarGraphNode("c"));
		
		assertEquals(new VarGraphNode("a"), t.getSubject());
		assertEquals(new VarGraphNode("b"), t.getPredicate());
		assertEquals(new VarGraphNode("c"), t.getObject());
		
		assertEquals(new VarGraphNode("a"), t.get(0));
		assertEquals(new VarGraphNode("b"), t.get(1));
		assertEquals(new VarGraphNode("c"), t.get(2));
		assertEquals(null, t.get(3));
		
		
	}

	
}
