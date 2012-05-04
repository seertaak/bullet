package org.bullet.primitive;

import java.util.List;

import org.bullet.CodeLoc;
import org.bullet.Interpreter;

public interface PrimitiveFn {
	// primitives have access to the interpreter, can therefore 
	// evaluate the args as needed rather than funcalls where 
	// everything is already evaluated in order.
	Object call(Interpreter interp, List<Object> args, CodeLoc begin,
			CodeLoc end);
}
