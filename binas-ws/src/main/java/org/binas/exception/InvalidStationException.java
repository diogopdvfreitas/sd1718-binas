package org.binas.exception;

public class InvalidStationException extends Exception{

	private static final long serialVersionUID = 1L;

	public InvalidStationException() {
	}

	public InvalidStationException(String message) {
		super(message);
	}

}
