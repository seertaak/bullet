package org.bullet.primitive;

import java.util.List;

import org.bullet.CodeLoc;
import org.bullet.Interpreter;
import org.bullet.BulletException;
import org.bullet.value.Symbol;

public class PMVar implements PrimitiveFn {

	public Object call(Interpreter interp, List<Object> args, 
			CodeLoc begin, CodeLoc end) {
		int N = args.size();
		if (N != 2 && N != 1) {
			throw new BulletException("Expcted: (mvar varname [initialvalue]), got: " 
					+ args);
		}
		
		// TODO: get the class!
		Symbol symbol = (Symbol) args.get(0);
		
		Object value;
		if (N == 2) {
			Object form = args.get(1);
			value = interp.eval(form);
		} else {
			value = null;
		}
		if (interp.menv() == null) {
			
		}
		interp.menv().bind(symbol, value);
		
		return value;
	}

}
