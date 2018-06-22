package org.dice_group.sparrow.sparql;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.syntax.ElementWalker;
import org.dice_group.sparrow.annotation.impl.GroupAnnotation;
import org.dice_group.sparrow.graph.GraphNode;
import org.dice_group.sparrow.graph.Triple;
import org.dice_group.sparrow.graph.impl.URIGraphNode;
import org.dice_group.sparrow.graph.impl.VarGraphNode;
import org.dice_group.sparrow.sparql.SparqlElementVisitor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SparqlElementVisitorTest {

	
	private Query query;
	private Set<GraphNode> expected;

	@Parameters
	public static Collection<Object[]> data(){
		List<Object[]> data = new LinkedList<Object[]>();
		HashSet<GraphNode> set1 = new HashSet<GraphNode>();
		HashSet<GraphNode> set2 = new HashSet<GraphNode>();
		VarGraphNode varO = new VarGraphNode("o");
		varO.addAnnotation(new GroupAnnotation(0));
		VarGraphNode varO_1 = new VarGraphNode("o");
		varO_1.addAnnotation(new GroupAnnotation(0));
		VarGraphNode varS = new VarGraphNode("s");
		varS.addAnnotation(new GroupAnnotation(0));
		URIGraphNode uriBerlin = new URIGraphNode("http://test.com/res/Berlin");
		uriBerlin.addAnnotation(new GroupAnnotation(0));
		URIGraphNode uriBerlin_1 = new URIGraphNode("http://test.com/res/Berlin");
		uriBerlin_1.addAnnotation(new GroupAnnotation(0));
		URIGraphNode p1 = new URIGraphNode("http://test.com#p1");
		p1.addAnnotation(new GroupAnnotation(0));
		URIGraphNode p2 = new URIGraphNode("http://test.com#p2");
		p2.addAnnotation(new GroupAnnotation(0));
		URIGraphNode p2_1 = new URIGraphNode("http://test.com#p2");
		p2_1.addAnnotation(new GroupAnnotation(0));
		
		varO.addRelation(new Triple(varO, p2, uriBerlin));
		p2.addRelation(new Triple(varO, p2, uriBerlin));
		uriBerlin.addRelation(new Triple(varO, p2, uriBerlin));
		
		varO_1.addRelation(new Triple(varO_1, p2_1, uriBerlin_1));
		p2_1.addRelation(new Triple(varO_1, p2_1, uriBerlin_1));
		uriBerlin_1.addRelation(new Triple(varO_1, p2_1, uriBerlin_1));
		varS.addRelation(new Triple(varS, p1, varO_1));
		p1.addRelation(new Triple(varS, p1, varO_1));
		varO_1.addRelation(new Triple(varS, p1, varO_1));
		
		set1.add(varO);
		set1.add(p2);
		set1.add(uriBerlin);
		
		set2.add(varO_1);
		set2.add(p2_1);
		set2.add(uriBerlin_1);
		set2.add(varS);
		set2.add(p1);

		data.add(new Object[] {"Select ?o WHERE {?o <http://test.com#p2> <http://test.com/res/Berlin>}", set1});
		data.add(new Object[] {"Select ?s WHERE {?s <http://test.com#p1> ?o . ?o <http://test.com#p2> <http://test.com/res/Berlin> }", set2});
		data.add(new Object[] {"Select ?o WHERE {?s <http://test.com#p1> ?o . ?o <http://test.com#p2> <http://test.com/res/Berlin> }", set2});
		return data;
	}
	
	public SparqlElementVisitorTest(String query, Set<GraphNode> expected) {
		this.expected=expected;
		this.query = QueryFactory.create(query);
	}
	
	@Test
	public void visitorTest() {
		SparqlElementVisitor elVisitor = new SparqlElementVisitor();
		elVisitor.setElementWhere(query.getQueryPattern());
		ElementWalker.walk(query.getQueryPattern(), elVisitor);
		Set<GraphNode> nodeList = elVisitor.getNodes();
		assertEquals(expected, nodeList);
	}
}
