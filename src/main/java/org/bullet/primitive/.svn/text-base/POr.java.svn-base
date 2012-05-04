package org.bullet.primitive;

import java.util.List;

import org.bullet.CodeLoc;
import org.bullet.Interpreter;

public class POr implements PrimitiveFn {

	public Object call(Interpreter interp, List<Object> args, 
			CodeLoc begin, CodeLoc end) {
		if ((Boolean) interp.eval(args.get(0)))
			return true;
		return interp.eval(args.get(1));
	}
}
