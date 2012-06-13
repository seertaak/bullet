package org.bt.asm;

import java.io.PrintStream;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;

import org.joor.Reflect;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import sun.invoke.anon.AnonymousClassLoader;

import static org.objectweb.asm.commons.Method.*;
import static org.objectweb.asm.Type.*;

@SuppressWarnings("restriction")
public class DynaTest implements Opcodes {
	
	private static final String CLASSNAME = "/org/bullet/asm/DynaTestGen";
	private static final Handle BOOTSTRAP = new Handle(H_INVOKESTATIC, 
		    "org/dynalang/dynalink/DefaultBootstrapper","bootstrap", MethodType.methodType(CallSite.class,
		        MethodHandles.Lookup.class, String.class, MethodType.class).toMethodDescriptorString());
	private static final Object[] BOOTSTRAP_ARGS = new Object[0];
		
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		ClassWriter cv = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
		
		cv.visit(V1_7, ACC_PUBLIC + ACC_SUPER, CLASSNAME, null, "java/lang/Object", null);
		
		{ 
			Method m = getMethod("void <init> ()");
			GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC, m, null, null, cv);
			mg.loadThis();
			mg.invokeConstructor(getType(Object.class), m);
			mg.returnValue();
			mg.endMethod();
		}
		
		Type IT = getType(Integer.class);
		
		{
			Method m = getMethod("void main (String[])");
			GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC + ACC_STATIC, m, null, null, cv);
			
			Type DT = getType(DynaTestMod.class);
			Type SBT = getType(StringBuilder.class);
			Type PST = getType(PrintStream.class);
			Type ST = getType(System.class);
			
			int dt = mg.newLocal(DT);
			mg.newInstance(DT);
			mg.dup();
			mg.invokeConstructor(DT, getMethod("void <init> ()"));
			mg.storeLocal(dt);
			
			mg.getStatic(ST, "out", PST);
			mg.loadLocal(dt);
			
			mg.invokeDynamic("dyn:getProp:fibi", getMethodDescriptor(getType(String.class), getType(Object.class)), 
					BOOTSTRAP, BOOTSTRAP_ARGS);
//			mg.invokeVirtual(DT, getMethod("String getFibi ()"));
			mg.invokeVirtual(PST, Method.getMethod("void println (String)"));
			
			// THE STAR OF THE SHOW: INVOKE_STATIC
			//mg.invokeStatic(DT, getMethod("Integer sum (Integer, Integer)"));
			
			mg.returnValue();
			mg.endMethod();
		}
		{
			Method m = getMethod("void main2 (String[])");
			GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC + ACC_STATIC, m, null, null, cv);
			
			Type DTM = getType(DynaTestMod.class);
			
			mg.newInstance(DTM);
			mg.dup();
			mg.dup();
			mg.invokeConstructor(DTM, getMethod("void <init> ()"));
			// foo called via virtual call
			mg.invokeVirtual(DTM, getMethod("void foo ()"));
			// foo called via invoke dynamic call
			mg.invokeDynamic("dyn:callPropWithThis:foo", getMethodDescriptor(getType(void.class), getType(Object.class)), 
					BOOTSTRAP, BOOTSTRAP_ARGS);
			
			mg.returnValue();
			mg.endMethod();
		}
		
		cv.visitEnd();
		
		Type DT = getType(DynaTestMod.class);
		System.out.println(getMethodDescriptor(getType(String.class), DT));
		AnonymousClassLoader acl = new AnonymousClassLoader();
		Class<?> type = acl.loadClass(cv.toByteArray());
		Reflect.on(type).call("main", new Object[] { new String[] {} } );
		Reflect.on(type).call("main2", new Object[] { new String[] {} } );
		
//		System.out.println(getMethodDescriptor(IT, IT, IT));
	}

}
