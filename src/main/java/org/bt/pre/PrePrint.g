tree grammar PrePrint;

options {
  language = Java;
  output = template;
  tokenVocab = Pre;
  ASTLabelType = CommonTree;
}

@header {
  package org.bt.pre;
}


document
  : ns=nodes -> template(ns={$ns.st}) "<ns>"
  ;

nodes
  : (ns+=node)* -> template(ns={$ns}) "<ns; separator=\" \">" 
  ;

node
  : ^(BLOCK d1=nodes)           -> template(d1={$d1.st})  "(<d1>)"
  | ^(BRACESBLOCK d2=nodes)     -> template(d2={$d2.st})  "{<d2>}"
  | ^(BRACKETSBLOCK d3=nodes)   -> template(d3={$d3.st})  "[<d3>]"
  | id=ID                       -> template(t={$id.text}) "<t>" 
  | i=INT                       -> template(t={$i.text})  "<t>" 
  | i=FLOAT                     -> template(t={$i.text})  "<t>" 
  | i=STRING                    -> template(t={$i.text})  "<t>" 
  | i=CHAR                      -> template(t={$i.text})  "<t>" 
  | v=(TRUE|FALSE)              -> template(t={$v.text})  "<t>"
  | ARROW                       -> template()             "->" 
  ;