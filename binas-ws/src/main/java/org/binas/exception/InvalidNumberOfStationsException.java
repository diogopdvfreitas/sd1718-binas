package org.binas.exception;

public class InvalidNumberOfStationsException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidNumberOfStationsException() {
	}
	
	public InvalidNumberOfStationsException(String message) {
		super(message);
	}
}
