package org.dice_group.sparrow.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.dice_group.sparrow.annotation.SparqlAnnotation;
import org.dice_group.sparrow.annotation.impl.GroupAnnotation;
import org.dice_group.sparrow.graph.impl.BNodeGraphNode;
import org.dice_group.sparrow.graph.impl.LiteralGraphNode;
import org.dice_group.sparrow.graph.impl.URIGraphNode;
import org.dice_group.sparrow.graph.impl.VarGraphNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class GraphNodeTests {

	private GraphNode same;
	private GraphNode node;
	private GraphNode[] diff;

	@Parameters
	public static Collection<Object[]> data() {
		 List<Object[]> testConfigs = new ArrayList<Object[]>();

		 GraphNode orig = new URIGraphNode("http://test.com");
		 GraphNode[] diff = new GraphNode[] {new LiteralGraphNode("http://test.com"),
				 new URIGraphNode("http://abc.com")};
		 GraphNode same = new URIGraphNode("http://test.com");
		 same.addAnnotation(new GroupAnnotation());
	     testConfigs.add(new Object[] { orig,same, diff });

		 orig = new LiteralGraphNode("abcde");
		 diff = new GraphNode[] {new LiteralGraphNode("http://test.com"),
				 new URIGraphNode("abcde")};
		 same = new LiteralGraphNode("abcde");
		 same.addAnnotation(new GroupAnnotation());
	     testConfigs.add(new Object[] { orig,same, diff });

		 orig = new VarGraphNode("abcde");
		 diff = new GraphNode[] {new VarGraphNode("http://test.com"),
				 new URIGraphNode("abcde")};
		 same = new VarGraphNode("abcde");
		 same.addAnnotation(new GroupAnnotation());
	     testConfigs.add(new Object[] { orig,same, diff });
	     
		 orig = new BNodeGraphNode("_:abcde");
		 diff = new GraphNode[] {new BNodeGraphNode("http://test.com"),
				 new VarGraphNode("_:abcde")};
		 same = new BNodeGraphNode("_:abcde");
		 same.addAnnotation(new GroupAnnotation());
	     testConfigs.add(new Object[] { orig,same, diff });
	        
	     return testConfigs;
	}

	public GraphNodeTests(GraphNode original, GraphNode same, GraphNode[] different) {
		this.node = original;
		this.same = same;
		this.diff = different;
	}

	@Test
	public void test(){
		System.out.println("Testing Node "+node);
		//equals
		assertEquals(node, same);
		for(GraphNode diffNode : diff) {
			assertNotEquals(node, diffNode);
		}
		//get name & use rule
		//TODO this is that in further versions the test will lead to error, if the gNode rules are changed
		assertEquals(node.useRule(), node.getName());
		//get relations
		node.setRelations(Lists.newArrayList(new Triple(node, diff[0], node)));
		node.addRelation(new Triple(node, node, node));
		List<Triple> relations = node.getRelations();
		assertEquals(relations.size(), 2);
		assertTrue(relations.contains(new Triple(node, node, node)));
		assertTrue(relations.contains(new Triple(node, diff[0], node)));
		
	
		//get annotations
		node.addAnnotation(new GroupAnnotation(1));
		HashSet<SparqlAnnotation> annos = node.getAnnotations();
		assertEquals(annos.size(), 1);
		assertTrue(annos.contains(new GroupAnnotation(1)));
		System.out.println("Ending Test Node "+node);
	}
}
