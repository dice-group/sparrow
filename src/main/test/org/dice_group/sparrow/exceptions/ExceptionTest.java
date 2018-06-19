package org.dice_group.sparrow.exceptions;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ExceptionTest {

	
	@Test
	public void messageTest() {
		Exception e = new GraphContainsCycleException();
		assertEquals(GraphContainsCycleException.MESSAGE, e.getMessage());
		e = new GraphContainsCycleException("Test");
		assertEquals(GraphContainsCycleException.MESSAGE+"Test", e.getMessage());

		
		e = new RootNodeNotVarException();
		assertEquals(RootNodeNotVarException.MESSAGE, e.getMessage());
		e = new RootNodeNotVarException("Test");
		assertEquals(RootNodeNotVarException.MESSAGE+"Test", e.getMessage());

		
		e = new RuleHasNotNObjectsException(2);
		assertEquals(RuleHasNotNObjectsException.MESSAGE+"2", e.getMessage());
		e = new RuleHasNotNObjectsException(3, "Test");
		assertEquals(RuleHasNotNObjectsException.MESSAGE+"3 Test", e.getMessage());

		
		e = new RuleNotAvailableException();
		assertEquals(RuleNotAvailableException.MESSAGE, e.getMessage());
		e = new RuleNotAvailableException("Test");
		assertEquals(RuleNotAvailableException.MESSAGE+"Test", e.getMessage());

		
	}
}
