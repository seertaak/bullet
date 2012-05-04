package org.bullet.value;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bullet.BulletException;
import org.bullet.CodeLoc;
import org.bullet.Interpreter;
import org.bullet.asm.JProxy;

public class Objekt {
	
	private Object proxy; // proxy object if we are implementing interfaces or extending classes.
	
	// also need a "super" object within methods, which can access all of the object's superclass
	// methods, if there are any!
	
	private List<Object> cargs;
	private Class<?> superClass;
	private List<Class<?>> implIfaces;
	private CodeLoc srcBegin, srcEnd;
	private Environment ethis;
	private Environment esuper;
	private Interpreter interp;

	public Objekt(Interpreter interp, List<Object> cargs, Class<?> superClass,
			List<Class<?>> implIfaces, CodeLoc srcBegin, 
			CodeLoc srcEnd) {
		super();
		this.interp = interp;
		this.cargs = cargs;
		this.superClass = superClass;
		this.implIfaces = implIfaces;
		this.srcEnd = srcEnd;
		this.srcBegin = srcBegin;
		this.esuper = new Environment();
		this.ethis = new Environment();
	}

	public Object proxy() throws IOException {
		if (proxy == null) {
			JProxy jproxy = new JProxy(interp, this);
			proxy = jproxy.genProxy();
		}
		return proxy;
	}
	
	public Object invoke(String methName, List<Object> args) {
		Symbol methSymb = Symbol.valueOf(methName, srcBegin);
		if (ethis.isBound(methSymb)) {
			Object mobj = ethis.lookup(methSymb);
			if (mobj instanceof Method) {
				Method mfn = (Method) mobj;
				
				// since we don't know the location of the call
				// site, we can't annotate this bexp with src.
				BExpr bexp = new BExpr();
				bexp.add(this);
				bexp.add(mfn);
				bexp.addAll(args);
				
				Object result = interp.eval(bexp);
				return result;
			} else {
				throw new BulletException("Incompatible implementation of " + methName +
						" on (bt) object " + this + ": expected interpreted method (MLambda), got: " +
						mobj.getClass() + ":" + mobj.toString(), srcBegin);
			}
		} else {
			throw new BulletException("Native call for unimplemented interpreted method " + methName +
					" on (bt) object " + this, srcBegin);
		}
	}

	public Class<?> superClass() {
		return superClass;
	}

	public List<Class<?>> interfaces() {
		return implIfaces;
	}

	public Environment esuper() {
		return esuper;
	}
	
	public Environment ethis() {
		return ethis;
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("Proxy[");
		boolean first = true;
		if (superClass != null) {
			first = false;
			str.append(superClass.getSimpleName());
		}
		if (implIfaces != null) {
			for (Class<?> iface : implIfaces) {
				if (first)
					first = false;
				else
					str.append(", ");
				str.append(iface.getSimpleName());
			}
		}
		str.append("]");
		return str.toString();
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	@Override
	public boolean equals(Object that) {
		return EqualsBuilder.reflectionEquals(this, that);
	}

	public List<Object> getCArgs() {
		return cargs;
	}

	public CodeLoc srcBegin() {
		return srcBegin;
	}
	
	public CodeLoc srcEnd() {
		return srcEnd;
	}
	
	// super's bindings are basically the "this" guy.
	
}
