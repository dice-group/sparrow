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
				"(Thing AND (http://test.com#p1 VALUE http://test.com/res/Berlin))" });
		data.add(new Object[] { "SELECT ?s ?o ?t WHERE { ?s <http://test.com#p1> ?o . ?s <http://test.com#p2> <http://test.com/res/obj1> . ?o <http://test.com#p1> <http://test.com/res/obj3> . ?o <http://test.com#p3> ?t . ?t <http://test.com#p2> <http://test.com/res/obj1> }", 
				"(Thing AND (http://test.com#p1 SOME ((Thing AND (http://test.com#p1 VALUE http://test.com/res/obj3)) AND (Thing AND (http://test.com#p3 SOME ((Thing AND (http://test.com#p2 VALUE http://test.com/res/obj1)))))))) AND (Thing AND (http://test.com#p2 VALUE http://test.com/res/obj1))" });
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
