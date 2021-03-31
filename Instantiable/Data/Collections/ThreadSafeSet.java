package Reika.DragonAPI.Instantiable.Data.Collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;


public class ThreadSafeSet<E> implements Set<E> {

	protected final Set<E> data = Collections.synchronizedSet(new HashSet<E>());

	@FunctionalInterface
	public static interface IterationHandle<E> {

		public IterationResult handle(E entry);

	}

	public static interface IterationResult {

		public boolean removeFromSet();
		public boolean continueIterating();
		public Object getReturnValue();

	}

	public static class ReturnValue implements IterationResult {

		public Object returnedValue;
		public boolean removeFromSet = false;

		public ReturnValue(Object o) {
			returnedValue = o;
		}

		public ReturnValue(Object o, boolean rem) {
			this(o);
			removeFromSet = rem;
		}

		@Override
		public boolean removeFromSet() {
			return removeFromSet;
		}

		@Override
		public boolean continueIterating() {
			return false;
		}

		@Override
		public Object getReturnValue() {
			return returnedValue;
		}

	}

	public static enum DefaultIterationResult implements IterationResult {
		CONTINUE,
		REMOVEANDCONTINUE,
		REMOVEANDBREAK,
		BREAK,
		RETURNTRUE,
		RETURNFALSE,
		RETURN0,
		RETURN1;

		@Override
		public boolean removeFromSet() {
			switch(this) {
				case REMOVEANDBREAK:
				case REMOVEANDCONTINUE:
					return true;
				default:
					return false;
			}
		}

		@Override
		public boolean continueIterating() {
			switch(this) {
				case CONTINUE:
				case REMOVEANDCONTINUE:
					return true;
				default:
					return false;
			}
		}

		@Override
		public Object getReturnValue() {
			switch(this) {
				case RETURNTRUE:
					return true;
				case RETURNFALSE:
					return false;
				case RETURN0:
					return 0;
				case RETURN1:
					return 1;
				default:
					return null;
			}
		}
	}

	/** Iterates the set, so you do not need to write a sync block. */
	public Object iterate(IterationHandle<E> func) {
		synchronized (data) {
			Iterator<E> it = data.iterator(); // Must be in the synchronized block
			while (it.hasNext()) {
				E val = it.next();
				IterationResult res = func.handle(val);
				if (res.removeFromSet()) {
					it.remove();
				}
				if (!res.continueIterating()) {
					return res.getReturnValue();
				}
			}
		}
		return null;
	}

	public E iterateAsSearch(Function<E, Boolean> check) {
		return this.iterateAsSearch(check, null);
	}

	public E iterateAsSearch(Function<E, Boolean> check, Function<E, Boolean> validity) {
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
