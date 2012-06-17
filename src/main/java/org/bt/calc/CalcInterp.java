package org.bt.calc;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.bt.parser.BulletLexer;
import org.bt.parser.BulletParser;
import org.bt.parser.BulletParser.code_return;
import org.bt.pre.PreProcessor;

public class CalcInterp {
	
	public static void main(String...args) throws IOException, RecognitionException, URISyntaxException {
		Path inputFile = Paths.get(CalcInterp.class.getResource("/test.bt").toURI());
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
		Calculator calc = new Calculator(nodes);
		calc.init();
		calc.program();
	}
}
