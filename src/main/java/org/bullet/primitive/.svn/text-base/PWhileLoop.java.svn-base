package org.bullet.primitive;

import java.util.List;

import org.bullet.CodeLoc;
import org.bullet.Interpreter;
import org.bullet.value.BExpr;

public class PWhileLoop implements PrimitiveFn {
	public Object call(Interpreter interp, List<Object> args, 
			CodeLoc begin, CodeLoc end) {
		BExpr test = (BExpr) args.get(0);
		List<Object> body = args.subList(1, args.size());
		
		while ((Boolean) interp.eval(test)) {
			interp.pushEnv();
		
			for (Object form : body) {
				interp.eval(form);
			}
			
			interp.popEnv();
		}
		
		return null;
	}
}
