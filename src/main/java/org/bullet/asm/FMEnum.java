package org.bullet.asm;

import java.io.IOException;

import org.bullet.value.Environment;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import org.objectweb.asm.Opcodes;

public class FMEnum {
	
	public static void main(String...args) throws IOException {
		
		Environment zuper = new Environment();
		
		ClassReader clr = new ClassReader("org/bullet/Pair");
		clr.accept(new ClassVisitor(0) {
			@Override
			public MethodVisitor visitMethod(int access, String name,
					String desc, String signature, String[] exceptions) {
				
				System.out.println(name + ":" + ((access & Opcodes.ACC_ABSTRACT) != 0) + ":" + desc ); //+ ":" + signature);
				
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

}
