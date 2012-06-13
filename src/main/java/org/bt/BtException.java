package org.bt;


public class BtException extends RuntimeException {
	private static final long serialVersionUID = 7543045681116185309L;

	public BtException(String string) {
		super(string);
	}
	
	public BtException(String string, SrcLoc srcLoc) {
		super(string + " at: " + srcLoc);
	}

	public BtException(Throwable e, SrcLoc srcLoc) {
		super("Caught bullet exception at: " + srcLoc + ": ", e);
	}
	
	public BtException(Exception e) {
		super(e);
	}

	public BtException() {
	}

}
