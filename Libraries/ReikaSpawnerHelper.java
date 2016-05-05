/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

public class ReikaSpawnerHelper {

	/** Returns a mob spawner's type name. Args: Spawner TileEntity */
	public static String getMobSpawnerMobName(TileEntityMobSpawner spw) {
		return spw.func_145881_a().getEntityNameToSpawn();
	}

	/** Sets a mob spawner type. Args: Spawner TileEntity, Name */
	public static void setMobSpawnerMob(TileEntityMobSpawner spw, String name) {
		spw.func_145881_a().setEntityName(name);
	}

	/** Returns a mob spawner's type name. Args: World, x, y, z */
	public static String getSpawnerTypeName(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (!(te instanceof TileEntityMobSpawner))
			return null;
		return getMobSpawnerMobName((TileEntityMobSpawner)te);
	}

	/** Returns a mob spawner's entity ID. Args: World, x, y, z */
	public static int getSpawnerTypeID(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (!(te instanceof TileEntityMobSpawner))
			return -1;
		TileEntityMobSpawner spw = (TileEntityMobSpawner)te;
		String name = getMobSpawnerMobName(spw);
		return ReikaEntityHelper.mobNameToID(name);
	}

	/** Copies a spawner's spawn type to an Items. Args: Item, Spawner TileEntity */
	public static void addMobNBTToItem(ItemStack is, TileEntityMobSpawner spw) {
		if (is == null)
			return;
		String name = getMobSpawnerMobName(spw);
		setSpawnerItemNBT(is, name, false);
	}

	/** Sets a spawner's spawn type from an Items. Args: Item, Spawner TileEntity */
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

	public static void setSpawnerItemNBT(ItemStack is, String mob, boolean force) {
		if (is.stackTagCompound == null)
			is.setTagCompound(new NBTTagCompound());
		if (force || !is.stackTagCompound.hasKey("Spawner")) {
			is.stackTagCompound.setString("Spawner", mob);
		}
	}

	/** Forcibly runs the spawn cycle for a spawner. Args: Spawner TileEntity, number of mobs, Potion effects */
	public static void forceSpawn(TileEntityMobSpawner spw, int num, PotionEffect... potions) {
		World world = spw.worldObj;
		if (world.isRemote)
			return;
		String name = getMobSpawnerMobName(spw);
		for (int i = 0; i < num; i++) {
			Entity e = EntityList.createEntityByName(name, world);
			double ex = ReikaRandomHelper.getRandomPlusMinus(spw.xCoord+0.5, 3.5D);
			double ez = ReikaRandomHelper.getRandomPlusMinus(spw.zCoord+0.5, 3.5D);
			double ey = ReikaRandomHelper.getRandomPlusMinus(spw.yCoord+0.5, 1.5D);
			e.setPositionAndRotation(ex, ey, ez, 0, 0);
			if (e instanceof EntityLivingBase && potions != null) {
				for (int m = 0; m < potions.length; m++)
					((EntityLivingBase)e).addPotionEffect(potions[m]);
			}
			if (e instanceof EntityLivingBase && e.worldObj != null)
				((EntityLiving)e).onSpawnWithEgg((IEntityLivingData)null);
			world.spawnEntityInWorld(e);
		}
	}

	/** Gets a spawner type from an Items. Args: Item */
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
