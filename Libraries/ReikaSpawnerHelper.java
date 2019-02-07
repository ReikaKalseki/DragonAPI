/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.util.List;

import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

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
	public static void setSpawnerFromItemNBT(ItemStack is, TileEntityMobSpawner spw, boolean keepLogic) {
		if (is == null)
			return;
		if (is.stackTagCompound == null)
			return;
		if (!is.stackTagCompound.hasKey("Spawner"))
			return;
		String name = is.stackTagCompound.getString("Spawner");
		if (keepLogic && is.stackTagCompound.hasKey("logic")) {
			MobSpawnerBaseLogic lgc = new ConstructableSpawnerLogic(spw);
			lgc.readFromNBT(is.stackTagCompound.getCompoundTag("logic"));
			setSpawnerLogic(spw, lgc);
		}
		setMobSpawnerMob(spw, name);
	}

	private static void setSpawnerLogic(TileEntityMobSpawner spw, MobSpawnerBaseLogic lgc) {
		NBTTagCompound tag = new NBTTagCompound();
		spw.writeToNBT(tag);
		lgc.writeToNBT(tag);
		spw.readFromNBT(tag);
	}

	public static void generateSpawnerTooltip(ItemStack is, List li) {
		if (is.stackTagCompound.hasKey("Spawner")) {
			li.add("Spawns "+ReikaEntityHelper.getEntityDisplayName(is.stackTagCompound.getString("Spawner")));
		}
		else {
			li.add("No entity data");
		}
		if (is.stackTagCompound.hasKey("logic")) {
			if (GuiScreen.isShiftKeyDown()) {
				MobSpawnerBaseLogic lgc = new TemporarySpawnerLogic();
				lgc.readFromNBT(is.stackTagCompound.getCompoundTag("logic"));
				li.add("Min Delay: "+EnumChatFormatting.WHITE+lgc.minSpawnDelay+" ticks");
				li.add("Max Delay: "+EnumChatFormatting.WHITE+lgc.maxSpawnDelay+" ticks");
				li.add("Max Near Mobs: "+EnumChatFormatting.WHITE+lgc.maxNearbyEntities);
				li.add("Spawn Count: "+EnumChatFormatting.WHITE+lgc.spawnCount);
				li.add("Spawn Range: "+EnumChatFormatting.WHITE+lgc.spawnRange+"m");
				li.add("Activation Range: "+EnumChatFormatting.WHITE+lgc.activatingRangeFromPlayer+"m");
			}
			else {
				li.add(EnumChatFormatting.LIGHT_PURPLE+"Hold LSHIFT for spawner parameters");
			}
		}
		else {
			li.add("Default spawn parameters");
		}
	}

	public static void setSpawnerItemNBT(ItemStack is, String mob, boolean force) {
		if (is.stackTagCompound == null)
			is.setTagCompound(new NBTTagCompound());
		if (force || !is.stackTagCompound.hasKey("Spawner")) {
			is.stackTagCompound.setString("Spawner", mob);
		}
	}

	public static void setSpawnerItemNBT(ItemStack is, int minDelay, int maxDelay, int maxNear, int spawnCount, int spawnRange, int activeRange, boolean force) {
		setSpawnerItemNBT(is, new TemporarySpawnerLogic(minDelay, maxDelay, maxNear, spawnCount, spawnRange, activeRange), force, true);
	}

	public static void setSpawnerItemNBT(ItemStack is, MobSpawnerBaseLogic lgc, boolean force, boolean keepLogic) {
		if (is.stackTagCompound == null)
			is.setTagCompound(new NBTTagCompound());
		if (force || !is.stackTagCompound.hasKey("Spawner")) {
			is.stackTagCompound.setString("Spawner", lgc.getEntityNameToSpawn());
		}
		if (keepLogic) {
			if (force || !is.stackTagCompound.hasKey("logic")) {
				NBTTagCompound tag = new NBTTagCompound();
				lgc.writeToNBT(tag);
				is.stackTagCompound.setTag("logic", tag);
			}
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

	public static boolean hasCustomLogic(TileEntityMobSpawner spw) {
		MobSpawnerBaseLogic lgc = spw.func_145881_a();
		return !isDefaultParams(lgc);
	}

	private static boolean isDefaultParams(MobSpawnerBaseLogic lgc) {
		TemporarySpawnerLogic lgc2 = new TemporarySpawnerLogic();
		return lgc.minSpawnDelay == lgc2.minSpawnDelay && lgc.maxSpawnDelay == lgc2.maxSpawnDelay && lgc.maxNearbyEntities == lgc2.maxNearbyEntities && lgc.spawnCount == lgc2.spawnCount && lgc.spawnRange == lgc2.spawnRange && lgc.activatingRangeFromPlayer == lgc2.activatingRangeFromPlayer;
	}

	private static class TemporarySpawnerLogic extends MobSpawnerBaseLogic {

		private TemporarySpawnerLogic() {
			//init with defaults
		}

		private TemporarySpawnerLogic(int minDelay, int maxDelay, int maxNear, int spawnCount, int spawnRange, int activeRange) {
			maxSpawnDelay = maxDelay;
			minSpawnDelay = minDelay;
			maxNearbyEntities = maxNear;
			this.spawnCount = spawnCount;
			this.spawnRange = spawnRange;
			activatingRangeFromPlayer = activeRange;
		}

		@Override
		public void func_98267_a(int p_98267_1_) {}

		@Override
		public World getSpawnerWorld() {
			return null;
		}

		@Override
		public int getSpawnerX() {
			return 0;
		}

		@Override
		public int getSpawnerY() {
			return 0;
		}

		@Override
		public int getSpawnerZ() {
			return 0;
		}

	}

	private static class ConstructableSpawnerLogic extends MobSpawnerBaseLogic {

		private final WorldLocation location;

		private ConstructableSpawnerLogic(TileEntity te) {
			location = new WorldLocation(te);
		}

		@Override
		public void func_98267_a(int val) {
			this.getSpawnerWorld().addBlockEvent(this.getSpawnerX(), this.getSpawnerY(), this.getSpawnerZ(), Blocks.mob_spawner, val, 0);
		}

		@Override
		public World getSpawnerWorld() {
			return location.getWorld();
		}

		@Override
		public int getSpawnerX() {
			return location.xCoord;
		}

		@Override
		public int getSpawnerY() {
			return location.yCoord;
		}

		@Override
		public int getSpawnerZ() {
			return location.zCoord;
		}

	}

}
