package org.dice_group.sparrow.exceptions;

public class TooManyProjectVarsException extends Exception {

	public TooManyProjectVarsException(String string) {
		// TODO Auto-generated constructor stub
		super("Too many Project Variables : Variables encountered "+string);
	}

}
