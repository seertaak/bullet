package org.bt.rt.value;

public class Symbol {
	
	final private String symbol;
	final int hash;
	
	public Symbol(String symbol) {
		this.symbol = symbol.intern();
		this.hash = this.symbol.hashCode();
	}

	public String get() {
		return symbol;
	}
	
	public static Symbol valueOf(String symbol) {
		return new Symbol(symbol);
	}
	
	@Override
	public String toString() {
		return "'" + symbol;
	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof Symbol)
			return hash == ((Symbol)that).hash;
		else
			return false;
	}
	
	public String getData() {
		return symbol;
	}

	public static String getData(Object object) {
		if (object instanceof Symbol)
			return ((Symbol)object).symbol;
		else
			return null;
	}
}
