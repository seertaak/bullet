tree grammar Infix;

options {
  language = Java;
  output = AST;
  tokenVocab = Pre;
  ASTLabelType = CommonTree;
}

@header {
  package org.bt.parser;
}

document
  : ns=nodes 
  ;

nodes
  : (ns+=node)*  
  ;

node
  : ^(BLOCK d1=nodes)           
  | ^(BRACESBLOCK d2=nodes)     
  | ^(BRACKETSBLOCK d3=nodes)
  | literal   
  ;
  
literal
  : ID               
  | INT              
  | FLOAT          
  | STRING        
  | CHAR            
  | TRUE
  | FALSE    
  ;  
   
/*literal
  : ^(IDENT id=ID)               
  | ^(INTLIT i=INT)              
  | ^(FLOATLIT i=FLOAT)          
  | ^(STRINGLIT i=STRING)        
  | ^(CHARLIT i=CHAR)            
  | ^(BOOLLIT v=(TRUE|FALSE))    
  ;*/ 