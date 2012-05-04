package org.bullet.lexer;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MatchOneChar implements TokenMatcher {
	
	private char ch;
	private String sch;
	
	public MatchOneChar(char ch) {
		this.ch = ch;
		this.sch = String.valueOf(ch);
	}
	
	public static MatchOneChar oneChar(char oneChar) {
		return new MatchOneChar(oneChar);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, 
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public String match(String in) {
		return in.codePointAt(0) == ch ? sch : null;
	}
}
