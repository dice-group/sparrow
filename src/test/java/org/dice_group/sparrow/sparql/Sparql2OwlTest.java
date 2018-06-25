package org.dice_group.sparrow.sparql;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.dice_group.sparrow.exceptions.GraphContainsCycleException;
import org.dice_group.sparrow.exceptions.RootNodeNotVarException;
import org.dice_group.sparrow.exceptions.RuleHasNotNObjectsException;
import org.dice_group.sparrow.exceptions.RuleNotAvailableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class Sparql2OwlTest {

	private String query;
	private String expected;

	@Parameters
	public static Collection<Object[]> data() {
		List<Object[]> data = new LinkedList<Object[]>();
		data.add(new Object[] { "SELECT ?s {?s <http://test.com#p1> <http://test.com/res/Berlin>}", 
				"(Thing THAT ( http://test.com#p1 SOME http://test.com/res/Berlin ))" });
		data.add(new Object[] { "SELECT ?s ?o ?t WHERE { ?s <http://test.com#p1> ?o . ?s <http://test.com#p2> <http://test.com/res/obj1> . ?o <http://test.com#p1> <http://test.com/res/obj3> . ?o <http://test.com#p3> ?t . ?t <http://test.com#p2> <http://test.com/res/obj1> }", 
				"(Thing THAT (http://test.com#p1 SOME ((Thing THAT ( http://test.com#p1 SOME http://test.com/res/obj3 )) AND (Thing THAT (http://test.com#p3 SOME ((Thing THAT ( http://test.com#p2 SOME http://test.com/res/obj1 )))))))) AND (Thing THAT ( http://test.com#p2 SOME http://test.com/res/obj1 ))" });
		data.add(new Object[] { "SELECT ?s WHERE { ?s <http://test.com#p1> ?o . ?o <http://test.com#name> \"Fritz\". ?s ?x ?y . ?y <http://rdf.type> <http://Obj.com>}", 
		"(Thing THAT (http://test.com#p1 SOME ((Thing THAT ( http://test.com#name SOME \"Fritz\" ))))) AND (Thing THAT (baseRule SOME ((Thing THAT ( http://rdf.type SOME http://Obj.com )))))" });

		return data;
	}

	public Sparql2OwlTest(String query, String expected) {
		this.query = query;
		this.expected = expected;
	}

	@Test
	public void bridgeTest() throws RootNodeNotVarException, IOException, RuleHasNotNObjectsException,
			RuleNotAvailableException, GraphContainsCycleException {
		Sparql2Owl bridge = new Sparql2Owl("src/test/resources/rules.brg", true);
		System.out.println("Converting SPARQL Query: ");
		System.out.println(query);
		String owl = bridge.convertSparqlQuery(query);
		System.out.println("Output: ");
		System.out.println(owl);
		assertEquals(expected, owl);
	}

}
