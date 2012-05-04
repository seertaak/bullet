package org.bullet.asm;

import org.bullet.Interpreter;
import org.bullet.value.Objekt;

public class ExtendTest {
	
	private Objekt objekt;
	private Interpreter interp;
	
	public ExtendTest(Interpreter interp, Objekt objekt) {
		this.interp = interp;
		this.objekt = objekt;
	}
	
	public void bar() {
//		super.foo();
	}
	
	protected void foo() {
		System.out.println("ExtendTest.foo: before super call");
//		super.foo();
		System.out.println("ExtendTest.foo: after super call");
	}
	
	public static void main(String...args) {
		//ExtendTest et = new ExtendTest();
//		et.foo();
	}
}
