package org.dice_group.sparrow.rule;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.dice_group.sparrow.exceptions.GraphContainsCycleException;
import org.dice_group.sparrow.exceptions.RootNodeNotVarException;
import org.dice_group.sparrow.exceptions.RuleHasNotNObjectsException;
import org.dice_group.sparrow.exceptions.RuleNotAvailableException;
import org.dice_group.sparrow.graph.GraphNode;
import org.dice_group.sparrow.graph.Triple;
import org.dice_group.sparrow.graph.impl.LiteralGraphNode;
import org.dice_group.sparrow.graph.impl.URIGraphNode;
import org.dice_group.sparrow.graph.impl.VarGraphNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

public class RuleIndicatorTest {

	private RuleIndicator indicator;
	
	
	@Test
	public void executionTest() throws RootNodeNotVarException, RuleNotAvailableException, GraphContainsCycleException, IOException, RuleHasNotNObjectsException {
		this.indicator=new RuleIndicator("src/test/resources/rules.brg", true);
		GraphNode n1 = new URIGraphNode("n1");
		GraphNode rule1 = new URIGraphNode("rule1");
		GraphNode rule2 = new URIGraphNode("rule2");
		GraphNode v1 = new VarGraphNode("v1");
		GraphNode n2 = new URIGraphNode("n2");
		GraphNode n3 = new URIGraphNode("n3");
		GraphNode l1 = new LiteralGraphNode("l1");
		GraphNode v2 = new VarGraphNode("v2");
		
		//?v1 WHERE {?v1 rule1 n1}
		//expect Thing AND ( rule1 n1 )
		Triple rel1 = new Triple(v1, rule1, n1);
		n1.addRelation(rel1);
		v1.addRelation(rel1);
		rule1.addRelation(rel1);
		assertEquals("(  Thing AND ( rule1 n1 )  ) ", indicator.injectRules(v1).toString());

		//?v1 WHERE {?v1 rule1 n1 . ?v1 rule2 l1}
		//expect Thing AND ( rule1 n1 )	AND ( rule2 l1 )
		Triple rel2 = new Triple(v1, rule2, l1);
		l1.addRelation(rel2);
		v1.addRelation(rel2);
		rule2.addRelation(rel2);
		this.indicator=new RuleIndicator("src/test/resources/rules.brg", true);
		assertEquals("( (  Thing AND ( rule1 n1 )  ) ) AND ( (  Thing AND ( rule2 l1 )  ) ) ",indicator.injectRules(v1).toString());
		
		
		rel1 = new Triple(n2, v2, n3);
		n2.addRelation(rel1);
		v2.addRelation(rel1);
		n3.addRelation(rel1);
		this.indicator=new RuleIndicator("src/test/resources/rules.brg", true);
		assertEquals("(  Thing VALUE n3 AND Thing^(-1) VALUE n2  ) ",indicator.injectRules(v2).toString());
	}
}
