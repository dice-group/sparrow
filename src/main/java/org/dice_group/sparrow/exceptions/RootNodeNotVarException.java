package org.dice_group.sparrow.exceptions;

public class RootNodeNotVarException extends Exception {
	public static final String MESSAGE = "Root Node of graph is not a variable. ";

	public RootNodeNotVarException(String message) {
		super(MESSAGE + message);
	}

	public RootNodeNotVarException() {
		super(MESSAGE);
	}

}
