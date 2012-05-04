package org.bullet.primitive;

import java.util.ArrayList;
import java.util.List;

import org.bullet.BulletException;
import org.bullet.CodeLoc;
import org.bullet.Interpreter;
import org.bullet.Pair;
import org.bullet.value.Symbol;
import org.bullet.value.BExpr;

public class PQQuote implements PrimitiveFn {
	
	private final static Symbol UNQQUOTE = Symbol.valueOf("unqquote", null);
	private final static Symbol SPLUNQQUOTE = Symbol.valueOf("splunqquote", null);
	
	public Pair<Object, Boolean> transform(Interpreter interp, Object form) {
		if (form instanceof BExpr) {
			BExpr formExpr = (BExpr) form;
			List<Object> fis = BExpr.getData(form); 
			
			Object symbol = fis.get(0);
			if (UNQQUOTE.equals(symbol) 
				|| SPLUNQQUOTE.equals(symbol)) 
			{
				List<Object> rest = fis.subList(1, fis.size());
				if (rest.size() != 1)
					throw new BulletException();
				
				Object arg = rest.get(0);
				Object result = interp.eval(arg);
				return new Pair<Object, Boolean>(result, SPLUNQQUOTE.equals(symbol));
			} else {
				List<Object> newFis = new ArrayList<Object>();
				
				for (Object fi : fis) {
					Pair<Object, Boolean> nfi = transform(interp, fi);
					if (nfi.second)
						newFis.addAll(BExpr.getData(nfi.first));
					else
						newFis.add(nfi.first);
				}
				
				BExpr result = new BExpr(newFis, formExpr.getBegin(), 
						formExpr.getEnd());
				
				return new Pair<Object, Boolean>(result, false);
			}
		} else {
			return new Pair<Object, Boolean>(form, false);
		}
	}
	
	public Object call(Interpreter interp, List<Object> args, 
			CodeLoc begin, CodeLoc end) {
		
		/* visit each node of the syntax tree, checking to see if it is
		 * either (unquote ...) or (splunqquote ...), and replacing these
		 * with the eval(...).
		 */
		
		// var names '( "Martin" "Jojo")
		// val (qquote (splunqquote ,names)
		
		if (args.size() > 1)
			throw new RuntimeException("unqquote must be called with precisely one param.: " 
					+ args);
		
		Pair<Object, Boolean> dsdata = transform(interp, args.get(0));
		return dsdata.first;
	}
}
