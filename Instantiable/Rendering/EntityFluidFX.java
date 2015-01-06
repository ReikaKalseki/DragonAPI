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

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public class EntityFluidFX extends EntityFX {

	private Fluid type;

	public EntityFluidFX(World world, double x, double y, double z, Fluid f) {
		this(world, x, y, z, 0, 0, 0, f);
	}

	public EntityFluidFX(World world, double x, double y, double z, double vx, double vy, double vz, Fluid f) {
		super(world, x, y, z, vx, vy, vz);
		particleIcon = f.getStillIcon();
		particleScale = 1F;
		type = f;
		motionX = vx;
		motionY = vy;
		motionZ = vz;
	}

	@Override
	public int getBrightnessForRender(float par1)
	{
		return type.getLuminosity() > 12 ? 240 : super.getBrightnessForRender(par1);
	}

	@Override
	public int getFXLayer()
	{
		return 2;
	}

}
