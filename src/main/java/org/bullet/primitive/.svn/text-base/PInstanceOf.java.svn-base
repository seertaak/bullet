package org.bullet.primitive;

import java.util.List;

import org.bullet.BulletException;
import org.bullet.CodeLoc;
import org.bullet.Interpreter;

public class PInstanceOf implements PrimitiveFn {

	public Object call(Interpreter interp, List<Object> args, 
			CodeLoc begin, CodeLoc end) {
		if (args.size() != 2)
			throw new BulletException("Expected ('instance?' classname instance), got: " + args);
		
		Object clVal = interp.eval(args.get(0));
		Object inst = interp.eval(args.get(1));
		
		Class<?> clazz = (Class<?>) clVal;
		
		return clazz.isAssignableFrom(inst.getClass());
	}

}
