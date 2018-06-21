package org.dice_group.sparrow.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.LinkedList;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.dice_group.sparrow.exceptions.GraphContainsCycleException;
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

		// build relations
		Triple t1 = new Triple(node, node2, node3);
		Triple t2 = new Triple(node4, node2, node);
		Triple t3 = new Triple(node4, node2, node3);
		Triple t4 = new Triple(node1, node3, node4);
		Triple t5 = new Triple(node1, node, expected);
		triples = new Triple[] { t1, t2, t3, t4, t5 };
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

	@Test
	public void cycleCheckTest() {
		// cycle
		HashSet<GraphNode> graph = new HashSet<GraphNode>();
		GraphNode node = new URIGraphNode("test");
		graph.add(node);
		GraphNode node1 = new URIGraphNode("test1");
		graph.add(node1);
		GraphNode node2 = new URIGraphNode("test2");
		graph.add(node2);
		GraphNode node3 = new URIGraphNode("test3");
		graph.add(node3);
		node.addRelation(new Triple(node, node1, node3));
		node.addRelation(new Triple(node, node1, node2));
		node3.addRelation(new Triple(node3, node1, node2));
		node2.addRelation(new Triple(node2, node1, node));
		try {
			GraphUtils.checkCycles(graph);
			fail();
		} catch (GraphContainsCycleException e) {
			e.printStackTrace();
			System.out.println("This was expected");
		}
		// no cycle

		graph = new HashSet<GraphNode>();
		node = new URIGraphNode("test");
		graph.add(node);
		node1 = new URIGraphNode("test1");
		graph.add(node1);
		node2 = new URIGraphNode("test2");
		graph.add(node2);
		node3 = new URIGraphNode("test3");
		graph.add(node3);
		GraphNode node4 = new URIGraphNode("test4");
		graph.add(node4);
		node.addRelation(new Triple(node, node1, node3));
		node.addRelation(new Triple(node, node1, node2));
		node3.addRelation(new Triple(node3, node1, node1));
		node2.addRelation(new Triple(node2, node1, node3));
		node2.addRelation(new Triple(node4, node1, node2));
		node4.addRelation(new Triple(node4, node1, node2));
		try {
			GraphUtils.checkCycles(graph);
		} catch (GraphContainsCycleException e) {
			e.printStackTrace();
			System.out.println("This was not expected");
			fail();

		}

		assertEquals(graph, GraphUtils.getAllNodes(node));
	}

	@Test
	public void pathZigZagChecking() {
		// zig zag
		HashSet<GraphNode> graph = new HashSet<GraphNode>();
		GraphNode node = new URIGraphNode("test");
		graph.add(node);
		GraphNode node1 = new URIGraphNode("test1");
		graph.add(node1);
		GraphNode node2 = new URIGraphNode("test2");
		graph.add(node2);
		GraphNode node3 = new URIGraphNode("test3");
		graph.add(node3);
		GraphNode node4 = new URIGraphNode("test4");
		graph.add(node4);
		GraphNode node5 = new URIGraphNode("test5");
		graph.add(node5);
		GraphNode node6 = new URIGraphNode("test6");
		graph.add(node6);
		node1.addRelation(new Triple(node1, node2, node));
		node.addRelation(new Triple(node1, node2, node));
		node2.addRelation(new Triple(node1, node2, node));
		// n1 -- n2 --> n
		// n6 -- n2 --> n1
		node6.addRelation(new Triple(node6, node2, node1));
		node1.addRelation(new Triple(node6, node2, node1));
		node2.addRelation(new Triple(node6, node2, node1));
		// n6 -- n2 --> n3
		node3.addRelation(new Triple(node6, node2, node3));
		node6.addRelation(new Triple(node6, node2, node3));
		node2.addRelation(new Triple(node6, node2, node3));
		// n4 -- n2 --> n5
		node5.addRelation(new Triple(node4, node2, node5));
		node4.addRelation(new Triple(node4, node2, node5));
		node2.addRelation(new Triple(node4, node2, node5));
		assertFalse(GraphUtils.checkOneZigZagToNode(node5, node));
		// n3 -- n2 --> n4
		node3.addRelation(new Triple(node3, node2, node4));
		node4.addRelation(new Triple(node3, node2, node4));
		node2.addRelation(new Triple(node3, node2, node4));

		assertTrue(GraphUtils.checkOneZigZagToNode(node5, node));
		assertEquals(graph, GraphUtils.getAllNodes(node));
	}

	@Test
	public void betterPathCheck() {
		GraphNode node = new URIGraphNode("test");
		graph.add(node);
		GraphNode node1 = new URIGraphNode("test1");
		graph.add(node1);
		GraphNode node2 = new URIGraphNode("test2");
		graph.add(node2);
		GraphNode node3 = new URIGraphNode("test3");
		graph.add(node3);
		GraphNode node4 = new URIGraphNode("test4");
		graph.add(node4);
		// inv n3 -- n2 --> n1
		node3.addRelation(new Triple(node3, node2, node1));
		node1.addRelation(new Triple(node3, node2, node1));
		node2.addRelation(new Triple(node3, node2, node1));
		// n -- n2 --> n4
		node.addRelation(new Triple(node, node2, node4));
		node4.addRelation(new Triple(node, node2, node4));
		node2.addRelation(new Triple(node, node2, node4));
		// n4 -- n2 --> n1
		node3.addRelation(new Triple(node4, node2, node1));
		node1.addRelation(new Triple(node4, node2, node1));
		node2.addRelation(new Triple(node4, node2, node1));
		assertFalse(GraphUtils.checkBetterWay(node, node3, new LinkedList<GraphNode>(), Lists.newArrayList(node4)));
		// n4 -- n2 --> n3
		node3.addRelation(new Triple(node4, node2, node3));
		node1.addRelation(new Triple(node4, node2, node3));
		node2.addRelation(new Triple(node4, node2, node3));

		assertTrue(GraphUtils.checkBetterWay(node, node3, new LinkedList<GraphNode>(), Lists.newArrayList()));
		assertTrue(GraphUtils.checkBetterWay(node, node3, new LinkedList<GraphNode>(), Lists.newArrayList(node4)));

		
	}

}
