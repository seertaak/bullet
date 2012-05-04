package org.bullet.primitive;

import java.util.ArrayList;
import java.util.List;

import org.bullet.CodeLoc;
import org.bullet.Interpreter;
import org.bullet.BulletException;
import org.bullet.value.Environment;
import org.bullet.value.Lambda;
import org.bullet.value.Symbol;
import org.bullet.value.BExpr;

public class PLambda implements PrimitiveFn {
	public Object call(Interpreter interp, List<Object> args, 
			CodeLoc begin, CodeLoc end) {
		
		List<Object> fnFormArgs = BExpr.getData(args.get(0));
		if (fnFormArgs == null)
			throw new BulletException("Null formal arguments for " + args, begin);
		List<Symbol> formParams = new ArrayList<Symbol>();
		boolean hasRest = false;
		for (Object arg : fnFormArgs) {
			if (arg instanceof Symbol) {
				formParams.add((Symbol) arg);
			} else if (arg instanceof BExpr) {
				if (hasRest)
					throw new BulletException("Can only have one rest arg.");
				
				List<Object> parts = BExpr.getData(arg);
				if (parts.size() != 2)
					throw new BulletException("Expected (rest restName), found: " + arg);
				
				formParams.add((Symbol) parts.get(1));
				hasRest = true;
			} else if (arg == null) {
				hasRest = false;
			} else {
				throw new BulletException("Expected a symbol, found: " + arg);
			}
		}
		
		List<Object> exprs = args.subList(1, args.size());
		Environment freeVars = capture(interp, formParams, exprs);
		
		//System.out.println("\nPLambda:captured:" + freeVars);
		
		return new Lambda(formParams, exprs, hasRest, freeVars);
	}

	public static Environment capture(Interpreter interp, List<Symbol> formParams, 
			List<Object> exprs) {
		// we need to implement a mini evaluator here.
		Environment env = new Environment();
		
		for (Object expr : exprs)
			captureRec(interp, formParams, expr, env);
		
		return env;
	}
	
	public static void captureRec(Interpreter interp, List<Symbol> formParams, 
			Object form, Environment env) {
		if (form instanceof BExpr) {
			for (Object datum : (BExpr) form)
				captureRec(interp, formParams, datum, env);
		} else if (form instanceof Symbol) {
			// is the symbol known in this environment?
			Symbol symb = (Symbol) form;
//			if (formParams.contains(symb))
//				System.out.println("captureRec:" + symb + " is a formal parameter.");
			if (interp.isBound(symb) && !formParams.contains(symb)) {
//				System.out.println("captureRec:captured:" + symb  + ":" + interp.getVar(symb));
				env.bind(symb, interp.getVar(symb));
			}
		}
	}
}
