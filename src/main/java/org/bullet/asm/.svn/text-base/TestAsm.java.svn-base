package org.bullet.asm;

import org.joor.Reflect;
import org.objectweb.asm.*;

import sun.invoke.anon.AnonymousClassLoader;

public class TestAsm implements Opcodes {
	
	private static final String CLASS_NAME_BIN = "org/bullet/asm/TestG"; 
	private static final String CLASS_NAME_JAVA = CLASS_NAME_BIN.replace("/", ".");
	
	public static void main(String... args) {
		AnonymousClassLoader acll = new AnonymousClassLoader();
		StdClassLoader cll = new StdClassLoader(TestAsm.class.getClassLoader());
		try {
			byte[] klassBytes = dump();
			Class<?> aklass = acll.loadClass(klassBytes);
			Class<?> nklass = cll.loadClass(CLASS_NAME_JAVA, klassBytes);
			System.out.println(Reflect.on(aklass).call("sum").get());
			System.out.println(Reflect.on(nklass).call("sum").get());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	public static byte[] dump () throws Exception {

		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;
		AnnotationVisitor av0;

		cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, CLASS_NAME_BIN, null, "java/lang/Object", null);

		cw.visitSource("Test.java", null);

		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(3, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
			mv.visitInsn(RETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", "Lorg/bullet/asm/Test;", null, l0, l1, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "sum", "()I", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(6, l0);
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, 0);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(7, l1);
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, 1);
			Label l2 = new Label();
			mv.visitLabel(l2);
			Label l3 = new Label();
			mv.visitJumpInsn(GOTO, l3);
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitLineNumber(8, l4);
			mv.visitFrame(Opcodes.F_APPEND,2, new Object[] {Opcodes.INTEGER, Opcodes.INTEGER}, 0, null);
			mv.visitVarInsn(ILOAD, 0);
			mv.visitVarInsn(ILOAD, 1);
			mv.visitInsn(IADD);
			mv.visitVarInsn(ISTORE, 0);
			Label l5 = new Label();
			mv.visitLabel(l5);
			mv.visitLineNumber(7, l5);
			mv.visitIincInsn(1, 1);
			mv.visitLabel(l3);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitVarInsn(ILOAD, 1);
			mv.visitIntInsn(BIPUSH, 10);
			mv.visitJumpInsn(IF_ICMPLT, l4);
			Label l6 = new Label();
			mv.visitLabel(l6);
			mv.visitLineNumber(9, l6);
			mv.visitVarInsn(ILOAD, 0);
			mv.visitInsn(IRETURN);
			Label l7 = new Label();
			mv.visitLabel(l7);
			mv.visitLocalVariable("sum", "I", null, l1, l7, 0);
			mv.visitLocalVariable("i", "I", null, l2, l6, 1);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}
}
