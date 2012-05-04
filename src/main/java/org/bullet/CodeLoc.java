package org.bullet;

import java.io.File;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CodeLoc {
	
	private final File srcFile;
	private final int lineNum;
	private final int charPos;
	
	public CodeLoc(File srcFile, int lineNum, int charPos) {
		super();
		if (srcFile == null)
			throw new NullPointerException();
		this.srcFile = srcFile;
		this.lineNum = lineNum;
		this.charPos = charPos;
	}
	
	public int getLineNum() {
		return lineNum;
	}
	
	public int getCharPos() {
		return charPos;
	}
	
	public File getSrcFile() {
		return srcFile;
	}

	@Override
	public boolean equals(Object that) {
		return EqualsBuilder.reflectionEquals(this, that);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return new StringBuilder(srcFile != null ? srcFile.getPath() : "")
			.append(":")
			.append(lineNum+1)
			.append(",")
			.append(charPos)
			.toString();
	}

}
