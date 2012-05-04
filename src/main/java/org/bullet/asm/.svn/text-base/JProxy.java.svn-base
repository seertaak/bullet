package org.bullet.asm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;

import org.bullet.BulletException;
import org.bullet.CodeLoc;
import org.bullet.Interpreter;
import org.bullet.value.Environment;
import org.bullet.value.Lambda;
import org.bullet.value.Objekt;
import org.bullet.value.Symbol;
import org.joor.Reflect;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.google.common.base.Joiner;

import sun.invoke.anon.AnonymousClassLoader;

public class JProxy implements Opcodes {

	private static ThreadLocal<AnonymousClassLoader> cll = new ThreadLocal<AnonymousClassLoader>() {
		@Override
		protected AnonymousClassLoader initialValue() {
			return new AnonymousClassLoader();
		}
	};

	private Objekt objekt;
	private Interpreter interp;

	public JProxy(Interpreter interp, Objekt objekt) {
		this.objekt = objekt;
		this.interp = interp;
	}

	public Class<?> genClass() throws IOException {
		if (objekt.superClass() == null && 
				(objekt.interfaces() == null || objekt.interfaces().isEmpty()))
		{
			throw new BulletException("Either objekt.superClass or objekt.interfaces must be present.", 
					objekt.srcBegin());
		}

		final ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;
		AnnotationVisitor av0;

		List<Class<?>> parentClasses = new ArrayList<>();
		StringBuilder classNameStr = new StringBuilder();
		classNameStr.append("org/bullet/gen/JProxy");
		if (objekt.superClass() != null) {
			classNameStr.append("_xtnd_");
			classNameStr.append(objekt.superClass().getSimpleName());
			parentClasses.add(objekt.superClass());
		}
		if (objekt.interfaces() != null) {
			classNameStr.append("_impl");
			boolean first = true;
			for (Class<?> iface : objekt.interfaces()) {
				if (!first)
					first = false;
				else
					classNameStr.append("_");
				classNameStr.append(iface.getSimpleName());
				parentClasses.add(iface);
			}
		}

		String className = classNameStr.toString();
		String intClassName = clDescName(className);
		String superClass = clBinaryName(objekt.superClass());

		cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, className, null, superClass, null);

		final CodeLoc srcBegin = objekt.srcBegin();
		if (srcBegin != null)
			cw.visitSource(srcBegin.getSrcFile().getPath(), null);
		{
			// interp
			fv = cw.visitField(ACC_PRIVATE, "interp", "Lorg/bullet/Interpreter;", null, null);
			fv.visitEnd();
			// objekt field
			fv = cw.visitField(ACC_PRIVATE, "objekt", "Lorg/bullet/value/Objekt;", null, null);
			fv.visitEnd();
		}

		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Lorg/bullet/Interpreter;Lorg/bullet/value/Objekt;)V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			if (srcBegin != null)
				mv.visitLineNumber(11, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, superClass, "<init>", "()V");
			Label l1 = new Label();
			mv.visitLabel(l1);
			if (srcBegin != null)
				mv.visitLineNumber(srcBegin.getLineNum(), l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(PUTFIELD, className, "interp", "Lorg/bullet/Interpreter;");
			Label l2 = new Label();
			mv.visitLabel(l2);
			if (srcBegin != null)
				mv.visitLineNumber(srcBegin.getLineNum(), l2);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitFieldInsn(PUTFIELD, className, "objekt", "Lorg/bullet/value/Objekt;");
			Label l3 = new Label();
			mv.visitLabel(l3);
			if (srcBegin != null)
				mv.visitLineNumber(srcBegin.getLineNum(), l3);
			mv.visitInsn(RETURN);
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitLocalVariable("this", intClassName, null, l0, l4, 0);
			mv.visitLocalVariable("interp", "Lorg/bullet/Interpreter;", null, l0, l4, 1);
			mv.visitLocalVariable("objekt", "Lorg/bullet/value/Objekt;", null, l0, l4, 2);
			mv.visitMaxs(2, 3);
			mv.visitEnd();
		}

		{
			// toString
			mv = cw.visitMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			if (srcBegin != null)
				mv.visitLineNumber(srcBegin.getLineNum(), l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETSTATIC, "org/apache/commons/lang3/builder/ToStringStyle", 
					"SHORT_PREFIX_STYLE", "Lorg/apache/commons/lang3/builder/ToStringStyle;");
			mv.visitMethodInsn(INVOKESTATIC, "org/apache/commons/lang3/builder/ToStringBuilder", 
					"reflectionToString", 
					"(Ljava/lang/Object;Lorg/apache/commons/lang3/builder/ToStringStyle;)Ljava/lang/String;");
			mv.visitInsn(ARETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", intClassName, null, l0, l1, 0);
			mv.visitMaxs(2, 1);
			mv.visitEnd();
		}
		{
			// toString
			mv = cw.visitMethod(ACC_PUBLIC, "hashCode", "()I", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			if (srcBegin != null)
				mv.visitLineNumber(srcBegin.getLineNum(), l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(ICONST_0);
			mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
			mv.visitMethodInsn(INVOKESTATIC, "org/apache/commons/lang3/builder/HashCodeBuilder", "reflectionHashCode", "(Ljava/lang/Object;[Ljava/lang/String;)I");
			mv.visitInsn(IRETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", intClassName, null, l0, l1, 0);
			mv.visitMaxs(2, 1);
			mv.visitEnd();
		}

		final Environment esuper = objekt.esuper();
		
		// TODO: handle interfaces!
		
		for (Class<?> pcl : parentClasses) {
			visitClMethods(className, pcl, esuper, srcBegin, cw);
		}
		
		Class<?> cl = cll.get().loadClass(cw.toByteArray());
		return cl;
	}

	private void visitClMethods(final String className, final Class<?> pcl, 
			final Environment esuper, final CodeLoc srcBegin, 
			final ClassWriter cw) throws IOException 
	{
		System.out.println("Visiting superclass: " + pcl.getSimpleName());
		ClassReader clr = new ClassReader(clBinaryName(pcl));
		clr.accept(new ClassVisitor(0) {
			@Override
			public MethodVisitor visitMethod(int access, String name,
					String desc, String signature, String[] exceptions) {

				boolean concrete = (access & Opcodes.ACC_ABSTRACT) == 0;
				boolean visible = (access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) != 0;
				
				if (visible) {
					if (concrete) {
						// need to generate a bullet function that will call the real super method.
						esuper.bind(Symbol.valueOf(name, srcBegin), desc);
						System.out.println("Adding marker for concrete implementation of: " + name + ":" + desc);
					} else {
						// we need to generate a stub that implements this method, which
						// just calls our objekt's this environment for the method
						// by the same name.
						System.out.println("Implementing stub for: " + name + ":" + desc);
						
						String[] methParams = methParamsFromTypeDesc(desc);
						int N = methParams.length;

						/*
						MethodVisitor mv;
						mv = cw.visitMethod(ACC_PUBLIC, name, desc, signature, exceptions);
						mv.visitCode();
						Label l0 = new Label();
						mv.visitLabel(l0);
						//mv.visitLineNumber(15, l0);
						mv.visitTypeInsn(NEW, "org/bullet/value/BExpr");
						mv.visitInsn(DUP);
						mv.visitMethodInsn(INVOKESPECIAL, "org/bullet/value/BExpr", "<init>", "()V");
						mv.visitVarInsn(ASTORE, N+1);	// this + args -> then code local var.
						Label l1 = new Label();
						mv.visitLabel(l1);
						//mv.visitLineNumber(16, l1);
						mv.visitVarInsn(ALOAD, N+1);
						mv.visitVarInsn(ALOAD, 0);
						mv.visitFieldInsn(GETFIELD, className, "objekt", "Lorg/bullet/value/Objekt;");
						mv.visitMethodInsn(INVOKEVIRTUAL, "org/bullet/value/BExpr", "add", "(Ljava/lang/Object;)Z");
						mv.visitInsn(POP);
						Label l2 = new Label();
						mv.visitLabel(l2);
						//mv.visitLineNumber(17, l2);
						mv.visitVarInsn(ALOAD, N+1);
						mv.visitLdcInsn(name);
						mv.visitInsn(ACONST_NULL);
						mv.visitMethodInsn(INVOKESTATIC, "org/bullet/value/Symbol", "valueOf", "(Ljava/lang/String;Lorg/bullet/CodeLoc;)Lorg/bullet/value/Symbol;");
						mv.visitMethodInsn(INVOKEVIRTUAL, "org/bullet/value/BExpr", "add", "(Ljava/lang/Object;)Z");
						mv.visitInsn(POP);
						//////////////////////////////////////
						
						for (String parm : methParams) {
							System.out.println("Param: " + parm);
							Label l3 = new Label();
							mv.visitLabel(l3);
							if (srcBegin != null)
								mv.visitLineNumber(srcBegin.getLineNum(), l3);
							mv.visitVarInsn(ALOAD, 5);
							mv.visitVarInsn(ALOAD, 1);
							mv.visitMethodInsn(INVOKEVIRTUAL, "org/bullet/value/BExpr", "add", "(Ljava/lang/Object;)Z");
							mv.visitInsn(POP);
						}
						
						////////////////////////////////////////////////
						Label l7 = new Label();
						mv.visitLabel(l7);
						mv.visitLineNumber(22, l7);
						mv.visitVarInsn(ALOAD, 0);
						mv.visitFieldInsn(GETFIELD, "org/bullet/asm/FooImpl", "interp", "Lorg/bullet/Interpreter;");
						mv.visitVarInsn(ALOAD, 5);
						mv.visitMethodInsn(INVOKEVIRTUAL, "org/bullet/Interpreter", "eval", "(Ljava/lang/Object;)Ljava/lang/Object;");
						mv.visitInsn(ARETURN);
						Label l8 = new Label();
						mv.visitLabel(l8);
						
						int n = 0;
						mv.visitLocalVariable("this", "Lorg/bullet/asm/FooImpl;", null, l0, l8, n++);
						for (String parm : methParams)
							mv.visitLocalVariable("param_" + n, parm, null, l0, l8, n++);
						mv.visitLocalVariable("code", "Lorg/bullet/value/BExpr;", null, l1, l8, n++);
						mv.visitMaxs(3, 2 + methParams.length);
						mv.visitEnd();
						*/
					}
				}

				return super.visitMethod(access, name, desc, signature, exceptions);
			}
			@Override
			public FieldVisitor visitField(int access, String name,
					String desc, String signature, Object value) {

				System.out.println("Field:" + name + ":" + desc ); //+ ":" + signature);

				return super.visitField(access, name, desc, signature, value);
			}
		}, 0);

		
	}

	public Object genProxy() throws IOException {
		Class<?> cl = genClass();
		return Reflect.on(cl).create(interp, objekt).get();
	}
	
	private static String clBinaryName(Class<?> cl) {
		return cl == null ? "java/lang/Object" : cl.getCanonicalName().replace('.', '/');
	}
	
	private static String clDescName(String clBinName) {
		return "L" + clBinName + ";";
	}
	
	private static String clDescName(Class<?> cl) {
		return clDescName(clBinaryName(cl));
	}
	
	private static String[] methParamsFromTypeDesc(String desc) {
		return desc.substring(1, desc.indexOf(')')).split(";");
	}


	public static void main(String...args) throws IOException {

		List<Object> cargs = Collections.emptyList();
		Class<?> superClass = JPanel.class;
		List<Class<?>> implIfaces = Arrays.<Class<?>>asList(List.class);
		CodeLoc srcBegin = null;
		CodeLoc srcEnd = null;
		Objekt obj = new Objekt(new Interpreter(), cargs, superClass, implIfaces, srcBegin, srcEnd);

		System.out.println(obj);
		System.out.println(obj.proxy());
		System.out.println(obj.esuper());

		//System.out.println(Integer.class.getCanonicalName().replace(".", "/"));
	}
}

/*
 */




