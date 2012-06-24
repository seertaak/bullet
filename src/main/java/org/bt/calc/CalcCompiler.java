package org.bt.calc;

import static org.objectweb.asm.Type.getType;
import static org.objectweb.asm.commons.Method.getMethod;

import java.io.PrintWriter;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.bt.BtException;
import org.bt.parser.BulletLexer;
import org.bt.parser.BulletParser;
import org.bt.parser.BulletParser.code_return;
import org.bt.pre.PreProcessor;
import org.joor.Reflect;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.util.TraceClassVisitor;

import sun.invoke.anon.AnonymousClassLoader;

public class CalcCompiler implements Opcodes {
	
	private static final String CLASSNAME = "org/bt/calc/CalcCompiled";
	@SuppressWarnings("unused")
	private static final Handle BOOTSTRAP = new Handle(H_INVOKESTATIC, 
		    "org/dynalang/dynalink/DefaultBootstrapper","bootstrap", MethodType.methodType(CallSite.class,
		        MethodHandles.Lookup.class, String.class, MethodType.class).toMethodDescriptorString());
	@SuppressWarnings("unused")
	private static final Object[] BOOTSTRAP_ARGS = new Object[0];
	private ClassWriter cw;
	private TraceClassVisitor cv;
	//private CheckClassAdapter cv;
	private GeneratorAdapter mg;
	private AnonymousClassLoader classLoader;
	
	public CalcCompiler() {
		cw = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
		cv = new TraceClassVisitor(cw, new PrintWriter(System.out));
		//cv = new CheckClassAdapter(cw);
		cv.visit(V1_7, ACC_PUBLIC + ACC_SUPER, CLASSNAME, null, "java/lang/Object", null);
		{ 
			Method m = getMethod("void <init> ()");
			GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC, m, null, null, cv);
			mg.visitCode();
			mg.loadThis();
			mg.invokeConstructor(getType(Object.class), m);
			mg.returnValue();
			mg.endMethod();
		}
		
		this.mg = new GeneratorAdapter(ACC_PUBLIC, getMethod("void calc ()"), null, null, cv);
		classLoader = new AnonymousClassLoader();
	}
	
	public Object compile(Path inputFile) {
		try {
			System.out.println("Reading from: " + inputFile.toString());
			String preSrc = PreProcessor.process(new ANTLRFileStream(inputFile.toString(), "UTF8"));
			System.out.println("Pre-processed source: " + preSrc);
			
			BulletLexer lex = new BulletLexer(new ANTLRStringStream(preSrc));
			CommonTokenStream tokens = new CommonTokenStream(lex);
	
			BulletParser parser = new BulletParser(tokens);
			code_return code = parser.code();
			Tree docTree = (Tree) code.getTree();
			
			System.out.println("Parsed AST: " + docTree.toStringTree());
			CommonTreeNodeStream nodes = new CommonTreeNodeStream(docTree);
			nodes.setTokenStream(tokens);
			CalcEmiter ce = new CalcEmiter(nodes);
			ce.init(this.mg);
			ce.program();	// at this point, everything has been emited!
			mg.returnValue();
			mg.endMethod();
			
			cv.visitEnd();
			
			Class<?> calcClass = classLoader.loadClass(cw.toByteArray());
			return calcClass.newInstance();
		} catch (Throwable e) {
			throw new BtException(e);
		}
	}

	
	public static void main(String...args) throws URISyntaxException {
		Path inputFile = Paths.get(CalcInterp.class.getResource("/test.bt").toURI());
		CalcCompiler cc = new CalcCompiler();
		Object calculator = cc.compile(inputFile);
		Reflect.on(calculator).call("calc");
	}
}
