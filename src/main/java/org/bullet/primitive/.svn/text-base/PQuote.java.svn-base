package org.bullet.primitive;

import java.util.List;

import org.bullet.CodeLoc;
import org.bullet.Interpreter;

public class PQuote implements PrimitiveFn {
	public Object call(Interpreter interp, List<Object> args, 
			CodeLoc begin, CodeLoc end) {
		if (args.size() > 1)
			throw new RuntimeException("(val) must be called with precisely one param.: " 
					+ args);
		
		return args.get(0);
	}
}
