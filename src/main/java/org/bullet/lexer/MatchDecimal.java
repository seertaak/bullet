package org.bullet.lexer;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MatchDecimal implements TokenMatcher {
	private static final MatchDecimal INSTANCE = new MatchDecimal();

	public static MatchDecimal decimal() {
		return INSTANCE;
	}
	
	@Override
	public String match(String in) {
		return LexerUtils.matchDecimal(in);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, 
				ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
