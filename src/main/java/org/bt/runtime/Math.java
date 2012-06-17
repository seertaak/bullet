package org.bt.runtime;

import org.bt.BtException;

public class Math {

	public static Object add(Object x, Object y) {
		Object val = null;
		if (x instanceof Double) {
			double dl = (Double) x;
			if (y instanceof Double) {
				double dr = (Double) y;
				val = dl + dr;
			} else if (y instanceof Integer) {
				double dr = (double) (int) (Integer) y;
				val = dl + dr;
			} else {
				throw new BtException("Illegal argument to arithmetic operation: " + y + ":" + y.getClass().getSimpleName());
			}
		} else if (x instanceof Integer) {
			if (y instanceof Double) {
				double dl = (Double) x;
				double dr = (Double) y;
				val = dl + dr;
			} else if (y instanceof Integer) {
				int dl = (Integer) x;
				int dr = (Integer) y;
				val = dl + dr;
			}
		} else {
			throw new BtException("Illegal argument to arithmetic operation: " + x + ":" + x.getClass().getSimpleName());
		}
		return val;
	}
	public static Object subtract(Object x, Object y) {
		Object val = null;
		if (x instanceof Double) {
			double dl = (Double) x;
			if (y instanceof Double) {
				double dr = (Double) y;
				val = dl - dr;
			} else if (y instanceof Integer) {
				double dr = (double) (int) (Integer) y;
				val = dl - dr;
			} else {
				throw new BtException("Illegal argument to arithmetic operation: " + y + ":" + y.getClass().getSimpleName());
			}
		} else if (x instanceof Integer) {
			if (y instanceof Double) {
				double dl = (Double) x;
				double dr = (Double) y;
				val = dl - dr;
			} else if (y instanceof Integer) {
				int dl = (Integer) x;
				int dr = (Integer) y;
				val = dl - dr;
			}
		} else {
			throw new BtException("Illegal argument to arithmetic operation: " + x + ":" + x.getClass().getSimpleName());
		}
		return val;
	}
	public static Object multiply(Object x, Object y) {
		Object val = null;
		if (x instanceof Double) {
			double dl = (Double) x;
			if (y instanceof Double) {
				double dr = (Double) y;
				val = dl * dr;
			} else if (y instanceof Integer) {
				double dr = (double) (int) (Integer) y;
				val = dl * dr;
			} else {
				throw new BtException("Illegal argument to arithmetic operation: " + y + ":" + y.getClass().getSimpleName());
			}
		} else if (x instanceof Integer) {
			if (y instanceof Double) {
				double dl = (Double) x;
				double dr = (Double) y;
				val = dl * dr;
			} else if (y instanceof Integer) {
				int dl = (Integer) x;
				int dr = (Integer) y;
				val = dl * dr;
			}
		} else {
			throw new BtException("Illegal argument to arithmetic operation: " + x + ":" + x.getClass().getSimpleName());
		}
		return val;
	}
	public static Object divide(Object x, Object y) {
		Object val = null;
		if (x instanceof Double) {
			double dl = (Double) x;
			if (y instanceof Double) {
				double dr = (Double) y;
				val = dl / dr;
			} else if (y instanceof Integer) {
				double dr = (double) (int) (Integer) y;
				val = dl / dr;
			} else {
				throw new BtException("Illegal argument to arithmetic operation: " + y + ":" + y.getClass().getSimpleName());
			}
		} else if (x instanceof Integer) {
			if (y instanceof Double) {
				double dl = (Double) x;
				double dr = (Double) y;
				val = dl / dr;
			} else if (y instanceof Integer) {
				int dl = (Integer) x;
				int dr = (Integer) y;
				val = dl / dr;
			}
		} else {
			throw new BtException("Illegal argument to arithmetic operation: " + x + ":" + x.getClass().getSimpleName());
		}
		return val;
	}
	public static Object mod(Object x, Object y) {
		Object val = null;
		if (x instanceof Double) {
			double dl = (Double) x;
			if (y instanceof Double) {
				double dr = (Double) y;
				val = dl % dr;
			} else if (y instanceof Integer) {
				double dr = (double) (int) (Integer) y;
				val = dl % dr;
			} else {
				throw new BtException("Illegal argument to arithmetic operation: " + y + ":" + y.getClass().getSimpleName());
			}
		} else if (x instanceof Integer) {
			if (y instanceof Double) {
				double dl = (Double) x;
				double dr = (Double) y;
				val = dl % dr;
			} else if (y instanceof Integer) {
				int dl = (Integer) x;
				int dr = (Integer) y;
				val = dl % dr;
			}
		} else {
			throw new BtException("Illegal argument to arithmetic operation: " + x + ":" + x.getClass().getSimpleName());
		}
		return val;
	}
}