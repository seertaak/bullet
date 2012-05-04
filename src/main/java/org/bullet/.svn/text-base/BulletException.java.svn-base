package org.bullet;

public class BulletException extends RuntimeException {
	private static final long serialVersionUID = -7154825231050771381L;

	public BulletException(String string) {
		super(string);
	}
	
	public BulletException(String string, CodeLoc srcLoc) {
		super(string + " at: " + srcLoc);
	}

	public BulletException(Throwable e, CodeLoc srcLoc) {
		super("Caught bullet exception at: " + srcLoc + ": ", e);
	}
	
	public BulletException(Exception e) {
		super(e);
	}

	public BulletException() {
	}

}
