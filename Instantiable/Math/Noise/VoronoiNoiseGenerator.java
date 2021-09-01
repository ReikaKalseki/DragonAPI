package Reika.DragonAPI.Instantiable.Math.Noise;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import net.minecraft.util.MathHelper;

import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;

// voronoi.cpp
//
// Copyright (C) 2003, 2004 Jason Bevins
//
// This library is free software; you can redistribute it and/or modify it
// under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation; either version 2.1 of the License, or (at
// your option) any later version.
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
// License (COPYING.txt) for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this library; if not, write to the Free Software Foundation,
// Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//
// The developer's email is jlbezigvins@gmzigail.com (for great email, take
// off every 'zig'.)
//

/** Adapted from Libnoise, ported to Java and reworked for MC worldgen application.
  All '//' comments (including above) are theirs. */
public class VoronoiNoiseGenerator extends NoiseGeneratorBase {

	private static final int X_NOISE_GEN = 6971;
	private static final int Y_NOISE_GEN = 1013;
	private static final int Z_NOISE_GEN = 1619;
	private static final int SEED_NOISE_GEN = 31337;

	private static final double SQRT_3 = Math.sqrt(3);

	public boolean calculateDistance = false;
	public double randomFactor = 1;

	private final ArrayList<Root> candidateList = new ArrayList();

	public VoronoiNoiseGenerator(long seed) {
		super(seed);
	}

	private int IntValueNoise3D(int x, int y, int z, int rseed) {
		// All constants are primes and must remain prime in order for this noise
		// function to work correctly.
		int n = (X_NOISE_GEN*x+Y_NOISE_GEN*y+Z_NOISE_GEN*z+SEED_NOISE_GEN*rseed) & 0x7fffffff;
		n = (n >> 13) ^ n;
		return (n*(n*n*60493+19990303)+1376312589) & 0x7fffffff;
	}

	private double ValueNoise3D (int x, int y, int z, int rseed) {
		return 1D - this.IntValueNoise3D(x, y, z, rseed)/1073741824D;
	}

	@Override
	protected boolean displaceCalculation() {
		return false;
	}

	@Override
	protected double calcValue(double x, double y, double z, double f, double a) {
		if (f != 1 && f > 0) {
			x *= f;
			y *= f;
			z *= f;
		}

		int xInt = MathHelper.floor_double(x);
		int yInt = MathHelper.floor_double(y);
		int zInt = MathHelper.floor_double(z);

		candidateList.clear();

		// Inside each unit cube, there is a seed point at a random position.  Go
		// through each of the nearby cubes until we find a cube with a seed point
		// that is closest to the specified position.
		for (int zCur = zInt - 2; zCur <= zInt+2; zCur++) {
			for (int yCur = yInt - 2; yCur <= yInt+2; yCur++) {
				for (int xCur = xInt - 2; xCur <= xInt+2; xCur++) {

					// Calculate the position and distance to the seed point inside of
					// this unit cube.
					double xPos = xCur+randomFactor*this.ValueNoise3D(xCur, yCur, zCur, (int)seed);
					double yPos = yCur+randomFactor*this.ValueNoise3D(xCur, yCur, zCur, (int)seed+1);
					double zPos = zCur+randomFactor*this.ValueNoise3D(xCur, yCur, zCur, (int)seed+2);
					double xDist = xPos - x;
					double yDist = yPos - y;
					double zDist = zPos - z;
					double dist = xDist*xDist+yDist*yDist+zDist*zDist;

					// This seed point is closer to any others found so far, so record
					// this seed point.
					candidateList.add(new Root(xPos, yPos, zPos, dist));
				}
			}
		}

		Collections.sort(candidateList);
		Root candidate = candidateList.get(0);

		double value = 0;
		if (calculateDistance) {
			// Determine the distance to the nearest seed point.
			value = candidate.distance*SQRT_3-1;
		}

		// Return the calculated distance with the displacement value applied.
		return value+this.ValueNoise3D(MathHelper.floor_double(candidate.position.xCoord), MathHelper.floor_double(candidate.position.yCoord), MathHelper.floor_double(candidate.position.zCoord), 0);
	}

	public DecimalPosition getClosestRoot(double x, double y, double z) {
		double x0 = x;
		double y0 = y;
		double z0 = z;
		x += this.getXDisplacement(x0, y0, z0);
		y += this.getYDisplacement(x0, y0, z0);
		z += this.getZDisplacement(x0, y0, z0);
		this.getValue(x, y, z);
		DecimalPosition raw = candidateList.get(0).position;
		return new DecimalPosition(raw.xCoord/inputFactor, raw.yCoord/inputFactor, raw.zCoord/inputFactor);
	}

	public Collection<DecimalPosition> getNeighborCellsAt(double x, double y, double z) {
		x *= inputFactor;
		y *= inputFactor;
		z *= inputFactor;

		HashSet<DecimalPosition> ret = new HashSet();

		int xInt = MathHelper.floor_double(x);
		int yInt = MathHelper.floor_double(y);
		int zInt = MathHelper.floor_double(z);

		for (int zCur = zInt - 2; zCur <= zInt+2; zCur++) {
			for (int yCur = yInt - 2; yCur <= yInt+2; yCur++) {
				for (int xCur = xInt - 2; xCur <= xInt+2; xCur++) {

					double xPos = xCur+randomFactor*this.ValueNoise3D(xCur, yCur, zCur, (int)seed);
					double yPos = yCur+randomFactor*this.ValueNoise3D(xCur, yCur, zCur, (int)seed+1);
					double zPos = zCur+randomFactor*this.ValueNoise3D(xCur, yCur, zCur, (int)seed+2);
					ret.add(new DecimalPosition(xPos/inputFactor, yPos/inputFactor, zPos/inputFactor));
				}
			}
		}

		return ret;
	}

	public Collection<DecimalPosition> getCellsWithin3D(double x, double y, double z, double r) {
		x *= inputFactor;
		y *= inputFactor;
		z *= inputFactor;
		r *= inputFactor;

		HashSet<DecimalPosition> ret = new HashSet();

		int xInt = MathHelper.floor_double(x);
		int yInt = MathHelper.floor_double(y);
		int zInt = MathHelper.floor_double(z);
		int dr = MathHelper.ceiling_double_int(r+2);

		for (int zCur = zInt - dr; zCur <= zInt+dr; zCur++) {
			for (int yCur = yInt - dr; yCur <= yInt+dr; yCur++) {
				for (int xCur = xInt - dr; xCur <= xInt+dr; xCur++) {

					double xPos = xCur+randomFactor*this.ValueNoise3D(xCur, yCur, zCur, (int)seed);
					double yPos = yCur+randomFactor*this.ValueNoise3D(xCur, yCur, zCur, (int)-seed);
					double zPos = zCur+randomFactor*this.ValueNoise3D(xCur, yCur, zCur, (int)~seed);
					DecimalPosition d = new DecimalPosition(xPos, yPos, zPos);
					if (d.getDistanceTo(x, y, z) <= r)
						ret.add(new DecimalPosition(d.xCoord/inputFactor, d.yCoord/inputFactor, d.zCoord/inputFactor));
				}
			}
		}

		return ret;
	}

	public Collection<DecimalPosition> getCellsWithin2D(double x, double z, double r) {
		x *= inputFactor;
		z *= inputFactor;
		r *= inputFactor;

		HashSet<DecimalPosition> ret = new HashSet();

		int xInt = MathHelper.floor_double(x);
		int zInt = MathHelper.floor_double(z);
		int dr = MathHelper.ceiling_double_int(r+2);

		for (int zCur = zInt - dr; zCur <= zInt+dr; zCur++) {
			for (int xCur = xInt - dr; xCur <= xInt+dr; xCur++) {

				double xPos = xCur+randomFactor*this.ValueNoise3D(xCur, 0, zCur, (int)seed);
				double zPos = zCur+randomFactor*this.ValueNoise3D(xCur, 0, zCur, (int)~seed);
				DecimalPosition d = new DecimalPosition(xPos, 0, zPos);
				if (d.getDistanceTo(x, 0, z) <= r)
					ret.add(new DecimalPosition(d.xCoord/inputFactor, 0, d.zCoord/inputFactor));
			}
		}

		return ret;
	}

	/** RAW distance */
	public double getEdgeRatio(double x, double z) {

		//DecimalPosition closest = this.getClosestRoot(x, 0, z);
		//x = closest.xCoord;
		//z = closest.zCoord;

		this.getValue(x, 0, z);

		x += this.getXDisplacement(x, 0, z);
		z += this.getZDisplacement(x, 0, z);

		DecimalPosition candidate1 = candidateList.get(0).position;
		DecimalPosition candidate2 = candidateList.get(1).position;

		double cx = (candidate1.xCoord+candidate2.xCoord)/(2*inputFactor);
		double cy = (candidate1.zCoord+candidate2.zCoord)/(2*inputFactor);
		double dx = (candidate2.xCoord-candidate1.xCoord)/inputFactor;
		double dy = (candidate2.zCoord-candidate1.zCoord)/inputFactor;
		double invslope = -dx/dy;
		double mpx = cx+1;
		double mpy = cy+invslope;
		double dLx = mpx-cx;
		double dLy = mpy-cy;

		// dist from point to line defined by two points: https://wikimedia.org/api/rest_v1/media/math/render/svg/be2ab4a9d9d77f1623a2723891f652028a7a328d
		double num = dLy*x-dLx*z+mpx*cy-mpy*cx;
		double denom = dLx*dLx+dLy*dLy;
		double dist = Math.abs(num)/Math.sqrt(denom);
		return dist;

		/*
		double xCandidate = candidate1.xCoord;
		double yCandidate = candidate1.zCoord;
		double xCandidate2 = candidate2.xCoord;
		double yCandidate2 = candidate2.zCoord;

		double midX = (xCandidate+xCandidate2)/2D;
		double midY = (yCandidate+yCandidate2)/2D;
		double dx = xCandidate2-xCandidate;
		double dy = yCandidate2-yCandidate;
		double invslope = -dx/dy;
		double midp2X = midX+1;
		double midp2Y = midY+invslope;
		double dLx = midp2X-midX;
		double dLy = midp2Y-midY;

		double num = dLy*x*inputFactor-dLx*z*inputFactor+midp2X*midY-midp2Y*midX;
		double denom = dLx*dLx+dLy*dLy;
		double dist = Math.abs(num)/Math.sqrt(denom);

		// Return the calculated distance with the displacement value applied.
		return dist;*/
	}

	/** Chunk is in BLOCK coords! */
	public boolean chunkContainsCenter(int x, int z) {
		DecimalPosition pos = this.getClosestRoot(x, 0, z);
		return pos.xCoord >= x && pos.zCoord >= z && pos.xCoord < x+16 && pos.yCoord < z+16;
	}

	private static final class Root implements Comparable<Root> {

		private final DecimalPosition position;
		private final double distance;

		private Root(double x, double y, double z, double d) {
			position = new DecimalPosition(x, y, z);
			distance = d;
		}

		@Override
		public int compareTo(Root o) {
			return Double.compare(distance, o.distance);
		}

	}
}