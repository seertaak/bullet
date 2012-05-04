package org.bullet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bullet.primitive.PrimitiveFn;
import org.bullet.value.Environment;
import org.bullet.value.Lambda;
import org.bullet.value.Method;
import org.bullet.value.Macro;
import org.bullet.value.Objekt;
import org.bullet.value.Primitive;
import org.bullet.value.Symbol;
import org.bullet.value.BExpr;

public class Interpreter {
	
	//private static boolean VERBOSE = true;
	private static final Symbol THIS_SYMB = Symbol.valueOf("this", null);
	private static final Symbol INTERPRETER = Symbol.valueOf("interpreter", null);
	private static final File BULLET_BT = new File(
				System.getProperty("user.dir"), "bullet.bt");
//	private static final File BULLET_BT = new File(
//				"/home/mpercossi/src/bullet/src/main/bullet/bullet.bt");
	private LinkedList<Environment> env;
	private LinkedList<Environment> menv;	
	
	private void bindPrimitives() {
		pushEnv();
		
		bind(INTERPRETER, this);
		
		for (Map.Entry<String, PrimitiveFn> prim : Primitive.all().entrySet()) {
			bind(Symbol.valueOf(prim.getKey(), null), prim.getValue());
		}
	}
	
	public Interpreter() {
		env = new LinkedList<>();
		menv = new LinkedList<>();
		
		bindPrimitives();
		
		Reader parser = new Reader();
		if (BULLET_BT.exists()) {
			Object syn = parser.parse(BULLET_BT);
			if (syn instanceof BExpr 
					&& Symbol.valueOf("begin", null).equals(((BExpr) syn).get(0)))
				((BExpr) syn).set(0, Symbol.valueOf("include", null));

			eval(syn);
		} else {
			System.out.println("Unable to find bullet.bt");
		}
	}
	
	public void bind(Symbol ident, Object value) {
		env().bind(ident, value);
	}
	
	public void bind(Symbol ident, Variable var) {
		env().bind(ident, var);
	}
	
	public boolean isBound(Symbol ident) {
		for (Environment e : env)
			if (e.isBound(ident))
				return true;
		return false;
	}
	
	public boolean isPrimitive(Symbol ident) {
		for (Environment e : env)
			if (e.isBound(ident) && e.isPrimitive(ident))
				return true;
		return false;
	}
	
	public Object lookup(Symbol symb) {
		return getVar(symb).get();
	}
	
	public Variable getVar(Symbol symb) {
		for (Environment e : env)
			if (e.isBound(symb))
				return e.getVar(symb);
		
		throw new BulletException("No variable exists for symbol: " 
				+ symb, symb.getLoc());
	}
	
	public final Environment env() {
		return env.peek();
	}
	public final Environment menv() {
		return menv.peek();
	}
	public final LinkedList<Environment> getEnvStack() {
		return env;
	}
	
	public Object read(String form) {
		throw new BulletException("Not implemented yet.");
	}
	
	public Object load(File srcFile) {
		return load(srcFile, (String[]) null);
	}
	
	public Object load(File srcFile, List<String> cmdLnArgs) {
		return load(srcFile, cmdLnArgs.toArray(new String[] {}));
	}
	
	public Object load(File srcFile, String[] cmdLnArgs) {
		Reader parser = new Reader();
		Object forms = parser.parse(srcFile);
		if (cmdLnArgs != null && cmdLnArgs.length > 0) {
			Environment main = pushEnv();
			bind(Symbol.valueOf("args", null), cmdLnArgs);
			popEnv();
			bind(Symbol.valueOf("main", null), main);
		}
		Object result = eval(forms);
		return result;
	}
	
	public Object eval(Object form) {
		if (form == null)
			return null;
		if (form instanceof BExpr)
			return evalForm((BExpr) form);
		else if (form instanceof Symbol)
			return lookup((Symbol) form);
		else
			return form;
	}
	
	public Object eval(List<Object> forms, Environment env) {
		pushEnv(env);
		int N = forms.size();
		for (int i = 0; i < N; ++i) {
			Object result = eval(forms.get(i));
			if (i == N-1) {
				popEnv();
				return result;
			}
		}
		popEnv();
		return null;
	}
	
	private Object evalPrimitive(PrimitiveFn prim, List<Object> args,
			CodeLoc begin, CodeLoc end) {
		return prim.call(this, args, begin, end);
	}
	
	private Object evalLambda(Lambda lambda, List<Object> args,
			CodeLoc begin, CodeLoc end) {
		// evaluate the args in order
		if (lambda.freeVars() == null)
			throw new BulletException("Lambda with null free vars.", begin);
		
//		System.out.println("evalLambda:lamdba.freeVars():" + lambda.freeVars());
		
		pushEnv(lambda.freeVars());
		pushEnv();
		
		// code below handles rest arguments.
		
		int N = lambda.formalParams().size();
		
		for (int i = 0; i < N-1; i++) {
			Symbol formParm = lambda.formalParams().get(i);
			Object actParm = eval(args.get(i));
			bind(formParm, actParm);
		}
		
		if (lambda.lastParmRest()) {
			Symbol formParm = lambda.formalParams().get(N-1);
			List<Object> rest = new ArrayList<Object>();
			
			for (int i = N-1; i < args.size(); i++) 
				rest.add(eval(args.get(i)));
			
			BExpr restExp = new BExpr(rest, begin, end);
			bind(formParm, restExp);
		} else {
			if (N > 0) {
				Symbol formParm = lambda.formalParams().get(N-1);
				if (args.size() <= N-1)
					throw new BulletException("Missing args: " 
							+ args + " vs " + lambda.formalParams(),
							formParm.getLoc());
				Object actParm = eval(args.get(N-1));
				bind(formParm, actParm);
			}
		}
		
		Object lastValue = null;
		for (Object form : lambda.body()) {
//			if (VERBOSE)
//				System.out.println("lambda eval: " + form);
			lastValue = eval(form);
//			if (VERBOSE)
//				System.out.println("lambda result: " + lastValue);
		}
		
		popEnv();
		popEnv();

		return lastValue;
	}
	
	private Object evalJavaMethod(Object obj, List<Object> args,
			CodeLoc begin, CodeLoc end) {
		// ("Martin" equals "Alex")
		// 
		
		if (args == null)
			throw new BulletException("Attempting to evaluate a java method "
					+ "with null args: bad state.", begin);
		
		if (args.isEmpty())
			return obj;
		
//		System.out.println("obj: " + obj);
//		System.out.println("rest: " + rest);
		
		String methName = Symbol.getData(args.get(0));
		if (methName == null) {
			throw new BulletException("Undefined java method " + args.get(0)
					+ " on object " + obj + " with class " 
					+ obj.getClass().getSimpleName()
					+ ", actual args " + args.subList(1, args.size()) 
					+ " at location: " + begin);
		}
		//System.out.println(methName);
		int numArgs = args.size() - 1;

		if (obj == null)
			throw new BulletException("Attempting to evaluate a java method "
					+ "with null obj: bad state.", begin);
		
		Class<?> clazz = obj.getClass();
		
		//System.out.println(clazz);
		
		if (numArgs == 0) {
			// it could be a field access.
			
			for (Field field : clazz.getFields()) {
				
//				System.out.println(clazz.getSimpleName() + "#" + field.getName());
				
				if (!field.isAccessible())
					field.setAccessible(true);
				
				if (!field.getName().equals(methName))
					continue;
				
				try {
					Object result = field.get(obj);
					return result;
				} catch (IllegalArgumentException e) {
					System.out.println("Field obj: " + obj);
					System.out.println("Field name: " + methName);
					System.out.println("Field: " + field);
					throw new BulletException(e, begin);
				} catch (IllegalAccessException e) {
					System.out.println("Field obj: " + obj);
					System.out.println("Field name: " + methName);
					System.out.println("Field: " + field);
					throw new BulletException(e, begin);
				}
			}
		}
		
		Pair<Object, Boolean> result = invokeMethod(obj, clazz, methName, args);
		if (!result.second && obj instanceof Class) {
			result = invokeMethod(obj, (Class<?>) obj, methName, args);
		}
		if (!result.second) {
			throw new BulletException("Undefined java method " + methName 
					+ " on obj:" + obj + " with class:" + obj.getClass() 
					+ ", actual args: " + args + " at location: " + begin);
		}
		
		return result.first;
	}
	
	public Object evalForm(Object form) {
		BExpr expr = (BExpr) form;
		List<Object> leaves = expr.data();
		
		if (expr.isEmpty()) {
			throw new BulletException("Cannot evaluate ()", expr.getBegin());
		}

		//		System.out.println(leaves.get(0) + ", " + leaves.get(0).getClass());
		Object first = eval(leaves.get(0));
		List<Object> rest = leaves.subList(1, leaves.size());

		if (first instanceof PrimitiveFn) {
			Object result = evalPrimitive((PrimitiveFn)first, rest,
					expr.getBegin(), expr.getEnd());
			return result;
		} else if (first instanceof Lambda) {
			Object result = evalLambda((Lambda)first, rest,
					expr.getBegin(), expr.getEnd());
			return result;
		} else if (first instanceof Macro) {
			Object result = evalMacro((Macro)first, rest,
					expr.getBegin(), expr.getEnd());
			return result;
		} else if (first instanceof Environment) {
			Environment env = (Environment) first;
			if (rest.size() != 1)
				throw new BulletException("Expected only one argument to environment lookup.", 
						expr.getBegin());
			
			Object result = env.lookup((Symbol) rest.get(0));
			return result;
		} else if (first instanceof Object[]) {
			if (rest.size() != 1)
				throw new BulletException("Expected only one argument to environment lookup.", 
						expr.getBegin());
			Object[] arr = (Object[]) first;
			Object ix = eval(rest.get(0));
			int i = (Integer) ix;
			if (i >= arr.length)
				throw new BulletException("Out of range index: " + i,
						expr.getBegin());
			Object result = arr[i];
			return result;
		} else if (first instanceof Objekt) {
			Objekt bobj = (Objekt) first;
			Object result = evalBObjLookup(bobj, rest, expr.getBegin(), expr.getEnd());
			return result;
		} else {
			Object result = evalJavaMethod(first, rest,
					expr.getBegin(), expr.getEnd());
			return result;
		}
	}
	
	private Object evalBObjLookup(Objekt bobj, List<Object> rest, CodeLoc begin,
			CodeLoc end) 
	{
		Object first = rest.get(0);
		if (!(first instanceof Symbol)) 
			throw new BulletException("Second argument in a objekt lookup must be a symbol.", begin);
		
		Environment ethis = bobj.ethis();
		Environment esuper = bobj.esuper();
		Symbol prop = (Symbol) first;
		
		if (ethis.isBound(prop)) {
			Object propVal = ethis.lookup(prop);
			if (first instanceof Lambda) {
				Lambda mfn = (Lambda) first;
				List<Object> args = rest.size() > 1 ? rest.subList(1, rest.size()) : Collections.emptyList();

				// evaluate the args in order
				if (mfn.freeVars() == null)
					throw new BulletException("Lambda with null free vars.", begin);

				//		System.out.println("evalLambda:lamdba.freeVars():" + lambda.freeVars());

				pushEnv(mfn.freeVars());
				pushEnv(esuper);
				pushEnv(ethis);
				pushEnv();
				bind(THIS_SYMB, bobj);

				// code below handles rest arguments.

				int N = mfn.formalParams().size();

				for (int i = 0; i < N-1; i++) {
					Symbol formParm = mfn.formalParams().get(i);
					Object actParm = eval(args.get(i));
					bind(formParm, actParm);
				}

				if (mfn.lastParmRest()) {
					Symbol formParm = mfn.formalParams().get(N-1);
					rest = new ArrayList<Object>();

					for (int i = N-1; i < args.size(); i++) 
						rest.add(eval(args.get(i)));

					BExpr restExp = new BExpr(rest, begin, end);
					bind(formParm, restExp);
				} else {
					if (N > 0) {
						Symbol formParm = mfn.formalParams().get(N-1);
						if (args.size() <= N-1)
							throw new BulletException("Missing args: " 
									+ args + " vs " + mfn.formalParams(),
									formParm.getLoc());
						Object actParm = eval(args.get(N-1));
						bind(formParm, actParm);
					}
				}

				Object lastValue = null;
				for (Object form : mfn.body()) {
					//			if (VERBOSE)
					//				System.out.println("lambda eval: " + form);
					lastValue = eval(form);
					//			if (VERBOSE)
					//				System.out.println("lambda result: " + lastValue);
				}

				popEnv();
				popEnv();

				return lastValue; 
			} else {
				return propVal;
			}
		} else if (esuper.isBound(prop)) {
			try {
				return evalJavaMethod(bobj.proxy(), rest, begin, end);
			} catch (IOException e) {
				throw new BulletException(e, begin);
			}
		} else {
			throw new BulletException("Undefined property " + prop + " on " + bobj, begin);
		}
		
	}

	private Object macroMkUniqNames(Object form, 
			Map<Symbol, Symbol> translations,
			CodeLoc begin, CodeLoc end) {
//		System.out.println("macroMkUniqNames: " + form);
		if (form instanceof BExpr) {
			// ok, first do the same check for sub-datums.
			List<Object> dts = new ArrayList<Object>(BExpr.getData(form));
			
			if ("var".equals(Symbol.getData(dts.get(0)))) {
				Object varNameFm = dts.get(1);
				if (varNameFm instanceof BExpr) {
					List<Object> vndts = BExpr.getData(varNameFm);
					if ("uniq".equals(Symbol.getData(vndts.get(0)))) {
						Symbol name = (Symbol) vndts.get(1);
						Symbol newName = name;
						int version = 0;
						while (isBound(newName)) {
							newName = Symbol.valueOf(name.getData() + version,
									((BExpr) form).getBegin());
							version++;
						}
						translations.put(name, newName);
						//System.out.println("Found one! " + object
//								+ ": " + vndts + ": " + name);
						dts.set(1, newName);
					}
				}
				dts.set(2, macroMkUniqNames(dts.get(2), translations, begin, end));
				return new BExpr(dts, begin, end);
			} else {
				if ("uniq".equals(Symbol.getData(dts.get(0)))) {
					Symbol name = (Symbol) dts.get(1);
					//System.out.println("Found one! " + object
							//+ ": " + dts + ": " + name);
					Symbol uniqName = translations.get(name);
					return uniqName;
				} else {
					List<Object> udts = new ArrayList<Object>();
					for (Object dt : dts) {
						Object udt = macroMkUniqNames(dt, translations, begin, end);
						udts.add(udt);
					}
					return new BExpr(udts, begin, end);
				}
			}
		} else {
			return form;
		}
	}
	
	private Object evalMacro(Macro macro, List<Object> args,
			CodeLoc begin, CodeLoc end) {
		
//		System.out.println("FREE VARS: " + macro.formalParams()  +":"+ macro.freeVars());
		
		// can't do a simple pushEnv, because otherwise macro doesn't
		// have access to the scope!
		// pushEnv(macro.freeVars());
		// would result in any set calls inside the macro being evaluated
		// in that inner stack, and when we pop at the end, it's gone.
		// so we need to do the stack ourselves unfortunately.
		Environment oldBinds = new Environment();
		
		for (Symbol symb : macro.freeVars().names()) {
			if (isBound(symb))
				oldBinds.bind(symb, getVar(symb));
			
			bind(symb, macro.freeVars().lookup(symb));
		}
		
		

		pushEnv();
		
		int N = macro.formalParams().size();
		
//		if (VERBOSE) {
//			System.out.println("Eval macro args: " + args);
//			System.out.println("Eval macro args size: " + N);
//			System.out.println("Eval macro formPArms: " + macro.formalParams());
//		}
		
		for (int i = 0; i < N-1; i++) {
			Symbol formParm = macro.formalParams().get(i);
			Object actParm = args.get(i);
			bind(formParm, actParm);
		}
		
		if (macro.lastParmRest()) {
			Symbol formParm = macro.formalParams().get(N-1);
			List<Object> rest = new ArrayList<Object>();
			
			for (int i = N-1; i < args.size(); i++) 
				rest.add(args.get(i));

			Object restArg = new BExpr(rest, begin, end);
//			System.out.println("Rest arg: " + rest);
			bind(formParm, restArg);
		} else {
			if (N > 0) {
				Symbol formParm = macro.formalParams().get(N-1);
				Object actParm = args.get(N-1);
				bind(formParm, actParm);
			}
		}
		
		Map<Symbol, Symbol> uniqNames = new HashMap<Symbol, Symbol>();
		Object lastValue = null;
		for (Object form : macro.body()) {
//			if (VERBOSE)
//				System.out.println("Macro: " + form);
//			System.out.println("Env: " + env());
			Object trans = macroMkUniqNames(form, uniqNames, begin, end);
//			if (VERBOSE)
//				System.out.println("Trans: " + trans);
			
			lastValue = eval(trans);
		}
		
		popEnv();
		
//		System.out.println(env.names());
		
//		System.out.println("Macro trans: " + lastValue.getClass() + ", " + lastValue);
		Object result = eval(lastValue);
//		System.out.println("Result of macro: " + result.getClass() + ", " + result);
		
		for (Symbol symb : macro.freeVars().names()) {
			if (oldBinds.isBound(symb))
				bind(symb, oldBinds.getVar(symb));
			else
				env().unbind(symb);
		}
		
		// had we used the pushEnv approach refuted above, we would have had
		// to end it with:
		//popEnv();
		
		return result;
	}
	
	public Environment pushEnv() {
		Environment result = new Environment();
		env.push(result);
		return result;
	}
	public Environment pushMEnv() {
		Environment result = new Environment();
		menv.push(result);
		return result;
	}
	private Environment pushEnv(Environment env) {
		this.env.push(env);
		return env;
	}
	public Environment popEnv() {
		return env.pop();
	}
	public Environment popMEnv() {
		return menv.pop();
	}
	
	
	public Pair<Object,Boolean> invokeMethod(Object obj, Class<?> clazz, String methName, 
			List<Object> rest) {
		int numArgs = rest.size()-1;
		Class<?>[] argCls = null;
		Object[] args = null;
		for (java.lang.reflect.Method method : clazz.getMethods()) {
			
//			if (VERBOSE)
//				System.out.println(clazz.getSimpleName() + "#" + method.getName());
			
			if (!method.isAccessible())
				method.setAccessible(true);
			
			if (!method.getName().equals(methName))
				continue;
			
			Class<?>[] pts = method.getParameterTypes();
			
//			System.out.println(Arrays.toString(pts));
			
			if (pts.length > numArgs)
				continue;
			else if (pts.length < numArgs) {
				if (method.isVarArgs()) {
					args = new Object[pts.length];
					argCls = new Class<?>[pts.length];
					
					for (int i = 0; i < pts.length-1; ++i) {
						args[i] = eval(rest.get(i+1));
						argCls[i] = args[i].getClass();
					}
					
					Object[] restArgs = new Object[numArgs-pts.length+1];
					for (int i = 0; i < restArgs.length; ++i) 
						restArgs[i] = eval(rest.get(i + pts.length));
					
					args[pts.length-1] = restArgs;
					argCls[pts.length -1] = Array.class;
				} else {
					continue;
				}
			} else {
				argCls = new Class<?>[numArgs];
				args = new Object[numArgs];
				
				boolean argsMatch = true;
				for (int i = 0; i < numArgs; i++) {
					args[i] = eval(rest.get(i+1));
					if (args[i] == null) {
						argsMatch = true;
						break;
					}
						
					argCls[i] = args[i].getClass();
					
					
					Class<?> cl = method.getParameterTypes()[i];

					if (cl.isPrimitive()) {
						if (cl == int.class && args[i].getClass() == Integer.class)
							continue;
						else if (cl == long.class && args[i].getClass() == Long.class)
							continue;
						else if (cl == byte.class && args[i].getClass() == Byte.class)
							continue;
						else if (cl == short.class && args[i].getClass() == Short.class)
							continue;
						else if (cl == double.class && args[i].getClass() == Double.class)
							continue;
						else if (cl == float.class && args[i].getClass() == Float.class)
							continue;
						else if (cl == char.class && args[i].getClass() == Character.class)
							continue;
						else if (cl == boolean.class && args[i].getClass() == Boolean.class)
							continue;
					}

					//				if (VERBOSE) 
					//					System.out.print("Potential match: " + method + ": " + i + ": " + cl + "...");

					if (!cl.isAssignableFrom(args[i].getClass())) {
						argsMatch = false;
						//					if (VERBOSE)
						//						System.out.println("rejected.");
						break;
					}

					//				if (VERBOSE) 
					//					System.out.println("accepted.");
				}

				if (!argsMatch)
					continue;
			}

			try {
				Object o = method.invoke(obj, args);
				return new Pair<Object, Boolean>(o, true);
			} catch (IllegalArgumentException e) {
				System.out.println("Method obj: " + obj);
				System.out.println("Method name: " + methName);
				System.out.println("Method args: " + Arrays.asList(args));
				System.out.println("Method: " + method);
				throw new BulletException(e);
			} catch (IllegalAccessException e) {
				System.out.println("Method obj: " + obj);
				System.out.println("Method name: " + methName);
				System.out.println("Method args: " + Arrays.asList(args));
				System.out.println("Method: " + method);
				throw new BulletException(e);
			} catch (InvocationTargetException e) {
				System.out.println("Method obj: " + obj);
				System.out.println("Method name: " + methName);
				System.out.println("Method args: " + Arrays.asList(args));
				System.out.println("Method: " + method);
				throw new BulletException(e);
			}
		}
		
		return new Pair<Object, Boolean>(null, false);
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		Bullet.main(args);
	}
}
