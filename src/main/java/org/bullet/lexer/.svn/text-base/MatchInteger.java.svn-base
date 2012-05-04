package org.bullet.lexer;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MatchInteger implements TokenMatcher {
	private static final MatchInteger INSTANCE = new MatchInteger();

	public static MatchInteger integer() {
		return INSTANCE;
	}
	
	@Override
	public String match(String in) {
		return LexerUtils.matchInteger(in);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, 
				ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
