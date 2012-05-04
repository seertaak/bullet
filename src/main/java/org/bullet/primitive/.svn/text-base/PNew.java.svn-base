package org.bullet.primitive;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bullet.BulletException;
import org.bullet.CodeLoc;
import org.bullet.Interpreter;

public class PNew implements PrimitiveFn {

	public Object call(Interpreter interp, List<Object> args, 
			CodeLoc begin, CodeLoc end) {
		if (args.size() < 1)
			throw new BulletException("Expected ('new' classname arg*), got: " + args);
		
		Object clVal = interp.eval(args.get(0));
//		System.out.println("clVal: " + clVal);
		Object[] argsa = new Object[args.size()-1];
		for (int i = 0; i < argsa.length; i++) {
			Object argsai = interp.eval(args.get(i+1));
			argsa[i] = argsai;
		}

//		System.out.println("Ctor args: " + Arrays.asList(argsa));
		
		Object result = null;
		boolean consFound = false;
		
		Class<?> clazz = (Class<?>) clVal;
		for (Constructor<?> cons : clazz.getDeclaredConstructors()) {
			if (cons.getParameterTypes().length == args.size()-1) {
				
				boolean argsMatch = true;
				for (int i = 0; i < cons.getParameterTypes().length; i++) {
					Class<?> cl = cons.getParameterTypes()[i];
					
					if (!cl.isAssignableFrom(argsa[i].getClass())) {
						argsMatch = false;
						break;
					}
				}
				
				if (!argsMatch)
					continue;
				
				consFound = true;
				
				try {
					result = cons.newInstance(argsa);
				} catch (IllegalArgumentException e) {
					System.out.println("Ctor args: " + Arrays.asList(argsa));
					System.out.println("Ctor: " + cons);
					throw new BulletException(e, begin);
				} catch (InstantiationException e) {
					System.out.println("Ctor args: " + Arrays.asList(argsa));
					System.out.println("Ctor: " + cons);
					throw new BulletException(e, begin);
				} catch (IllegalAccessException e) {
					System.out.println("Ctor args: " + Arrays.asList(argsa));
					System.out.println("Ctor: " + cons);
					throw new BulletException(e, begin);
				} catch (InvocationTargetException e) {
					System.out.println("Ctor args: " + Arrays.asList(argsa));
					System.out.println("Ctor: " + cons);
					throw new BulletException(e, begin);
				}
				break;
			}
		}
		
		if (!consFound) {
			List<Class<?>> argTypes = new ArrayList<Class<?>>();
			for (Object arg : argsa)
				argTypes.add(arg.getClass());
			
			throw new BulletException("Constructor does not exist for: " + Arrays.asList(argsa) + "; " 
					+ argTypes, begin);
		}
		
		return result;
	}
	
	
	public static void main(String[] args) { 
		File file = new File("foo");
		File file2 = new File("bar");
		String bar = "bar";
		
		System.out.println(file.getClass());
		System.out.println(file.getClass().isAssignableFrom(file2.getClass()));
		System.out.println(file.getClass().isAssignableFrom(bar.getClass()));
	}
}
