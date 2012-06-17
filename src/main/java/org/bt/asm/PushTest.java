package org.bt.asm;

import static org.objectweb.asm.Type.getType;
import static org.objectweb.asm.commons.Method.getMethod;

import org.joor.Reflect;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import sun.invoke.anon.AnonymousClassLoader;

public class PushTest implements Opcodes {
	
	public static void main(String...args) throws InstantiationException, IllegalAccessException {
		ClassWriter cv = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
		cv.visit(V1_7, ACC_PUBLIC + ACC_SUPER, "Foo", null, "java/lang/Object", null);
		{ 
			Method m = getMethod("void <init> ()");
			GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC, m, null, null, cv);
			mg.loadThis();
			mg.invokeConstructor(getType(Object.class), m);
			mg.returnValue();
			mg.endMethod();
		}
		{ 
			Method m = getMethod("void test ()");
			GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC, m, null, null, cv);
			
			
		    mg.getStatic(getType(java.lang.System.class), "out", getType(java.io.PrintStream.class));

		    int x = mg.newLocal(Type.getType(Object.class));
			mg.push(20.0);
			mg.box(Type.DOUBLE_TYPE);
			mg.storeLocal(x);
			
			
			mg.push(30.0);
			mg.box(Type.DOUBLE_TYPE);
			mg.loadLocal(x);
			
			mg.invokeStatic(getType(org.bt.runtime.Math.class), getMethod("Object add (Object, Object)"));
			
			mg.invokeVirtual(getType(Object.class), getMethod("String toString()"));
		    
		    mg.invokeVirtual(getType(java.io.PrintStream.class), getMethod("void println (String)"));
		    
			mg.returnValue();
			mg.endMethod();
		}
		
		Class<?> calcClass = new AnonymousClassLoader().loadClass(cv.toByteArray());
		Object o = calcClass.newInstance();
		
		Reflect.on(o).call("test");
	}
	
	public static void foo() {
		int x = 5;
		int y = 10;
		System.out.println(x+y);
	}

}
