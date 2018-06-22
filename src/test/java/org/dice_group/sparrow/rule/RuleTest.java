package org.dice_group.sparrow.rule;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.dice_group.sparrow.graph.Triple;
import org.dice_group.sparrow.graph.impl.URIGraphNode;
import org.dice_group.sparrow.graph.impl.VarGraphNode;
import org.dice_group.sparrow.owl.OWLNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class RuleTest {

	private Object[] input;
	private String output;
	private boolean dismiss;
	private String expectedOWL;
	private Triple relation;
	private boolean fitOutcome;

	@Parameters
	public static Collection<Object[]> data() {
		List<Object[]> data = new LinkedList<Object[]>();
		// input output, dismiss, expectedOWL
		data.add(new Object[] { new Object[] { new VarGraphNode("?s"), "rdf:type", new URIGraphNode("URI_1") },
				"class URI_1", true, "class dbr:Human",
				new Triple(new VarGraphNode("?s"), new URIGraphNode("rdf:type"), new URIGraphNode("<dbr:Human>")),
				true });
		data.add(new Object[] { new Object[] { new VarGraphNode("?s"), new URIGraphNode("URI_0"), new URIGraphNode("URI_1") },
				"URI_0 URI_1", true, "rdf:label dbr:Human",
				new Triple(new VarGraphNode("?s"), new URIGraphNode("rdf:label"), new URIGraphNode("dbr:Human")),
				true });
		data.add(new Object[] { new Object[] { new VarGraphNode("?s"), "rdf:type", new URIGraphNode("URI_1") },
				"class URI_1", true, "class dbr:Human",
				new Triple(new VarGraphNode("?s"), new URIGraphNode("rdf:class"), new URIGraphNode("dbr:Human")),
				false });
		data.add(new Object[] { new Object[] { new VarGraphNode("?s"), "rdf:class", new URIGraphNode("URI_1") },
				"class URI_1", false, "class <dbr:Human>",
				new Triple(new VarGraphNode("?s"), new URIGraphNode("rdf:class"), new URIGraphNode("<dbr:Human>")),
				true });
		data.add(new Object[] { new Object[] { new URIGraphNode("URI_1"), "rdf:class", new VarGraphNode("%%_1") },
				"class %%_1", false, "class ?s",
				new Triple(new URIGraphNode("dbr:human"), new URIGraphNode("rdf:class"), new VarGraphNode("s")),
				true });
		return data;
	}

	public RuleTest(Object[] input, String output, boolean dismiss, String expectedOWL, Triple relation,
			boolean fitOutcome) {
		this.input = input;
		this.output = output;
		this.dismiss = dismiss;
		this.expectedOWL = expectedOWL;
		this.relation = relation;
		this.fitOutcome = fitOutcome;
	}

	@Test
	public void ruleExecutionTest() {
		Rule r = new Rule(this.input, this.output, this.dismiss, 0);
		assertEquals(fitOutcome, r.fits(relation, 0));
		if (fitOutcome) {
			OWLNode n = r.execute(relation);
			assertEquals(this.expectedOWL, n.toString());
		}
	}

}
