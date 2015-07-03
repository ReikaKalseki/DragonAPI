/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
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

public class VeryLegacyMagicCropHandler extends CropHandlerBase {

	public final Block oreID;
	public final Block netherOreID;
	/** Crafting Item */
	public final Item essenceID;
	public final Item cropEssenceID;
	private final int configChance;
	private ItemStack christmasEssence;

	public static enum EssenceType {
		COW(EssenceClass.SOUL, "Cow", 0),
		CREEPER(EssenceClass.SOUL, "Creeper", 1),
		MAGMA(EssenceClass.SOUL, "Magma", 2),
		SKELETON(EssenceClass.SOUL, "Skeleton", 3),
		SLIME(EssenceClass.SOUL, "Slime", 4),
		SPIDER(EssenceClass.SOUL, "Spider", 5),
		GHAST(EssenceClass.SOUL, "Ghast", 6),
		WITHER(EssenceClass.SOUL, "Wither", 7),

		NATURE(EssenceClass.MATERIAL, "Essence", 0),
		REDSTONE(EssenceClass.MATERIAL, "Redstone", 1),
		GLOWSTONE(EssenceClass.MATERIAL, "Glowstone", 2),
		DIAMOND(EssenceClass.MATERIAL, "Diamond", 3),
		IRON(EssenceClass.MATERIAL, "Iron", 4),
		GOLD(EssenceClass.MATERIAL, "Gold", 5),
		LAPIS(EssenceClass.MATERIAL, "Lapis", 6),
		BLAZE(EssenceClass.MATERIAL, "Blaze", 7),
		EMERALD(EssenceClass.MATERIAL, "Emerald", 8),
		ENDER(EssenceClass.MATERIAL, "Ender", 9),
		OBSIDIAN(EssenceClass.MATERIAL, "Obsidian", 10),
		COAL(EssenceClass.MATERIAL, "Coal", 11),
		XP(EssenceClass.MATERIAL, "Experience", 12),
		DYE(EssenceClass.MATERIAL, "Dye", 13),
		NETHER(EssenceClass.MATERIAL, "Nether", 14),
		//DEATH(EssenceClass.MATERIAL, "DeathBloom", 0),

		COPPER(EssenceClass.MOD, "Copper", 0),
		TIN(EssenceClass.MOD, "Tin", 1),
		SILVER(EssenceClass.MOD, "Silver", 2),
		LEAD(EssenceClass.MOD, "Lead", 3),
		QUARTZ(EssenceClass.MOD, "Quartz", 4),
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
		DARKIRON(EssenceClass.MOD, "Darkiron", 28),

		WATER(EssenceClass.ELEMENT, "Water", 0),
		FIRE(EssenceClass.ELEMENT, "Fire", 1),
		EARTH(EssenceClass.ELEMENT, "Earth", 2),
		AIR(EssenceClass.ELEMENT, "Air", 3);

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

		private EssenceType(EssenceClass type, String name, int meta) {
			this.type = type;
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

		public String getSeedFieldName() {
			return type.seedPrefix+tag;
		}

		public String getCropFieldName() {
			return type.cropPrefix+tag;
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

		public String getEssenceFieldName() {
			switch(type) {
			case ELEMENT:
				return "ElementEssence";
			case MATERIAL:
				return "CropEssence";
			case SOUL:
				return "SoulEssence";
			case MOD:
				return "ModCropEssence";
			default:
				return "";
			}
		}

		public static boolean initialized() {
			return !cropIDs.isEmpty() && !seedIDs.isEmpty() && !essenceIDs.isEmpty();
		}
	}

	private static enum EssenceClass {
		MATERIAL("MagicCrop", "MagicSeeds"),
		MOD("ModMagicCrop", "ModMagicSeeds"),
		SOUL("SoulCrop", "SoulSeeds"),
		ELEMENT("ElementCrop", "ElementSeeds");

		public final String seedPrefix;
		public final String cropPrefix;

		private EssenceClass(String crop, String seed) {
			seedPrefix = seed;
			cropPrefix = crop;
		}
	}

	private static final VeryLegacyMagicCropHandler instance = new VeryLegacyMagicCropHandler();

	private VeryLegacyMagicCropHandler() {
		super();
		Block idore = null;
		Block idnether = null;
		Item idessence = null;
		Item idcropessence = null;
		int chance = -1;
		if (this.hasMod()) {
			Class c = this.getMod().getBlockClass();
			for (int i = 0; i < EssenceType.essenceList.length; i++) {
				EssenceType type = EssenceType.essenceList[i];
				String cropf = type.getCropFieldName();
				String seedf = type.getSeedFieldName();
				String essf = type.getEssenceFieldName();
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

				f = c.getField("MagicEssence");
				Item essence = (Item)f.get(null);
				idessence = essence;


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
		essenceID = idessence;
		cropEssenceID = idcropessence;
		configChance = chance >= 0 ? chance : 10;

		christmasEssence = cropEssenceID != null ? new ItemStack(cropEssenceID, 1, 20) : null;
	}

	@Override
	public boolean isCrop(Block id) {
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
		return this.isCrop(b) && meta == 7;
	}

	@Override
	public void makeRipe(World world, int x, int y, int z) {
		world.setBlockMetadataWithNotify(x, y, z, 7, 3);
	}

	public static VeryLegacyMagicCropHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return EssenceType.initialized() && configChance != -1 && oreID != null && netherOreID != null && essenceID != null && cropEssenceID != null;
	}

	public boolean isEssenceOre(Block id) {
		return id == netherOreID || id == oreID;
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
		if (ReikaRandomHelper.doWithChance(20*(1+fortune)) && christmasEssence != null)
			li.add(christmasEssence);
		ItemStack nature = EssenceType.NATURE.getEssence();
		if (ReikaRandomHelper.doWithChance(20*(1+fortune)) && nature != null)
			li.add(nature);
		if (ReikaRandomHelper.doWithChance(20*(1+fortune))) {
			ItemStack weak = this.getWeakEssence();
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

		ModOreList.ESSENCE.initialize();
		ReikaJavaLibrary.pConsole("DRAGONAPI: Registering Magic Crops Essence ore to the Ore Dictionary!");
	}

	public ItemStack getWeakEssence() {
		return essenceID != null ? new ItemStack(essenceID, 1, 0) : null;
	}

	@Override
	public int getGrowthState(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}

	@Override
	public ArrayList<ItemStack> getDropsOverride(World world, int x, int y, int z, Block id, int meta, int fortune) {
		return null;
	}

}
