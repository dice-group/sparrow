package org.dice_group.sparrow.exceptions;

public class TooManyProjectVarsException extends Exception {

	public TooManyProjectVarsException(String string) {
		super("Too many Project Variables : Variables encountered "+string);
	}

}
