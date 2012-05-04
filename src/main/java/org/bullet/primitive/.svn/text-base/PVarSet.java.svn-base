package org.bullet.primitive;

import java.io.FileNotFoundException;
import java.util.List;

import org.bullet.Bullet;
import org.bullet.CodeLoc;
import org.bullet.Interpreter;
import org.bullet.BulletException;
import org.bullet.Pair;
import org.bullet.Variable;
import org.bullet.value.BExpr;
import org.bullet.value.Environment;
import org.bullet.value.Objekt;
import org.bullet.value.Symbol;

public class PVarSet implements PrimitiveFn {

	private Pair<Object, Symbol> parseDeref(Interpreter interp, Object form, CodeLoc begin) {
		if (form instanceof BExpr) {
			BExpr bexp = (BExpr) form;
			if (bexp.size() != 2) {
				return null;
			} else {
				Object obj = interp.eval(bexp.get(0));
				if (!(obj instanceof Objekt || obj instanceof Environment))
						throw new BulletException("Expected Objekt or Environment obj, found: " 
								+ obj + ": " + obj.getClass().getSimpleName(), 
								begin); //bexp.getBegin());
				Object fld = bexp.get(1);
				//System.out.println("FLD: " + fld + ": " + fld.getClass());
				if (fld instanceof BExpr) {
					Object r = interp.eval(fld);
					//System.out.println("R: " + r + ": " + fld.getClass());
					fld = r;
				}
				if (!(fld instanceof Symbol))
					throw new BulletException("Expected Symbol as field, found: " 
							+ fld + ": " + fld.getClass().getSimpleName(), bexp.getBegin());
				Pair<Object, Symbol> result = new Pair<>(obj, 
						(Symbol) fld);
				return result;
			}
		} else if (form instanceof Symbol) {
			return new Pair<>(null, (Symbol) form);
		} else {
			return null;
		}
	}

	public Object call(Interpreter interp, List<Object> args, 
			CodeLoc begin, CodeLoc end) {
		if (args.size() != 2) {
			throw new BulletException("Expected: (set varname value), got: " 
					+ args, begin);
		}
		
		//System.out.println("VarSet: " + args);
		
		// support:
		// var obj: class | new
		// set obj.name "Martin"
		
		// set a.b.c.d.f 5
		// ((((a b) c) d) f)
		
		Pair<Object, Symbol> obj_fld = parseDeref(interp, args.get(0), begin);
		if (obj_fld == null) {
			throw new BulletException("Unable to parse object/field names: " 
					+ args, begin);
		}
		
		Object obj = obj_fld.first;
		Symbol fld = obj_fld.second;
		
		Object form = args.get(1);
		Object value = interp.eval(form);
		
		if (obj != null) {
			if (obj instanceof Environment) {
				Environment env = (Environment) obj;
				if (!env.isBound(fld)) {
					env.bind(fld, value);
				} else {
					Variable var = env.getVar(fld);
					var.set(value);
				}
			} else if (obj instanceof Objekt) {
				Objekt bobj = (Objekt) obj;
				Environment objEnv = bobj.ethis();
				if (objEnv.isBound(fld)) {
					Variable var = objEnv.getVar(fld);
					var.set(value);
				} else {
					objEnv.bind(fld, value);
				}
			} else {
				throw new BulletException("First argument is not an Environment or BObject, but a "
						+ obj.getClass().getSimpleName(), begin);
			}
		} else {
			if (interp.isBound(fld)) {
				Variable var = interp.getVar(fld);
				var.set(value);
			} else {
				interp.env().bind(fld, value);
			}
		}
		
		return value;
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		Bullet.main(args);
	}

}
