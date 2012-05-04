package org.bullet.lexer;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MatchKeywords implements TokenMatcher {
	
	private String[] keywords;

	public MatchKeywords(String...keywords) {
		this.keywords = keywords;
	}
	
	public static MatchKeywords keywords(String...keywords) {
		return new MatchKeywords(keywords);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, 
				ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public String match(String in) {
		for (String keyword : keywords) {
			if (in.startsWith(keyword)) {
				return keyword;
			}
		}
		return null;
	}

}
