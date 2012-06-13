package org.bt.rt.value;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

import org.bt.BtException;

/**
 * This is the linking block used in bullet lists. Note that it can be used
 * as well to store generic pairs (just like in common lisp).
 * 
 * @author mpercossi
 */
public class Cell implements Serializable, List<Object> {
	private static final long serialVersionUID = 6211582448025724259L;
	
	public static final int CircularList = -1;
	public static final int DottedList = -2;

	public static final Cell Empty = new Cell();

	public Object car;
	public Object cdr;

	public Cell() {}	
	public Cell(Object car, Object cdr) {
		this.car = car;
		this.cdr = cdr;
	}

	public static Cell make(Object car, Object cdr) {
		return new Cell(car, cdr);
	}

	public static Cell make(Object car) {
		return new Cell(car, Empty);
	}

	public static Cell make() {
		return new Cell(Empty, Empty);
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		Object list = this;
		while (list instanceof Cell) {
			Cell pair = (Cell) list;
			Object obj = pair.car;
			hash = 31*hash + (obj==null ? 0 : obj.hashCode());
			list = pair.cdr;
		}
		if (list != Empty && list != null)
			hash = hash ^ list.hashCode();
		return hash;
	}

	static public boolean equals(Cell pair1, Cell pair2) {
		if (pair1 == pair2)
			return true;
		if (pair1 == null || pair2 == null)
			return false;
		for (;;) {
			Object x1 = pair1.car;
			Object x2 = pair2.car;
			if (x1 != x2 && (x1 == null || ! x1.equals(x2)))
				return false;
			x1 = pair1.cdr;
			x2 = pair2.cdr;
			if (x1 == x2)
				return true;
			if (x1 == null || x2 == null)
				return false;
			if (! (x1 instanceof Cell) || !(x2 instanceof Cell))
				return x1.equals(x2);
			pair1 = (Cell) x1;
			pair2 = (Cell) x2;

		}
	}

	@Override
	public boolean equals (Object obj) {
		if ((obj != null) && (obj instanceof Cell))
			return equals (this, (Cell) obj);
		else
			return false;
	}

	@Override
	public String toString() {
		Object rest = this;
		int i = 0;
		StringBuilder sbuf = new StringBuilder();
		sbuf.append('(');
		for (;;)
		{
			if (rest == Empty)
				break;
			if (i > 0)
				sbuf.append(' ');
			if (i >= 10) {
				sbuf.append("...");
				break;
			}
			if (rest instanceof Cell) {
				Cell pair = (Cell) rest;
				sbuf.append(pair.car);
				rest = pair.cdr;
			} else {
				sbuf.append("~ ");
				sbuf.append(rest);
				break;
			}
			i++;
		}
		sbuf.append(')');
		return sbuf.toString();
	}
	

	// A cell can never be empty, because it could always simply be containing null in its car, or even 
	// the pair null, null.
	public boolean isEmpty() {
		return this == Empty || (car == Empty && cdr == Empty); 
	}
	
	@Override
	public Iterator<Object> iterator() {
		return new Iterator<Object>() {
			private Iterator<Cell> i = cellIterator();
			@Override
			public boolean hasNext() {
				return i.hasNext();
			}
			@Override
			public Object next() {
				return i.next().car;
			}
			@Override
			public void remove() {
				i.remove();
			}
		};
	}

	public Iterator<Cell> cellIterator() {
		return new Iterator<Cell>() {
			
			private Cell prev = null;
			private Cell curr = Cell.this;
			
			private boolean hasNext = true;
			@Override
			public boolean hasNext() {
				return hasNext;
			}
			@Override
			public Cell next() {
				if (!hasNext) 
					throw new BtException();
				Object cell = curr.cdr;
				hasNext = cell != Empty && cell instanceof Cell;
				prev = curr;
				curr = (Cell) cell;
				return prev; 
			}
			@Override
			public void remove() {
				prev.car = curr.car;
				prev.cdr = curr.cdr;
			}
		};
	}
	
	final public Cell last() {
		Iterator<Cell> i = cellIterator();
		Cell cell = null;
		while (i.hasNext())
			cell = i.next();
		return cell;
	}
	
	@Override
	public boolean add(Object e) {
		if (car == Empty)
			car = e;
		else
			last().cdr = Cell.make(e);
		return true;
	}
	
	@Override
	public boolean remove(Object o) {
		Iterator<Object> i = iterator();
		while (i.hasNext()) {
			Object next = i.next();
			if (o == next || (o != null && o.equals(next))) {
				i.remove();
			}
		}
		return false;
	}
	@Override
	public boolean containsAll(Collection<?> c) {
		Set<?> remaining = new HashSet<>(c);
		Iterator<Object> i = iterator();
		while (i.hasNext())
			remaining.remove(i.next());
		return remaining.isEmpty();
	}
	@Override
	public boolean addAll(Collection<? extends Object> c) {
		Cell cell = last();
		for (Object o : c) {
			cell.cdr = Cell.make(o);
			cell = (Cell) cell.cdr;
		}
		return true;
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		Set<?> objs = c instanceof Set ? (Set<?>) c : new HashSet<>(c);
		Iterator<Object> i = iterator();
		while (i.hasNext()) {
			Object next = i.next();
			if (objs.contains(next)) {
				i.remove();
			}
		}
		return false;
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		Set<?> objs = c instanceof Set ? (Set<?>) c : new HashSet<>(c);
		Iterator<Object> i = iterator();
		while (i.hasNext()) {
			Object next = i.next();
			if (!objs.contains(next)) {
				i.remove();
			}
		}
		return false;
	}
	
	@Override
	public void clear() {
		car = cdr = null;
	}
	
	/**
	 * A safe function to count the length of a list.
	 * @param obj the putative list to measure
	 * @param allowOtherSequence if a non-List Sequence is seen, allow that
	 * @return the length, or -1 for a circular list, or -2 for a dotted list. 
	 */
	public int listLength() {
		// Based on list-length implementation in
		// Guy L Steele jr: "Common Lisp:  The Language", 2nd edition, page 414
		int n = 0;
		Object slow = this;
		Object fast = this;
		for (;;) {
			if (fast == Empty)
				return n;
			if (!(fast instanceof Cell))
				return -2;
			Cell fastCell = (Cell) fast;
			if (fastCell.cdr == Empty)
				return n+1;
			if (fast == slow && n > 0)
				return -1;
			if (!(fastCell.cdr instanceof Cell)) {
				n++;
				fast = fastCell.cdr;
				continue;
			}
			if (!(slow instanceof Cell))
				return -2;
			slow = ((Cell)slow).cdr;
			fast = ((Cell)fastCell.cdr).cdr;
			n += 2;
		}
	}
	
	public boolean isProper() {
		return listLength() >= -1;
	}
	
	public boolean isFinite() {
		return listLength() >= 0;
	}
	
	@Override
	public int size() {
		int length = listLength();
		if (length >= 0)
			return length;
		else if (length == -1)
			throw new BtException("Size of a circular list is infinite (use listLength()?)");
		else // length == -2 
			throw new BtException("Can't take the size of a dotted list (use listLength()?)");
	}
	
	public ListBuilder mkAppender() {
		return new ListBuilder(this, last());
	}
	public static class ListBuilder {
		private Cell head;
		private Cell tail;
		public ListBuilder() {}
		ListBuilder(Cell head, Cell tail) {
			this.head = head;
			this.tail = tail;
		}
		public static ListBuilder make() {
			return new ListBuilder();
		}
		public final ListBuilder add(Object o) {
			return append(o);
		}
		public ListBuilder append(Object o) {
			Cell cell = Cell.make(o);
			if (head == null) {
				head = tail = cell;
			} else {
				tail.cdr = cell;
				tail = cell;
			}
			return this;
		}
		public Cell get() {
			return head;
		}
	}
	@Override
	public boolean contains(Object o) {
		Iterator<Object> i = iterator();
		while (i.hasNext()) {
			Object m = i.next();
			if (o == m || (o != null && o.equals(m)))
				return true;
		}
		return false;
	}
	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		int length = size();
		if (length == CircularList) {
			throw new UnsupportedOperationException("Unable to create array from a circular list");
		} else if (length == DottedList) {
			throw new UnsupportedOperationException("Unable to create array from a dotted list");
		} else {
			if (a.length < length)
				a = (T[]) new Object[length];
			int i = 0;
			Iterator<Object> it = iterator();
			while (it.hasNext())
				a[i++] = (T) it.next();
			return a;
		}
	}
	
	@Override
	public Object[] toArray() {
		int length = size();
		if (length == CircularList) {
			throw new UnsupportedOperationException("Unable to create array from a circular list");
		} else if (length == DottedList) {
			throw new UnsupportedOperationException("Unable to create array from a dotted list");
		} else {
			Object[] a = new Object[length];
			int i = 0;
			Iterator<Object> it = iterator();
			while (it.hasNext())
				a[i++] = it.next();
			return a;
		}
	}
	
	public Cell getCell(int index) {
		Iterator<Cell> i = cellIterator();
		Cell cell = null;
		
		while (index-- >= 0 && i.hasNext())
			cell = i.next();
		
		if (index >= 0)
			return null;
		
		return cell;
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends Object> xs) {
		Cell cell = getCell(index);
		Object next = cell.cdr;
		cell.cdr = Empty;
		// TODO: can do following in a single pass rather than two.
		for (Object x : xs) {
			cell.cdr = Cell.make(x);
			cell = (Cell) cell.cdr;
		}
		cell.cdr = next;
		return true;
	}
	@Override
	public Object get(int index) {
		return getCell(index).car;
	}
	@Override
	public Object set(int index, Object element) {
		return getCell(index).car = element;
	}
	@Override
	public void add(int index, Object element) {
		Cell cell = getCell(index);
		Object next = cell.cdr;
		cell.cdr = Cell.make(element);
		((Cell) cell.cdr).cdr = next;
	}
	@Override
	public Object remove(int index) {
		Iterator<Cell> i = cellIterator();
		Cell cell = null;
		while (index-- >= 0 && i.hasNext())
			cell = i.next();
		if (index >= 0)
			return null;
		else {
			i.remove();
			return cell.car;
		}
	}
	@Override
	public int indexOf(Object o) {
		return 0;
	}
	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public ListIterator<Object> listIterator() {
		throw new UnsupportedOperationException();
	}
	@Override
	public ListIterator<Object> listIterator(int index) {
		throw new UnsupportedOperationException();
	}
	@Override
	public List<Object> subList(int fromIndex, int toIndex) {
		Cell from = getCell(fromIndex);
		ListBuilder bld = ListBuilder.make();
		toIndex -= fromIndex;
		Iterator<Object> it = from.iterator();
		while (toIndex-- >= 0 && it.hasNext())
			bld.append(it.next());
		return bld.get();
	}
	
	public static Cell list(Object...args) {
		ListBuilder bld = ListBuilder.make();
		for (Object arg : args)
			bld.append(arg);
		return bld.get();
	}
	
	public static Cell cons(Object car, Object cdr) {
		return Cell.make(car, cdr);
	}
	
	public static void main(String...args) {
		System.out.println(Cell.make("4", 2345.2));
		System.out.println(list(0, 1, 2, 3));
		Cell list = list(0, 1, 2, 3);
		//System.out.println(list.contains(1));
		//System.out.println(list.size());
		for (Object o : list) {
			System.out.println(o);
		}
		Cell list2 = (Cell) list.subList(0, 1);
		list2.addAll(Arrays.asList(2, 3, 4));
		System.out.println(list2);
		System.out.println(list2.get(0));
		System.out.println(cons(5, cons(6, Empty)));
		System.out.println(cons(5, list2));
		list2.last().cdr = list2;
		System.out.println(list2);
		Cell cell3 = Cell.make();
		cell3.add(1);
		System.out.println(cell3);
	}
}
