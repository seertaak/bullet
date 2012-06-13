tree grammar Calculator;

options {
  language = Java;
  output = AST;
  tokenVocab = Bullet;
  ASTLabelType = CommonTree;
}

@header {
  package org.bt.parser;
  
  import com.google.common.base.Joiner;
  
  import org.bt.BtException;
  import org.bt.SrcLoc;
  import java.lang.Integer;
}

program
  : exprs
  ;

exprs returns [List<?> vals]
  : es+=bexpr+ {
    $vals = $es;
  }
  ;

bexpr returns [Object val]
  : ^(BEXPR ID es=exprs) {
    if ($ID.text.equals("print")) {
      List<?> children = ((CommonTree)$es.tree).getChildren();
      System.out.println(Joiner.on(" ").join(children));
      
      $val = null;
    } else {
      throw new BtException("Unidentified operation", new SrcLoc(null, $BEXPR.line, $BEXPR.pos));
    }
  }
  | literal {
    CommonTree tree = (CommonTree) $literal.tree;
    Token token = tree.getToken();
    String text = token.getText();
    if (token.getType() == STRING)
      $val = text.substring(1, text.length()-1);
    else if (token.getType() == INT)
      $val = Integer.valueOf(text);
    else
      $val = text;
  }
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
  