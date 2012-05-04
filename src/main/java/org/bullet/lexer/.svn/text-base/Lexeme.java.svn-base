package org.bullet.lexer;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bullet.CodeLoc;

public class Lexeme {
	
	private final CodeLoc begin;
	private final CodeLoc end;
	private final String text;
	private final Token type;
	
	public Lexeme(String text, Token type, 
			CodeLoc begin, CodeLoc end) {
		super();
		this.text = text;
		this.type = type;
		this.begin = begin;
		this.end = end;
	}

	public String getText() {
		return text;
	}

	public Token getType() {
		return type;
	}
	
	public boolean eof() {
		return type == Token.EOF;
	}
	
	public CodeLoc getBegin() {
		return begin;
	}
	
	public CodeLoc getEnd() {
		return end;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, 
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	@Override
	public boolean equals(Object that) {
		return EqualsBuilder.reflectionEquals(this, that);
	}
}
