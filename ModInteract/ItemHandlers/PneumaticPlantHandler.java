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
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.CropHandlerBase;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;

public class PneumaticPlantHandler extends CropHandlerBase {

	private final HashMap<Block, Plants> blockMap = new HashMap();
	private final ItemHashMap<Plants> itemMap = new ItemHashMap();

	public static enum Plants {
		SQUID(0, "squidPlant"),
		FIREFLOWER(1, "fireFlower"),
		CREEPER(2, "creeperPlant"),
		SLIME(3, "slimePlant"),
		RAIN(4, "rainPlant"),
		ENDER(5, "enderPlant"),
		LIGHTNING(6, "lightningPlant"),
		ADRENALINE(7, "adrenalinePlant"),
		BURST(8, "burstPlant"),
		POTION(9, "potionPlant"),
		REPULSION(10, "repulsionPlant"),
		HELIUM(11, "heliumPlant"),
		CHOPPER(12, "chopperPlant"),
		MUSIC(13, "musicPlant"),
		PROPULSION(14, "propulsionPlant"),
		FLYINGFLOWER(15, "flyingFlower");

		public final int seedMeta;
		private final String blockField;
		private Block block;
		private ItemStack seed;

		private static final Plants[] plantList = values();

		private Plants(int meta, String f) {
			seedMeta = meta;
			blockField = f;
		}

		public Block getBlock() {
			return block;
		}

		public ItemStack getSeed() {
			return seed.copy();
		}
	}

	private static final PneumaticPlantHandler instance = new PneumaticPlantHandler();

	public static PneumaticPlantHandler getInstance() {
		return instance;
	}

	private PneumaticPlantHandler() {
		if (this.hasMod()) {
			try {
				Class ic = this.getMod().getItemClass();
				Field seedf = ic.getDeclaredField("plasticPlant");
				Item seed = (Item)seedf.get(null);
				Class bc = this.getMod().getBlockClass();
				for (int i = 0; i < Plants.plantList.length; i++) {
					Plants p = Plants.plantList[i];
					Field f = bc.getDeclaredField(p.blockField);
					p.block = (Block)f.get(null);
					p.seed = new ItemStack(seed, 1, p.seedMeta);
					blockMap.put(p.block, p);
					itemMap.put(p.seed, p);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				this.logFailure(e);
			}
		}
		else {
			this.noMod();
		}
	}

	@Override
	public int getHarvestedMeta(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return meta <= 6 ? 0 : 7;
	}

	@Override
	public boolean isCrop(Block id, int meta) {
		return blockMap.containsKey(id);
	}

	@Override
	public boolean isRipeCrop(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return this.isCrop(world.getBlock(x, y, z), meta) && meta == this.getRipeMeta(meta);
	}

	@Override
	public void makeRipe(World world, int x, int y, int z) {
		world.setBlockMetadataWithNotify(x, y, z, this.getRipeMeta(world.getBlockMetadata(x, y, z)), 3);
	}

	private int getRipeMeta(int base_meta) {
		if (base_meta <= 6) {
			return 6;
		}
		else {
			return 13;
		}
	}

	@Override
	public boolean isSeedItem(ItemStack is) {
		return itemMap.containsKey(is);
	}

	@Override
	public ArrayList<ItemStack> getAdditionalDrops(World world, int x, int y, int z, Block id, int meta, int fortune) {
		return new ArrayList();
	}

	@Override
	public boolean initializedProperly() {
		return !blockMap.isEmpty() && !itemMap.isEmpty();
	}

	@Override
	public ModList getMod() {
		return ModList.PNEUMATICRAFT;
	}

	@Override
	public ArrayList<ItemStack> getDropsOverride(World world, int x, int y, int z, Block id, int meta, int fortune) {
		return null;
	}

	@Override
	public int getGrowthState(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z)%7;
	}

	@Override
	public boolean neverDropsSecondSeed() {
		return false;
	}

}
