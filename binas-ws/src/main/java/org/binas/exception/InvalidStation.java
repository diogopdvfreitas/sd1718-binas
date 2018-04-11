package org.binas.exception;

public class InvalidStation extends Exception{

	private static final long serialVersionUID = 1L;

	public InvalidStation() {
	}

	public InvalidStation(String message) {
		super(message);
	}

}
