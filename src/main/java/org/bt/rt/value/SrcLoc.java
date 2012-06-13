package org.bt.rt.value;

import java.io.Serializable;
import java.nio.file.Path;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SrcLoc implements Serializable {
	private static final long serialVersionUID = -4630952251814368154L;
	
	private final Path src;
	public final int line, col;
	
	public SrcLoc(Path src, int line, int col) {
		super();
		this.src = src;
		this.line = line;
		this.col = col;
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
		return new StringBuilder(src != null ? src.toString() : "")
			.append(":")
			.append(line+1)
			.append(",")
			.append(col)
			.toString();
	}


}
