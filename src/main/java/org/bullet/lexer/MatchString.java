package org.bullet.lexer;

import org.apache.commons.lang3.StringEscapeUtils;

public class MatchString implements TokenMatcher {

	private static final MatchString INSTANCE = new MatchString();

	@Override
	public String match(String in) {
		char c = in.charAt(0);
		if (c == '"') {
			for (int i = 1; i < in.length(); i++) {
				c = in.charAt(i);
				
				if (c == '\\') {
					if (in.length() > i+1) 
						i++;
					else 
						break;
				} else if (c == '"') {
					return in.substring(0, i+1);
				}
			}
		}
		
		return null;
	}
	
	public static final MatchString string() {
		return INSTANCE;
	}
	
	public static void main(String...args) {
		String str = new MatchString().match("\"this is a \\\"string\\\"\"");
		System.out.println(str);
		System.out.println(StringEscapeUtils.unescapeJava(str.substring(1, str.length()-1)));
	}

}
