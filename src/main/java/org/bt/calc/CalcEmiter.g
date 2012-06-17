tree grammar CalcEmiter;

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
  import org.objectweb.asm.Type;
  
  import static org.objectweb.asm.commons.Method.*;
  import static org.objectweb.asm.Type.*;
  
  import org.bt.BtException;
  import org.bt.SrcLoc;
  import org.bt.IdentifierAttributes;
  import org.bt.SymbolTable;
  import org.bt.runtime.Math;
}

@members {
  private SymbolTable root;
  private GeneratorAdapter mg;
  
  public void init(GeneratorAdapter mg) {
    root = new SymbolTable();
    this.mg = mg;
  }
}

program
  : exprs
  ;

exprs
  : bexpr+ 
  ;

bexpr
  : literal
//  | varDef              { $val = $varDef.val; }
//  | setExpr             { $val = $setExpr.val; }
  | printExpr
  | arithExpr
  | ^(BEXPR b=bexpr)
  ;

printExpr
@init {
  mg.getStatic(getType(java.lang.System.class), "out", getType(java.io.PrintStream.class));
}
  : {((CommonTree)input.LT(3)).getText().equals("print")}?  // semantic predicate: is the ID == 'print'?
    ^(BEXPR ID es=exprs) 
  {
    mg.invokeVirtual(getType(Object.class), getMethod("String toString()"));
    mg.invokeVirtual(getType(java.io.PrintStream.class), getMethod("void println (String)"));
  }
  ;  

//varDef
//returns [Object val]
//scope {boolean isDef;} 
//  : {((CommonTree)input.LT(3)).getText().equals("var")}?  // semantic predicate: is the ID == 'var'? 
//    ^(BEXPR op=ID id=ID v=bexpr) {
//    $varDef::isDef = true;
//    String ident = $op.text;
//    if (root.lookup(ident) != null)
//      throw new BtException("Illegal attempt to redeclare variable " + ident, new SrcLoc(null, $BEXPR.line, $BEXPR.pos));
//    root.insert(new IdentifierAttributes(ident));
//    System.out.println("Variable: " + root.lookup(ident) + " := " + $v.tree.toStringTree());
//    $val = $v.val;
//  }
//  ;
//  
//setExpr returns [Object val]
//  : {((CommonTree)input.LT(3)).getText().equals("set")}?  // semantic predicate: is the ID == 'set'? 
//    ^(BEXPR op=ID id=ID v=bexpr) {
//    String ident = $op.text;
//    IdentifierAttributes idAttr = root.lookup(ident);
//    if (idAttr == null) {
//      idAttr = new IdentifierAttributes(ident);
//      root.insert(idAttr);
//    }
//    System.out.println("Variable set: " + root.lookup(ident));
//    $val = $v.val;
//  }
//  ;

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
      String meth;
      switch (etyp) {
      case 0: meth = "add";         break;
      case 1: meth = "subtract";    break;
      case 2: meth = "multiply";    break;
      case 3: meth = "divide";      break;
      case 4: meth = "mod";         break;
      default: throw new BtException("Unexpected operator type: " + etyp);
      }
    
      mg.invokeStatic(getType(org.bt.runtime.Math.class), getMethod("Object " + meth + " (Object, Object)"));
  }
  ;
 
literal 
  : ID 
  | INT { 
    mg.push(Integer.valueOf($INT.text));
    mg.box(Type.INT_TYPE);  
  }
  | FLOAT { 
    mg.push(Double.valueOf($FLOAT.text)); 
    mg.box(Type.DOUBLE_TYPE);  
  }
  | STRING {     
    String text = $STRING.text;
    mg.push(text.substring(1, text.length()-1));
  }
  | CHAR { 
    mg.push($CHAR.text); 
    mg.box(Type.CHAR_TYPE);  
  }
  | boolLit { 
    mg.push($boolLit.val); 
    mg.box(Type.BOOLEAN_TYPE);  
  }
  ;
  
boolLit returns [boolean val]
  : TRUE    { $val = true;  }
  | FALSE   { $val = false; }
  ;
