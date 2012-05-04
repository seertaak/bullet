package org.bullet.primitive;

import java.util.List;

import org.bullet.BulletException;
import org.bullet.CodeLoc;
import org.bullet.Interpreter;

public class PAnd implements PrimitiveFn {
	public Object call(Interpreter interp, List<Object> args, 
			CodeLoc begin, CodeLoc end) {
		Object o = interp.eval(args.get(0));
		if (o instanceof Boolean) {
			boolean firstCond = (Boolean) o;
			if (!firstCond)
				return false;
			
			o = interp.eval(args.get(1));
			if (o instanceof Boolean) 
				return o;
			else
				throw new BulletException(
						"Illegal argument \"" + args.get(1) + "\" to <and> primitive "
						+ "evaluates to:" + o + ", which is not boolean; "
						+ "location: "+ begin);
		} else {
			throw new BulletException(
					"Illegal argument \"" + args.get(0) + "\" to <and> primitive "
					+ "evaluates to:" + o + ", which is not boolean; "
					+ "location: "+ begin);
		}
	}
}
