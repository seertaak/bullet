package org.bullet.primitive;

import java.util.List;

import org.bullet.BulletException;
import org.bullet.CodeLoc;
import org.bullet.Interpreter;
import org.bullet.Pair;
import org.bullet.value.Symbol;
import org.bullet.value.BExpr;

public class PImport implements PrimitiveFn {
	
	private Pair<CodeLoc, String> parseClassName(Object form) {
		StringBuilder str = new StringBuilder();
		CodeLoc loc = null;
		if (form instanceof BExpr) {
			List<Object> subs = BExpr.getData(form);
			boolean first = true;
			for (Object sub : subs) {
				if (first)
					first = false;
				else
					str.append(".");

				str.append(parseClassName(sub).second);
			}
			loc = ((BExpr) form).getBegin();
		} else if (form instanceof Symbol) {
			str.append(Symbol.getData(form));
			loc = ((Symbol) form).getLoc();
		}
		return new Pair<CodeLoc, String>(loc, str.toString());
	}

	public Object call(Interpreter interp, List<Object> args, 
			CodeLoc begin, CodeLoc end) {
		Pair<CodeLoc, String> codeLoc_className = parseClassName(args.get(0));
		CodeLoc loc = codeLoc_className.first;
		String className = codeLoc_className.second;
		String[] simpleNames = className.split("[.]");
		
		//System.out.println("Import class: " + Arrays.asList(simpleNames));
		
		try {
			@SuppressWarnings("static-access")
			Object result = getClass().forName(className);
			String name = simpleNames[simpleNames.length-1];
			Symbol symb = Symbol.valueOf(name, loc);
//			System.out.println("Binding " + result + " to " + name);
			interp.env().bind(symb, result);
			//System.out.println("New env: " + env);
			return null;
		} catch (ClassNotFoundException e) {
			throw new BulletException(e, loc);
		}
	}
}
