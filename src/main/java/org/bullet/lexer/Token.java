package org.bullet.lexer;

import static org.bullet.lexer.MatchIdentifier.*;
import static org.bullet.lexer.MatchKeywords.*;
import static org.bullet.lexer.MatchOneChar.*;
import static org.bullet.lexer.MatchTwoChars.*;
import static org.bullet.lexer.MatchInteger.*;
import static org.bullet.lexer.MatchDecimal.*;
import static org.bullet.lexer.MatchString.*;
import static org.bullet.lexer.MatchChar.*;

/**
 * Bullet tokens. Note that the order of appearance is important, since
 * lexing consists simply loops over <code>Token</code> each of 
 * <code>Token.values()</code>.
 * @author mpercossi
 */
public enum Token {
	Arrow				(twoChars(". ")),	// eg: print "martin". substring 0 2
	WS					(null), // this one is directly by Lexer.
	OParen				(oneChar('(')),
	CParen				(oneChar(')')),
	Period				(oneChar('.')),
	Pipe				(oneChar('|')),
	Colon				(oneChar(':')),
	Dollar				(oneChar('$')),
	SemiColon			(oneChar(';')),
	QQuote				(oneChar('`')),
	UnquoteSplicing		(twoChars(",@")),
	Unquote				(oneChar(',')),
	Ampersand			(oneChar('&')),
	Ident				(identifier()),
	BoolLit				(keywords("true", "false")),
	NullLit				(keywords("null")),
	DecimalLit			(decimal()),
	IntLit				(integer()),
	StringLit			(string()),
	CharLit				(character()),
	Quote				(oneChar('\'')),
	EOF					(keywords("____EOF____"));	// this one is also special of course.
	
	private final TokenMatcher matcher;
	
	private Token(TokenMatcher matcher) {
		this.matcher = matcher;
	}
	
	public String match(String in) {
		return matcher.match(in);
	}
}
