/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.ParticleController;

import net.minecraft.entity.Entity;

import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Interfaces.PositionController;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;


public class PulsingSpherePositionController implements PositionController {

	public final int cycleLength;
	public final double innerRadius;
	public final double outerRadius;
	public final double theta;
	public final double phi;

	public final DecimalPosition center;
	private final double[] cartesian;
	private final double radiusDelta;
	private final double phase;

	private double currentRadius;

	public PulsingSpherePositionController(int l, double x, double y, double z, double r1, double r2, double theta, double phi) {
		this(l, x, y, z, r1, r2, theta, phi, calcPhase(theta, phi));
	}

	private static double calcPhase(double theta, double phi) {
		return phi/4D+theta/6D;
	}

	public PulsingSpherePositionController(int l, double x, double y, double z, double r1, double r2, double theta, double phi, double phase) {
		cycleLength = l;
		center = new DecimalPosition(x, y, z);
		innerRadius = r1;
		outerRadius = r2;
		this.theta = theta;
		this.phi = phi;
		radiusDelta = (r2-r1)/2D;
		cartesian = ReikaPhysicsHelper.polarToCartesian(1, theta, phi);
		this.phase = phase;
	}

	@Override
	public void update(Entity e) {
		currentRadius = innerRadius+radiusDelta+radiusDelta*Math.sin(phase+18*e.ticksExisted/(double)cycleLength);
	}

	@Override
	public double getPositionX(Entity e) {
		return center.xCoord+currentRadius*cartesian[0];
	}

	@Override
	public double getPositionY(Entity e) {
		return center.yCoord+currentRadius*cartesian[1];
	}

	@Override
	public double getPositionZ(Entity e) {
		return center.zCoord+currentRadius*cartesian[2];
	}

}
