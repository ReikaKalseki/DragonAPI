/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Rendering;

import net.minecraft.block.Block;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.world.World;


public class EntityBlockTexFX extends EntityDiggingFX {

	public EntityBlockTexFX(World world, double x, double y, double z, double vx, double vy, double vz, Block b, int meta) {
		super(world, x, y, z, vx, vy, vz, b, meta);
		motionX = vx;
		motionY = vy;
		motionZ = vz;
	}

	public EntityBlockTexFX setGravity(float g) {
		particleGravity = g;
		return this;
	}

	public EntityBlockTexFX setScale(float s) {
		particleScale = s;
		return this;
	}

	public EntityBlockTexFX setLife(int l) {
		particleMaxAge = l;
		return this;
	}

	@Override
	public int getFXLayer()
	{
		return 2;
	}

	@Override
	public void onUpdate() {

		double vx = motionX;
		double vy = motionY;
		double vz = motionZ;
		super.onUpdate();
		motionX = vx;
		motionY = vy;
		motionZ = vz;

	}

}
