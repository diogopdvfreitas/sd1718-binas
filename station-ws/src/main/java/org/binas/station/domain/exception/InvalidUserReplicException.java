package org.binas.station.domain.exception;

/** Exception used to signal a problem while initializing a station. */
public class InvalidUserReplicException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidUserReplicException() {
	}

	public InvalidUserReplicException(String message) {
		super(message);
	}
}
