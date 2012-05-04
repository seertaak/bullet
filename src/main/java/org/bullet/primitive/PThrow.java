package org.bullet.primitive;

import java.util.List;

import org.bullet.BulletException;
import org.bullet.CodeLoc;
import org.bullet.Interpreter;

public class PThrow implements PrimitiveFn {

	@Override
	public Object call(Interpreter interp, List<Object> args, CodeLoc begin,
			CodeLoc end) {
		
		// args must be a single Exception element.
		if (args.size() != 1)
			throw new BulletException("Expected a single argument to throw, found: " 
					+ args, begin);
		
		Object o = interp.eval(args.get(0));
		if (!(o instanceof Throwable))
			throw new BulletException("Expected a Throwable in throw, found: " 
					+ o, begin);
		
		throw new BulletException((Throwable) o, begin);
	}
}
