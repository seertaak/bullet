package org.bt.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.bt.parser.BulletParser.code_return;
import org.bt.pre.PreProcessor;

public class ParseTest {
	
	public static void main(String...args) throws IOException, RecognitionException {
		Path inputFile = Paths.get("/home/mpercossi/bt/calc.bt");
		String preSrc = PreProcessor.process(new ANTLRFileStream(inputFile.toString(), "UTF8"));
		
		System.out.println("Source code:\n" + preSrc);
		
		BulletLexer lex = new BulletLexer(new ANTLRStringStream(preSrc));
		CommonTokenStream tokens = new CommonTokenStream(lex);

		BulletParser parser = new BulletParser(tokens);
		code_return code = parser.code();
		System.out.println(((Tree)code.tree).toStringTree().toString());
		Tree docTree = (Tree) code.tree;
		
		System.out.println("TREE:" + docTree.toStringTree());
		CommonTreeNodeStream nodes = new CommonTreeNodeStream(docTree);
		nodes.setTokenStream(tokens);
		Calculator calc = new Calculator(nodes);
		calc.program();
	}
}
