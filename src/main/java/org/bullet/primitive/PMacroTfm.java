package org.bullet.primitive;

import java.util.ArrayList;
import java.util.List;

import org.bullet.BulletException;
import org.bullet.CodeLoc;
import org.bullet.Interpreter;
import org.bullet.value.Environment;
import org.bullet.value.Macro;
import org.bullet.value.Symbol;
import org.bullet.value.BExpr;

public class PMacroTfm implements PrimitiveFn {

	public Object call(Interpreter interp, List<Object> args, 
			CodeLoc begin, CodeLoc end) {
		List<Object> fnFormArgs = BExpr.getData(args.get(0));
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
				
				formParams.add((Symbol)parts.get(1));
				hasRest = true;
			} else if (arg == null) {
				hasRest = false;
			} else {
				throw new BulletException("Expected a symbol, found: " + arg);
			}
		}
		
		List<Object> exprs = args.subList(1, args.size());
		Environment freeVars = PLambda.capture(interp, formParams, exprs);
		
//		System.out.println("MacroTfm: captured: " + freeVars);
		
		return new Macro(formParams, exprs, hasRest, freeVars);
	}

}
