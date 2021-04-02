package Reika.DragonAPI.Instantiable.Data.Collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


public class ThreadSafeSet<E> implements Set<E> {

	protected final Set<E> data = Collections.synchronizedSet(new HashSet<E>());

	/** Iterates the set, so you do not need to write a sync block. */
	public final <R> R iterate(Function<Iterator<E>, R> func) {
		synchronized(data) {
			return func.apply(data.iterator());
		}
	}

	/** Iterates the set, so you do not need to write a sync block. */
	public final void simpleIterate(Consumer<E> func) {
		/*
		synchronized (data) {
			for (E e : data) {
				func.accept(e);
			}
		}*/
		data.forEach(func);
	}

	public final E iterateAsSearch(Function<E, Boolean> check) {
		return this.iterateAsSearch(check, null);
	}

	public final E iterateAsSearch(Function<E, Boolean> check, Function<E, Boolean> validity) {
		synchronized (data) {
			Iterator<E> it = data.iterator(); // Must be in the synchronized block
			while (it.hasNext()) {
				E val = it.next();
				if (validity != null) {
					boolean valid = validity.apply(val);
					if (!valid) {
						it.remove();
						continue;
					}
				}
				if (check.apply(val))
					return val;
			}
		}
		return null;
	}

	/** Return true in the function to remove */
	public final void filterElements(Predicate<E> check) {
		//synchronized (data) {
		/*
			Iterator<E> it = data.iterator(); // Must be in the synchronized block
			while (it.hasNext()) {
				E val = it.next();
				if (check.apply(val)) {
					it.remove();
				}
			}
		 */
		data.removeIf(check);
		//}
	}

	public final boolean removeIf(Predicate<? super E> filter) {
		return data.removeIf(filter);
	}

	public final void forEach(Consumer<? super E> filter) {
		data.forEach(filter);
	}

	public final void copyTo(ArrayList<E> li) {
		synchronized (data) {
			li.clear();
			Iterator<E> it = data.iterator(); // Must be in the synchronized block
			while (it.hasNext()) {
				E val = it.next();
				li.add(val);
			}
		}
	}

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
		throw new UnsupportedOperationException("This set does not support natural iteration! Use .iterate()!");//return data.iterator();
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
		return data.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return data.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return data.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return data.addAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return data.retainAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return data.removeAll(c);
	}

	@Override
	public void clear() {
		data.clear();
	}

}
