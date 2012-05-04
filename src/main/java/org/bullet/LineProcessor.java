package org.bullet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;


/*
 * A BExpr can store a SourceCodeLocation object,
 * which allows us to retrieve the start and end
 * line number (y-axis), character pos (x-axis)
 * information.
 * 
 */

public class LineProcessor {
	
	private BufferedReader in;
	private File srcFile;
	private CodeFragment currLine;
	private Iterator<CodeFragment> tfm;
	
	public LineProcessor(File srcFile) {
		this.srcFile = srcFile;
		try {
			this.in = new BufferedReader(new FileReader(srcFile));
		} catch (FileNotFoundException e) {
			throw new BulletException("Source file: " + srcFile + " not found.");
		}
	}
	
	private int extendLine(String line) {
		return line.indexOf('\\');
	}
	
	private List<CodeFragment> deleteInlineComments() throws IOException {
		List<CodeFragment> lines = new ArrayList<CodeFragment>();
		
		int y = 0;
		String line;
		while ((line = in.readLine()) != null) {
			boolean ws = true;
			
			int left = -1;
			int right = -1;
			for (int i = line.length()-1; i >= 0; i--) {
				char ch = line.charAt(i);
				if (ch != ' ') {
					right = i+1;
					break;
				}
			}
			if (right == -1) {
				y++;
				continue;
			}
			for (int i = 0; i < right; i++) {
				char ch = line.charAt(i);
				if (ch == '\t') {
					throw new BulletException("Tab characters are not permitted in Bullet!", 
							new CodeLoc(srcFile, y, i));
				}
							
				if (ws && ch != ' ') {
					ws = false;
					left = i;
				}
				
				if (ch == '/' 
					&& line.length() > i+1 
					&& line.charAt(i+1) == '/') 
				{
					right = i;
					break;
				}
			}
			
			if (right == -1)
				right = line.length();
			
			if (left >= 0 && right > left) {
				CodeLoc begin = new CodeLoc(srcFile, y, left);
				CodeLoc end = new CodeLoc(srcFile, y, right);
				lines.add(new CodeFragment(line.substring(left, right), 
						begin, end));
			}
			y++;
		}
		
		return lines;
	}
	
	@SuppressWarnings("unused")
	private List<CodeFragment> mergeLines(List<CodeFragment> input) throws IOException {
		List<CodeFragment> output = new ArrayList<CodeFragment>();
		List<CodeFragment> extLines = new ArrayList<CodeFragment>();
		
		for (CodeFragment srcLine : input) {
			String line = srcLine.code();
			
			int x = extendLine(line);
			if (x < 0) {
				if (!extLines.isEmpty()) {
					StringBuilder mergedLine = new StringBuilder();
					for (CodeFragment l : extLines) {
						mergedLine.append(l.code()).append(" ");
					}

					mergedLine.append(line);
					output.add(new CodeFragment(mergedLine.toString(),
							extLines.get(0).begin(), srcLine.end()));
					
					extLines.clear();
				} else {
					output.add(srcLine);
				}
				
			} else {
				extLines.add(new CodeFragment(line.substring(0, x),
						srcLine.begin(), 
						new CodeLoc(srcFile, 
								srcLine.begin().getLineNum(), 
								x + srcLine.begin().getCharPos())));
			}
		}
		
		
		
		return output;
	}
	
	private void eatLine() throws IOException {
		if (currLine == null) {
			if (tfm.hasNext())
				tfm.next();
		}
		currLine = null;
	}
	
	private CodeFragment peekLine() throws IOException {
		if (currLine == null) {
			if (tfm.hasNext())
				currLine = tfm.next();
			else 
				currLine = null;
		}
		return currLine;
	}
	
	public List<CodeFragment> desugar(List<CodeFragment> input) throws IOException {
		tfm = input.iterator();
		List<CodeFragment> output = new ArrayList<CodeFragment>();
		
		Stack<Integer> indentStack = new Stack<Integer>();
		
		CodeFragment currln = peekLine();
		int x = currln.begin().getCharPos();
		indentStack.push(x);
		
		while ((currln = peekLine()) != null) {
			x = currln.begin().getCharPos();
			int curx = indentStack.peek();
			int n = output.size()-1;
			
			if (x < curx) {
				while (indentStack.peek() > x) {
					CodeFragment last = output.get(n);
					output.set(n, 
						new CodeFragment(last.code() + ")",
							last.begin(),
							last.end()));
					indentStack.pop();
				}
			} else if (x == curx) {
				output.add(new CodeFragment(
						"(" + currln.code() + ")",
						currln.begin(), currln.end()));
				eatLine();
			} else if (x == curx + 1) {
				throw new BulletException("Illegal one space indent", 
						new CodeLoc(srcFile, currln.begin().getLineNum(), x));
			} else if (x == curx + 2) {
				CodeFragment last = output.get(n);
				String lastLn = last.code();
				output.set(n, new CodeFragment(
						lastLn.substring(0, lastLn.length()-1) + " ",
						last.begin(), last.end()));
				indentStack.push(x);
			} else if (x == curx + 3) {
				throw new BulletException("Illegal three space indent", 
						new CodeLoc(srcFile, currln.begin().getLineNum(), x));
			} else {
				CodeFragment prevLine = output.get(n);
				String prevLn = prevLine.code();
				
				while (x >= curx + 4) {
					CodeFragment newCode = new CodeFragment(
							prevLn.substring(0, prevLn.length()-1) + " " +
							currln.code() + " ",
							prevLine.begin(), currln.end());
					
					output.set(n, newCode);
					
					eatLine();
					currln = peekLine();
					if (currln == null) 
						throw new BulletException("Peek line returned null", newCode.begin());
					x = currln.begin().getCharPos();
					prevLine = newCode;
					prevLn = prevLine.code();
				}
				
				CodeFragment newCode = new CodeFragment(
						prevLn.substring(0, prevLn.length()-1) + ")",
						prevLine.begin(), prevLine.end());
				output.set(n, newCode);
			}
		}
		
		int n = output.size()-1;
		while (indentStack.size() > 1) {
			CodeFragment last = output.get(n);
			output.set(n, 
					new CodeFragment(last.code() + ")",
							last.begin(),
							last.end()));
			indentStack.pop();
		}
		
		return output;
	}
	
	public List<CodeFragment> process() {
		try {
			List<CodeFragment> pass1 = deleteInlineComments();
//			List<CodeFragment> pass2 = mergeLines(pass1);
			List<CodeFragment> pass3 = desugar(pass1);
			
			return pass3;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) throws IOException {
		/*
		LineProcessor2 wsd = new LineProcessor2(
				new File("/home/mpercossi/src/bullet/src/main/bullet/scratch.bt"));
		List<CodeFragment> processed = wsd.process();
		for (CodeFragment line : processed) {
			System.out.println(line);
		}
		*/
		Bullet.main(args);
	}
}
