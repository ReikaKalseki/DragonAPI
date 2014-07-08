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

public class MagicCropHandler extends CropHandlerBase {

	public final int oreID;
	public final int netherOreID;
	/** Crafting Item */
	public final int essenceID;
	public final int cropEssenceID;
	private final int configChance;
	private ItemStack christmasEssence;
	private ItemStack natureEssence;

	public static enum EssenceType {
		COW(EssenceClass.SOUL, "Cow", 0),
		CREEPER(EssenceClass.SOUL, "Creeper", 1),
		SPIDER(EssenceClass.SOUL, "Spider", 5),
		SKELETON(EssenceClass.SOUL, "Skeleton", 3),
		ENDER(EssenceClass.MATERIAL, "Ender", 9),
		BLAZE(EssenceClass.MATERIAL, "Blaze", 7),
		SLIME(EssenceClass.SOUL, "Slime", 4),
		MAGMA(EssenceClass.SOUL, "Magma", 2),
		GHAST(EssenceClass.SOUL, "Ghast", 6),
		WITHER(EssenceClass.SOUL, "Wither", 7),
		DYE(EssenceClass.MATERIAL, "Dye", 13),
		XP(EssenceClass.MATERIAL, "XP", 12),
		COAL(EssenceClass.MATERIAL, "Coal", 11),
		IRON(EssenceClass.MATERIAL, "Iron", 4),
		GOLD(EssenceClass.MATERIAL, "Gold", 5),
		REDSTONE(EssenceClass.MATERIAL, "Redstone", 1),
		LAPIS(EssenceClass.MATERIAL, "Lapis", 6),
		DIAMOND(EssenceClass.MATERIAL, "Diamond", 3),
		EMERALD(EssenceClass.MATERIAL, "Emerald", 8),
		PLATINUM(EssenceClass.MATERIAL, "Platinum", 13),
		ESSENCE(EssenceClass.MATERIAL, "Essence", 0),
		GLOWSTONE(EssenceClass.MATERIAL, "Glowstone", 2),
		OBSIDIAN(EssenceClass.MATERIAL, "Obsidian", 10),
		NETHER(EssenceClass.MATERIAL, "Nether", 14),
		DEATH(EssenceClass.MATERIAL, "DeathBloom", 0),
		COPPER(EssenceClass.MATERIAL, "Copper", 0),
		TIN(EssenceClass.MATERIAL, "Tin", 1),
		SILVER(EssenceClass.MATERIAL, "Silver", 2),
		URANIUM(EssenceClass.MATERIAL, "Uranium", 15),
		LEAD(EssenceClass.MATERIAL, "Lead", 3),
		NICKEL(EssenceClass.MATERIAL, "Nickel", 12),
		ALUMINUM(EssenceClass.MATERIAL, "Alumin", 8),
		QUARTZ(EssenceClass.MATERIAL, "Quartz", 4),
		SAPPHIRE(EssenceClass.MATERIAL, "Sapphire", 5),
		RUBY(EssenceClass.MATERIAL, "Ruby", 6),
		PERIDOT(EssenceClass.MATERIAL, "Peridot", 7),
		FORCE(EssenceClass.MATERIAL, "Force", 9),
		ARDITE(EssenceClass.MATERIAL, "Ardite", 11),
		COBALT(EssenceClass.MATERIAL, "Cobalt", 10),
		SHARD(EssenceClass.MATERIAL, "ThaumcraftShard", 14),
		OIL(EssenceClass.MATERIAL, "Oil", 16),
		EARTH(EssenceClass.ELEMENT, "Earth", 2),
		AIR(EssenceClass.ELEMENT, "Air", 3),
		FIRE(EssenceClass.ELEMENT, "Fire", 1),
		WATER(EssenceClass.ELEMENT, "Water", 0);

		private final EssenceClass type;
		private final String tag;
		private int cropID = -1;
		private int seedID = -1;
		private int essenceID = -1;
		private final int essenceMeta;

		public static final EssenceType[] essenceList = values();
		private static final HashMap<Integer, EssenceType> cropIDs = new HashMap();
		private static final HashMap<Integer, EssenceType> essenceIDs = new HashMap();
		private static final HashMap<Integer, EssenceType> seedIDs = new HashMap();

		private EssenceType(EssenceClass type, String name, int meta) {
			this.type = type;
			tag = name;
			essenceMeta = meta;
		}

		private void setIDs(int crop, int seed, int essence) {
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
			return new ItemStack(essenceID, 1, essenceMeta);
		}

		public ItemStack getSeeds() {
			return new ItemStack(seedID, 1, 0);
		}

		public ItemStack getCrop() {
			return new ItemStack(cropID, 1, 0);
		}

		public String getEssenceFieldName() {
			if (this.isMod())
				return "ModEssence";
			switch(type) {
			case ELEMENT:
				return "ElementEssence";
			case MATERIAL:
				return "CropEssence";
			case SOUL:
				return "soulCropEssence";
			default:
				return "";
			}
		}

		private boolean isMod() {
			switch(this) {
			case ALUMINUM:
			case ARDITE:
			case COBALT:
			case COPPER:
			case FORCE:
			case LEAD:
			case NICKEL:
			case OIL:
			case PERIDOT:
			case PLATINUM:
			case QUARTZ:
			case RUBY:
			case SAPPHIRE:
			case SHARD:
			case SILVER:
			case TIN:
			case URANIUM:
				return true;
			default:
				return false;
			}
		}

		public static boolean initialized() {
			return !cropIDs.isEmpty() && !seedIDs.isEmpty() && !essenceIDs.isEmpty();
		}
	}

	private static enum EssenceClass {
		MATERIAL("mCrop", "mSeeds"),
		SOUL("soulCrop", "sSeeds"),
		ELEMENT("eCrop", "eSeeds");

		public final String seedPrefix;
		public final String cropPrefix;

		private EssenceClass(String crop, String seed) {
			seedPrefix = seed;
			cropPrefix = crop;
		}
	}

	private static final MagicCropHandler instance = new MagicCropHandler();

	private MagicCropHandler() {
		super();
		int idore = -1;
		int idnether = -1;
		int idessence = -1;
		int idcropessence = -1;
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

					type.setIDs(crop.blockID, seed.itemID, essence.itemID);
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
			}
			try {
				Field f = c.getField("BlockOreEssence");
				Block ore = (Block)f.get(null);
				idore = ore.blockID;

				f = c.getField("BlockOreEssenceNether");
				ore = (Block)f.get(null);
				idnether = ore.blockID;

				f = c.getField("MagicEssence");
				Item essence = (Item)f.get(null);
				idessence = essence.itemID;

				f = c.getDeclaredField("seeddropchance");
				f.setAccessible(true);
				chance = f.getInt(null);
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
		}
		else {
			this.noMod();
		}
		oreID = idore;
		netherOreID = idnether;
		essenceID = idessence;
		cropEssenceID = idcropessence;
		configChance = chance >= 0 ? chance : 10;

		natureEssence = cropEssenceID >= 0 ? new ItemStack(cropEssenceID, 1, 0) : null;
		christmasEssence = cropEssenceID >= 0 ? new ItemStack(cropEssenceID, 1, 20) : null;
	}

	@Override
	public boolean isCrop(int id) {
		return EssenceType.cropIDs.containsKey(id);
	}

	@Override
	public boolean isSeedItem(ItemStack is) {
		return EssenceType.seedIDs.containsKey(is.itemID);
	}

	@Override
	public float getSecondSeedDropRate() {
		return configChance/100F;
	}

	@Override
	public boolean isRipeCrop(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		return this.isCrop(id) && meta == 7;
	}

	@Override
	public void makeRipe(World world, int x, int y, int z) {
		world.setBlockMetadataWithNotify(x, y, z, 7, 3);
	}

	public static MagicCropHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return EssenceType.initialized() && configChance != -1 && oreID != -1 && netherOreID != -1 && essenceID != -1 && cropEssenceID != -1;
	}

	public boolean isEssenceOre(int id) {
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
	public ArrayList<ItemStack> getAdditionalDrops(World world, int x, int y, int z, int id, int meta, int fortune) {
		ArrayList<ItemStack> li = new ArrayList();
		if (ReikaRandomHelper.doWithChance(20*(1+fortune)) && christmasEssence != null)
			li.add(christmasEssence);
		if (ReikaRandomHelper.doWithChance(20*(1+fortune)) && natureEssence != null)
			li.add(natureEssence);
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

		ModOreList.ESSENCE.reloadOreList();
		ReikaJavaLibrary.pConsole("DRAGONAPI: Registering Magic Crops Essence ore to the Ore Dictionary!");
	}

	public ItemStack getWeakEssence() {
		return essenceID >= 0 ? new ItemStack(essenceID, 1, 0) : null;
	}

}
