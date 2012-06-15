package org.bt;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SymbolTable {
	
	private Map<String, IdentifierAttributes> symTab;
	
	public SymbolTable() {
		symTab = new HashMap<String, IdentifierAttributes>();
	}
	
	public IdentifierAttributes lookup(String identifier) {
		return symTab.get(identifier);
	}
	
	public SymbolTable store(IdentifierAttributes identifier) {
		symTab.put(identifier.identifier(), identifier);
		return this;
	}
	
	
	@Override
	public String toString() {
		return Objects.toString(this);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(this);
	}
	
	@Override
	public boolean equals(Object that) {
		return Objects.equals(this, that);
	}
	
	
}
