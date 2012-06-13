package org.bt.rt.value;

import java.util.HashMap;

/**
 * This is the dumb, super-megamorphic version of an bullet object. It is
 * nothing more than a property map along with one or more generated
 * method calls.
 * 
 * The idea is that we can do more optimizations further on down just by 
 * creating a new subclass for Objekt, for example for objects that have
 * a small number of properties.
 * 
 * @author mpercossi
 *
 */
public class MegaObjekt implements Objekt {
	
	public HashMap<Object, Object> properties;
	
	/**
	 * Create an object with the given properties.
	 * 
	 * Lambda function: new Objekt("__call__", methodHandle)
	 * But note that we don't want to waste objects: the lambda
	 * is itself an objekt, so really the object needs to compile itself,
	 * and note the function that needs to be called (which is just generated
	 * in the most appropriate and specific w.r.t arguments), transforming
	 * it into a method handle and storing it in its own properties map
	 * as an entry for "__call__". 
	 * 
	 * meth display (
	 *  
	 * 
	 * @param properties
	 */
	public MegaObjekt(Object...properties) {
		this.properties = new HashMap<>();
		for (int i = 0; i < properties.length; i += 2) {
			this.properties.put(properties[i], properties[i+1]);
		}
	}
	
	public MegaObjekt() {
		this.properties = new HashMap<>();
	}
	
	@Override
	public Object __property__(Object property) {
		return properties.get(property);
	}
	
	@Override
	public Objekt __property__(Object property, Object value) {
		properties.put(property, value);
		return this;
	}

	@Override
	public String toString() {
		return properties.toString();
	}
	
	@Override
	public int hashCode() {
		return properties.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MegaObjekt))
			return false;
		MegaObjekt that = (MegaObjekt) obj;
		return properties.equals(that.properties);
	}
	
	public static void main(String...args) {
		MegaObjekt obj = new MegaObjekt("x", Variable.make(5), "y", Variable.make(2));
		
		obj.__property__("y", 6);
	}

}
