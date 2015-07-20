/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
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
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.CropHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;

public class LegacyMagicCropHandler extends CropHandlerBase {

	public final Block oreID;
	public final Block netherOreID;
	public final Block endOreID;

	public final Item dropID;

	private final int configChance;

	public enum EssenceType {
		COW(EssenceClass.SOULPASSIVE, "Cow", 0),
		SHEEP(EssenceClass.SOULPASSIVE, "Sheep", 2),
		PIG(EssenceClass.SOULPASSIVE, "Pig", 1),
		CHICKEN(EssenceClass.SOULPASSIVE, "Chicken", 3),
		CREEPER(EssenceClass.SOULHOSTILE, "Creeper", 1),
		MAGMA(EssenceClass.SOULHOSTILE, "Magma", 4),
		SKELETON(EssenceClass.SOULHOSTILE, "Skeleton", 5),
		SLIME(EssenceClass.SOULHOSTILE, "Slime", 6),
		SPIDER(EssenceClass.SOULHOSTILE, "Spider", 7),
		GHAST(EssenceClass.SOULHOSTILE, "Ghast", 3),
		WITHER(EssenceClass.SOULHOSTILE, "Wither", 8),
		BLAZE(EssenceClass.SOULHOSTILE, "Blaze", 0),
		ENDER(EssenceClass.SOULHOSTILE, "Enderman", 2),

		ESSENCE(EssenceClass.MINICIO, "Minicio", 0), //Own item
		COAL(EssenceClass.MATERIAL1, "Coal", 1),
		DYE(EssenceClass.MATERIAL1, "Dye", 2),
		REDSTONE(EssenceClass.MATERIAL2, "Redstone", 0),
		GLOWSTONE(EssenceClass.MATERIAL2, "Glowstone", 1),
		OBSIDIAN(EssenceClass.MATERIAL2, "Obsidian", 2),
		IRON(EssenceClass.MATERIAL3, "Iron", 0),
		GOLD(EssenceClass.MATERIAL3, "Gold", 1),
		LAPIS(EssenceClass.MATERIAL3, "Lapis", 2),
		QUARTZ(EssenceClass.MATERIAL3, "Quartz", 4),
		XP(EssenceClass.MATERIAL3, "Experience", 3),
		DIAMOND(EssenceClass.MATERIAL4, "Diamond", 0),
		EMERALD(EssenceClass.MATERIAL4, "Emerald", 1),
		WATER(EssenceClass.MATERIAL1, "Water", 5),
		FIRE(EssenceClass.MATERIAL1, "Fire", 4),
		EARTH(EssenceClass.MATERIAL1, "Earth", 3),
		AIR(EssenceClass.MATERIAL1, "Air", 0),

		ABSORPTION(EssenceClass.POTION, "Absorption", 0),
		FIRERESIST(EssenceClass.POTION, "FireResistance", 1),
		HASTE(EssenceClass.POTION, "Haste", 2),
		NIGHTVISION(EssenceClass.POTION, "NightVision", 3),
		REGEN(EssenceClass.POTION, "Regeneration", 4),
		RESISTANCE(EssenceClass.POTION, "Resistance", 5),
		STRENGTH(EssenceClass.POTION, "Strength", 6),
		WATERBREATHING(EssenceClass.POTION, "WaterBreathing", 7),
		/*
		COPPER(EssenceClass.MOD, "Copper", 0),
		TIN(EssenceClass.MOD, "Tin", 1),
		SILVER(EssenceClass.MOD, "Silver", 2),
		LEAD(EssenceClass.MOD, "Lead", 3),
		CQUARTZ(EssenceClass.MOD, "Quartz", 4),
		SAPPHIRE(EssenceClass.MOD, "Sapphire", 5),
		RUBY(EssenceClass.MOD, "Ruby", 6),
		PERIDOT(EssenceClass.MOD, "Peridot", 7),
		ALUMINUM(EssenceClass.MOD, "Alumin", 8),
		FORCE(EssenceClass.MOD, "Force", 9),
		COBALT(EssenceClass.MOD, "Cobalt", 10),
		ARDITE(EssenceClass.MOD, "Ardite", 11),
		NICKEL(EssenceClass.MOD, "Nickel", 12),
		PLATINUM(EssenceClass.MOD, "Platinum", 13),
		SHARD(EssenceClass.MOD, "ThaumcraftShard", 14),
		URANIUM(EssenceClass.MOD, "Uranium", 15),
		OIL(EssenceClass.MOD, "Oil", 16),
		RUBBER(EssenceClass.MOD, "Rubber", 17),
		VINTEUM(EssenceClass.MOD, "Vinteum", 18),
		TOPAZ(EssenceClass.MOD, "BlueTopaz", 19),
		CHIMERITE(EssenceClass.MOD, "Chimerite", 20),
		MOONSTONE(EssenceClass.MOD, "Moonstone", 21),
		SUNSTONE(EssenceClass.MOD, "Sunstone", 22),
		IRIDIUM(EssenceClass.MOD, "Iridium", 23),
		YELLORITE(EssenceClass.MOD, "Yellorite", 24),
		OSMIUM(EssenceClass.MOD, "Osmium", 25),
		MAGANESE(EssenceClass.MOD, "Manganese", 26),
		SULFUR(EssenceClass.MOD, "Sulfur", 27),
		DARKIRON(EssenceClass.MOD, "Darkiron", 28);
		 */;
		private final EssenceClass type;
		private final String tag;
		private Block cropID = null;
		private Item seedID = null;
		private Item essenceID = null;
		private final int essenceMeta;

		public static final EssenceType[] essenceList = values();
		private static final HashMap<Block, EssenceType> cropIDs = new HashMap();
		private static final HashMap<Item, EssenceType> essenceIDs = new HashMap();
		private static final HashMap<Item, EssenceType> seedIDs = new HashMap();

		private EssenceType(EssenceClass c, String name, int meta) {
			type = c;
			tag = name;
			essenceMeta = meta;
		}

		private void setIDs(Block crop, Item seed, Item essence) {
			seedID = seed;
			cropID = crop;
			essenceID = essence;
			cropIDs.put(crop, this);
			seedIDs.put(seed, this);
			essenceIDs.put(essence, this);
		}

		private String getSeedFieldName() {
			return "Seeds"+tag;
		}

		private String getCropFieldName() {
			return "Crop"+tag;
		}

		public ItemStack getEssence() {
			return essenceID != null ? new ItemStack(essenceID, 1, essenceMeta) : null;
		}

		public ItemStack getSeeds() {
			return seedID != null ? new ItemStack(seedID, 1, 0) : null;
		}

		public ItemStack getCrop() {
			return cropID != null ? new ItemStack(cropID, 1, 0) : null;
		}

		public static boolean initialized() {
			return !cropIDs.isEmpty() && !seedIDs.isEmpty() && !essenceIDs.isEmpty();
		}
	}

	private static enum EssenceClass {
		SOULPASSIVE("SoulEssencePassive"),
		SOULHOSTILE("SoulEssenceHostile"),
		MINICIO("MinicioEssence"),
		MATERIAL1("T1Essence"),
		MATERIAL2("T2Essence"),
		MATERIAL3("T3Essence"),
		MATERIAL4("T4Essence"),
		POTION("PotionPetals"),
		MOD("");

		private final String field;

		private EssenceClass(String s) {
			field = s;
		}
	}

	public enum MiscEssence {
		NATURE("T1Essence", 6),
		CHRISTMAS("ChristmasEssence"),
		TAINTED("TaintedEssence"),
		ACCIO("AccioEssence"),
		CRUCIO("CrucioEssence"),
		IMPERIO("ImperioEssence"),
		ZIVICIO("ZivicioEssence");

		private final String field;
		private final int metadata;
		private Item item;

		private MiscEssence(String s) {
			this(s, 0);
		}

		private MiscEssence(String s, int meta) {
			field = s;
			metadata = meta;
		}

		public ItemStack getItem() {
			return item != null ? new ItemStack(item, 1, metadata) : null;
		}
	}

	private static final LegacyMagicCropHandler instance = new LegacyMagicCropHandler();

	private LegacyMagicCropHandler() {
		super();
		Block idore = null;
		Block idnether = null;
		Block idend = null;
		Item iddrop = null;
		int chance = -1;
		if (this.hasMod()) {
			Class c = this.getMod().getBlockClass();
			for (int i = 0; i < EssenceType.essenceList.length; i++) {
				EssenceType type = EssenceType.essenceList[i];
				String cropf = type.getCropFieldName();
				String seedf = type.getSeedFieldName();
				String essf = type.type.field;
				try {
					Field f = c.getField(cropf);
					Block crop = (Block)f.get(null);

					f = c.getField(seedf);
					Item seed = (Item)f.get(null);

					f = c.getField(essf);
					Item essence = (Item)f.get(null);

					type.setIDs(crop, seed, essence);
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
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				Field f = c.getField("EssenceOre");
				Block ore = (Block)f.get(null);
				idore = ore;

				f = c.getField("EssenceOreNether");
				ore = (Block)f.get(null);
				idnether = ore;

				f = c.getField("EssenceOreEnd");
				ore = (Block)f.get(null);
				idend = ore;

				f = c.getField("ExperienceDrop");
				Item drop = (Item)f.get(null);
				iddrop = drop;

				Class c2 = Class.forName("com.mark719.magicalcrops.ConfigHandler");
				f = c2.getDeclaredField("seeddropchance");
				f.setAccessible(true);
				chance = f.getInt(null);
			}
			catch (ClassNotFoundException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: "+this.getMod()+" class not found! "+e.getMessage());
				e.printStackTrace();
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
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			this.noMod();
		}
		oreID = idore;
		netherOreID = idnether;
		endOreID = idend;
		dropID = iddrop;
		configChance = chance >= 0 ? chance : 10;
	}

	@Override
	public boolean isCrop(Block id, int meta) {
		return EssenceType.cropIDs.containsKey(id);
	}

	@Override
	public boolean isSeedItem(ItemStack is) {
		return EssenceType.seedIDs.containsKey(is.getItem());
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

	private static LegacyMagicCropHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return EssenceType.initialized() && configChance != -1 && oreID != null && netherOreID != null && endOreID != null && dropID != null;
	}

	public boolean isEssenceOre(Block id) {
		return id == netherOreID || id == oreID || id == endOreID;
	}

	@Override
	public ModList getMod() {
		return ModList.MAGICCROPS;
	}

	@Override
	public int getHarvestedMeta(World world, int x, int y, int z) {
		return 0;
	}

	@Override
	public ArrayList<ItemStack> getAdditionalDrops(World world, int x, int y, int z, Block id, int meta, int fortune) {
		ArrayList<ItemStack> li = new ArrayList();
		if (MiscEssence.CHRISTMAS.item != null && ReikaRandomHelper.doWithChance(20*(1+fortune)))
			li.add(MiscEssence.CHRISTMAS.getItem());
		ItemStack nature = MiscEssence.NATURE.getItem();
		if (nature != null && ReikaRandomHelper.doWithChance(20*(1+fortune)))
			li.add(nature);
		if (ReikaRandomHelper.doWithChance(20*(1+fortune))) {
			ItemStack weak = EssenceType.ESSENCE.getEssence();
			if (weak != null) {
				li.add(weak);
				if (ReikaRandomHelper.doWithChance(25*(1+fortune)))
					li.add(weak);
			}
		}
		return li;
	}

	public void registerEssence() {
		ItemStack ore = new ItemStack(oreID, 1, 0);
		OreDictionary.registerOre("oreEssence", ore);

		ore = new ItemStack(netherOreID, 1, 0);
		OreDictionary.registerOre("oreNetherEssence", ore);

		ore = new ItemStack(endOreID, 1, 0);
		OreDictionary.registerOre("oreEndEssence", ore);

		ModOreList.ESSENCE.initialize();
		ReikaJavaLibrary.pConsole("DRAGONAPI: Registering Magic Crops Essence ore to the Ore Dictionary!");
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
		return configChance == 0;
	}

}
