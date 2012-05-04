package org.bullet.asm;

import org.bullet.Interpreter;
import org.bullet.value.BExpr;
import org.bullet.value.Objekt;
import org.bullet.value.Symbol;

public class FooImpl {
	
	private Interpreter interp;
	private Objekt obj;

	public Object bar(Integer x, String y, String z, Integer ci) {
		BExpr code = new BExpr();
		code.add(obj);
		code.add(Symbol.valueOf("run", null));
		code.add(x);
		code.add(y);
		code.add(z);
		code.add(ci);
		return interp.eval(code);
	}

}
