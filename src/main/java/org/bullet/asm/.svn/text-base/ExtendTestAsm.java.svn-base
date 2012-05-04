package org.bullet.asm;
import java.util.*;
import org.objectweb.asm.*;

public class ExtendTestAsm implements Opcodes {

	public static byte[] dump () throws Exception {

		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;
		AnnotationVisitor av0;

		cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, "org/bullet/asm/ExtendTest", null, "org/bullet/asm/Base", null);

		cw.visitSource("ExtendTest.java", null);

		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(4, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "org/bullet/asm/Base", "<init>", "()V");
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(5, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(ICONST_4);
			mv.visitFieldInsn(PUTFIELD, "org/bullet/asm/ExtendTest", "x", "I");
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(6, l2);
			mv.visitInsn(RETURN);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLocalVariable("this", "Lorg/bullet/asm/ExtendTest;", null, l0, l3, 0);
			mv.visitMaxs(2, 1);
			mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}
}
