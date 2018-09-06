package org.dice_group.sparrow.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.syntax.ElementWalker;
import org.dice_group.sparrow.exceptions.GraphContainsCycleException;
import org.dice_group.sparrow.graph.impl.DirectedAcyclicGraph;
import org.dice_group.sparrow.sparql.ListElementVisitor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class DirectedAcyclicGraphTest {

	@Parameters
	public static Collection<Object[]> data(){
		List<Object[]> data = new LinkedList<Object[]>();
		data.add(new Object[] {"PREFIX exa:<http://example.com/> SELECT ?x {?x exa:a exa:Person. ?x exa:livesIn ?y. ?y exa:locatedIn exa:Europe }", "?x 	-- http://example.com/a --> http://example.com/Person\n" + 
				"	-- http://example.com/livesIn --> ?y	-- http://example.com/locatedIn --> http://example.com/Europe\n\n"});
		data.add(new Object[] {"PREFIX exa:<http://example.com/> SELECT ?x {?x exa:a ?y. ?y exa:livesIn ?z. ?y exa:locatedIn exa:Europe }", "?x 	-- http://example.com/a --> ?y	-- http://example.com/livesIn --> ?z\n" + 
				"					-- http://example.com/locatedIn --> http://example.com/Europe\n" + 
				"\n"});
		data.add(new Object[] {"PREFIX exa:<http://example.com/> SELECT ?x {?x exa:a ?y. ?y exa:livesIn ?z. ?y exa:locatedIn exa:Europe FILTER NOT EXISTS {?z ?p ?o}}", "?x 	-- http://example.com/a --> ?y	-- http://example.com/livesIn --> ?z[(notexists (bgp (triple ?z ?p ?o))), ]\n" + 
				"					-- http://example.com/locatedIn --> http://example.com/Europe\n" + 
				"\n"});
		data.add(new Object[] {"PREFIX exa:<http://example.com/> SELECT ?x {?x exa:a ?y. ?y exa:livesIn ?z. ?y exa:locatedIn exa:Europe . ?x exa:type exa:Person}", "?x 	-- http://example.com/a --> ?y	-- http://example.com/livesIn --> ?z\n" + 
				"					-- http://example.com/locatedIn --> http://example.com/Europe\n" + 
				"\n" + 
				"	-- http://example.com/type --> http://example.com/Person\n"});
		data.add(new Object[] {"PREFIX exa:<http://example.com/> SELECT ?y {?x exa:a exa:Person. ?x exa:livesIn ?y. ?y exa:locatedIn exa:Europe }", "?y 	-- http://example.com/locatedIn --> http://example.com/Europe\n" + 
				"	-- http://example.com/livesInInverse --> ?x	-- http://example.com/a --> http://example.com/Person\n" + 
				"\n"});

		return data;
	}

	private String in;
	private String out;
	
	public DirectedAcyclicGraphTest(String query, String output) {
		this.in = query;
		this.out = output;
	}
	
	@Test
	public void fullTest() throws GraphContainsCycleException {
		Query query = QueryFactory.create(this.in);
		query.getPrefixMapping().clearNsPrefixMap();
		ListElementVisitor visitor = new ListElementVisitor();
		visitor.setElementWhere(query.getQueryPattern());

		ElementWalker.walk(query.getQueryPattern(), visitor);
		assertTrue(visitor.graphs.size()==1);
		Node root = query.getProjectVars().get(0).asNode();
		DirectedAcyclicGraph<Node> dag = DirectedAcyclicGraph.create(visitor.graphs.get(0), root);
		System.out.println("######### Converting ########");
		System.out.println(query);
		System.out.println("######### TO DAG #########");
		System.out.println(dag);
		System.out.println("\n");
		assertEquals(this.out, dag.toString());
	}
	
}
