package org.bullet.primitive;

import java.util.List;

import org.bullet.BulletException;
import org.bullet.CodeLoc;
import org.bullet.Interpreter;
import org.bullet.value.BExpr;

public class PCond implements PrimitiveFn {

	public Object call(Interpreter interp, List<Object> args, 
			CodeLoc begin, CodeLoc end) {
		
		// arguments are a series of forms:
		// (<condition> <form>)
		// (<condition> <form>)
		
		for (Object arg : args) {
			if (!(arg instanceof BExpr))
				throw new BulletException("<cond> expects arguments of form: " +
						"(<condition> <consequence>), but found: " + arg + "; "
						+ "location: " + begin);
			
			BExpr vclause = (BExpr) arg;
			List<Object> clause = BExpr.getData(vclause);
			
			if (clause.size() != 2)
				throw new BulletException("<cond> expects arguments of form: " +
						"(<condition> <consequence>), but found: " + arg + "; "
						+ "location: " + begin);
			
			Object antecedent = clause.get(0);
			Object consequence = clause.get(1);
			
			if ((Boolean) interp.eval(antecedent)) {
				return interp.eval(consequence);
			}
		}
		
		return null;
	}

}
