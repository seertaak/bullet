/**
 *   Copyright (c) Martin Percossi. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

package org.bullet.primitive;

import java.util.List;

import org.bullet.CodeLoc;
import org.bullet.Interpreter;
import org.bullet.value.Environment;
import org.bullet.value.Symbol;

public class PClass implements PrimitiveFn {

	/**
	 * This primitive is called whenever a class definition is encountered in a bullet source 
	 * file. E.g.
	 * 
	 * class
	 *   mvar x
	 *   etc
	 * class NamedCl
	 *   mvar x
	 *   etc
	 *   
	 * class Point
	 *   derives Value
	 *   mvar x 0d
	 *   mvar y 0d
	 *   mfunc init (x y)	// this is the constructor!
	 *     set 
	 *       this.x x
	 *       this.y y
	 *   mfunc mag ()
	 *     Math sqrt: + 
	 *       * this.x x // note: without "this."!
	 *       * this.y y
	 *   
	 *   // this function is called whenever a point 
	 *   // is the first argument to a form:
	 *   // var p: new Point 1.0d 0.0d
	 *   // print p.0 // prints "1.0"
	 *   // print p.x // of course this also works, and is much more reasonable!
	 *   mfunc invoke (i)
	 *     cond
	 *       == i 0 ; this.x
	 *       == i 1 ; this.y
	 *       true: new BulletException "Unallowed arguments." SOURCE.loc
	 *   mmacro swap ()
	 *     qquote
	 *       var uniq.tmp ,x
	 *       set 
	 *         ,x ,y
	 *         ,y  uniq.tmp
	 *   
	 * 
	 * After this definition, Point is a variable which itself has a namespace containing
	 * 
	 * What exactly is mfunc? And mvar? Can they be defined in terms of func and var, respectively?
	 * 
	 * Two special functions:
	 * - init: constructor
	 * - invoke: called when (obj args) is called and (head args) is not an existing
	 *           member function.
	 *           
	 * 
	 * 
	 * An mfunc is just a standard function whose argument list is prepended by the automatic
	 * "this" pointer to the target class. 
	 * 
	 * 1. Make a note of the class name if present (can make anonymous classes in Bullet),
	 *    a variable in the current environment will be bound to this name with the evaluated
	 *    version of the class (essentially itself an environment pointing to the function/method/etc.
	 *    definitions bound in the class [note that this is dynamic; the class definition simply 
	 *    describes its initial state.]).
	 * 2. Parent classes : for each of the parent classes, we need to get all the names for the 
	 *    parent class and include them in the current class. Either that or our 
	 *    <code>Environment</code> needs to refer to the parent class' <code>Environment</code>.
	 *    Also for <code>is-a</code> to work correctly, we need to store some data to preserve
	 *    the hierarchy.
	 * 3. Body:
	 *      - before we run the body, we need to set up an <code>Environment</code>
	 *      - we evaluate the body per standard -- any var/class/etc. definitions
	 *        will be stored in the <code>Environment</code>.
	 **/

	public Object call(Interpreter interp, List<Object> args, 
			CodeLoc begin, CodeLoc end) {
		Environment classEnv = interp.pushEnv();
		Environment instEnv = interp.pushMEnv();
		Symbol className = getClassName(args, begin, end);
		
		for (Object arg : args.subList(className != null ? 1 : 0, args.size())) {
			interp.eval(arg);
		}
		
		interp.popMEnv();
		interp.popEnv();
		
//		Klass klass = new Klass(className, classEnv, instEnv, begin, end);
//		if (className != null) {
//			 then we need to bind the variables in the top level env!
//			interp.env().bind(className, klass);
//		}
		
		return null;
	}

	private Symbol getClassName(List<Object> args, CodeLoc begin,
			CodeLoc end) {
		if (args.size() >= 1 && args.get(0) instanceof Symbol) {
			return (Symbol) args.get(0);
		} else 
			return null;
	}
}
