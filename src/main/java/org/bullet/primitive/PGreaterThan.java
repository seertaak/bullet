package org.bullet.primitive;

import java.util.List;

import org.bullet.BulletException;
import org.bullet.CodeLoc;
import org.bullet.Interpreter;

public class PGreaterThan implements PrimitiveFn {

	public Object call(Interpreter interp, List<Object> args, 
			CodeLoc begin, CodeLoc end) {
		
		if (args.size() != 2)
			throw new BulletException("Expected 2 args to >, but found: " 
					+ args, begin);
		
		Object first = interp.eval(args.get(0));
		Object second = interp.eval(args.get(1));
		
		return ((Integer) first) > ((Integer) second);
	}

}
