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
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.CropHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;

public class MagicCropHandler extends CropHandlerBase {

	public final Block oreID;
	public final Block netherOreID;
	public final Block endOreID;

	private final int configChance;

	public enum EssenceType {
		COW(EssenceClass.ANIMAL, "Cow"),
		SHEEP(EssenceClass.ANIMAL, "Sheep"),
		PIG(EssenceClass.ANIMAL, "Pig"),
		CHICKEN(EssenceClass.ANIMAL, "Chicken"),
		CREEPER(EssenceClass.MOB, "Creeper"),
		MAGMA(EssenceClass.MOB, "Magma"), //May not exist, may throw error
		SKELETON(EssenceClass.MOB, "Skeleton"),
		SLIME(EssenceClass.MOB, "Slime"),
		SPIDER(EssenceClass.MOB, "Spider"),
		GHAST(EssenceClass.MOB, "Ghast"),
		WITHER(EssenceClass.MOB, "Wither"),
		BLAZE(EssenceClass.MOB, "Blaze"),
		ENDER(EssenceClass.MOB, "Enderman"),

		ESSENCE(EssenceClass.MATERIAL, "Minicio"),
		NATURE(EssenceClass.MATERIAL, "Nature"),
		COAL(EssenceClass.MATERIAL, "Coal"),
		DYE(EssenceClass.MATERIAL, "Dye"),
		REDSTONE(EssenceClass.MATERIAL, "Redstone"),
		GLOWSTONE(EssenceClass.MATERIAL, "Glowstone"),
		OBSIDIAN(EssenceClass.MATERIAL, "Obsidian"),
		IRON(EssenceClass.MATERIAL, "Iron"),
		GOLD(EssenceClass.MATERIAL, "Gold"),
		LAPIS(EssenceClass.MATERIAL, "Lapis"),
		QUARTZ(EssenceClass.MATERIAL, "Quartz"),
		XP(EssenceClass.MATERIAL, "Experience"),
		DIAMOND(EssenceClass.MATERIAL, "Diamond"),
		EMERALD(EssenceClass.MATERIAL, "Emerald"),
		NETHER(EssenceClass.MATERIAL, "Nether"),
		WATER(EssenceClass.ELEMENT, "Water"),
		FIRE(EssenceClass.ELEMENT, "Fire"),
		EARTH(EssenceClass.ELEMENT, "Earth"),
		AIR(EssenceClass.ELEMENT, "Air"),
		/*
		ABSORPTION(EssenceClass.POTION, "Absorption", 0),
		FIRERESIST(EssenceClass.POTION, "FireResistance", 1),
		HASTE(EssenceClass.POTION, "Haste", 2),
		NIGHTVISION(EssenceClass.POTION, "NightVision", 3),
		REGEN(EssenceClass.POTION, "Regeneration", 4),
		RESISTANCE(EssenceClass.POTION, "Resistance", 5),
		STRENGTH(EssenceClass.POTION, "Strength", 6),
		WATERBREATHING(EssenceClass.POTION, "WaterBreathing", 7),
		 */
		ALUMINUM(EssenceClass.MOD, "Aluminium"),
		ARDITE(EssenceClass.MOD, "Ardite"),
		COBALT(EssenceClass.MOD, "Cobalt"),
		COPPER(EssenceClass.MOD, "Copper"),
		CERTUS(EssenceClass.MOD, "CertusQuartz"),
		LEAD(EssenceClass.MOD, "Lead"),
		NICKEL(EssenceClass.MOD, "Nickel"),
		OSMIUM(EssenceClass.MOD, "Osmium"),
		PERIDOT(EssenceClass.MOD, "Peridot"),
		RUBY(EssenceClass.MOD, "Ruby"),
		SAPPHIRE(EssenceClass.MOD, "Sapphire"),
		PLATINUM(EssenceClass.MOD, "Platinum"),
		RUBBER(EssenceClass.MOD, "Rubber"),
		SILVER(EssenceClass.MOD, "Silver"),
		TIN(EssenceClass.MOD, "Tin"),
		SULFUR(EssenceClass.MOD, "Sulfur"),
		YELLORITE(EssenceClass.MOD, "Yellorite"),
		ALUMITE(EssenceClass.MOD, "Alumite"),
		BLIZZ(EssenceClass.MOD, "Blizz"),
		BRONZE(EssenceClass.MOD, "Bronze"),
		ELECTRUM(EssenceClass.MOD, "Electrum"),
		ENDERIUM(EssenceClass.MOD, "Enderium"),
		FLUIX(EssenceClass.MOD, "Fluix"),
		INVAR(EssenceClass.MOD, "Invar"),
		LUMIUM(EssenceClass.MOD, "Lumium"),
		MANASTEEL(EssenceClass.MOD, "Manasteel"),
		MANYULLYN(EssenceClass.MOD, "Manyullyn"),
		SALTPETER(EssenceClass.MOD, "Saltpeter"),
		SIGNALUM(EssenceClass.MOD, "Signalum"),
		STEEL(EssenceClass.MOD, "Steel"),
		TERRASTEEL(EssenceClass.MOD, "Terrasteel"),
		;
		private final EssenceClass type;
		private final String tag;
		private Block cropID = null;
		private Item seedID = null;
		private Item essenceID = null;
		//private final int essenceMeta;

		private static final EssenceType[] essenceList = values();

		private static final HashMap<Block, EssenceType> cropIDs = new HashMap();
		private static final HashMap<Item, EssenceType> essenceIDs = new HashMap();
		private static final HashMap<Item, EssenceType> seedIDs = new HashMap();

		private EssenceType(EssenceClass c, String name) {
			type = c;
			tag = name;
			//essenceMeta = meta;
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
			return tag+"Seeds";
		}

		private String getEssenceFieldName() {
			return tag+"Essence";
		}

		private String getCropFieldName() {
			return tag+"Crop";
		}

		public ItemStack getEssence() {
			return essenceID != null ? new ItemStack(essenceID) : null;
		}

		public ItemStack getSeeds() {
			return seedID != null ? new ItemStack(seedID, 1, 0) : null;
		}

		public ItemStack getCrop() {
			return cropID != null ? new ItemStack(cropID, 1, 0) : null;
		}

		private static boolean initialized() {
			int len = essenceList.length;
			return cropIDs.size() == len && seedIDs.size() == len && essenceIDs.size() == len;
		}

		public boolean isModEssence() {
			return type == EssenceClass.MOD;
		}
	}

	private static enum EssenceClass {
		ANIMAL(),
		MOB(),
		MATERIAL(),
		ELEMENT(),
		POTION(),
		MOD();

		//private final String field;

		private EssenceClass() {
			//field = s;
		}
	}

	public enum MiscEssence {
		CHRISTMAS("Christmas"),
		TAINTED("Tainted"),
		ACCIO("Accio"),
		CRUCIO("Crucio"),
		IMPERIO("Imperio"),
		ZIVICIO("Zivicio");

		private final String field;
		//private final int metadata;
		private Item item;

		private static final MiscEssence[] list = values();
		private static final HashMap<Item, MiscEssence> itemMap = new HashMap();

		private MiscEssence(String s) {
			field = s;
		}

		//private MiscEssence(String s, int meta) {
		//	field = s;
		//}

		public ItemStack getItem() {
			return item != null ? new ItemStack(item) : null;
		}

		private void setIDs(Item essence) {
			item = essence;
			itemMap.put(essence, this);
		}

		public String getEssenceFieldName() {
			return field+"Essence";
		}

		private static boolean initialized() {
			return itemMap.size() == list.length;
		}
	}

	private static final MagicCropHandler instance = new MagicCropHandler();

	private MagicCropHandler() {
		super();
		Block idore = null;
		Block idnether = null;
		Block idend = null;
		int chance = -1;
		if (this.hasMod()) {
			try {
				Class blocks = this.getMod().getBlockClass();
				Class crops = Class.forName("com.mark719.magicalcrops.handlers.MCrops");
				Class seeds = Class.forName("com.mark719.magicalcrops.handlers.MSeeds");
				Class essences = Class.forName("com.mark719.magicalcrops.handlers.Essence");
				Class mods = Class.forName("com.mark719.magicalcrops.handlers.ModCompat");
				for (int i = 0; i < EssenceType.essenceList.length; i++) {
					EssenceType type = EssenceType.essenceList[i];
					String cropf = type.getCropFieldName();
					String seedf = type.getSeedFieldName();
					String essf = type.getEssenceFieldName();
					try {
						Block crop;
						Item seed;
						Item essence;
						if (type.isModEssence()) {
							Field f = mods.getField(cropf);
							crop = (Block)f.get(null);

							f = mods.getField(seedf);
							seed = (Item)f.get(null);

							f = mods.getField(essf);
							essence = (Item)f.get(null);
						}
						else {
							Field f = crops.getField(cropf);
							crop = (Block)f.get(null);

							f = seeds.getField(seedf);
							seed = (Item)f.get(null);

							f = essences.getField(essf);
							essence = (Item)f.get(null);
						}

						type.setIDs(crop, seed, essence);
					}
					catch (NoSuchFieldException e) {
						DragonAPICore.logError(this.getMod()+" field not found! "+e.getMessage());
						e.printStackTrace();
						this.logFailure(e);
					}
					catch (IllegalAccessException e) {
						DragonAPICore.logError("Illegal access exception for reading "+this.getMod()+"!");
						e.printStackTrace();
						this.logFailure(e);
					}
					catch (NullPointerException e) {
						DragonAPICore.logError("Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
						e.printStackTrace();
						this.logFailure(e);
					}
					catch (Exception e) {
						e.printStackTrace();
						this.logFailure(e);
					}
				}
				for (int i = 0; i < MiscEssence.list.length; i++) {
					MiscEssence type = MiscEssence.list[i];
					String essf = type.getEssenceFieldName();
					try {
						Field f = essences.getField(essf);
						Item essence = (Item)f.get(null);
						type.setIDs(essence);
					}
					catch (NoSuchFieldException e) {
						DragonAPICore.logError(this.getMod()+" field not found! "+e.getMessage());
						e.printStackTrace();
						this.logFailure(e);
					}
					catch (IllegalAccessException e) {
						DragonAPICore.logError("Illegal access exception for reading "+this.getMod()+"!");
						e.printStackTrace();
						this.logFailure(e);
					}
					catch (NullPointerException e) {
						DragonAPICore.logError("Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
						e.printStackTrace();
						this.logFailure(e);
					}
					catch (Exception e) {
						e.printStackTrace();
						this.logFailure(e);
					}
				}
				try {
					Field f = blocks.getField("MinicioOre");
					Block ore = (Block)f.get(null);
					idore = ore;

					f = blocks.getField("MinicioOreNether");
					ore = (Block)f.get(null);
					idnether = ore;

					f = blocks.getField("MinicioOreEnd");
					ore = (Block)f.get(null);
					idend = ore;

					//f = c.getField("ExperienceDrop");
					//Item drop = (Item)f.get(null);
					//iddrop = drop;
				}
				catch (NoSuchFieldException e) {
					DragonAPICore.logError(this.getMod()+" field not found! "+e.getMessage());
					e.printStackTrace();
					this.logFailure(e);
				}
				catch (IllegalAccessException e) {
					DragonAPICore.logError("Illegal access exception for reading "+this.getMod()+"!");
					e.printStackTrace();
					this.logFailure(e);
				}
				catch (NullPointerException e) {
					DragonAPICore.logError("Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
					e.printStackTrace();
					this.logFailure(e);
				}
				catch (Exception e) {
					e.printStackTrace();
					this.logFailure(e);
				}
				try {
					Class c = Class.forName("com.mark719.magicalcrops.config.ConfigMain");
					Field f = c.getDeclaredField("SECOND_SEED_CHANCE");
					f.setAccessible(true);
					chance = f.getInt(null);
				}
				catch (ClassNotFoundException e) {
					DragonAPICore.logError(this.getMod()+" class not found! "+e.getMessage());
					e.printStackTrace();
					this.logFailure(e);
				}
				catch (NoSuchFieldException e) {
					DragonAPICore.logError(this.getMod()+" field not found! "+e.getMessage());
					e.printStackTrace();
					this.logFailure(e);
				}
				catch (IllegalAccessException e) {
					DragonAPICore.logError("Illegal access exception for reading "+this.getMod()+"!");
					e.printStackTrace();
					this.logFailure(e);
				}
				catch (NullPointerException e) {
					DragonAPICore.logError("Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
					e.printStackTrace();
					this.logFailure(e);
				}
				catch (Exception e) {
					e.printStackTrace();
					this.logFailure(e);
				}
			}
			catch (ClassNotFoundException e) {
				DragonAPICore.logError(this.getMod()+" class not found! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}
		}
		else {
			this.noMod();
		}
		oreID = idore;
		netherOreID = idnether;
		endOreID = idend;
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

	public static MagicCropHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return EssenceType.initialized() && MiscEssence.initialized() && configChance != -1 && oreID != null && netherOreID != null && endOreID != null;
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
		ItemStack nature = EssenceType.NATURE.getEssence();
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
		DragonAPICore.log("Registering Magic Crops Essence ore to the Ore Dictionary!");
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
