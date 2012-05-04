package org.bullet.primitive;

import java.util.List;

import org.bullet.CodeLoc;
import org.bullet.Interpreter;

public class PEquals implements PrimitiveFn {

	public Object call(Interpreter interp, List<Object> args, 
			CodeLoc begin, CodeLoc end) {
		Object first = interp.eval(args.get(0));
		Object second = interp.eval(args.get(1));
		
		if (first != null)
			return first.equals(second);
		else
			return second == null;
	}

}
