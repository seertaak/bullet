package org.bt;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Objects;

public class IdentifierAttributes {

	private String identifier;
	private Map<String, Object> attributes;
	
	public IdentifierAttributes(String identifier) {
		this.identifier = java.util.Objects.requireNonNull(identifier, "identifier must be null");
		this.attributes = new HashMap<>();
	}
	
	public IdentifierAttributes set(String attribute, Object value) {
		attributes.put(attribute, value);
		return this;
	}
	
	public Object get(String attribute) {
		return attributes.get(attribute);
	}
	
	public String identifier() {
		return identifier;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("identifier", identifier)
				.add("attributes", attributes)
				.toString();
	}
	
	@Override
	public int hashCode() {
		return java.util.Objects.hash(identifier.hashCode(), attributes.hashCode());
	}
	
	@Override
	public boolean equals(Object that) {
		if (this == that)
			return true;
		if (!(that instanceof IdentifierAttributes))
			return false;

		final IdentifierAttributes ithat = (IdentifierAttributes) that;
		
		return Objects.equal(identifier, ithat.identifier)
			&& Objects.equal(attributes, ithat.attributes);
	}

	public static void main(String...args) {
		IdentifierAttributes name = new IdentifierAttributes("name");
		name.set("location", new SrcLoc(null, 0, 2));
		IdentifierAttributes name2 = new IdentifierAttributes("name");
		name2.set("location", new SrcLoc(null, 0, 2));
		System.out.println(name);
		System.out.println(name.hashCode());
		System.out.println(name.equals(name2));
	}
}
