package org.bullet;

import java.io.File;
import java.io.FileNotFoundException;

public class Bullet {
	private static final File DEBUG_FILE = new File("/home/mpercossi/src/bullet/src/main/bullet/hello-world.bt");
	public static boolean DEBUG = false;
	
	public static void main(String[] args) throws FileNotFoundException {
		Interpreter interp = new Interpreter();
		if (DEBUG) {
			interp.load(DEBUG_FILE, new String[] { "Foo", "Martin" } );
		} else {
			String[] bargs = new String[args.length-1];
			System.arraycopy(args, 1, bargs, 0, args.length-1);
			interp.load(new File(args[0]), bargs);
		}
	}
}
