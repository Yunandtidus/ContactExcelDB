package com.mva.bd.exception;

public class BDException extends Exception {
	private static final long serialVersionUID = 1L;

	public BDException() {
	}

	public BDException(String msg) {
		super(msg);
	}

	public BDException(Throwable cause) {
		super(cause);
	}

	public BDException(String message, Throwable cause) {
		super(message, cause);
	}
}
