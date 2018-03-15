package org.dice_group.sparrow.exceptions;

public class GraphContainsCycleException extends Exception {

	public static final String MESSAGE = "Graph contains a cycle which can not be converted to OWL. ";
	
	
	public GraphContainsCycleException(String message) {
		super(MESSAGE+message);
	}
	
	public GraphContainsCycleException() {
		super(MESSAGE);
	}
	

}
