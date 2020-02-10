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
import java.util.ArrayList;

import com.google.common.collect.HashBiMap;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.CropHandlerBase;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class IC2RubberLogHandler extends CropHandlerBase {

	private static final IC2RubberLogHandler instance = new IC2RubberLogHandler();

	//meta%6: 0: hand-placed; 1: grown; 2+:

	public final Block logBlock;
	private final ItemStack resin;

	private final HashBiMap<ForgeDirection, Integer> directions = HashBiMap.create();

	private IC2RubberLogHandler() {
		super();

		if (!ModList.IC2.isLoaded()) {
			logBlock = null;
			resin = null;
			return;
		}

		logBlock = this.getFieldBlock("rubberWood");
		resin = this.getField("resin");

		directions.put(ForgeDirection.WEST, 4);
		directions.put(ForgeDirection.EAST, 5);
		directions.put(ForgeDirection.SOUTH, 3);
		directions.put(ForgeDirection.NORTH, 2);
	}

	private Block getFieldBlock(String s) {
		ItemStack is = this.getField(s);
		return is != null ? Block.getBlockFromItem(is.getItem()) : null;
	}

	private ItemStack getField(String s) {
		try {
			Class c = ModList.IC2.getItemClass();
			Field f = c.getDeclaredField(s);
			f.setAccessible(true);
			return (ItemStack)f.get(null);
		}
		catch (Exception e) {
			e.printStackTrace();
			DragonAPICore.logError("Exception for reading "+this.getMod()+"!");
			this.logFailure(e);
			return null;
		}
	}

	public static IC2RubberLogHandler getInstance() {
		return instance;
	}

	@Override
	public int getHarvestedMeta(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return meta+6;
	}

	@Override
	public boolean isCrop(Block id, int meta) {
		return id == logBlock && this.isValidMeta(meta);
	}

	private boolean isValidMeta(int meta) {
		return meta%6 >= 2;
	}

	@Override
	public boolean isRipeCrop(World world, int x, int y, int z) {
		return this.isRipeCrop(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
	}

	public boolean isRipeCrop(Block b, int meta) {
		return this.isValidMeta(meta) && meta < 6;
	}

	@Override
	public void makeRipe(World world, int x, int y, int z) {
		world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z)%6, 3);
	}

	@Override
	public int getGrowthState(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		if (!this.isValidMeta(meta))
			return -1;
		return meta < 6 ? 0 : 1;
	}

	@Override
	public boolean isSeedItem(ItemStack is) {
		return false;//resin != null && ReikaItemHelper.matchStacks(is, resin);
	}

	@Override
	public ArrayList<ItemStack> getDropsOverride(World world, int x, int y, int z, Block id, int meta, int fortune) {
		ArrayList<ItemStack> li = new ArrayList();
		li.add(ReikaItemHelper.getSizedItemStack(resin, 1+world.rand.nextInt(3)));
		return li;
	}

	@Override
	public ArrayList<ItemStack> getAdditionalDrops(World world, int x, int y, int z, Block id, int meta, int fortune) {
		return new ArrayList();
	}

	@Override
	public boolean neverDropsSecondSeed() {
		return true;
	}

	@Override
	public boolean initializedProperly() {
		return resin != null && logBlock != null;
	}

	@Override
	public ModList getMod() {
		return ModList.IC2;
	}
	/*
	@Override
	public CropFormat getShape() {
		return CropFormat.BLOCKSIDE;
	}*/

	public int getMeta(ForgeDirection dir) {
		return directions.get(dir);
	}

	public ForgeDirection getForMeta(int meta) {
		return directions.inverse().get(meta);
	}

}
