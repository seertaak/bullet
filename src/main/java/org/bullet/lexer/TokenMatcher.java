package org.bullet.lexer;

public interface TokenMatcher {
	/**
	 * Returns the longest match of <code>this</code> token in the 
	 * string <code>in</code>, and <code>null</null> if the token 
	 * does not match the input.
	 */
	String match(String in);
}
