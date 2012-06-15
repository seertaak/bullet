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

exprs returns [List<Object> vals]
@init{ $vals = new ArrayList<>(); }
  : (bexpr {
   $vals.add($bexpr.val);
  })+ 
  ;

bexpr returns [Object val]
  : ^(BEXPR ID es=exprs) {
    if ($ID.text.equals("print")) {
      System.out.println(Joiner.on(" ").join($exprs.vals));
      $val = null;
    } else {
      throw new BtException("Unidentified operation", new SrcLoc(null, $BEXPR.line, $BEXPR.pos));
    }
  }
  | literal { $val = $literal.val; }
  ;
  
literal returns [Object val]
  : ID      { $val = $ID; }
  | ARROW   { $val = $ARROW;}
  | INT     { $val = Integer.valueOf($INT.text);  }
  | FLOAT   { $val = Double.valueOf($FLOAT.text); }
  | STRING  {     
    String text = $STRING.text;
    $val = text.substring(1, text.length()-1);
  }
  | CHAR    { $val = $CHAR.text; }
  | boolLit { $val = $boolLit.val; }
  ;
  
boolLit returns [boolean val]
  : TRUE    { $val = true;  }
  | FALSE   { $val = false; }
  ;
