package org.dice_group.sparrow.owl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OWLTest {

	@Test
	public void simpleNodeTest() {
		OWLNode node = new OWLNode("test");
		assertEquals("test", node.toString());
	}

	@Test
	public void seqNodeTest() {
		// class A (rule1 B (rule2 C))
		OWLSeqNode node = new OWLSeqNode("class A");
		node.putChild(new OWLSeqNode("rule1 B").putChild(new OWLNode("rule2 C")));
		System.out.println(node.toString());
		assertEquals("class A AND ( rule1 B AND ( rule2 C ) ) ", node.toString());
		
		//class A (rule1 B) AND class B ( rule2 C (rule3 D))
		node = new OWLSeqNode("class A");
		node.putChild(new OWLNode("rule1 B"));
		node.putChild(new OWLSeqNode("class B").putChild(new OWLSeqNode("rule2 C").putChild(new OWLNode("rule3 D"))));
		System.out.println(node.toString());
		assertEquals("class A AND ( rule1 B ) AND ( class B AND ( rule2 C AND ( rule3 D ) ) ) ", node.toString());
		
		node = new OWLSeqNode();
		node.putChild(new OWLNode("class A"));
		assertEquals(" ( class A ) ", node.toString());
		
		//class A ( rule1 B ) AND ( class A )
		node = new OWLSeqNode(new OWLSeqNode("class A").putChild(new OWLNode("rule1 B")));
		node.putChild(new OWLNode("class A"));
		assertEquals("class A AND ( rule1 B ) AND ( class A ) ", node.toString());
		
		node = new OWLSeqNode(new OWLSeqNode("class A"));
		node.putChild(new OWLNode("rule1 B")); 
		assertEquals("class A AND ( rule1 B ) ", node.toString());
		
		
		node = new OWLSeqNode(new OWLNode("class A"));
		node.putChild(new OWLNode("rule1 B"));
		assertEquals("class A AND ( rule1 B ) ", node.toString());

	}

	@Test
	public void queryTest() {
		OWLQuery query = new OWLQuery();
		//(class A) AND (class B) AND (class C ( rule1 D ) )
		OWLNode aNode = new OWLNode("class A");
		query.addOWLNode(aNode);
		query.addOWLNode(OWLNode.AND_NODE);
		query.addOWLNode(new OWLNode("class B"));
		query.addOWLNode(OWLNode.AND_NODE);
		query.addOWLNode(new OWLSeqNode("class C").putChild(new OWLNode("rule1 D")));
		System.out.println("Query: "+query);
		assertEquals("class A AND class B AND ( class C AND ( rule1 D )  ) ", query.toString());
		assertEquals("class B", query.getOWLNodeByIndex(2).toString());
		assertEquals(aNode, query.getOWLNodeByName("class A").get(0));
		assertTrue(query.getOWLNodeByName("not In List").isEmpty());
		query.putOWLNode(new OWLNode("abc"), 1);
		assertEquals("abc", query.getOWLNodeByIndex(1).name);
		
		query = new OWLQuery();
		query.addOWLNode(new OWLSeqNode("?s").putChild(new OWLNode("rule1 SOME")));
		query.build(); 
		assertEquals("(  Thing AND ( rule1 SOME ( Thing ) )  ) ", query.toString());
		query = new OWLQuery();
		query.addOWLNode(new OWLNode("?s"));
		query.addOWLNode(OWLNode.AND_NODE);
		query.addOWLNode(OWLNode.GROUP_START_NODE);
		query.addOWLNode(new OWLNode("rule1 SOME"));
		query.addOWLNode(OWLNode.GROUP_END_NODE);
		query.addOWLNode(OWLNode.AND_NODE);
		query.addOWLNode(OWLNode.GROUP_START_NODE);
		query.addOWLNode(new OWLNode("rule2 SOME"));
		query.addOWLNode(OWLNode.GROUP_END_NODE);
		query.addOWLNode(OWLNode.AND_NODE);
		query.addOWLNode(new OWLNode("rule3 SOME"));
		query.build(); 
		assertEquals("Thing AND ( rule1 SOME ( Thing ) ) AND ( rule2 SOME ( Thing ) ) AND rule3 SOME ( Thing ) ", query.toString());
	}
}
