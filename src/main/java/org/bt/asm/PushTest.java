package org.bt.asm;

import static org.objectweb.asm.Type.getType;
import static org.objectweb.asm.commons.Method.getMethod;

import java.io.PrintWriter;

import org.joor.Reflect;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import sun.invoke.anon.AnonymousClassLoader;

public class PushTest implements Opcodes {
	
	public static void main(String...args) throws InstantiationException, IllegalAccessException {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
		TraceClassVisitor cv = new TraceClassVisitor(cw, new PrintWriter(System.out));
		//CheckClassAdapter cv = new CheckClassAdapter(tcv);
	
		
		cv.visit(V1_7, ACC_PUBLIC + ACC_SUPER, "Foo", null, "java/lang/Object", null);
		{ 
			Method m = getMethod("void <init> ()");
			GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC, m, null, null, cv);
			mg.visitCode();
			mg.loadThis();
			mg.invokeConstructor(getType(Object.class), m);
			mg.returnValue();
			mg.endMethod();
		}
		{ 
			Method m = getMethod("void test ()");
			GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC, m, null, null, cv);
			
			mg.visitCode();
		    mg.getStatic(getType(java.lang.System.class), "out", getType(java.io.PrintStream.class));

		    int x = mg.newLocal(Type.getType(Object.class));
			mg.push(20.0);
			mg.box(Type.DOUBLE_TYPE);
			mg.storeLocal(x);
			
			Label point = mg.newLabel();
			mg.push(30.0);
			mg.box(Type.DOUBLE_TYPE);
			mg.loadLocal(x);
			
			mg.invokeStatic(getType(org.bt.runtime.Math.class), getMethod("Object add (Object, Object)"));
			
			mg.invokeVirtual(getType(Object.class), getMethod("String toString()"));
		    
		    mg.invokeVirtual(getType(java.io.PrintStream.class), getMethod("void println (String)"));
		    
			mg.returnValue();
			mg.endMethod();
		}
		{
			Method m = getMethod("void testLoop ()");
			GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC, m, null, null, cv);
			mg.visitCode();
		    Label L2 = mg.newLabel();	// note: see mg.mark(L2) below.
		    
			int i = mg.newLocal(Type.INT_TYPE);
			mg.push(0);
			mg.storeLocal(i);
			mg.goTo(L2);
			
			Label L1 = mg.mark();
		    mg.getStatic(getType(java.lang.System.class), "out", getType(java.io.PrintStream.class));
		    mg.loadLocal(i);
		    mg.invokeVirtual(getType(java.io.PrintStream.class), getMethod("void println (int)"));
		    mg.iinc(i, 1);
		    mg.mark(L2);
		    
		    mg.loadLocal(i);
		    mg.push(10);
		    mg.ifICmp(GeneratorAdapter.LT, L1);

		    mg.returnValue();
		    mg.endMethod();
		}
		{
			MethodVisitor mv;
			mv = cv.visitMethod(ACC_PUBLIC + ACC_STATIC, "foo2", "()V", null, null);
			//mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(90, l0);
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, 0);
			Label l1 = new Label();
			mv.visitLabel(l1);
			Label l2 = new Label();
			mv.visitJumpInsn(GOTO, l2);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLineNumber(91, l3);
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitVarInsn(ILOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V");
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitLineNumber(90, l4);
			mv.visitIincInsn(0, 1);
			mv.visitLabel(l2);
			mv.visitVarInsn(ILOAD, 0);
			mv.visitIntInsn(BIPUSH, 10);
			mv.visitJumpInsn(IF_ICMPLT, l3);
			Label l5 = new Label();
			mv.visitLabel(l5);
			mv.visitLineNumber(93, l5);
			mv.visitInsn(RETURN);
			mv.visitLocalVariable("i", "I", null, l1, l5, 0);
			mv.visitMaxs(2, 1);
			mv.visitEnd();
			
		}
		cv.visitEnd();
		
		Class<?> calcClass = new AnonymousClassLoader().loadClass(cw.toByteArray());
		Object o = calcClass.newInstance();
		
		Reflect.on(o).call("testLoop");
	}
	
	public static boolean foo(int x, int y) {
		boolean foo;
		if (x > y)
			foo = true;
		else
			foo = false;
		return foo;
	}

}
