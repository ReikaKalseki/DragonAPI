/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Collections;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class OneWayCollections {

	public static final class OneWayList<E> extends ArrayList<E> {

		@Override
		public final E remove(int o) {
			throw new UnsupportedOperationException("You cannot remove entries from this list!");
		}

		@Override
		public final boolean remove(Object o) {
			throw new UnsupportedOperationException("You cannot remove entries from this list!");
		}

		@Override
		public final boolean removeAll(Collection c)  {
			throw new UnsupportedOperationException("You cannot remove entries from this list!");
		}

		@Override
		public final E set(int index, E element) {
			throw new UnsupportedOperationException("You cannot overwrite entries in this list!");
		}

		@Override
		public final void clear() {
			throw new UnsupportedOperationException("You cannot clear this list!");
		}

		@Override
		public final Iterator<E> iterator() {
			return new WrapperIterator(super.iterator());
		}
	}

	public static final class OneWaySet<E> extends HashSet<E> {

		@Override
		public final boolean remove(Object o) {
			throw new UnsupportedOperationException("You cannot remove entries from this set!");
		}

		@Override
		public final boolean removeAll(Collection c)  {
			throw new UnsupportedOperationException("You cannot remove entries from this set!");
		}

		@Override
		public final void clear() {
			throw new UnsupportedOperationException("You cannot clear this set!");
		}

		@Override
		public final Iterator<E> iterator() {
			return new WrapperIterator(super.iterator());
		}
	}

	public static final class OneWayMap<K, V> extends HashMap<K, V> {

		@Override
		public final V remove(Object obj) {
			throw new UnsupportedOperationException("You cannot remove entries from this map!");
		}

		@Override
		public final void clear() {
			throw new UnsupportedOperationException("You cannot clear this map!");
		}

		@Override
		public final V put(K key, V value) {
			if (this.containsKey(key)) {
				throw new UnsupportedOperationException("You cannot overwrite entries in this map!");
			}
			else {
				return super.put(key, value);
			}
		}

		@Override
		public final Set<K> keySet() {
			return Collections.unmodifiableSet(super.keySet());
		}

		@Override
		public final Collection<V> values() {
			return Collections.unmodifiableCollection(super.values());
		}

		@Override
		public final Set<Map.Entry<K, V>> entrySet() {
			return new WrapperEntrySet(super.entrySet());
		}

	}

	private static final class WrapperEntrySet<K, V> extends AbstractSet<Map.Entry<K,V>> {

		private final Set<Map.Entry<K, V>> wrapped;

		private WrapperEntrySet(Set<Map.Entry<K, V>> set) {
			wrapped = set;
		}

		@Override
		public Iterator<Map.Entry<K,V>> iterator() {
			return wrapped.iterator();
		}

		@Override
		public boolean contains(Object o) {
			return wrapped.contains(o);
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException("You cannot remove entries from this map with its entry set!");
		}

		@Override
		public int size() {
			return wrapped.size();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException("You cannot clear this map entry set!");
		}

	}

	private static final class WrapperIterator<E> implements Iterator<E> {

		private final Iterator<E> wrapped;

		private WrapperIterator(Iterator<E> wrap) {
			wrapped = wrap;
		}

		public final void remove() {
			throw new UnsupportedOperationException("You cannot remove entries from this collection with an iterator!");
		}

		@Override
		public boolean hasNext() {
			return wrapped.hasNext();
		}

		@Override
		public E next() {
			return wrapped.next();
		}

	}

}
