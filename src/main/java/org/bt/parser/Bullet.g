grammar Bullet;

options {
  output = AST;
  backtrack = true;
}

tokens {
	BEXPR;
	IDENT;
	INTLIT;
	FLOATLIT;
	STRINGLIT;
	CHARLIT;
	BOOLLIT;
	UNQUOTE;
	UNQUOTESPL;
}

@parser::header {
   package org.bt.parser;
}

@lexer::header {
   package org.bt.parser;
}

code	
  :	exprs EOF!
	;
	
expr	
  :	bexpr
	|	literal
	;

bexpr 
  : '(' colonExpr ')' -> ^(BEXPR colonExpr)
  ;
   
colonExpr	
  :	dollarExpr (':' colonExpr)? -> dollarExpr ^(BEXPR colonExpr)?
	;
		
dollarExpr	
  :	scExpr ('$' dollarExpr)? -> scExpr dollarExpr? 
	;
		
scExpr
@init{boolean scChain = false;}	
  : pipeExpr (';' {scChain = true;} pipeExpr)* 
    -> {scChain}? ^(BEXPR pipeExpr)+
    -> pipeExpr
	;

pipeExpr
  : (arrowExpr -> arrowExpr) ('|' e=arrowExpr -> $e ^(BEXPR $pipeExpr))*
  ;

arrowExpr
  : (exprs -> exprs) ('->' e=exprs -> ^(BEXPR $arrowExpr) $e )*
	;
	
exprs 	
  : orExpr*
	;

orExpr
@init{boolean simple = true;}
  : andExpr ('or' {simple = false;} orExpr)?
    -> {!simple}? ^(BEXPR 'or' andExpr orExpr) 
    -> andExpr
  ;
	
andExpr
@init{boolean simple = true;}
	: equalExpr ('and' {simple = false;} andExpr)?
	  -> {!simple}? ^(BEXPR 'and' equalExpr andExpr) 
	  -> equalExpr
	;
	
equalExpr
@init{boolean simple = true; int typ = 0; }
  : compExpr (('==' {typ = 0;} | '!=' {typ = 1;}) {simple = false; } compExpr)?
   -> {simple}? compExpr
   -> {typ==0}? ^(BEXPR '==' compExpr+)
   -> {typ==1}? ^(BEXPR '!=' compExpr+)
   -> compExpr
   ;
	
compExpr
@init{boolean simple = true; int typ = 0; }
  : addExpr (('<' {typ = 0;} | '>' {typ = 1;} | '<=' {typ = 2;} | '>=' {typ = 3;}) {simple = false; } addExpr)?
   -> {simple}? addExpr
   -> {typ==0}? ^(BEXPR '<' addExpr+)
   -> {typ==1}? ^(BEXPR '>' addExpr+)
   -> {typ==2}? ^(BEXPR '<=' addExpr+)
   -> {typ==3}? ^(BEXPR '>=' addExpr+)
   -> addExpr
  ;

addExpr
@init{boolean isSum = false; boolean isAdd = false;}
  : multExpr (('+' {isAdd = true;} | '-') {isSum = true; } addExpr)?
   -> {isSum && isAdd}? ^(BEXPR '+' multExpr addExpr)
   -> {isSum && !isAdd}? ^(BEXPR '-' multExpr addExpr)
   -> multExpr
  ;
  
multExpr
@init{boolean isProd = false; int typ = 0; }
  : unaryOpExpr (('*' {typ = 0;} | '/' {typ = 1;} | '%' {typ=2;}) {isProd = true; } multExpr)?
   -> {isProd && typ==0}? ^(BEXPR '*' unaryOpExpr multExpr)
   -> {isProd && typ==1}? ^(BEXPR '/' unaryOpExpr multExpr)
   -> {isProd && typ==2}? ^(BEXPR '%' unaryOpExpr multExpr)
   -> unaryOpExpr
  ;
  
unaryOpExpr
@init{int typ = 0; }
  : ('-' {typ = 1;} | 'not' {typ=2;})? dotExpr
    -> {typ==1}? ^(BEXPR '-' dotExpr)
    -> {typ==2}? ^(BEXPR 'not' dotExpr)
    -> dotExpr 
  ;
  
dotExpr
@init{boolean isDot = false;}
  : lhs=unaryOpExpr0 ('.' {isDot = true;} rhs=dotExpr)? 
    -> {isDot}? ^(BEXPR $lhs $rhs)
    -> $lhs
  ;
  
unaryOpExpr0
@init{int typ = 0; }
  : ('$' {typ=1;} | '$@' {typ=2;})? expr
    -> {typ==1}? ^(BEXPR UNQUOTE expr)
    -> {typ==2}? ^(BEXPR UNQUOTESPL expr)
    -> expr
  ;

literal	
  :	ID
	|	INT
	|	FLOAT
	|	STRING
	|	CHAR
	|	boolLit
	;
	
boolLit	
  :	TRUE
	|	FALSE
	;
	
TRUE	:	'true';
FALSE	:	'false';

ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'?'|'_')*
    | ('.'|'\''|'`'|','|';'|':'|'!'|'@'|'#'|'$'|'%'|'^'|'&'|'*'|'~'|'|'|'>'|'<'|'/'|'+'|'='|'-'|'¦'|'¬'|'°'|'²'|'³'
       |'µ'|'¶'|'·'|'¹'|'º'|'»'|'×'|'÷')+
    ;

INT :	'0'..'9'+
    ;

FLOAT
    :   ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ;

COMMENT
    :   '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    |   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
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
