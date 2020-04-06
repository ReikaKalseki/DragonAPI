/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.ItemHandlers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.CropHandlerBase;

public class FluxedCrystalHandler extends CropHandlerBase {

	public final Item seed;
	public final Item universalSeed;
	public final Item roughShard;
	public final Item smoothShard;

	public final Block crop;

	private final int configChance;

	private Method doDrop;

	private static final FluxedCrystalHandler instance = new FluxedCrystalHandler();

	private FluxedCrystalHandler() {
		super();
		Block idcrop = null;
		Item univ = null;
		Item seed = null;
		Item rough = null;
		Item smooth = null;
		int chance = -1;
		if (this.hasMod()) {
			try {
				Class c = this.getMod().getBlockClass();
				Field f = c.getField("crystal");
				idcrop = (Block)f.get(null);

				Class c2 = this.getMod().getItemClass();
				f = c2.getField("universalSeed");
				univ = (Item)f.get(null);

				f = c2.getField("seed");
				seed = (Item)f.get(null);

				f = c2.getField("shardRough");
				rough = (Item)f.get(null);

				f = c2.getField("shardSmooth");
				smooth = (Item)f.get(null);

				doDrop = idcrop.getClass().getMethod("dropCropDrops", World.class, int.class, int.class, int.class, int.class, boolean.class);

				//Class c3 = Class.forName("fluxedCrystals.config.ConfigProps");
			}/*
			catch (ClassNotFoundException e) {
				DragonAPICore.log("DRAGONAPI: "+this.getMod()+" class not found! "+e.getMessage());
				e.printStackTrace();
			}*/
			catch (NoSuchFieldException e) {
				DragonAPICore.logError(this.getMod()+" field not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				DragonAPICore.logError("Illegal access exception for reading "+this.getMod()+"!");
				e.printStackTrace();
			}
			catch (NullPointerException e) {
				DragonAPICore.logError("Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
				e.printStackTrace();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			this.noMod();
		}
		crop = idcrop;
		this.seed = seed;
		universalSeed = univ;
		roughShard = rough;
		smoothShard = smooth;
		configChance = chance >= 0 ? chance : 0;
	}

	@Override
	public boolean isCrop(Block id, int meta) {
		return id == crop;
	}

	@Override
	public boolean isSeedItem(ItemStack is) {
		return is.getItem() == seed;
	}
	/*
	@Override
	public float getSecondSeedDropRate() {
		return configChance/100F;
	}*/

	@Override
	public boolean isRipeCrop(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		return this.isCrop(b, meta) && meta == 7;
	}

	@Override
	public void makeRipe(World world, int x, int y, int z) {
		world.setBlockMetadataWithNotify(x, y, z, 7, 3);
	}

	public static FluxedCrystalHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return seed != null && crop != null && universalSeed != null && roughShard != null && smoothShard != null;
	}

	@Override
	public ModList getMod() {
		return ModList.FLUXEDCRYSTALS;
	}

	@Override
	public int getHarvestedMeta(World world, int x, int y, int z) {
		return 0;
	}

	@Override
	public ArrayList<ItemStack> getAdditionalDrops(World world, int x, int y, int z, Block id, int meta, int fortune) {
		ArrayList<ItemStack> li = new ArrayList();
		return li;
	}

	@Override
	public ArrayList<ItemStack> getDropsOverride(World world, int x, int y, int z, Block id, int meta, int fortune) {
		this.triggerDropCode(world, x, y, z, id, meta, fortune);
		return new ArrayList();
	}

	private void triggerDropCode(World world, int x, int y, int z, Block id, int meta, int fortune) {
		boolean secondSeed = rand.nextInt(100) <= configChance;
		try {
			doDrop.invoke(id, world, x, y, z, fortune, secondSeed);
		}
		catch (Exception e) {

		}
	}

	@Override
	public int getGrowthState(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}

	@Override
	public boolean neverDropsSecondSeed() {
		return configChance == 0;
	}

	@Override
	public boolean isTileEntity() {
		return false;
	}

}
