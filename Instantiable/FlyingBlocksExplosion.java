/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class FlyingBlocksExplosion extends Explosion {

	private World world;

	public FlyingBlocksExplosion(World world, Entity e, double x, double y, double z, float power) {
		super(world, e, x, y, z, power);
		this.world = world;
	}

	@Override
	public void doExplosionA() {
		int r = (int)explosionSize+2;
		int x = (int)Math.floor(explosionX);
		int y = (int)Math.floor(explosionY);
		int z = (int)Math.floor(explosionZ);

		List<EntityFallingBlock> li = new ArrayList();
		if (!world.isRemote) {
			for (int i = x-r; i <= x+r; i++) {
				for (int j = y-r; j <= y+r; j++) {
					for (int k = z-r; k <= z+r; k++) {
						Block b = world.getBlock(i, j, k);
						int meta = world.getBlockMetadata(i, j, k);
						if (this.canEntitize(world, i, j, k, b, meta)) {
							EntityFallingBlock e = new EntityFallingBlock(world, i, j, k, b, meta);
							li.add(e);
							e.field_145812_b = -10000;
							e.field_145813_c = false;
							world.setBlockToAir(i, j, k);
							world.spawnEntityInWorld(e);
						}
					}
				}
			}
		}

		super.doExplosionA();

		for (int i = 0; i < li.size(); i++) {
			EntityFallingBlock e = li.get(i);
			double dx = e.posX-explosionX;
			double dy = e.posY-explosionY;
			double dz = e.posZ-explosionZ;

			double dd = 0.3;
			double vy = 1.5;
			Random rand = new Random();

			e.motionX = dx*dd*rand.nextDouble();
			e.motionY = dy*dd*rand.nextDouble()+vy;
			e.motionZ = dz*dd*rand.nextDouble();

			e.velocityChanged = true;
		}
	}

	protected final boolean canEntitize(World world, int x, int y, int z, Block b, int meta) {
		if (b == Blocks.air)
			return false;
		if (b == Blocks.bedrock)
			return false;
		if (b.hasTileEntity(meta))
			return false;
		if (ReikaWorldHelper.softBlocks(world, x, y, z))
			return false;
		if (b.getRenderType() != 0) //To prevent weird looking flying sand entities
			return false;
		double dd = ReikaMathLibrary.py3d(x+0.5-explosionX, y+0.5-explosionY, z+0.5-explosionZ);
		return dd <= explosionSize+0.5;
	}

}
