package org.bt.rt.value;

public class Variable {

	public Object value;
	
	public Variable(Object value) {
		this.value = value;
	}
	
	public static Variable make(Object value) {
		return new Variable(value);
	}
}
