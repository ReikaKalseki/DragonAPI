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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class ImmutableCollections {

	public static final class ImmutableList<E> extends ArrayList<E> {

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
			throw new UnsupportedOperationException("You cannot overwrite entries in this list!");
		}

		@Override
		public final Iterator<E> iterator() {
			return new WrapperIterator(super.iterator());
		}
	}

	public static final class ImmutableSet<E> extends HashSet<E> {

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
