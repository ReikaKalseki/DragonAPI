/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
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
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.Block.SemiUnbreakable;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class FlyingBlocksExplosion extends Explosion {

	private World world;

	private TumbleCreator tumbleCreator = null;

	public boolean canBreakBedrock = false;

	public FlyingBlocksExplosion(WorldLocation loc, float power) {
		this(loc.getWorld(), loc.xCoord+0.5, loc.yCoord+0.5, loc.zCoord+0.5, power);
	}

	public FlyingBlocksExplosion(TileEntity loc, float power) {
		this(loc.worldObj, loc.xCoord+0.5, loc.yCoord+0.5, loc.zCoord+0.5, power);
	}

	public FlyingBlocksExplosion(World world, double x, double y, double z, float power) {
		super(world, null, x, y, z, power);
		this.world = world;
	}

	public void doExplosion() {
		this.doExplosionA();
		this.doExplosionB(true);
	}

	public FlyingBlocksExplosion setTumbling(TumbleCreator c) {
		tumbleCreator = c;
		return this;
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
						Effect e = this.calcEffect(world, i, j, k, b, meta);
						e.trigger(world, i, j, k, b, meta, tumbleCreator, li);
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

	protected final Effect calcEffect(World world, int x, int y, int z, Block b, int meta) {
		Effect e = this.getEffect(world, x, y, z, b, meta);
		if (e == Effect.NOTHING)
			return e;
		double dd = ReikaMathLibrary.py3d(x+0.5-explosionX, y+0.5-explosionY, z+0.5-explosionZ);
		return dd <= explosionSize+0.5 ? e : Effect.NOTHING;
	}

	protected final Effect getEffect(World world, int x, int y, int z, Block b, int meta) {
		if (b == Blocks.air)
			return Effect.NOTHING;
		if (b == Blocks.bedrock)
			return canBreakBedrock ? Effect.ENTITIZE : Effect.NOTHING;
		if (b.blockHardness < 0)
			return canBreakBedrock ? Effect.ENTITIZE : Effect.NOTHING;
		if (b instanceof SemiUnbreakable)
			return ((SemiUnbreakable)b).isUnbreakable(world, x, y, z, meta) ? Effect.NOTHING : Effect.ENTITIZE;
		if (b.hasTileEntity(meta))
			return Effect.NOTHING;
		if (ReikaWorldHelper.softBlocks(world, x, y, z))
			return Effect.BREAK;
		if (b.getRenderType() != 0 && !b.renderAsNormalBlock() && !b.isOpaqueCube()) { //To prevent weird looking flying sand entities
			;//return false;
		}
		return Effect.ENTITIZE;
	}

	public static interface TumbleCreator {

		public EntityTumblingBlock createBlock(World world, int x, int y, int z, Block b, int meta);

	}

	private static enum Effect {
		ENTITIZE(),
		BREAK(),
		NOTHING();

		public void trigger(World world, int x, int y, int z, Block b, int meta, TumbleCreator c, List<EntityFallingBlock> li) {
			switch(this) {
				case BREAK:
					ReikaWorldHelper.dropAndDestroyBlockAt(world, x, y, z, null, true, false);
					break;
				case ENTITIZE:
					EntityFallingBlock e = c != null ? c.createBlock(world, x, y, z, b, meta) : new EntityFallingBlock(world, x, y, z, b, meta);
					li.add(e);
					e.field_145812_b = -10000;
					e.field_145813_c = false;
					world.setBlockToAir(x, y, z);
					world.spawnEntityInWorld(e);
					break;
				case NOTHING:
				default:
					break;
			}
		}
	}

}
