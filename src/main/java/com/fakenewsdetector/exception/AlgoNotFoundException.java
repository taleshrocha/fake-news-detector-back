package com.fakenewsdetector.exception;

public class AlgoNotFoundException extends RuntimeException {

	public AlgoNotFoundException(String algo) {
		super("Could not found algorithm " + algo);
	}
}
