package org.bullet.lexer;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.bullet.BulletException;
import org.bullet.CodeFragment;
import org.bullet.CodeLoc;
import org.bullet.LineProcessor;

public class Lexer {
	
	private Queue<Lexeme> lookAheadTokens;
	private Iterator<CodeFragment> lineIt;
	private CodeFragment currLine;
	private CodeLoc lastLoc;
	private int pos;
	private File srcFile; 
	
	public Lexer(File srcFile) {
		this.srcFile = srcFile;
		LineProcessor lineProc = new LineProcessor(srcFile);
		this.lineIt = lineProc.process().iterator();
		this.pos = 0;
		this.lookAheadTokens = new LinkedList<Lexeme>();
		this.lastLoc = null;
		this.currLine = lineIt.next();
	}
	
	public String inputData() {
		String code = currLine.code();
		if (pos < code.length())
			return code.substring(pos);
		else if (pos >= code.length() && lineIt.hasNext()) {
			currLine = lineIt.next();
			lastLoc = currLine.end();
			pos = 0;
			return currLine.code();
		} else {
			return null;
		}
	}
	
	public Lexeme peek() {
		return peek(0);
	}
	
	public Lexeme peek(int lookAhead) {
		while (lookAhead >= lookAheadTokens.size()) {
			String input = inputData();
			if (input == null)
				return new Lexeme(null, Token.EOF, 
						lastLoc, lastLoc);

			for (Token t : Token.values()) {
				if (t == Token.EOF) {
					// there's a problem, we may be at EOF.
					//return new TokenInfo(pos, null, Token.EOF);
					throw new BulletException(
							"Unidentified token: " + input,
							new CodeLoc(srcFile, 
									currLine.begin().getLineNum(), 
									pos));
				} else if (t == Token.WS) {
					int i = 0;
					char ch = input.charAt(i);
					while (ch == ' ' || ch == '\t' 
							|| ch == '\n' || ch == '\r')
					{
						if (++i == input.length())
							break;
						ch = input.charAt(i);
					}
					pos += i;
					if (i > 0)
						break;
				} else {
					String match = t.match(input);
					if (match != null) {
						int indent = currLine.begin().getCharPos();
						int start = pos + indent;
						int y = currLine.begin().getLineNum();
						pos += match.length();
						
						// we have a match
						Lexeme token = new Lexeme(
								match,
								t, new CodeLoc(srcFile, y, start), 
								new CodeLoc(srcFile, y, pos));
						lookAheadTokens.offer(token);
						break;
					}
				}
			}
		}
		
		//System.out.println(lookAheadTokens);
		int i = 0;
		for (Lexeme ti : lookAheadTokens) {
			if (i++ == lookAhead) {
				return ti;
			}
		}
		
		throw new BulletException("Unidentified token: " + inputData(), 
				currLine.end());
	}
	
	public void eat() {
		lookAheadTokens.poll();
	}
	
	public Queue<Lexeme> lookAheadTokens() {
		return lookAheadTokens;
	}
	
	public static void main(String[] args) throws IOException {
		Lexer wsd = new Lexer(
				new File("/home/mpercossi/src/bullet/src/main/bullet/scratch.bt"));
		Lexeme lxm;
		while ((lxm = wsd.peek()).getType() != Token.EOF) {
			System.out.println(lxm);
			wsd.eat();
		}
	}
}
