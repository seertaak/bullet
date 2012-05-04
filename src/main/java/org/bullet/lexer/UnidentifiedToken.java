package org.bullet.lexer;

public class UnidentifiedToken extends RuntimeException {
	private static final long serialVersionUID = -5203650153764494708L;
	
	public UnidentifiedToken(String token, int pos) {
		super("Unidentified token at character " + pos + ": " + token);
	}
}
