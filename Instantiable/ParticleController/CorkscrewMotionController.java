/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.ParticleController;

import net.minecraft.entity.Entity;
import Reika.DragonAPI.Interfaces.PositionController;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;

//incomplete
public class CorkscrewMotionController implements PositionController {

	public final double originX;
	public final double originY;
	public final double originZ;

	public final double length;
	public final double theta;
	public final double phi;

	public final double linearSpeed;
	public final double angleSpeed;
	public final double spiralRadius;

	private double pos;
	private double angle;

	public CorkscrewMotionController(double x, double y, double z, double len, double th, double ph, double v, double va, double r) {
		originX = x;
		originY = y;
		originZ = z;

		linearSpeed = v;
		angleSpeed = va;
		spiralRadius = r;

		length = len;
		theta = th;
		phi = ph;
	}

	@Override
	public void update(Entity e) {
		angle += angleSpeed;
		pos += linearSpeed;
	}

	@Override
	public double getPositionX(Entity e) {
		return originX+ReikaPhysicsHelper.polarToCartesian(pos, theta, phi)[0]+spiralRadius*Math.cos(Math.toRadians(angle))*Math.cos(Math.toRadians(theta));
	}

	@Override
	public double getPositionY(Entity e) {
		return originY+ReikaPhysicsHelper.polarToCartesian(pos, theta, phi)[1]+spiralRadius*Math.sin(Math.toRadians(angle))*Math.sin(Math.toRadians(theta));
	}

	@Override
	public double getPositionZ(Entity e) {
		return originZ+ReikaPhysicsHelper.polarToCartesian(pos, theta, phi)[2]+spiralRadius*Math.cos(Math.toRadians(angle))*Math.cos(Math.toRadians(theta));
	}

}
