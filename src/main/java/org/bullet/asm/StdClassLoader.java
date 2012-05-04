package org.bullet.asm;

public class StdClassLoader extends ClassLoader {

	public StdClassLoader() {
		super();
	}

	public StdClassLoader(ClassLoader parent) {
		super(parent);
	}

	public Class<?> loadClass(String className, byte[] classBytes) {
		return defineClass(className, classBytes, 0, classBytes.length);
	}
	
}
