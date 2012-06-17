tree grammar Calculator;

options {
  language = Java;
  output = AST;
  tokenVocab = Bullet;
  ASTLabelType = CommonTree;
}

@header {
  package org.bt.calc;
  
  import java.lang.Integer;
  import com.google.common.base.Joiner;
  import org.objectweb.asm.commons.GeneratorAdapter;
  
  import static org.objectweb.asm.commons.Method.*;
  import static org.objectweb.asm.Type.*;
  
  import org.bt.BtException;
  import org.bt.SrcLoc;
  import org.bt.IdentifierAttributes;
  import org.bt.SymbolTable;
}

@members {
  private SymbolTable root;
  private GeneratorAdapter mg;
  
  public void init() {
    root = new SymbolTable();
  }
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
  : printExpr           { $val = null; }
  | varDef              { $val = $varDef.val; }
  | setExpr             { $val = $setExpr.val; }
  | arithExpr           { $val = $arithExpr.val; }
  | ^(BEXPR b=bexpr)    { $val = $b.val; }
  | literal             { $val = $literal.val; }
  ;

printExpr
  : {((CommonTree)input.LT(3)).getText().equals("print")}?  // semantic predicate: is the ID == 'print'?
    ^(BEXPR ID es=exprs) 
  {
    String ident = $ID.text;
    List<Object> vals = $exprs.vals;
    if (vals != null)
      System.out.println(Joiner.on("").join(vals));
    else
      throw new BtException("Argument list to print is null", new SrcLoc(null, $BEXPR.line, $BEXPR.pos));
  }
  ;  
  
varDef returns [Object val]
  : {((CommonTree)input.LT(3)).getText().equals("var")}?  // semantic predicate: is the ID == 'var'? 
    ^(BEXPR op=ID id=ID v=bexpr) {
    String ident = $op.text;
    if (root.lookup(ident) != null)
      throw new BtException("Illegal attempt to redeclare variable " + ident, new SrcLoc(null, $BEXPR.line, $BEXPR.pos));
    root.insert(new IdentifierAttributes(ident));
    System.out.println("Variable: " + root.lookup(ident) + " := " + $v.tree.toStringTree());
    $val = $v.val;
  }
  ;
  
setExpr returns [Object val]
  : {((CommonTree)input.LT(3)).getText().equals("set")}?  // semantic predicate: is the ID == 'set'? 
    ^(BEXPR op=ID id=ID v=bexpr) {
    String ident = $op.text;
    IdentifierAttributes idAttr = root.lookup(ident);
    if (idAttr == null) {
      idAttr = new IdentifierAttributes(ident);
      root.insert(idAttr);
    }
    System.out.println("Variable set: " + root.lookup(ident));
    $val = $v.val;
  }
  ;

arithExpr returns [Object val]
@init{ int etyp = 0; }
  : {
      ((CommonTree)input.LT(3)).getText().equals("+") ||  
      ((CommonTree)input.LT(3)).getText().equals("-") ||  
      ((CommonTree)input.LT(3)).getText().equals("*") ||  
      ((CommonTree)input.LT(3)).getText().equals("/") ||    
      ((CommonTree)input.LT(3)).getText().equals("\%")   

    }?   
    ^(BEXPR ('+' {etyp=0;}|'-' {etyp=1;}|'*' {etyp=2;}|'/' {etyp=3;}|MODOP {etyp=4;}) es=exprs) 
  {
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
