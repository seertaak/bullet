package org.bt.asm;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

public class AsmPrint {
	
	public static void main(String...args) throws IOException {
		System.out.println("Printing " + args[0]);
		
		InputStream r = new FileInputStream(Paths.get(args[0]).toFile());
		ClassReader cr = new ClassReader(r);
		ClassVisitor cv = new TraceClassVisitor(new PrintWriter(System.out)); 
		cr.accept(cv, 0);
	}

}
