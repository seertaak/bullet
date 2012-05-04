package org.bullet.asm;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.joor.Reflect;

public class InspectClass {
	
	public static void main(String...args) {
		
		Class<?> klass = ArrayList .class;
		
		for (Method meth : klass.getDeclaredMethods()) {
			System.out.println(meth);
			if (Modifier.isAbstract(meth.getModifiers()))
				System.out.println("ABSTRACT:" + meth);
		}
	}

}
