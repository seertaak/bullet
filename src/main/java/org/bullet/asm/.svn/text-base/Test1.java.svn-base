package org.bullet.asm;

public class Test1 {
	
	private int counter = 0;
	
	private static class Ref {
		public int value = 0;
	}
	
	public Runnable foo() {
		final Ref counter2 = new Ref();
		return new Runnable() {
			
			@Override
			public void run() {
				counter++;
				counter2.value++;
			}
		};
	}

}
