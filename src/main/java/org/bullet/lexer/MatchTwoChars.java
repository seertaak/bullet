package org.bullet.lexer;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MatchTwoChars implements TokenMatcher {
	
	private char ch1;
	private char ch2;
	private String sch;
	
	public MatchTwoChars(String twoChars) {
		this.ch1 = twoChars.charAt(0);
		this.ch2 = twoChars.charAt(1);
		this.sch = twoChars;
	}
	
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, 
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public String match(String in) {
		return in.codePointAt(0) == ch1 
				? in.codePointAt(1) == ch2 ? sch : null
				: null;
	}
	
	public static MatchTwoChars twoChars(String twoChars) {
		return new MatchTwoChars(twoChars);
	}
	
	public static void main(String... args) {
		TokenMatcher comment = new MatchTwoChars("//");
		System.out.println(comment);
		System.out.println(comment.match("foobar"));
		System.out.println(comment.match("// a comment"));
	}
}
