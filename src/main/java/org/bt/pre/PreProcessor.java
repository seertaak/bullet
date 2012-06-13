package org.bt.pre;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.bt.parser.Infix;
import org.bt.pre.PreParser.document_return;

public class PreProcessor {
	public static String process(CharStream input) {
		try {
			PreLexer lex = new PreLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lex);
			
			PreParser parser = new PreParser(tokens);
			document_return document = parser.document();
			Tree docTree = (Tree) document.tree;
			
			System.out.println(docTree.toStringTree());
			CommonTreeNodeStream nodes = new CommonTreeNodeStream(docTree);
			nodes.setTokenStream(tokens);
			PrePrint pre = new PrePrint(nodes);
			org.bt.pre.PrePrint.document_return document2 = pre.document();
			
			return document2.st.toString();
			//return docTree.toStringTree();
		} catch (RecognitionException e) {
			throw new RuntimeException("Caught exception during preprocessing: " + e);
		}
	}
	public static String process2(CharStream input) {
		try {
			PreLexer lex = new PreLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lex);
			
			PreParser parser = new PreParser(tokens);
			document_return document = parser.document();
			Tree docTree = (Tree) document.tree;
			
			System.out.println(docTree.toStringTree());
			CommonTreeNodeStream nodes = new CommonTreeNodeStream(docTree);
			nodes.setTokenStream(tokens);
			
			Infix pre = new Infix(nodes);
			org.bt.parser.Infix.document_return document2 = pre.document();
			
			return ((Tree)document2.getTree()).toStringTree();
			//return docTree.toStringTree();
		} catch (RecognitionException e) {
			throw new RuntimeException("Caught exception during preprocessing: " + e);
		}
	}
}
