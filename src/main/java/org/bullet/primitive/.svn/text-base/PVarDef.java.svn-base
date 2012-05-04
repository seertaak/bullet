package org.bullet.primitive;

import java.util.List;

import org.bullet.CodeLoc;
import org.bullet.Interpreter;
import org.bullet.BulletException;
import org.bullet.value.Symbol;

public class PVarDef implements PrimitiveFn {

	public Object call(Interpreter interp, List<Object> args, 
			CodeLoc begin, CodeLoc end) {
		int N = args.size();
		if (N != 2 && N != 1) {
			throw new BulletException("Expcted: (var varname [initialvalue]), got: " 
					+ args);
		}
		
		Symbol symbol = (Symbol) args.get(0);
		
		/*
		if (interp.env().isBound(symbol)) 
			throw new BulletException("Symbol " 
					+ symbol 
					+ " is already bound to " 
					+ interp.env().lookup(symbol),
					symbol.getLoc());
		*/
		
		Object value;
		if (N == 2) {
			Object form = args.get(1);
			value = interp.eval(form);
		} else {
			value = null;
		}
		interp.env().bind(symbol, value);
		
		return value;
	}

}
