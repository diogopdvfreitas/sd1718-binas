package org.binas.exception;

public class InvalidEmailException extends Exception{

	private static final long serialVersionUID = 1L;

	public InvalidEmailException() {
	}

	public InvalidEmailException(String message) {
		super(message);
	}

}
