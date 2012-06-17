grammar Pre;

options {
  output=AST;
}

tokens {
  BLOCK;
  BRACKETSBLOCK;
  BRACESBLOCK;
  NEWLINE;
  TEXT;
  WS;
  INDENT2;
  INDENT4;
  DEDENT4;
  DEDENT42;
  DEDENT2;
  CODECOMMENT;
  
	BEXPR;
	IDENT;
	INTLIT;
	FLOATLIT;
	STRINGLIT;
	CHARLIT;
	BOOLLIT;
  
}

@parser::header {
   package org.bt.pre;
}

@members {
public String getErrorMessage(RecognitionException e,
                                                 String[] tokenNames)
{
    List stack = getRuleInvocationStack(e, this.getClass().getName());
    String msg = null;
    if (e != null && e.token != null && e.token.getText() != null && e.token.getText().contains("\t")) {
       msg = "Unescaped tab (\\t) characters are not allowed in bullet!";
    }else if (e instanceof NoViableAltException ) {
        NoViableAltException nvae = (NoViableAltException)e;
        msg = " no viable alt; token="+e.token+
            " (decision="+nvae.decisionNumber+
            " state "+nvae.stateNumber+")"+
            " decision=<<"+nvae.grammarDecisionDescription+">>";
    } else {
        msg = "Caught: " + e + " for token:" + e.token + ":";
        msg += super.getErrorMessage(e, tokenNames);
    }
    return stack+" "+msg;
}
public String getTokenErrorDisplay(Token t) {
    return t.toString();
}
}

@lexer::header {
   package org.bt.pre;
}

@lexer::members {

  private static java.util.Stack<Integer> sIndentStack = new Stack<Integer>();
  {
       sIndentStack.push(0);
  }

  private int previousIndents = -1;
  private int indentLevel = 0;
  private java.util.Queue<Token> tokens = new java.util.LinkedList<Token>();
  private java.util.Stack<Integer> indentStack = (java.util.Stack<Integer>) sIndentStack.clone();

  @Override
  public void emit(Token t) {
    state.token = t;
    tokens.offer(t);
  }

  @Override
  public Token nextToken() {
    super.nextToken();
    Token result = tokens.isEmpty() ? Token.EOF_TOKEN : tokens.poll();
    //System.out.println("Next token: " + result);
    return result;
  }

  private void jump(int ttype) {
     String label = "Dedent-4";
     if (ttype == INDENT4)
         label = "Indent-4";
     else if (ttype == INDENT2)
         label = "Indent-2";
     else if (ttype == DEDENT2)
         label = "Dedent-2";
     else if (ttype == DEDENT42)
         label = "Dedent-4-2";
    //System.out.println("EMIT: " + label);
    emit(new CommonToken(ttype, label));
  }
}

document	
  :	seq2 NEWLINE* EOF -> seq2
	;

seq2
  : sentence (NEWLINE sentence)* -> ^(BLOCK sentence)+
  | -> ^(BLOCK)
  ;

seq4
  : sentence (NEWLINE sentence)* -> sentence+
  |
  ;

sentence	
  :	node+ COMMENT? -> node+
  | COMMENT!        
	;

node	
  : literal
	|	OPAREN seq2 NEWLINE* CPAREN -> seq2
	|	OBRACKET seq4 NEWLINE* CBRACKET -> ^(BRACKETSBLOCK seq4)
	|	OBRACE seq4 NEWLINE* CBRACE -> ^(BRACESBLOCK seq4)
	|	INDENT2 seq2 NEWLINE* DEDENT2 -> seq2
	|	INDENT4 seq4 NEWLINE* ind4Suff -> seq4 ind4Suff?
	;

ind4Suff 
  : DEDENT4!
  | DEDENT42 seq2 NEWLINE* DEDENT2 -> seq2
  ; 

endBlock
  : CPAREN
  | DEDENT2
  | DEDENT4
  ;

literal 
  : ID
  | ARROW
  | INT
  | FLOAT
  | STRING
  | CHAR
  | boolLit
  ;
  
boolLit 
  : TRUE
  | FALSE
  ;
  
TRUE  : 'true';
FALSE : 'false';

ARROW: '. ';

ID  
  : ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'?'|'_')*
  | ('.'|'\''|'`'|','|';'|':'|'!'|'@'|'#'|'$'|'%'|'^'|'&'|'*'|'~'|'|'|'>'|'<'|'/'|'+'|'='|'-'|'¦'|'¬'|'°'|'²'|'³'
     |'µ'|'¶'|'·'|'¹'|'º'|'»'|'×'|'÷')+
  ;

INT : '0'..'9'+
    ;

FLOAT
    :   ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ;

COMMENT
    :   '//' ~('\n'|'\r')*
    |   '/*' ( options {greedy=false;} : . )* '*/'
    ;

STRING
    :  '"' ( ESC_SEQ | ~('\\'|'"') )* '"'
    ;

CHAR:  '\'' ( ESC_SEQ | ~('\''|'\\') ) '\''
    ;

fragment
EXPONENT : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UNICODE_ESC
    |   OCTAL_ESC
    ;

fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UNICODE_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;
  
OPAREN	:	'(';
CPAREN	:	')';
OBRACKET	:	'[';
CBRACKET	:	']';
OBRACE	:	'{';
CBRACE	:	'}';

WS	
  :	 (' ')+ {skip();}
	;

RETURN	:	'\n'+ (WS)? 
{
     int n = $WS.text == null ? 0 : $WS.text.length();
     int p = indentStack.peek();
     //System.out.println("RETURN: n=" + n + ", p=" + p + ", Indent stack: " + indentStack);
     if (n < p) {
        //System.out.println("n<p");
        while (indentStack.peek() > n) {
            indentStack.pop();
            int pp = indentStack.peek();
            
            int di = p - pp;
            int dc = n - pp;
            
            if (di >= 4) {
                if (dc >= 4) {
                    //System.out.println("MPD3: dc=" + dc + " ; di=" + di);
                    indentStack.push(n);
                    break;
                } else if (dc == 3) {
                     throw new RuntimeException("Illegal dedent to three-space indent at " + $WS);
                } else if (dc == 2) {
                    //System.out.println("MPD2: dc=" + dc + " ; di=" + di);
                    indentStack.push(n);
                    //emit(new CommonToken(NEWLINE, "NEWLINE"));
                    jump(DEDENT42);
                    //emit(new CommonToken(NEWLINE, "NEWLINE"));
                } else if (dc == 1) {
                    throw new RuntimeException("Illegal dedent to one-space indent at " + $WS);
                } else {
                    //System.out.println("MPD1: dc=" + dc + " ; di=" + di);
                    //emit(new CommonToken(NEWLINE, "NEWLINE"));
                    jump(DEDENT4);
                    emit(new CommonToken(NEWLINE, "NEWLINE"));
                }
            } else if (di == 2) {
                if (dc == 1) {
                    throw new RuntimeException("Illegal dedent to one-space indent at " + $WS);
                } else {
                    //System.out.println("MPD4: dc=" + dc + " ; di=" + di);
                    //emit(new CommonToken(NEWLINE, "NEWLINE"));
                    jump(DEDENT2);
                    emit(new CommonToken(NEWLINE, "NEWLINE"));
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
            p = pp;
        }         
     } else if (n == p) {
         emit(new CommonToken(NEWLINE, "NEWLINE"));
         //skip();
     }else if (n == p + 1) {
         if (true)
             throw new RuntimeException("Illegal one-space indent at " + $WS);
     }else if (n == p + 2) {
         indentStack.push(n);
         jump(INDENT2);
         //System.out.println("2-INDENT!: " + indentStack);
     } else if (n == p + 3) {
         if (true)
             throw new RuntimeException("Illegal three-space indent at " + $WS);
     } else if (n >= p + 4) {
         indentStack.push(n);
         jump(INDENT4);
         //System.out.println("4-INDENT!: " + indentStack);
     }
}	
   	;

   	
	
fragment INDENT2 : 	; // this one generated through actions
fragment INDENT4 : 	; // this one generated through actions
fragment DEDENT4: 	; // same as above.
fragment DEDENT42: 	; // same as above.
fragment DEDENT2:	; // ditto above.
fragment NEWLINE:	;		
