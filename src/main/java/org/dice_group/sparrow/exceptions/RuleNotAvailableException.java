package org.dice_group.sparrow.exceptions;

public class RuleNotAvailableException extends Exception {
	public static final String MESSAGE = "No Rule Found for GraphNode. ";

	public RuleNotAvailableException(String message) {
		super(MESSAGE + message);
	}

	public RuleNotAvailableException() {
		super(MESSAGE);
	}
}
