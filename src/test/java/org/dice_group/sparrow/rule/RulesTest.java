package org.dice_group.sparrow.rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.dice_group.sparrow.exceptions.RuleHasNotNObjectsException;
import org.dice_group.sparrow.exceptions.RuleNotAvailableException;
import org.dice_group.sparrow.graph.Triple;
import org.dice_group.sparrow.graph.impl.BNodeGraphNode;
import org.dice_group.sparrow.graph.impl.LiteralGraphNode;
import org.dice_group.sparrow.graph.impl.URIGraphNode;
import org.dice_group.sparrow.graph.impl.VarGraphNode;
import org.dice_group.sparrow.owl.OWLNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class RulesTest {
	
	@Parameters
	public static Collection<Object[]> data(){
		List<Object[]> data = new LinkedList<Object[]>();
		data.add(new Object[] {new String[] {"0 -> %% rdf:type URI_1 -> class URI_1"}, 
				new Triple(new VarGraphNode("s"), new URIGraphNode("rdf:type"),
						new URIGraphNode("dbr:Human")),
				"class dbr:Human", true});
		data.add(new Object[] {new String[] {"0 -> %% rdf:class URI_1 -> class URI_1"}, 
				new Triple(new VarGraphNode("s"), new URIGraphNode("rdf:type"),
						new URIGraphNode("dbr:Human")),
				"class dbr:Human", false});
		data.add(new Object[] {new String[] {"0 -> URI_0 %%_0 %%_1 -> %%_0 URI_0", "0 -> %% rdf:type URI_1 -> class URI_1"}, 
				new Triple(new VarGraphNode("s"), new URIGraphNode("rdf:type"),
						new URIGraphNode("dbr:Human")),
				"class dbr:Human", true});
		data.add(new Object[] {new String[] {"0 -> URI_0 %%_0 %%_1 -> %%_1 URI_0", "0 -> %% rdf:type URI_1 -> class URI_1"}, 
				new Triple(new URIGraphNode("dbr:Human"), new VarGraphNode("s"), new VarGraphNode("p")),
				"SOME dbr:Human", true});
		data.add(new Object[] {new String[] {"0 -> BNODE URI_1 LITERAL_1 -> BNODE (URI_1 LITERAL_1)"}, 
				new Triple(new BNodeGraphNode("_:abc"), new URIGraphNode("dbr:Human"), new LiteralGraphNode("\"Hello World\"")),
				"_:abc (dbr:Human \"Hello World\")", true});
		return data;
	}

	private String[] rules;
	private Triple relation;
	private String expected;
	private boolean expectedFit;

	public RulesTest(String[] rules, Triple relation, String expected, boolean expectedFit) {
		this.rules=rules;
		this.relation=relation;
		this.expected=expected;
		this.expectedFit=expectedFit;
	}
	
	@Test
	public void loadNExecute() throws RuleHasNotNObjectsException, RuleNotAvailableException {
		Rules r  = new Rules();
		for(String rule : rules)
			r.loadRule(rule, true);
		try {
			OWLNode result = r.execute(relation, 0);
			assertEquals(expectedFit, true);
			assertEquals(expected, result.toString());
		}catch(RuleNotAvailableException e) {
			assertEquals(expectedFit, false);
		}
	}
	
}
