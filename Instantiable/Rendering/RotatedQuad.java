/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Rendering;

import java.util.Arrays;

import net.minecraft.util.Vec3;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;


public class RotatedQuad {

	private final double[][] points = new double[4][2];

	public RotatedQuad(double r1, double r2, double r3, double r4, double rot) {
		double[][] p = new double[][]{
				{-r1, -r1},
				{+r2, -r2},
				{+r3, +r3},
				{-r4, +r4},
		};

		for (int i = 0; i < 4; i++) {
			Vec3 vec3 = Vec3.createVectorHelper(p[i][0], 0, p[i][1]);
			vec3 = ReikaVectorHelper.rotateVector(vec3, 0, rot, 0);
			p[i][0] = vec3.xCoord;
			p[i][1] = vec3.zCoord;
		}

		for (int i = 0; i < 4; i++)
			points[i] = p[i];


	}

	public double getPosX(int corner) {
		return points[corner][0];
	}

	public double getPosZ(int corner) {
		return points[corner][1];
	}

	@Override
	public String toString() {
		return Arrays.toString(points);
	}

}
