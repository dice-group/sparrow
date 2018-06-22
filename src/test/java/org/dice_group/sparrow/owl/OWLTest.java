package org.dice_group.sparrow.owl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class OWLTest {

	private String nodeString;
	private Object expected;



	@Parameters
	public static Collection<Object[]> data(){
		List<Object[]> data = new LinkedList<Object[]>();
		data.add(new Object[] {"Thing AND A", "Thing AND A"});
		data.add(new Object[] {"Thing AND (rule1 B)", "Thing AND (rule1 B)"});
		data.add(new Object[] {"(Thing AND (rule1 SOME (rule2 B AND A)))", "Thing AND (rule1 SOME ((rule2 B) AND A))"});
		data.add(new Object[] {"Thing", "Thing"});
		data.add(new Object[] {"(Thing AND A)", "Thing AND A"});		
		data.add(new Object[] {"rule1 B", "rule1 B"});
		data.add(new Object[] {"(rule1 SOME (rule2 C))", "rule1 SOME (rule2 C)"});
		data.add(new Object[] {"rule1 SOME (rule2 C)", "rule1 SOME (rule2 C)"});
		data.add(new Object[] {"rule1 VALUE B (B AND (rule2 C))", "rule1 SOME (B AND (rule2 C))"});
		return data;
	}
	
	public OWLTest(String nodeString, String expected) {
		this.nodeString=nodeString;
		this.expected=expected;
	}
	
	@Test
	public void parseTest() {
		OWLNode node = OWLParser.parse(this.nodeString);
		assertEquals(expected, node.build());
	}
	
}
