/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.CropHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class HarvestCraftHandler extends CropHandlerBase {

	public final Block cropID;
	private final Item[] seedDrops;
	private final Item[] otherDrops;

	private static final Random rand = new Random();

	private final Field cropType;
	private final Field cropGrowth;

	public static final int RIPE = 2;

	private static final HarvestCraftHandler instance = new HarvestCraftHandler();

	private HarvestCraftHandler() {
		super();
		Block idcrop = null;
		Item[] seeds = null;
		Item[] drops = null;
		Field type = null;
		Field growth = null;
		if (this.hasMod()) {
			Class c = this.getMod().getBlockClass();
			try {
				Field f = c.getDeclaredField("pamCrop");
				Block crop = (Block)f.get(null);
				idcrop = crop;

				f = c.getDeclaredField("PamSeeds");
				seeds = (Item[])f.get(null);

				f = c.getDeclaredField("PamCropItems");
				drops = (Item[])f.get(null);
			}
			catch (NoSuchFieldException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: "+this.getMod()+" field not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Illegal access exception for reading "+this.getMod()+"!");
				e.printStackTrace();
			}
			catch (NullPointerException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
				e.printStackTrace();
			}
			try {
				c = Class.forName("assets.pamharvestcraft.TileEntityPamCrop");
				type = c.getDeclaredField("cropID");
				type.setAccessible(true);
				growth = c.getDeclaredField("growthStage");
				growth.setAccessible(true);
			}
			catch (ClassNotFoundException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: "+this.getMod()+" class not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (NoSuchFieldException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: "+this.getMod()+" field not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (NullPointerException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
				e.printStackTrace();
			}
		}
		else {
			this.noMod();
		}
		cropID = idcrop;
		seedDrops = seeds;
		otherDrops = drops;

		cropType = type;
		cropGrowth = growth;
	}

	@Override
	public boolean isCrop(Block id) {
		return id == cropID;
	}

	@Override
	public boolean isSeedItem(ItemStack is) {
		return false;
	}

	@Override
	public float getSecondSeedDropRate() {
		return 1;
	}

	@Override
	public boolean isRipeCrop(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (b == cropID) {
			TileEntity te = world.getTileEntity(x, y, z);
			try {
				int stage = cropGrowth.getInt(te);
				return stage == RIPE;
			}
			catch (Exception e) {}
		}
		return false;
	}

	public static HarvestCraftHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return cropID != null && seedDrops != null && otherDrops != null && cropType != null && cropGrowth != null;
	}

	@Override
	public ModList getMod() {
		return ModList.HARVESTCRAFT;
	}

	@Override
	public int getHarvestedMeta(World world, int x, int y, int z) {
		return 0;
	}

	@Override
	public ArrayList<ItemStack> getAdditionalDrops(World world, int x, int y, int z, Block id, int meta, int fortune) {
		ArrayList<ItemStack> li = new ArrayList();
		if (id == cropID) {
			TileEntity te = world.getTileEntity(x, y, z);
			int crop = -1;
			try {
				crop = cropType.getInt(te);
			}
			catch (Exception e) {}
			if (crop > -1) {
				int numcrops = rand.nextInt(3) + 2;
				int numseeds = rand.nextInt(2) + 1;

				li.add(new ItemStack(seedDrops[crop], numseeds, 0));
				li.add(new ItemStack(otherDrops[crop], numcrops, 0));
			}
		}
		return li;
	}

	@Override
	public void editTileDataForHarvest(World world, int x, int y, int z) {
		if (world.isRemote)
			return;
		Block b = world.getBlock(x, y, z);
		if (b == cropID) {
			TileEntity te = world.getTileEntity(x, y, z);
			try {
				cropGrowth.set(te, 0);
			}
			catch (Exception e) {}
		}
		world.func_147479_m(x, y, z);
		world.markBlockForUpdate(x, y, z);
	}

	@Override
	public void makeRipe(World world, int x, int y, int z) {
		if (world.isRemote)
			return;
		Block b = world.getBlock(x, y, z);
		if (b == cropID) {
			TileEntity te = world.getTileEntity(x, y, z);
			try {
				cropGrowth.set(te, RIPE);
			}
			catch (Exception e) {}
		}
		world.func_147479_m(x, y, z);
		world.markBlockForUpdate(x, y, z);
	}

}
