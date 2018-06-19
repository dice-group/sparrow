package org.dice_group.sparrow.exceptions;

public class RuleHasNotNObjectsException extends Exception {
	public static final String MESSAGE = "Rule needs to have |Objects|=";

	public RuleHasNotNObjectsException(int n, String message) {
		super(MESSAGE + n +" "+ message);
	}

	public RuleHasNotNObjectsException(int n) {
		super(MESSAGE + n );
	}
}
