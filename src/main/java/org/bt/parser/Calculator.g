tree grammar Calculator;

options {
  language = Java;
  output = AST;
  tokenVocab = Bullet;
  ASTLabelType = CommonTree;
}

@header {
  package org.bt.parser;
}

program
  : exprs
  ;

exprs
  : bexpr+
  ;

bexpr
  : ^(BEXPR ID es=exprs) {
    if ($ID.text.equals("print")) { 
      System.out.println("PRINT: ");
      //for ( 
    } else { 
      System.out.println("Oops");
    }
  }
  | literal {System.out.println("LITERAL: " + $literal.text);}
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
  