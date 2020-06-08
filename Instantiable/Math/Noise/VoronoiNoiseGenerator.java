package Reika.DragonAPI.Instantiable.Math.Noise;

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

	private static final int X_NOISE_GEN = 1619;
	private static final int Y_NOISE_GEN = 31337;
	private static final int Z_NOISE_GEN = 6971;
	private static final int SEED_NOISE_GEN = 1013;

	private static final double SQRT_3 = Math.sqrt(3);

	public boolean calculateDistance = false;

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
	protected double calcValue(double x, double y, double z, double f, double a) {
		if (f != 1 && f > 0) {
			x *= f;
			y *= f;
			z *= f;
		}

		int xInt = MathHelper.floor_double(x);
		int yInt = MathHelper.floor_double(y);
		int zInt = MathHelper.floor_double(z);

		double minDist = Double.POSITIVE_INFINITY;
		DecimalPosition candidate = null;

		// Inside each unit cube, there is a seed point at a random position.  Go
		// through each of the nearby cubes until we find a cube with a seed point
		// that is closest to the specified position.
		for (int zCur = zInt - 2; zCur <= zInt+2; zCur++) {
			for (int yCur = yInt - 2; yCur <= yInt+2; yCur++) {
				for (int xCur = xInt - 2; xCur <= xInt+2; xCur++) {

					// Calculate the position and distance to the seed point inside of
					// this unit cube.
					double xPos = xCur+this.ValueNoise3D(xCur, yCur, zCur, (int)seed);
					double yPos = yCur+this.ValueNoise3D(xCur, yCur, zCur, (int)seed+1);
					double zPos = zCur+this.ValueNoise3D(xCur, yCur, zCur, (int)seed+2);
					double xDist = xPos - x;
					double yDist = yPos - y;
					double zDist = zPos - z;
					double dist = xDist*xDist+yDist*yDist+zDist*zDist;

					if (candidate == null || dist < minDist) {
						// This seed point is closer to any others found so far, so record
						// this seed point.
						minDist = dist;
						candidate = new DecimalPosition(xPos, yPos, zPos);
					}
				}
			}
		}

		double value;
		if (calculateDistance) {
			// Determine the distance to the nearest seed point.
			double dist = candidate.getDistanceTo(x, y, z);
			value = dist*SQRT_3-1;
		}
		else {
			value = 0;
		}

		// Return the calculated distance with the displacement value applied.
		return value+this.ValueNoise3D(MathHelper.floor_double(candidate.xCoord), MathHelper.floor_double(candidate.yCoord), MathHelper.floor_double(candidate.zCoord), 0);
	}
}