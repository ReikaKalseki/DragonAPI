package Reika.DragonAPI.Instantiable.Data.Collections;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class ThreadSafeSet<E> implements Set<E> {

	private HashSet<E> data = new HashSet();

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return data.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return data.iterator();
	}

	@Override
	public Object[] toArray() {
		return data.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return data.toArray(a);
	}

	@Override
	public boolean add(E e) {
		data = new HashSet(data);
		return data.add(e);
	}

	@Override
	public boolean remove(Object o) {
		data = new HashSet(data);
		return data.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return data.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		data = new HashSet(data);
		return data.addAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		data = new HashSet(data);
		return data.retainAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		data = new HashSet(data);
		return data.removeAll(c);
	}

	@Override
	public void clear() {
		data = new HashSet();
	}

}
