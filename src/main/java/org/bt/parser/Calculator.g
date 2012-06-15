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
  : ^(BEXPR b=bexpr) {
    $val = $b.val;
  } 
  | printExpr { $val = null; }
  | arithExpr { $val = $arithExpr.val; }
  | literal { $val = $literal.val; }
  ;

printExpr 
  : ^(BEXPR ID es=exprs) {
    String ident = $ID.text;
    if (ident.equals("print")) {
      List<Object> vals = $exprs.vals;
      if (vals != null)
        System.out.println(Joiner.on("").join(vals));
      else
        throw new BtException("Argument list to print is null", new SrcLoc(null, $BEXPR.line, $BEXPR.pos));
    } else if (ident.equals("set")) {
    } else if (ident.equals("var")) {
    } else {
      throw new BtException("Unidentified operation", new SrcLoc(null, $BEXPR.line, $BEXPR.pos));
    }
  }
  ;
  
arithExpr returns [Object val]
@init{ int etyp = 0; }
  : ^(BEXPR ('+' {etyp=0;}|'-' {etyp=1;}|'*' {etyp=2;}|'/' {etyp=3;}|MODOP {etyp=4;}) es=exprs) {
      List<Object> vals = $exprs.vals;
      
      if (vals.size() != 2) 
        throw new BtException("Expected exactly two arguments to arithmetic operator", new SrcLoc(null, $BEXPR.line, $BEXPR.pos));
        
      Object ol = vals.get(0);
      Object or = vals.get(1);
      
      if (ol instanceof Double) {
        double dl = (Double) ol;
        if (or instanceof Double) {
          double dr = (Double) or;
          if (etyp == 0) {
            $val = dl + dr;
          } else if (etyp == 1) {
            $val = dl - dr;
          } else if (etyp == 2) {
            $val = dl * dr;
          } else if (etyp == 3) {
            $val = dl / dr;
          } else if (etyp == 4) {
            $val = dl \% dr;
          }
        } else if (or instanceof Integer) {
          double dr = (double) (int) (Integer) or;
          if (etyp == 0) {
            $val = dl + dr;
          } else if (etyp == 1) {
            $val = dl - dr;
          } else if (etyp == 2) {
            $val = dl * dr;
          } else if (etyp == 3) {
            $val = dl / dr;
          } else if (etyp == 4) {
            $val = dl \% dr;
          }
        } else {
          throw new BtException("Illegal argument to arithmetic operation: " + or, new SrcLoc(null, $BEXPR.line, $BEXPR.pos));
        }
      } else if (ol instanceof Integer) {
        if (or instanceof Double) {
          double dl = (Double) ol;
          double dr = (Double) or;
          if (etyp == 0) {
            $val = dl + dr;
          } else if (etyp == 1) {
            $val = dl - dr;
          } else if (etyp == 2) {
            $val = dl * dr;
          } else if (etyp == 3) {
            $val = dl / dr;
          } else if (etyp == 4) {
            $val = dl \% dr;
          }
        } else if (or instanceof Integer) {
          int dl = (Integer) ol;
          int dr = (Integer) or;
          if (etyp == 0) {
            $val = dl + dr;
          } else if (etyp == 1) {
            $val = dl - dr;
          } else if (etyp == 2) {
            $val = dl * dr;
          } else if (etyp == 3) {
            $val = dl / dr;
          } else if (etyp == 4) {
            $val = dl \% dr;
          }
        } else {
          throw new BtException("Illegal argument to arithmetic operation: " + or, new SrcLoc(null, $BEXPR.line, $BEXPR.pos));
        }
      } else {
        throw new BtException("Illegal argument to arithmetic operation: " + ol, new SrcLoc(null, $BEXPR.line, $BEXPR.pos));
      }
  }
  ;
  
literal returns [Object val]
  : ID      { $val = $ID.text; }
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
