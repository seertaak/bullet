package org.bt.pre;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.bt.pre.PreParser.document_return;

public class PreProcessor {
	public static String process(CharStream input) {
		try {
			PreLexer lex = new PreLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lex);
			
			PreParser parser = new PreParser(tokens);
			document_return document = parser.document();
			Tree docTree = (Tree) document.tree;
			
			CommonTreeNodeStream nodes = new CommonTreeNodeStream(docTree);
			nodes.setTokenStream(tokens);
			PrePrint pre = new PrePrint(nodes);
			org.bt.pre.PrePrint.document_return document2 = pre.document();
			
			return document2.st.toString();
		} catch (RecognitionException e) {
			throw new RuntimeException("Caught exception during preprocessing: " + e);
		}
	}
}
