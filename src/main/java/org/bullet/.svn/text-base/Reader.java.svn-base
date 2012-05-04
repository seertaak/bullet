package org.bullet;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.bullet.lexer.Lexeme;
import org.bullet.lexer.Lexer;
import org.bullet.lexer.LexerUtils;
import org.bullet.lexer.Token;
import org.bullet.value.Symbol;
import org.bullet.value.BExpr;

public class Reader {

	private Lexer lex;
	private File srcFile;
	private CodeLoc lastLoc;

	public Object parse(File srcFile) {
		lex = new Lexer(srcFile);
		this.srcFile = srcFile;

		BExpr result = new BExpr(null);
		result.add(Symbol.valueOf("begin", null));

		Object r;
		while ((r = readInternal()) != Token.EOF)
			result.add(r);

		if (result.size() == 0)
			return null;
		if (result.size() == 1)
			return result.get(0);
		else
			return result;
	}
	
	public Object read(String code) {
		return null;
	}

	private Object readInternal() {
		return readInternal(true);
	}

	private Object readInternal(boolean checkPeriod) {
		Lexeme lxm = lex.peek();
		Lexeme lxmp1 = lex.peek(1);

		if (checkPeriod && lxmp1.getType() == Token.Period) {
			return dotExpr();
		}

		switch (lxm.getType()) {
		case OParen:
			return bexpr();
		case Quote:
			return qtExpr();
		case QQuote:
			return qqtExpr();
		case Unquote:
			return commaExpr();
		case UnquoteSplicing:
			return unqtSplicExpr();
		case NullLit:
			eatToken();
			return null;
		case IntLit:
			eatToken();
			return LexerUtils.parseInteger(lxm.getText());
		case DecimalLit:
			eatToken();
			return LexerUtils.parseDecimal(lxm.getText());
		case BoolLit:
			eatToken();
			return Boolean.valueOf(lxm.getText());
		case StringLit:
			eatToken();
			String result =  StringEscapeUtils.unescapeJava(lxm.getText().substring(1, lxm.getText().length() - 1));
			return result;
		case CharLit:
			eatToken();
			return StringEscapeUtils.unescapeJava(lxm.getText().substring(1, lxm.getText().length() - 1)).charAt(0);
		case Ident:
			eatToken();
			return Symbol.valueOf(lxm.getText(), lxm.getBegin());
		case Ampersand:
			return ampersExpr();
		case EOF:
			return Token.EOF;
		default:
			throw new BulletException("Unexpected lexeme \"" + lxm.getText() 
					+ "\"", lxm.getBegin());
		}
	}

	private Object unqtSplicExpr() {
		CodeLoc begin = eatToken(Token.UnquoteSplicing).getBegin();

		Object r = readInternal();

		BExpr result = new BExpr(begin, lastLoc);
		result.add(Symbol.valueOf("splunqquote", begin));
		result.add(r);

		return result;
	}

	private Object commaExpr() {
		CodeLoc begin = eatToken(Token.Unquote).getBegin();

		Object r = readInternal();

		BExpr result = new BExpr(begin, lastLoc);
		result.add(Symbol.valueOf("unqquote", begin));
		result.add(r);

		return result;
	}

	private Object qqtExpr() {
		CodeLoc begin = eatToken(Token.QQuote).getBegin();

		Object r = readInternal();

		BExpr leaves = new BExpr(begin, lastLoc);
		leaves.add(Symbol.valueOf("qquote", begin));
		leaves.add(r);

		return leaves;
	}

	private Object qtExpr() {
		CodeLoc begin = eatToken(Token.Quote).getBegin();

		Object r = readInternal();

		BExpr result = new BExpr(begin, lastLoc);

		result.add(Symbol.valueOf("quote", begin));
		result.add(r);

		return result;
	}

	private Object extraDotExpr() {
		eatToken(Token.Period);

		Object extra = readInternal(false);
		return extra;
	}

	private Object dotExpr() {

		CodeLoc begin = lex.peek().getBegin();

		BExpr es = new BExpr(begin);
		es.add(readInternal(false));
		eatToken(Token.Period);
		es.add(readInternal(false));
		es.setEnd(lex.peek().getBegin());

		BExpr prev = es;

		while (lex.peek().getType() == Token.Period) {
			Object extra = extraDotExpr();

			BExpr newEs = new BExpr(prev.getBegin());

			newEs.add(prev);
			newEs.add(extra);

			newEs.setEnd(lex.peek().getBegin());

			prev = newEs;
		}

		return prev;
	}

	private void eatToken() {
		lastLoc = lex.peek().getEnd();
		lex.eat();
	}

	private Lexeme eatToken(Token expected) {
		Lexeme lxm = lex.peek();
		if (lxm.getType() != expected) {
			throw new BulletException("Expected token " + expected
					+ ", found: " + lxm.getText() + " in " + srcFile
					+ " at location " + lxm.getBegin());
		}
		lastLoc = lxm.getEnd();
		lex.eat();
		return lxm;
	}

	private BExpr bexpr() {

		CodeLoc begin = eatToken(Token.OParen).getBegin();

		BExpr result = new BExpr(begin);
		List<Object> exprs = colonExpr();
		// System.out.println(exprs);
		result.addAll(exprs);

		CodeLoc end = eatToken(Token.CParen).getEnd();
		result.setEnd(end);

		return result;
	}

	private List<Object> colonExpr() {
		Lexeme lxm = lex.peek();
		Token type = lxm.getType();
		// :-> :| :; --- all not allowed.
		if (type == Token.Dollar || type == Token.Arrow || type == Token.Pipe
				|| type == Token.SemiColon)
			throw new BulletException("Can't have :->, ");

		List<Object> first = dollarExpr();

		lxm = lex.peek();
		type = lxm.getType();

		// a: b: c
		// a (b (c))
		if (type == Token.Colon) {
			eatToken();

			List<Object> result = new ArrayList<Object>();
			result.addAll(first);
			BExpr exp = new BExpr(lex.peek().getBegin());
			exp.addAll(colonExpr());
			exp.setEnd(lastLoc);
			result.add(exp);

			return result;
		} else {
			return first;
		}
	}

	private List<Object> dollarExpr() {
		Lexeme lxm = lex.peek();
		Token type = lxm.getType();
		// :-> :| :; --- all not allowed.
		if (type == Token.Arrow || type == Token.Pipe
				|| type == Token.SemiColon)
			throw new BulletException("Can't have :->, ");

		List<Object> first = scExpr();

		lxm = lex.peek();
		type = lxm.getType();

		// a: b: c
		// a (b (c))
		if (type == Token.Dollar) {
			eatToken();

			List<Object> result = new ArrayList<Object>();
			result.addAll(first);
			result.addAll(dollarExpr());

			return result;
		} else {
			return first;
		}
	}
	
	/**
	 * A semi-colon delimited expression: scExpr == colonExpr (';' colonExpr)*
	 * 
	 * @return
	 */
	private List<Object> scExpr() {
		Lexeme lxm = lex.peek();
		Token type = lxm.getType();

		CodeLoc begin = lxm.getBegin();
		List<Object> first = pipeExpr();

		lxm = lex.peek();
		type = lxm.getType();

		if (type == Token.SemiColon) {
			List<Object> result = new ArrayList<Object>();

			result.add(new BExpr(first, begin, lastLoc));
			while (type != Token.CParen && type != Token.Colon) {
				eatToken(Token.SemiColon);
				BExpr bexp = new BExpr(lex.peek().getBegin());
				bexp.addAll(pipeExpr());
				bexp.setEnd(lastLoc);
				result.add(bexp);
				lxm = lex.peek();
				type = lxm.getType();
			}

			return result;
		} else {
			return first;
		}
	}

	private List<Object> pipeExpr() {
		Lexeme lxm = lex.peek();
		Token type = lxm.getType();
		// :-> :| :; --- all not allowed.
		if (type == Token.Pipe || type == Token.SemiColon)
			throw new BulletException("Can't have ||, |;");

		CodeLoc begin = lxm.getBegin();
		List<Object> first = arrowExpr();

		lxm = lex.peek();
		type = lxm.getType();

		if (type == Token.Pipe) {
			List<Object> result = first;

			// a | b | c ( or x | map double | filter even
			// c (b a)
			// a | b | c | d ==> d (c (b a))
			// a | b ==> b a
			while (type == Token.Pipe) {
				eatToken();

				List<Object> lvs = new ArrayList<Object>();

				List<Object> nextExpr = arrowExpr();

				lvs.addAll(nextExpr);

				BExpr subExpr = new BExpr(result, begin, lastLoc);
				lvs.add(subExpr);

				result = lvs;

				lxm = lex.peek();
				type = lxm.getType();
			}

			return result;
		} else {
			return first;
		}
	}

	private List<Object> arrowExpr() {
		Lexeme lxm = lex.peek();
		Token type = lxm.getType();
		// :-> :| :; --- all not allowed.
		if (type == Token.Arrow || type == Token.Pipe
				|| type == Token.SemiColon)
			throw new BulletException("Can't have :->, ");

		CodeLoc begin = lxm.getBegin();
		List<Object> first = exprs();

		lxm = lex.peek();
		type = lxm.getType();

		if (type == Token.Arrow) {
			List<Object> result = null;
			BExpr fe = new BExpr(first, begin, lastLoc);

			// a -> b -> c
			// ((a) b) c
			while (type == Token.Arrow) {
				eatToken();

				List<Object> lvs = new ArrayList<Object>();
				lvs.add(fe);
				lvs.addAll(exprs());

				result = lvs;
				fe = new BExpr(lvs, begin, lastLoc);

				lxm = lex.peek();
				type = lxm.getType();
			}

			return result;
		} else {
			return first;
		}
	}

	private List<Object> exprs() {
		List<Object> leaves = new ArrayList<Object>();

		Lexeme lxm = lex.peek();
		Token type = lxm.getType();
		while (type != Token.CParen && type != Token.SemiColon
				&& type != Token.Pipe && type != Token.Arrow
				&& type != Token.Dollar && type != Token.Colon
				&& type != Token.EOF) {
			leaves.add(readInternal());
			lxm = lex.peek();
			type = lxm.getType();
		}

		return leaves;
	}

	private Object ampersExpr() {
		throw new BulletException("Fuck");

		/*
		 * eatToken(Token.Ampersand);
		 * 
		 * B tValue expr = readInternal(); if (expr instanceof VSymbol) return
		 * VIndirct.valueOf((VSymbol) expr); else throw new
		 * ParserErrorException( "Expected a symbol, found: " + expr);
		 */
	}

	public static void main(String[] args) throws FileNotFoundException {
		Bullet.main(args);
	}

}
