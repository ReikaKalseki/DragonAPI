/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;

public class ReikaSpawnerHelper {

	public static String getMobSpawnerMobName(TileEntityMobSpawner spw) {
		return spw.func_98049_a().getEntityNameToSpawn();
	}

	public static void setMobSpawnerMob(TileEntityMobSpawner spw, String name) {
		spw.func_98049_a().setMobID(name);
	}

	public static String getSpawnerTypeName(World world, int x, int y, int z) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (!(te instanceof TileEntityMobSpawner))
			return null;
		return getMobSpawnerMobName((TileEntityMobSpawner)te);
	}

	public static int getSpawnerTypeID(World world, int x, int y, int z) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (!(te instanceof TileEntityMobSpawner))
			return -1;
		TileEntityMobSpawner spw = (TileEntityMobSpawner)te;
		String name = getMobSpawnerMobName(spw);
		return ReikaEntityHelper.mobNameToID(name);
	}

	public static void addMobNBTToItem(ItemStack is, TileEntityMobSpawner spw) {
		if (is == null)
			return;
		String name = getMobSpawnerMobName(spw);
		if (is.stackTagCompound == null)
			is.setTagCompound(new NBTTagCompound());
		if (!is.stackTagCompound.hasKey("Spawner")) {
			is.stackTagCompound.setString("Spawner", name);
		}
	}

	public static void setSpawnerFromItemNBT(ItemStack is, TileEntityMobSpawner spw) {
		if (is == null)
			return;
		if (is.stackTagCompound == null)
			return;
		if (!is.stackTagCompound.hasKey("Spawner"))
			return;
		String name = is.stackTagCompound.getString("Spawner");
		setMobSpawnerMob(spw, name);
	}

	public static void forceSpawn(TileEntityMobSpawner spw, World world, int num, PotionEffect... potions) {
		if (world.isRemote)
			return;
		Random r = new Random();
		String name = getMobSpawnerMobName(spw);
		for (int i = 0; i < num; i++) {
			Entity e = EntityList.createEntityByName(name, world);
			double ex = -8+r.nextDouble()*17+spw.xCoord;
			double ez = -8+r.nextDouble()*17+spw.zCoord;
			double ey = spw.yCoord;
			int id = world.getBlockId((int)ex, (int)ey, (int)ez);
			while (id != 0) {
				ex = -8+r.nextDouble()*17+spw.xCoord;
				ez = -8+r.nextDouble()*17+spw.zCoord;
				ey = spw.yCoord;
				id = world.getBlockId((int)ex, (int)ey, (int)ez);
			}
			e.setPositionAndRotation(ex, ey, ez, 0, 0);
			if (e instanceof EntityLiving && potions != null) {
				for (int m = 0; m < potions.length; m++)
					((EntityLiving)e).addPotionEffect(potions[m]);
			}
			world.spawnEntityInWorld(e);
		}
	}

	public static String getSpawnerFromItemNBT(ItemStack is) {
		if (is == null)
			return null;
		if (is.stackTagCompound == null)
			return null;
		if (!is.stackTagCompound.hasKey("Spawner"))
			return null;
		String name = is.stackTagCompound.getString("Spawner");
		return name;
	}

}
