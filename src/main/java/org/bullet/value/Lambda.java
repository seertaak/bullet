package org.bullet.value;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bullet.Type;

public class Lambda {
	
	private List<Symbol> formalParams;
	private List<Object> exprs;
	private boolean lastParmRest;
	private Environment freeVars;

	public Lambda(List<Symbol> formalParams,
			List<Object> exprs, boolean lastParmRest, Environment freeVars) {
		this.formalParams = formalParams;
		this.exprs = exprs;
		this.lastParmRest = lastParmRest;
		this.freeVars = freeVars;
//		if (!freeVars.names().isEmpty())
//			System.out.println("Lambda:freeVars:" + freeVars);
	}
	
	public List<Symbol> formalParams() {
		return formalParams;
	}
	
	public List<Object> body() {
		return exprs;
	}

	public boolean lastParmRest() {
		return lastParmRest;
	}
	
	public Environment freeVars() {
		return freeVars;
	}
	
	public Type type() {
		return Type.Lambda;
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
