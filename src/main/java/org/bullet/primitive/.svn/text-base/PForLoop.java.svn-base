package org.bullet.primitive;

import java.util.List;

import org.bullet.CodeLoc;
import org.bullet.Interpreter;
import org.bullet.value.BExpr;

public class PForLoop implements PrimitiveFn {
	
	public Object call(Interpreter interp, List<Object> args, 
			CodeLoc begin, CodeLoc end) {
		
		BExpr clauses = (BExpr) args.get(0);
		List<Object> body = args.subList(1, args.size());
		
		BExpr init = (BExpr) clauses.get(0);
		BExpr test = (BExpr) clauses.get(1);
		BExpr incr = (BExpr) clauses.get(2);
		
		interp.pushEnv();
		
		interp.eval(init);
		
		while ((Boolean) interp.eval(test)) {
			interp.pushEnv();
			for (Object form : body) {
				interp.eval(form);
			}
			interp.popEnv();
			
			interp.eval(incr);
			
		}
		
		interp.popEnv();
		
		return null;
	}

}
