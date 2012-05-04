package org.bullet.asm;

import org.bullet.Interpreter;
import org.bullet.value.BExpr;
import org.bullet.value.Objekt;
import org.bullet.value.Symbol;

public class RunnableProxy implements Runnable {
	
	private Interpreter interp;
	private Objekt obj;
	
	@Override
	public void run() {
		BExpr code = new BExpr();
		code.add(obj);
		code.add(Symbol.valueOf("run", null));
		interp.eval(code);
	}
}
