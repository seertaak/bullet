package org.bullet.value;

import java.util.HashMap;
import java.util.Map;

import org.bullet.Type;
import org.bullet.primitive.PAnd;
import org.bullet.primitive.PBegin;
import org.bullet.primitive.PCond;
import org.bullet.primitive.PDivide;
import org.bullet.primitive.PEquals;
import org.bullet.primitive.PForLoop;
import org.bullet.primitive.PGreaterThan;
import org.bullet.primitive.PImport;
import org.bullet.primitive.PInclude;
import org.bullet.primitive.PLambda;
import org.bullet.primitive.PLessThan;
import org.bullet.primitive.PMacroTfm;
import org.bullet.primitive.PMinus;
import org.bullet.primitive.PNew;
import org.bullet.primitive.PNot;
import org.bullet.primitive.POr;
import org.bullet.primitive.PPlus;
import org.bullet.primitive.PPrint;
import org.bullet.primitive.PQQuote;
import org.bullet.primitive.PQuote;
import org.bullet.primitive.PShow;
import org.bullet.primitive.PThrow;
import org.bullet.primitive.PTimes;
import org.bullet.primitive.PVal;
import org.bullet.primitive.PVarDef;
import org.bullet.primitive.PVarSet;
import org.bullet.primitive.PWhileLoop;
import org.bullet.primitive.PrimitiveFn;

public class Primitive {
	
	private static Map<String, PrimitiveFn> primitives 
		= new HashMap<String, PrimitiveFn>();
	
	static {
		primitives.put("print", new PPrint());
		primitives.put("show", new PShow());
		primitives.put("+", new PPlus());
		primitives.put("-", new PMinus());
		primitives.put("*", new PTimes());
		primitives.put("/", new PDivide());
		primitives.put("fn", new PLambda());
		primitives.put("val", new PVal());
		primitives.put("begin", new PBegin());
		primitives.put("var", new PVarDef());
		primitives.put("set", new PVarSet());
		primitives.put("for", new PForLoop());
		primitives.put("while", new PWhileLoop());
		primitives.put("<", new PLessThan());
		primitives.put(">", new PGreaterThan());
		primitives.put("==", new PEquals());
		primitives.put("and", new PAnd());
		primitives.put("or", new POr());
		primitives.put("cond", new PCond());
		primitives.put("quote", new PQuote());
		primitives.put("qquote", new PQQuote());
		primitives.put("tfm", new PMacroTfm());
		primitives.put("new", new PNew());
		primitives.put("import", new PImport());
		primitives.put("not", new PNot());
		primitives.put("include", new PInclude());
		primitives.put("throw", new PThrow());
	}
	
	private PrimitiveFn value;
	
	private Primitive(String name) {
		this.value = primitives.get(name);
	}
	
	public static Object valueOf(Symbol primitive) {
		return primitive.get();
	}
	
	public Type type() {
		return Type.Primitive;
	}

	public PrimitiveFn get() {
		return value;
	}

	public static Map<String, PrimitiveFn> all() {
		return primitives;
	}
}
