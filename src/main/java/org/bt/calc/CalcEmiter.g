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
  import org.objectweb.asm.Label;
  
  import static org.objectweb.asm.commons.Method.*;
  import static org.objectweb.asm.Type.*;
  import static org.objectweb.asm.commons.GeneratorAdapter.*;
  
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
  
  private void loadVar(CommonTree id) {
    String ident = id.getText();
    IdentifierAttributes idas = root.lookup(ident);
    if (idas == null)
      throw new BtException("Reference to undefined variable " + ident, new SrcLoc(null, id.getLine(), 
                  id.getCharPositionInLine()));
    
    int varId = (Integer) idas.get("varId");
    mg.loadLocal(varId);
  }
}

program
  : exprs
  ;

exprs
  : bexpr+ 
  ;

bexpr
@init {
  String op = ((CommonTree)input.LT(3)).getText(); 
}
  : literal
  | varDef
  | setExpr
  | printExpr
  | arithExpr
  | labelDef
  | gotoStmt
  | incExpr
  | cmpExpr
  | whileExpr
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

varDef
  : {((CommonTree)input.LT(3)).getText().equals("var")}?  // semantic predicate: is the ID == 'var'? 
    ^(BEXPR 
      ID 
      id=ID {
	      String ident = $id.text;
	      if (root.lookup(ident) != null)
	        throw new BtException("Illegal attempt to redeclare variable " + ident, new SrcLoc(null, $id.line, $id.pos));
	    
	      IdentifierAttributes idas = new IdentifierAttributes(ident);
	      int varId = mg.newLocal(getType(java.lang.Object.class));
	      idas.set("varId", varId);
	      
	      root.insert(idas);
	    }
	    bexpr
	  ) 
  {
    String ident = $id.text;
    IdentifierAttributes idas = root.lookup(ident);
    if (idas == null)
      throw new BtException("Reference to undefined variable " + ident + ", symbol table: " + root, 
                new SrcLoc(null, $id.line, $id.pos));
    
    int varId = (Integer) idas.get("varId");
    mg.storeLocal(varId);
  }
  ;
  
whileExpr
@init{
   Label body = mg.newLabel();
   Label test = mg.newLabel();
   Label end = mg.newLabel();
   mg.goTo(test);
}
  : {((CommonTree)input.LT(3)).getText().equals("while")}?  // semantic predicate: is the ID == 'print'?
    ^(BEXPR
      ID {
        mg.mark(test);
      }
      ce=bexpr {
        mg.unbox(BOOLEAN_TYPE);
        mg.ifZCmp(EQ, end);
        mg.goTo(body);
        mg.mark(body);
      }
      es=exprs {
        mg.goTo(test);
        mg.mark(end);
      }
    )
  ;
  
labelDef
  : {((CommonTree)input.LT(3)).getText().equals("label")}?  // semantic predicate: is the ID == 'label'? 
    ^(BEXPR ID id=ID) {
      String ident = $id.text;
      if (root.lookup(ident) != null)
        throw new BtException("Illegal attempt to redeclare label " + ident, new SrcLoc(null, $id.line, $id.pos));
    
      IdentifierAttributes idas = new IdentifierAttributes(ident);
      Label label = mg.mark();
      idas.set("label", label);
      
      root.insert(idas);
    }    
  ;

incExpr
  : {((CommonTree)input.LT(3)).getText().equals("inc")}?  // semantic predicate: is the ID == 'label'? 
    ^(BEXPR ID id=ID) {
	    String ident = $id.text;
	    IdentifierAttributes idas = root.lookup(ident);
	    if (idas == null)
	      throw new BtException("Reference to undefined variable " + ident + ", symbol table: " + root, 
	                new SrcLoc(null, $id.line, $id.pos));
	    
	    int varId = (Integer) idas.get("varId");
	    mg.loadLocal(varId);
      mg.unbox(Type.INT_TYPE);
      mg.push(1);
      mg.math(GeneratorAdapter.ADD, Type.INT_TYPE);
      mg.box(Type.INT_TYPE);  
      mg.storeLocal(varId);
    }    
  ;

gotoStmt
  : {((CommonTree)input.LT(3)).getText().equals("goto")}?  // semantic predicate: is the ID == 'goto'? 
    ^(BEXPR ID id=ID) {
	    String ident = $id.text;
	    IdentifierAttributes idas = root.lookup(ident);
	    if (idas == null)
	      throw new BtException("Reference to undefined label " + ident + ", symbol table: " + root, 
	                new SrcLoc(null, $id.line, $id.pos));
      Label label = (Label) idas.get("label");
      mg.goTo(label);	                
    }    
  ;
  
setExpr
  : {((CommonTree)input.LT(3)).getText().equals("set")}?  // semantic predicate: is the ID == 'set'? 
    ^(BEXPR 
      ID 
      id=ID
      bexpr
    ) 
  {
    String ident = $id.text;
    IdentifierAttributes idas = root.lookup(ident);
    if (idas == null)
      throw new BtException("Reference to undefined variable " + ident + ", symbol table: " + root, 
                new SrcLoc(null, $id.line, $id.pos));
    
    int varId = (Integer) idas.get("varId");
    mg.storeLocal(varId);
  }
  ;

arithExpr
@init{ 
  int etyp = 0; 
  String op = ((CommonTree)input.LT(3)).getText(); 
}
  : {
      op.equals("+") || op.equals("-") || op.equals("*") ||  
      op.equals("/") || op.equals("\%")   
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

cmpExpr
@init{ 
  int etyp = 0;
  String op = ((CommonTree)input.LT(3)).getText(); 
}
  : {
      op.equals("<")   || op.equals(">")  || op.equals("<=") ||  
      op.equals(">=")  || op.equals("==") || op.equals("!=") ||
      op.equals("and") || op.equals("or")
  }?   
    ^(BEXPR 
       ('<' {etyp=0;}|'>' {etyp=1;}|'<=' {etyp=2;}|'>=' {etyp=3;}
        |'==' {etyp=4;}|'!=' {etyp=5;}|'and' {etyp=6;}|'or' {etyp=7;}) 
       es=exprs)
  {
    
    if (etyp <= 3) {
	    String meth;
	    switch (etyp) {
	    case 0:   meth = "lt";    break;
	    case 1:   meth = "gt";    break;
	    case 2:   meth = "lte";   break;
	    case 3:   meth = "gte";   break;
	    default: throw new IllegalStateException();
	    };
	    mg.invokeStatic(getType(org.bt.runtime.Numbers.class), getMethod("boolean " + meth + "(Object, Object)"));
	    mg.invokeStatic(getType(java.lang.Boolean.class), getMethod("Boolean valueOf(boolean)"));
    } else if (etyp == 4 || etyp == 5) {
      mg.invokeStatic(getType(java.util.Objects.class), getMethod("boolean equals(Object, Object)"));
      if (etyp == 5)
        mg.not();
      mg.invokeStatic(getType(java.lang.Boolean.class), getMethod("Boolean valueOf(boolean)"));      
    } else if (etyp == 6) {
      // A and B
      Label lbl = mg.newLabel();
      Label clauseAFalse = mg.newLabel();
      Label clauseBFalse = mg.newLabel();
      Label end = new Label();
      // Stack: B, A
      mg.swap();
      // Stack: A, B
      // Stack: A.value
      mg.unbox(BOOLEAN_TYPE);
      // if A.value == 0 (i.e. A.value == false)
      mg.ifZCmp(EQ, clauseAFalse);
      // true, so continue checking
      // Stack: B
      mg.unbox(BOOLEAN_TYPE);
      // Stack: B.value
      mg.ifZCmp(EQ, clauseBFalse);
      mg.push(true);
      mg.goTo(end);
      
      // false, so quit with false
      mg.mark(clauseAFalse);
      mg.pop();
      mg.mark(clauseBFalse);
      mg.push(false);
      
      mg.mark(end);
      mg.box(BOOLEAN_TYPE);
    } else if (etyp == 7) {
      // A or B
      Label clauseATrue = mg.newLabel();
      Label clauseBTrue = mg.newLabel();
      Label end = mg.newLabel();
      // Stack: B, A
      mg.swap();
      // Stack: A, B
      // Stack: A.value
      mg.unbox(BOOLEAN_TYPE);
      // if A.value == 0 (i.e. A.value == false)
      mg.push(true);
      mg.ifICmp(EQ, clauseATrue);
      // true, so continue checking
      // Stack: B
      mg.unbox(BOOLEAN_TYPE);
      // Stack: B.value
      mg.push(true);
      mg.ifICmp(EQ, clauseBTrue);
      mg.push(false);
      mg.goTo(end);
      
      // true, so quit with true
      mg.mark(clauseATrue);
      mg.pop();
      mg.mark(clauseBTrue);
      mg.push(true);
      
      mg.mark(end);
      mg.box(BOOLEAN_TYPE);
    }
  }
  ;
  
 
literal 
  : ID {
    loadVar($ID.tree);
  }
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
