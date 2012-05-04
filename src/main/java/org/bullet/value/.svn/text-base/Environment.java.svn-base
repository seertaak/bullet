package org.bullet.value;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bullet.BulletException;
import org.bullet.Variable;

public class Environment {
	
	protected Map<Symbol, Variable> bindings;
	
	public Environment() {
		bindings = new HashMap<>();
	}
	
	public Environment(Environment parent) {
		bindings = new HashMap<>();
		bindings.putAll(parent.bindings);
	}
	
	/**
	 * Bind in this environment.
	 * @param ident
	 * @param value
	 */
	public void bind(Symbol ident, Object value) {
		bindings.put(ident, new Variable(value));
	}
	
	/**
	 * Is bound in this specific environment. Note
	 * that the identifier may be present further
	 * down the stack!
	 * @param ident
	 * @return
	 */
	public boolean isBound(Symbol ident) {
		return bindings.containsKey(ident);
	}
	
	public void bind(Symbol ident, Variable var) {
		bindings.put(ident, var);
	}
	
	public void unbind(Symbol ident) {
		bindings.remove(ident);
	}
	
	public boolean isPrimitive(Symbol ident) {
		return isBound(ident) 
				&& lookup(ident) instanceof Primitive;
	}
	
	public Object lookup(Symbol symb) {
		return getVar(symb).get();
	}
	
	public Variable getVar(Symbol symb) {
		if (!bindings.containsKey(symb)) {
			throw new BulletException("Identifier " 
					+ symb + " not found.",
					symb.getLoc());
		}
		
		return bindings.get(symb);
	}
	
	@Override
	public String toString() {
		return bindings.toString();
	}

	public Set<Symbol> names() {
		return bindings.keySet();
	}
}
