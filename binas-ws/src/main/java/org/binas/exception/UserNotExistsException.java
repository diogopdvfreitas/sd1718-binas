package org.binas.exception;

public class UserNotExistsException extends Exception{

	private static final long serialVersionUID = 1L;

	public UserNotExistsException() {
	}

	public UserNotExistsException(String message) {
		super(message);
	}

}
