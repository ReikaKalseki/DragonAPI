package Reika.DragonAPI.Instantiable;

import java.util.Iterator;
import java.util.List;


public class ReadOnlyIterator<E> implements Iterator<E> {

	private final Iterator<E> data;

	public ReadOnlyIterator(List<E> li) {
		data = li.iterator();
	}

	@Override
	public boolean hasNext() {
		return data.hasNext();
	}

	@Override
	public E next() {
		return data.next();
	}

}
