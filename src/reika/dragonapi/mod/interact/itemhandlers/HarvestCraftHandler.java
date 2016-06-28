/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.mod.interact.itemhandlers;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.ModList;
import reika.dragonapi.base.CropHandlerBase;

public class HarvestCraftHandler extends CropHandlerBase {

	private static final Random rand = new Random();

	//private final Field cropType;
	//private final Field cropGrowth;

	//private static final EnumMap<CropType, Block> saplings = new EnumMap(CropType.class);
	//private static final EnumMap<CropType, Block> blocks = new EnumMap(CropType.class);
	//private static final EnumMap<CropType, ItemStack> seeds = new EnumMap(CropType.class);
	//private static final EnumMap<CropType, ItemStack> drops = new EnumMap(CropType.class);

	private static final int RIPE = 7;

	private static final HarvestCraftHandler instance = new HarvestCraftHandler();

	private final Class cropClass;

	/*
	public static enum CropType {
		APPLE(CropClass.TREE),
		ALMOND(CropClass.TREE),
		APRICOT(CropClass.TREE),
		AVOCADO(CropClass.TREE),
		BANANA(CropClass.TREE),
		CASHEW(CropClass.TREE),
		CHERRY(CropClass.TREE),
		CHESTNUT(CropClass.TREE),
		CINNAMON(CropClass.TREE),
		COCONUT(CropClass.TREE),
		DATE(CropClass.TREE),
		DRAGONFRUIT(CropClass.TREE),
		DURIAN(CropClass.TREE),
		FIG(CropClass.TREE),
		GRAPEFRUIT(CropClass.TREE),
		LEMON(CropClass.TREE),
		LIME(CropClass.TREE),
		MAPLE(CropClass.TREE),
		MANGO(CropClass.TREE),
		NUTMEG(CropClass.TREE),
		OLIVE(CropClass.TREE),
		ORANGE(CropClass.TREE),
		PAPAYA(CropClass.TREE),
		PAPERBARK(CropClass.TREE),
		PEACH(CropClass.TREE),
		PEAR(CropClass.TREE),
		PECAN(CropClass.TREE),
		PEPPERCORN(CropClass.TREE),
		PERSIMMON(CropClass.TREE),
		PISTACHIO(CropClass.TREE),
		PLUM(CropClass.TREE),
		POMEGRANATE(CropClass.TREE),
		STARFRUIT(CropClass.TREE),
		VANILLABEAN(CropClass.TREE),
		WALNUT(CropClass.TREE),
		BLACKBERRY(CropClass.FARM),
		BLUEBERRY(CropClass.FARM),
		CANDLEBERRY(CropClass.FARM),
		RASPBERRY(CropClass.FARM),
		STRAWBERRY(CropClass.FARM),
		CACTUSFRUIT(CropClass.FARM),
		ASPARAGUS(CropClass.FARM),
		BARLEY(CropClass.FARM),
		OATS(CropClass.FARM),
		RYE(CropClass.FARM),
		CORN(CropClass.FARM),
		BAMBOOSHOOT(CropClass.FARM),
		CANTALOUPE(CropClass.FARM),
		CUCUMBER(CropClass.FARM),
		WINTERSQUASH(CropClass.FARM),
		ZUCCHINI(CropClass.FARM),
		BEET(CropClass.FARM),
		ONION(CropClass.FARM),
		PARSNIP(CropClass.FARM),
		PEANUT(CropClass.FARM),
		RADISH(CropClass.FARM),
		RUTABAGA(CropClass.FARM),
		SWEETPOTATO(CropClass.FARM),
		TURNIP(CropClass.FARM),
		RHUBARB(CropClass.FARM),
		CELERY(CropClass.FARM),
		GARLIC(CropClass.FARM),
		GINGER(CropClass.FARM),
		SPICELEAF(CropClass.FARM),
		TEALEAF(CropClass.FARM),
		COFFEEBEAN(CropClass.FARM),
		MUSTARDSEEDS(CropClass.FARM),
		BROCCOLI(CropClass.FARM),
		CAULIFLOWER(CropClass.FARM),
		LEEK(CropClass.FARM),
		LETTUCE(CropClass.FARM),
		SCALLION(CropClass.FARM),
		ARTICHOKE(CropClass.FARM),
		BRUSSELSPROUT(CropClass.FARM),
		CABBAGE(CropClass.FARM),
		WHITEMUSHROOM(CropClass.FARM),
		BEAN(CropClass.FARM),
		SOYBEAN(CropClass.FARM),
		BELLPEPPER(CropClass.FARM),
		CHILIPEPPER(CropClass.FARM),
		EGGPLANT(CropClass.FARM),
		OKRA(CropClass.FARM),
		PEAS(CropClass.FARM),
		TOMATO(CropClass.FARM),
		COTTON(CropClass.FARM),
		PINEAPPLE(CropClass.FARM),
		GRAPE(CropClass.FARM),
		KIWI(CropClass.FARM),
		CRANBERRY(CropClass.FARM),
		RICE(CropClass.FARM),
		SEAWEED(CropClass.FARM);

		public final CropClass type;

		private CropType(CropClass c) {
			type = c;
		}

		public Block getSapling() {
			return saplings.get(this);
		}

		public Block getBlock() {
			return blocks.get(this);
		}

		public ItemStack getSeed() {
			ItemStack is = seeds.get(this);
			return is != null ? is.copy() : null;
		}

		public ItemStack getDrops() {
			ItemStack is = drops.get(this);
			return is != null ? is.copy() : null;
		}
	}

	private static enum CropClass {
		TREE(),
		FARM();
	}*/

	private HarvestCraftHandler() {
		super();
		Class c = null;
		if (this.hasMod()) {
			//Class c1 = this.getMod().getBlockClass();
			//Class c2 = this.getMod().getItemClass();
			try {
				/*
				Field f = c1.getDeclaredField("pamCrop");
				Block crop = (Block)f.get(null);
				idcrop = crop;

				f = c1.getDeclaredField("PamSeeds");
				seeds = (Item[])f.get(null);

				f = c1.getDeclaredField("PamCropItems");
				drops = (Item[])f.get(null);*/

				c = Class.forName("com.pam.harvestcraft.BlockPamCrop");
			}
			catch (ClassNotFoundException e) {
				DragonAPICore.logError(this.getMod()+" class not found! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (NullPointerException e) {
				DragonAPICore.logError("Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
				e.printStackTrace();
				this.logFailure(e);
			}
		}
		else {
			this.noMod();
		}
		//cropID = idcrop;
		//seedDrops = seeds;
		//otherDrops = drops;

		//cropType = type;
		//cropGrowth = growth;
		cropClass = c;
	}

	@Override
	public boolean isCrop(Block id, int meta) {
		return id.getClass() == cropClass;
	}

	@Override
	public boolean isSeedItem(ItemStack is) {
		return false;
	}

	@Override
	public boolean isRipeCrop(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return this.isCrop(world.getBlock(x, y, z), meta) && meta >= 7;
	}

	public static HarvestCraftHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return cropClass != null;
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
		ArrayList<ItemStack> li = new ArrayList();/*
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
		}*/
		return li;
	}

	@Override
	public void editTileDataForHarvest(World world, int x, int y, int z) {/*
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
		world.markBlockForUpdate(x, y, z);*/
	}

	@Override
	public void makeRipe(World world, int x, int y, int z) {/*
		if (world.isRemote)
			return;
		Block b = world.getBlock(x, y, z);
		if (b == cropID) {
			TileEntity te = world.getTileEntity(x, y, z);
			try {
				cropGrowth.set(te, RIPE);
			}
			catch (Exception e) {}
		}*/
		world.setBlockMetadataWithNotify(x, y, z, RIPE, 3);
		world.func_147479_m(x, y, z);
		world.markBlockForUpdate(x, y, z);
	}

	@Override
	public ArrayList<ItemStack> getDropsOverride(World world, int x, int y, int z, Block id, int meta, int fortune) {
		return null;
	}

	@Override
	public int getGrowthState(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}

	@Override
	public boolean neverDropsSecondSeed() {
		return false;
	}

}
