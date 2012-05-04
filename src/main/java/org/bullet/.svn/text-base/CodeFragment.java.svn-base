package org.bullet;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CodeFragment {
	private final String line;
	private final CodeLoc begin;
	private final CodeLoc end;
	public CodeFragment(String line, 
			CodeLoc begin, CodeLoc end) {
		this.line = line; 
		this.begin = begin;
		this.end = end;
	}
	public String code() {
		return line;
	}
	public CodeLoc begin() {
		return begin;
	}
	public CodeLoc end() {
		return end;
	}
	public String toString() {
		return ToStringBuilder.reflectionToString(this, 
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}