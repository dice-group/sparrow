package org.dice_group.sparrow.rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.syntax.ElementWalker;
import org.dice_group.sparrow.exceptions.GraphContainsCycleException;
import org.dice_group.sparrow.exceptions.RuleHasNotNObjectsException;
import org.dice_group.sparrow.exceptions.RuleNotAvailableException;
import org.dice_group.sparrow.graph.impl.DirectedAcyclicGraph;
import org.dice_group.sparrow.owl.OWLClassExpression;
import org.dice_group.sparrow.sparql.ListElementVisitor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class RuleTests {

	
	@Parameters
	public static Collection<Object[]> data(){
		List<Object[]> data = new LinkedList<Object[]>();
		data.add(new Object[] {"PREFIX exa:<http://example.com/> SELECT ?x {?x exa:a exa:Person. ?x exa:livesIn ?y. ?y exa:locatedIn exa:Europe }", ""});
		data.add(new Object[] {"PREFIX exa:<http://example.com/> SELECT ?x {?x exa:a ?y. ?y exa:livesIn ?z. ?y exa:locatedIn exa:Europe }", ""});
		data.add(new Object[] {"PREFIX exa:<http://example.com/> SELECT ?z {?x exa:a ?y. ?y exa:livesIn ?z. ?y exa:locatedIn exa:Europe }", ""});

		data.add(new Object[] {"PREFIX exa:<http://example.com/> SELECT ?x {?x exa:a ?y. ?y exa:livesIn ?z. ?y exa:locatedIn exa:Europe FILTER NOT EXISTS {?z ?p ?o}}", ""});
		data.add(new Object[] {"PREFIX exa:<http://example.com/> SELECT ?x {?x exa:a ?y. ?y exa:livesIn ?z. ?y exa:locatedIn exa:Europe . ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> exa:Person}", ""});
		data.add(new Object[] {"PREFIX exa:<http://example.com/> SELECT ?y {?x exa:a exa:Person. ?x exa:livesIn ?y. ?y exa:locatedIn exa:Europe }", "" + 
				"\n"});

		return data;
	}

	private String in;
	private String out;
	
	public RuleTests(String query, String output) {
		this.in = query;
		this.out = output;
	}
	
	@Test
	public void fullTest() throws GraphContainsCycleException, IOException, RuleHasNotNObjectsException, RuleNotAvailableException {
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
		RuleIndicator indicator = new RuleIndicator("src/main/resources/rules.brg");
		OWLClassExpression owl = indicator.executeDAG(dag);
		System.out.println("######## TO OWL #########");
		System.out.println(dag.getRules());
		System.out.println(owl);
		System.out.println("\n");

	}
}
