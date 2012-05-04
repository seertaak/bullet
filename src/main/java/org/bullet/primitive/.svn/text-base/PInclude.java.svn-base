package org.bullet.primitive;

import java.util.List;

import org.bullet.CodeLoc;
import org.bullet.Interpreter;

public class PInclude implements PrimitiveFn {

	public Object call(Interpreter interp, List<Object> body, 
			CodeLoc begin, CodeLoc end) {
		Object lastVal = null;
		// like begin, except we use the environment of the parent!
		
		for (Object form : body)
			lastVal = interp.eval(form);
				
		return lastVal;
	}

}
