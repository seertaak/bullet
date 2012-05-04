package org.bullet.primitive;

import java.util.List;

import org.bullet.CodeLoc;
import org.bullet.Interpreter;

public class PPrint implements PrimitiveFn {

	public Object call(Interpreter interp, List<Object> args, 
			CodeLoc begin, CodeLoc end) {
		for (Object arg : args) {
			Object res = interp.eval(arg);
			System.out.print(res);
		}
		
		System.out.println();
		
		return null;
	}
}
