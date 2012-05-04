package org.bullet.primitive;

import java.util.List;

import org.bullet.CodeLoc;
import org.bullet.Interpreter;

public class PPlus implements PrimitiveFn {

	public Object call(Interpreter interp, List<Object> args, 
			CodeLoc begin, CodeLoc end) {
		int sum = 0;
		for (Object arg : args) {
			//System.out.println("Plus:" + arg + "," + env);
			Object res = interp.eval(arg);
			sum += (Integer) res;
		}
		
		return sum;
	}

}
