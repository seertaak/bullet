package org.bullet.primitive;

import java.util.List;

import org.bullet.CodeLoc;
import org.bullet.Interpreter;

public class PMinus implements PrimitiveFn {

	public Object call(Interpreter interp, List<Object> args, 
			CodeLoc begin, CodeLoc end) {
		int sum = (Integer) interp.eval(args.get(0));
		for (Object arg : args.subList(1, args.size())) {
			//System.out.println("Plus:" + arg + "," + env);
			Object res = interp.eval(arg);
			sum -= (Integer) res;
		}
		return sum;
	}

}
