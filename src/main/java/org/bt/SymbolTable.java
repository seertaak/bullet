package org.bt;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Objects;

public class SymbolTable {
	
	private SymbolTable parent;
	private Map<String, IdentifierAttributes> symTab;
	
	public SymbolTable(SymbolTable parent) {
		this.parent = parent;
		this.symTab = new HashMap<>();
	}
	
	public SymbolTable() {
		this(null);
	}
	
	public IdentifierAttributes lookup(String ident) {
		return symTab.get(ident);
	}
	
	public SymbolTable insert(IdentifierAttributes ident) {
		symTab.put(ident.identifier(), ident);
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(parent, symTab);
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("parent", parent)
				.add("symTab", symTab)
				.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof SymbolTable))
			return false;
		
		SymbolTable that = (SymbolTable) obj;
			
		return Objects.equal(this.parent, that.parent)
			&& Objects.equal(this.symTab, that.symTab);
	}
	
}
