package org.bullet.lexer;

public class MatchIdentifier implements TokenMatcher {
	
	private static final MatchIdentifier INSTANCE = new MatchIdentifier();

	public static final boolean isOperator(char cp) {
		switch (cp) {
		case '~':
		case '@':
		case '#':
		case '%':
		case '^':
		case '*':
		case '+':
		case '-':
		case '=':
		case '<':
		case '>':
		case '?':
		case '/':
		case '!':
			return true;
		}
		
		return false;
	}
	
	private static final boolean isIdentStart(char cp) {
		return Character.isJavaIdentifierStart(cp)
			|| isOperator(cp);
	}
	
	private static final boolean isIdentPart(char cp) {
		return Character.isJavaIdentifierPart(cp)
			|| isOperator(cp);
	}
	
	public static final MatchIdentifier identifier() {
		return INSTANCE;
	}

	@Override
	public String match(String in) {
		if (isIdentStart(in.charAt(0))) {
			int i = 1;
			char ch = in.charAt(i);
			while (isIdentPart(ch)) {
				if (++i >= in.length())
					break;
				ch = in.charAt(i);
			}
			String result = in.substring(0, i);
			if (result.equals("true") || result.equals("false") || result.equals("null"))
				return null;
			else
				return result;
		}
		
		return null;
	}
	
	public static void main(String...args) {
		System.out.println(new MatchIdentifier().match("0+="));
	}
}
