/**
 *   Copyright (c) Martin Percossi. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

package org.bullet.value;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bullet.CodeLoc;
import org.bullet.Type;

public class BExpr implements List<Object> {
	
	private LinkedList<Object> data;
	private CodeLoc begin;
	private CodeLoc end;
	
	public BExpr() {
		this.data = new LinkedList<Object>();
	}
	
	public BExpr(CodeLoc begin) {
		this.data = new LinkedList<Object>();
		this.begin = begin;
	}
	
	public BExpr(List<Object> data, CodeLoc begin) {
		this.data = new LinkedList<>(data);
		this.begin = begin;
	}
	
	public BExpr(CodeLoc begin, CodeLoc end) {
		data = new LinkedList<Object>();
		this.begin = begin;
		this.end = end;
	}

	public BExpr(List<Object> data, CodeLoc begin, CodeLoc end) {
		this.data = new LinkedList<>(data);
		this.begin = begin;
		this.end = end;
	}
	
	public boolean hasLoc() {
		return begin != null && end != null;
	}

	public CodeLoc getBegin() {
		return begin;
	}
	
	public CodeLoc getEnd() {
		return end;
	}
	
	public void setEnd(CodeLoc loc) {
		end = loc;
	}
	
	public Type type() {
		return Type.Vector;
	}

	public List<Object> data() {
		return data;
	}

//	public static BExpr valueOf(List<Object> data) {
//		return new BExpr(data);
//	}
	
	public static BExpr valueOf(List<Object> data, CodeLoc begin) {
		return new BExpr(data, begin);
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("(");
		boolean first = true;
		for (Object datum : data) {
			if (first)
				first = false;
			else
				str.append(" ");
			if (datum == null)
				str.append("null");
			else
				str.append(datum.toString());
		}
		str.append(")");
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

	public Object get(int i) {
		return data.get(i);
	}

	public static List<Object> getData(Object v) {
		if (v instanceof BExpr)
			return ((BExpr) v).data();
		else 
			return null;
	}

	public boolean add(Object arg) {
		return data.add(arg);
	}

	public void add(int i, Object obj) {
		data.add(i, obj);
	}

	public boolean addAll(Collection<? extends Object> coll) {
		return data.addAll(coll);
	}

	public boolean addAll(int i, Collection<? extends Object> coll) {
		return data.addAll(i, coll);
	}

	public void clear() {
		data.clear();
	}

	public boolean contains(Object obj) {
		return data.contains(obj);
	}

	public boolean containsAll(Collection<?> coll) {
		return data.containsAll(coll);
	}

	public int indexOf(Object obj) {
		return data.indexOf(obj);
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public Iterator<Object> iterator() {
		return data.iterator();
	}

	public int lastIndexOf(Object obj) {
		return data.lastIndexOf(obj);
	}

	public ListIterator<Object> listIterator() {
		return data.listIterator();
	}

	public ListIterator<Object> listIterator(int ix) {
		return data.listIterator(ix);
	}

	public boolean remove(Object o) {
		return data.remove(o);
	}

	public Object remove(int i) {
		return data.remove(i);
	}

	public boolean removeAll(Collection<?> coll) {
		return data.removeAll(coll);
	}

	public boolean retainAll(Collection<?> coll) {
		return data.retainAll(coll);
	}

	public Object set(int i, Object o) {
		return data.set(i, o);
	}

	public int size() {
		return data.size();
	}

	public List<Object> subList(int start, int end) {
		return data.subList(start, end);
	}
	
	public BExpr subVec(int start, int end) {
		return new BExpr(subList(start, end), this.begin, this.end);
	}
	
	public BExpr subVec(int start) {
		return subVec(1, data.size());
	}

	public Object[] toArray() {
		return data.toArray();
	}

	public <T> T[] toArray(T[] arrType) {
		return data.toArray(arrType);
	}
}
