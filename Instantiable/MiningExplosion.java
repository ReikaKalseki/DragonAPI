/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class MiningExplosion extends Explosion {

	private World world;
	private final boolean dropCheap;

	private static final ArrayList<ItemStack> cheapBlocks = new ArrayList();

	public MiningExplosion(World par1World, Entity par2Entity, double par3, double par5, double par7, float par9, boolean drop_cheap) {
		super(par1World, par2Entity, par3, par5, par7, par9);
		world = par1World;
		dropCheap = drop_cheap;
	}

	@Override
	public void doExplosionA()
	{
		float f = explosionSize;
		HashSet hashset = new HashSet();
		int i;
		int j;
		int k;
		double d0;
		double d1;
		double d2;

		for (i = 0; i < 16; ++i)
		{
			for (j = 0; j < 16; ++j)
			{
				for (k = 0; k < 16; ++k)
				{
					if (i == 0 || i == 16 - 1 || j == 0 || j == 16 - 1 || k == 0 || k == 16 - 1)
					{
						double d3 = i / (16 - 1.0F) * 2.0F - 1.0F;
						double d4 = j / (16 - 1.0F) * 2.0F - 1.0F;
						double d5 = k / (16 - 1.0F) * 2.0F - 1.0F;
						double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
						d3 /= d6;
						d4 /= d6;
						d5 /= d6;
						float f1 = explosionSize * (0.7F + world.rand.nextFloat() * 0.6F);
						d0 = explosionX;
						d1 = explosionY;
						d2 = explosionZ;

						for (float f2 = 0.3F; f1 > 0.0F; f1 -= f2 * 0.75F)
						{
							int l = MathHelper.floor_double(d0);
							int i1 = MathHelper.floor_double(d1);
							int j1 = MathHelper.floor_double(d2);
							int k1 = world.getBlockId(l, i1, j1);

							if (k1 > 0)
							{
								Block block = Block.blocksList[k1];
								float f3 = exploder != null ? exploder.getBlockExplosionResistance(this, world, l, i1, j1, block) : block.getExplosionResistance(exploder, world, l, i1, j1, explosionX, explosionY, explosionZ);
								f1 -= (f3 + 0.3F) * f2;
							}

							if (f1 > 0.0F && (exploder == null || exploder.shouldExplodeBlock(this, world, l, i1, j1, k1, f1)))
							{
								hashset.add(new ChunkPosition(l, i1, j1));
							}

							d0 += d3 * f2;
							d1 += d4 * f2;
							d2 += d5 * f2;
						}
					}
				}
			}
		}

		affectedBlockPositions.addAll(hashset);
		explosionSize *= 2.0F;
		i = MathHelper.floor_double(explosionX - explosionSize - 1.0D);
		j = MathHelper.floor_double(explosionX + explosionSize + 1.0D);
		k = MathHelper.floor_double(explosionY - explosionSize - 1.0D);
		int l1 = MathHelper.floor_double(explosionY + explosionSize + 1.0D);
		int i2 = MathHelper.floor_double(explosionZ - explosionSize - 1.0D);
		int j2 = MathHelper.floor_double(explosionZ + explosionSize + 1.0D);
		List list = world.getEntitiesWithinAABBExcludingEntity(exploder, AxisAlignedBB.getAABBPool().getAABB(i, k, i2, j, l1, j2));
		Vec3 vec3 = world.getWorldVec3Pool().getVecFromPool(explosionX, explosionY, explosionZ);

		for (int k2 = 0; k2 < list.size(); ++k2)
		{
			Entity entity = (Entity)list.get(k2);
			double d7 = entity.getDistance(explosionX, explosionY, explosionZ) / explosionSize;

			if (d7 <= 1.0D)
			{
				d0 = entity.posX - explosionX;
				d1 = entity.posY + entity.getEyeHeight() - explosionY;
				d2 = entity.posZ - explosionZ;
				double d8 = MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);

				if (d8 != 0.0D)
				{
					d0 /= d8;
					d1 /= d8;
					d2 /= d8;
					double d9 = world.getBlockDensity(vec3, entity.boundingBox);
					double d10 = (1.0D - d7) * d9;
					if (this.canDamageEntity(entity))
						entity.attackEntityFrom(DamageSource.setExplosionSource(this), (int)((d10 * d10 + d10) / 2.0D * 8.0D * explosionSize + 1.0D));
					double d11 = EnchantmentProtection.func_92092_a(entity, d10);
					entity.motionX += d0 * d11;
					entity.motionY += d1 * d11;
					entity.motionZ += d2 * d11;

					if (entity instanceof EntityPlayer)
					{
						this.func_77277_b().put(entity, world.getWorldVec3Pool().getVecFromPool(d0 * d10, d1 * d10, d2 * d10));
					}
				}
			}
		}

		explosionSize = f;
	}

	@Override
	public void doExplosionB(boolean par1)
	{
		world.playSoundEffect(explosionX, explosionY, explosionZ, "random.explode", 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);

		if (explosionSize >= 2.0F && isSmoking)
		{
			world.spawnParticle("hugeexplosion", explosionX, explosionY, explosionZ, 1.0D, 0.0D, 0.0D);
		}
		else
		{
			world.spawnParticle("largeexplode", explosionX, explosionY, explosionZ, 1.0D, 0.0D, 0.0D);
		}

		Iterator iterator;
		ChunkPosition chunkposition;
		int i;
		int j;
		int k;
		int l;

		if (isSmoking) {
			iterator = affectedBlockPositions.iterator();

			while (iterator.hasNext()) {
				chunkposition = (ChunkPosition)iterator.next();
				i = chunkposition.x;
				j = chunkposition.y;
				k = chunkposition.z;
				l = world.getBlockId(i, j, k);

				if (par1) {
					double d0 = i + world.rand.nextFloat();
					double d1 = j + world.rand.nextFloat();
					double d2 = k + world.rand.nextFloat();
					double d3 = d0 - explosionX;
					double d4 = d1 - explosionY;
					double d5 = d2 - explosionZ;
					double d6 = MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
					d3 /= d6;
					d4 /= d6;
					d5 /= d6;
					double d7 = 0.5D / (d6 / explosionSize + 0.1D);
					d7 *= world.rand.nextFloat() * world.rand.nextFloat() + 0.3F;
					d3 *= d7;
					d4 *= d7;
					d5 *= d7;
					world.spawnParticle("explode", (d0 + explosionX * 1.0D) / 2.0D, (d1 + explosionY * 1.0D) / 2.0D, (d2 + explosionZ * 1.0D) / 2.0D, d3, d4, d5);
					world.spawnParticle("smoke", d0, d1, d2, d3, d4, d5);
				}

				if (l > 0) {
					Block block = Block.blocksList[l];

					int meta = world.getBlockMetadata(i, j, k);
					if ((l == Block.tnt.blockID || block.canDropFromExplosion(this)) && this.shouldDrop(block, meta)) {
						block.dropBlockAsItem(world, i, j, k, meta, 0);
					}

					//block.onBlockExploded(world, i, j, k, this);
					if (!world.isRemote)
						world.setBlock(i, j, k, 0);
				}
			}
		}

		if (isFlaming)
		{
			iterator = affectedBlockPositions.iterator();

			while (iterator.hasNext())
			{
				chunkposition = (ChunkPosition)iterator.next();
				i = chunkposition.x;
				j = chunkposition.y;
				k = chunkposition.z;
				l = world.getBlockId(i, j, k);
				int i1 = world.getBlockId(i, j - 1, k);

				if (l == 0 && Block.opaqueCubeLookup[i1] && new Random().nextInt(3) == 0)
				{
					world.setBlock(i, j, k, Block.fire.blockID);
				}
			}
		}
	}

	private boolean shouldDrop(Block block, int meta) {
		return dropCheap || !ReikaItemHelper.listContainsItemStack(cheapBlocks, new ItemStack(block.blockID, 1, meta));
	}

	public boolean canDamageEntity(Entity e) {
		return !(e instanceof EntityItem || e instanceof EntityXPOrb) && !(e instanceof EntityPlayer);
	}

	private static void addCheapBlock(Block b) {
		cheapBlocks.add(new ItemStack(b));
	}

	private static void addCheapBlock(Block b, int meta) {
		cheapBlocks.add(new ItemStack(b.blockID, 1, meta));
	}

	static {
		addCheapBlock(Block.grass);
		addCheapBlock(Block.dirt);
		addCheapBlock(Block.stone);
		addCheapBlock(Block.cobblestone);
		addCheapBlock(Block.sand);
		addCheapBlock(Block.netherrack);
		addCheapBlock(Block.gravel);
	}

}
