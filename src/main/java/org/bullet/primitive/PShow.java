package org.bullet.primitive;

import java.util.ArrayList;
import java.util.List;

import org.bullet.CodeLoc;
import org.bullet.Interpreter;
import org.bullet.value.BExpr;

public class PShow implements PrimitiveFn {

	public Object call(Interpreter interp, List<Object> args, 
			CodeLoc begin, CodeLoc end) {
		List<Object> nuArgs = new ArrayList<Object>();
		
		for (Object arg : args) {
			Object res = interp.eval(arg);
			nuArgs.add(res);
		}
		
		BExpr result = new BExpr(nuArgs, begin, end);
		System.out.println(result);
		
		return result;
	}
}
