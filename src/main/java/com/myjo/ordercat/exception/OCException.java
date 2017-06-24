package com.myjo.ordercat.exception;

/**
 *
 */
public class OCException extends RuntimeException {
	private static final long serialVersionUID = -8717859180087775381L;
	protected int errorCode = Integer.MIN_VALUE;

	public OCException(Throwable t) {
		super(t);
	}

	public OCException(String s, Exception e) {
		super(s, e);
	}

	public OCException(String s) {
		super(s);
	}

	public OCException(int errorCode, String s) {
		super(s);
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

}
