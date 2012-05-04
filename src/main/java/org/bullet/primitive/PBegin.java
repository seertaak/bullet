package org.bullet.primitive;

import java.util.List;

import org.bullet.CodeLoc;
import org.bullet.Interpreter;

public class PBegin implements PrimitiveFn {

	public Object call(Interpreter interp, List<Object> body, 
			CodeLoc begin, CodeLoc end) {
		
		Object lastVal = null;
		
		interp.pushEnv();
		
		for (Object form : body)
			lastVal = interp.eval(form);
				
		interp.popEnv();
				
		return lastVal;
	}

}
