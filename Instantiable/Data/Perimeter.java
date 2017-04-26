/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalLineSegment;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Immutable.LineSegment;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public final class Perimeter {

	private final LinkedList<Coordinate> points = new LinkedList();
	private boolean allowVertical = true;

	private int minX = Integer.MAX_VALUE;
	private int maxX = Integer.MIN_VALUE;
	private int minY = Integer.MAX_VALUE;
	private int maxY = Integer.MIN_VALUE;
	private int minZ = Integer.MAX_VALUE;
	private int maxZ = Integer.MIN_VALUE;

	public Perimeter() {

	}

	public Perimeter disallowVertical() {
		allowVertical = false;
		return this;
	}

	public boolean isClosed() {
		return !points.isEmpty() && points.getFirst().equals(points.getLast());
	}

	public boolean hasCoordinate(int x, int y, int z) {
		return points.contains(new Coordinate(x, y, z));
	}

	public Collection<Coordinate> getPoints() {
		return Collections.unmodifiableCollection(points);
	}

	public boolean addPoint(int x, int y, int z) {
		if (this.isValidNextPoint(x, y, z)) {
			points.add(new Coordinate(x, y, z));
			this.setLimits(x, y, z);
			return true;
		}
		else {
			return false;
		}
	}

	public Perimeter addPointBeforeLast(int x, int y, int z) {
		if (this.isValidNextPoint(x, y, z)) {
			Coordinate loc = points.removeLast();
			points.add(new Coordinate(x, y, z));
			points.addLast(loc);
			this.setLimits(x, y, z);
		}
		else {

		}
		return this;
	}

	private final void setLimits(int x, int y, int z) {
		if (x < minX)
			minX = x;
		if (x > maxX)
			maxX = x;
		if (y < minY)
			minY = y;
		if (y > maxY)
			maxY = y;
		if (z < minZ)
			minZ = z;
		if (z > maxZ)
			maxZ = z;
	}

	private boolean isValidNextPoint(int x, int y, int z) {
		if (points.isEmpty())
			return true;
		Coordinate loc = points.get(points.size()-1);
		int dy = y-loc.yCoord;
		if (dy != 0 && !allowVertical)
			return false;
		int dx = x-loc.xCoord;
		int dz = z-loc.zCoord;
		if (!ReikaMathLibrary.nBoolsAreTrue(1, dx != 0, dy != 0, dz != 0))
			return false;
		return true;
	}

	public ArrayList<AxisAlignedBB> getAABBs() {
		ArrayList<AxisAlignedBB> li = new ArrayList();
		if (points.size() < 2)
			return li;
		for (int i = 1; i < points.size(); i++) {
			Coordinate loc1 = points.get(i-1);
			Coordinate loc2 = points.get(i);
			int x1 = loc1.xCoord;
			int y1 = loc1.yCoord;
			int z1 = loc1.zCoord;
			int x2 = loc2.xCoord+1;
			int y2 = loc2.yCoord+1;
			int z2 = loc2.zCoord+1;
			int xmin = Math.min(x1, x2);
			int ymin = Math.min(y1, y2);
			int zmin = Math.min(z1, z2);
			int xmax = Math.max(x1, x2);
			int ymax = Math.max(y1, y2);
			int zmax = Math.max(z1, z2);
			AxisAlignedBB box = AxisAlignedBB.getBoundingBox(xmin, ymin, zmin, xmax, ymax, zmax);
			li.add(box);
		}
		return li;
	}

	//TODO
	public ArrayList<AxisAlignedBB> getAreaAABBs() {
		ArrayList<AxisAlignedBB> li = new ArrayList();
		return li;
	}

	public AxisAlignedBB getCircumscribedBox() {
		return AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public Collection<DecimalPosition> findIntersections(double x, double y, double z) {
		DecimalLineSegment line = new DecimalLineSegment(minX-4, minY, minZ-4, x, y, z);
		Collection<DecimalPosition> c = new ArrayList();
		for (LineSegment l : this.getLineSegments()) {
			DecimalPosition p = line.findIntersection2D(l.asDecimalSegment());
			if (p != null)
				c.add(p);
		}
		return c;
	}

	public boolean isPointInside(double x, double y, double z) {
		return this.findIntersections(x, y, z).size()%2 == 1;
	}

	private ArrayList<LineSegment> getLineSegments() {
		ArrayList<LineSegment> li = new ArrayList();
		Coordinate last = null;
		for (Coordinate c : points) {
			if (last != null) {
				li.add(new LineSegment(last, c));
			}
			last = c;
		}
		if (this.isClosed()) {
			li.add(new LineSegment(points.getLast(), points.getFirst()));
		}
		return li;
	}

	public void writeToNBT(String name, NBTTagCompound NBT) {
		NBTTagList tag = new NBTTagList();
		for (int i = 0; i < points.size(); i++) {
			Coordinate loc = points.get(i);
			NBTTagCompound cpd = loc.writeToTag();
			tag.appendTag(cpd);
		}
		NBT.setTag(name, tag);
	}

	public void readFromNBT(String name, NBTTagCompound NBT) {
		NBTTagList tag = NBT.getTagList(name, NBTTypes.COMPOUND.ID);
		points.clear();
		if (tag != null) {
			for (int i = 0; i < tag.tagCount(); i++) {
				NBTTagCompound cpd = tag.getCompoundTagAt(i);
				Coordinate loc = Coordinate.readTag(cpd);
				points.add(loc);
			}
		}
	}

	public void appendPoint(int x, int y, int z) {
		points.add(new Coordinate(x, y, z));
	}

	public void prependPoint(int x, int y, int z) {
		points.add(0, new Coordinate(x, y, z));
	}

	public void clear() {
		points.clear();
	}

	@Override
	public String toString() {
		return points.toString();
	}

	public boolean isEmpty() {
		return points.isEmpty() || points.size() == 1;
	}

	public Perimeter copy() {
		Perimeter p = new Perimeter();
		p.points.addAll(points);
		p.allowVertical = allowVertical;
		return p;
	}

	public int segmentCount() {
		return Math.max(0, points.size()-1);
	}

	public Coordinate getSegmentPreCoord(int segment) {
		return points.get(segment);
	}

	public Coordinate getSegmentPostCoord(int segment) {
		return points.get(segment+1);
	}

}
