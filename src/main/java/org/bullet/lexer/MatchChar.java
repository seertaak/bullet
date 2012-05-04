package org.bullet.lexer;

import static org.bullet.lexer.LexerUtils.*;

import org.apache.commons.lang3.StringEscapeUtils;

public class MatchChar implements TokenMatcher {

	private static final MatchChar INSTANCE = new MatchChar();
	
	public static final int escapeSeq(String in) {
		if (in.length() < 2) 
			return 0;
		else {
			if (in.charAt(0) == '\\') {
				char c = in.charAt(1);
				switch (c) {
				case 'b':
				case 't':
				case 'n':
				case 'f':
				case 'r':
				case '\"':
				case '\'':
				case '\\':
					return 2;
				default:
					int r = 1 + octalEsc(in.substring(1));
					return r;
				}
			} else {
				return 0;
			}
		}
	}

	private static final int octalEsc(String in) {
		int len = in.length();
		if (len > 1 && isOctalDigit(in.charAt(0))) {
			if (len > 2 && isOctalDigit(in.charAt(1))) {
				if (len > 3 && isOctalDigit(in.charAt(2))) {
					return 3;
				} else {
					return 2;
				}
			} else {
				return 1;
			}
		} else {
			return 0;
		}
	}
	
	private static final int stdChar(String in) {
		char c = in.charAt(0);
		if (c != '\'' && c != '\\') 
			return 1;
		else
			return 0;
	}

	@Override
	public String match(String in) {
		// TODO: add support for unicode escape codes!
		char c = in.charAt(0);
		if (c == '\'') {
			int end = 1;
			String subs = in.substring(1);
			int inc = escapeSeq(subs);
			if (inc == 0)
				inc = stdChar(subs);
			end += inc;
			c = in.charAt(end);
			if (end > 0 && in.charAt(end) == '\'')
				return in.substring(0, end+1);
			else
				return null;
		}
		return null;
	}
	
	public static final MatchChar character() {
		return INSTANCE;
	}
	
	public static void main(String...args) {
		String str = new MatchChar().match("'c'");
		System.out.println(str.substring(1, str.length()-1).charAt(0));
		
		str = new MatchChar().match("'\\n'");
		System.out.println(StringEscapeUtils.unescapeJava(str.substring(1, str.length()-1)));
		
//		str = new MatchChar().match("Martin");
//		System.out.println(str.substring(1, str.length()-1));
		
//		str = new MatchChar().match("1");
//		System.out.println(str.substring(1, str.length()-1));
		
	}

}
