package org.bullet.primitive;

import java.util.List;

import org.bullet.CodeLoc;
import org.bullet.Interpreter;

public class PNot implements PrimitiveFn {
	public Object call(Interpreter interp, List<Object> args, 
			CodeLoc begin, CodeLoc end) {
		return !(Boolean) interp.eval(args.get(0));
	}
}
