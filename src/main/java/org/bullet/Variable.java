package org.bullet;

public class Variable {
	
	private Object value;
	
	public Variable(Object value) {
		this.value = value;
	}
	
	public Object get() {
		return value;
	}
	
	public Object set(Object value) {
		this.value = value;
		return value;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}

}
