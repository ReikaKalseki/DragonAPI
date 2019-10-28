package Reika.DragonAPI.Instantiable.Data;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import Reika.DragonAPI.Instantiable.ReadOnlyIterator;
import Reika.DragonAPI.Instantiable.Data.PointPath.PointNode;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;

public class PointPath implements Iterable<PointNode>, Collection<PointNode> {

	private final ArrayList<PointNode> data = new ArrayList();
	private final HashSet<Point> set = new HashSet();

	public boolean add(Point p) {
		PointNode pn = this.getLast();
		if (!this.isEmpty() && !this.validStep(pn.position, p))
			throw new IllegalArgumentException("You cannot step between nonadjacent points "+pn+" and "+p+"!");
		this.addPoint(pn, p);
		return true;
	}

	private void addPoint(PointNode from, Point p) {
		PointNode pn = new PointNode(p);
		data.add(pn);
		set.add(p);
		from.next = pn;
		pn.previous = from;
	}

	public int size() {
		return data.size();
	}

	public PointNode get(int idx) {
		return data.get(idx);
	}

	public boolean removeLast() {
		if (this.isEmpty())
			return false;
		PointNode rem = data.remove(data.size()-1);
		set.remove(rem.position);
		return true;
	}

	public boolean contains(Object p) {
		return set.contains(p);
	}

	public void clear() {
		data.clear();
		set.clear();
	}

	@Override
	public String toString() {
		return data.toString();
	}

	private boolean validStep(Point p1, Point p2) {
		return ReikaDirectionHelper.getDirectionBetween(p1, p2) != null;
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	private PointNode getLast() {
		return data.get(data.size()-1);
	}

	@Override
	public Iterator<PointNode> iterator() {
		return new ReadOnlyIterator(data);
	}

	public static class PointNode {

		private final Point position;

		private PointNode previous;
		private PointNode next;

		private PointNode(Point p) {
			position = p;
		}

		public PointNode getPrevious() {
			return previous;
		}

		public PointNode getNext() {
			return next;
		}

		public Point point() {
			return new Point(position.x, position.y);
		}

		@Override
		public int hashCode() {
			return position.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof PointNode && ((PointNode)o).position.equals(position);
		}

	}

	@Override
	public Object[] toArray() {
		return data.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return (T[])data.toArray(new PointNode[data.size()]);
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends PointNode> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean add(PointNode e) {
		throw new UnsupportedOperationException();
	}

}
