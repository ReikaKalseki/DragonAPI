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
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;

public final class TinkerToolHandler extends ModHandlerBase {

	private static final TinkerToolHandler instance = new TinkerToolHandler();

	public enum Tools {
		PICK("pickaxe"),
		SPADE("shovel"),
		AXE("hatchet"),
		BROADSWORD("broadsword"),
		LONGSWORD("longsword"),
		RAPIER("rapier"),
		DAGGER("dagger"),
		CUTLASS("cutlass"),
		PAN("frypan"),
		SIGN("battlesign"),
		CHISEL("chisel"),
		MATTOCK("mattock"),
		SCYTHE("scythe"),
		LUMBERAXE("lumberaxe"),
		CLEAVER("cleaver"),
		HAMMER("hammer"),
		BATTLEAX("battleaxe");

		private Item item;
		private final String field;

		public static final Tools[] toolList = values();

		private Tools(String s) {
			field = s;
		}

		public Item getItem() {
			return item;
		}

	}

	public enum Weapons {
		SHURIKEN("shuriken"),
		KNIFE("throwingknife"),
		JAVELIN("javelin"),
		BOW("shortbow"),
		LONGBOW("longbow"),
		CROSSBOW("crossbow");

		private Item item;
		private final String field;

		public static final Weapons[] weaponList = values();

		private Weapons(String s) {
			field = s;
		}

		public Item getItem() {
			return item;
		}

	}

	public enum ToolBlocks {
		TOOLSTATION("toolStationWood"),
		TOOLSTATION2("toolStationStone"),
		TOOLFORGE("toolForge"),
		WORKBENCH("craftingStationWood"),
		CRAFTSLAB("craftingSlabWood"),
		FURNACE("furnaceSlab");

		private Block item;
		private final String field;

		private static final ToolBlocks[] list = values();

		private ToolBlocks(String s) {
			field = s;
		}

		public Block getItem() {
			return item;
		}

	}

	public enum ToolParts {
		ROD("toolRod", 1, 0.5F),
		BINDING("binding", 9, 0.5F),
		TOUGHBINDING("toughBinding", 15, 3),
		TOUGHROD("toughRod", 14, 3),
		PLATE("largePlate", 16, 8),
		PICK("pickaxeHead", 2, 1),
		SHOVEL("shovelHead", 3, 1),
		AXEHEAD("hatchetHead", 4, 1),
		PANHEAD("frypanHead", 10, 1),
		SIGN("signHead", 11, 1),
		CHISEL("chiselHead", 13, 0.5F),
		SCYTHE("scytheBlade", 18, 8),
		LUMBER("broadAxeHead", 17, 8),
		EXCAVATOR("excavatorHead", 19, 8),
		HAMMER("hammerHead", 21, 8),
		SWORD("swordBlade", 5, 1),
		CLEAVER("largeSwordBlade", 20, 8),
		DAGGER("knifeBlade", 12, 0.5F),
		WIDEGUARD("wideGuard", 6, 0.5F),
		FULLGUARD("fullGuard", 22, 3),
		HANDGUARD("handGuard", 7, 0.5F),
		CROSSBAR("crossbar", 8, 0.5F);

		private Item item;
		public final float ingotCost;
		public final int castMeta;
		private final String field;

		public static final ToolParts[] partList = values();

		private ToolParts(String s, int cast, float n) {
			field = s;
			ingotCost = n;
			castMeta = cast;
		}

		public Item getItem() {
			return item;
		}

		public ItemStack getItem(int materialID) {
			return new ItemStack(item, 1, materialID);
		}

		public ItemStack getCast() {
			return new ItemStack(getInstance().toolCastItem, 1, castMeta);
		}
	}

	public enum WeaponParts {
		ARROWHEAD("arrowhead", false, 25, 0.5F),
		SHURIKEN("partShuriken", true, 0, 0.5F),
		BOWSTRING("bowstring", false, 23, 3),
		CROSSBOWBODY("partCrossbowBody", true, 2, 5),
		CROSSBOWLIMB("partCrossbowLimb", true, 1, 4),
		FLETCHING("fletching", false, 24, 1),
		BOWLIMB("partBowLimb", true, 3, 1.5F);

		private Item item;
		private final String field;

		public final float ingotCost;
		public final int castMeta;
		private final boolean weaponCast;

		public static final WeaponParts[] partList = values();

		private WeaponParts(String s, boolean wcast, int cast, float n) {
			field = s;
			weaponCast = wcast;
			ingotCost = n;
			castMeta = cast;
		}

		public Item getItem() {
			return item;
		}

		public ItemStack getItem(int materialID) {
			return new ItemStack(item, 1, materialID);
		}

		public ItemStack getCast() {
			return new ItemStack(weaponCast ? getInstance().weaponCastItem : getInstance().toolCastItem, 1, castMeta);
		}
	}

	private boolean init = false;

	public final Item toolCastItem;
	public final Item toolWoodPattern;

	public final Item weaponCastItem;
	public final Item weaponWoodPattern;

	public final Item blankPattern;

	private final HashMap<Item, Tools> tools = new HashMap();
	private final HashMap<Item, Weapons> weapons = new HashMap();

	private TinkerToolHandler() {
		super();

		Item cast_t = null;
		Item pattern_t = null;
		Item cast_w = null;
		Item pattern_w = null;

		Item blank = null;

		if (this.hasMod()) {
			try {
				Class tic = Class.forName("tconstruct.tools.TinkerTools");
				for (int i = 0; i < Tools.toolList.length; i++) {
					Tools t = Tools.toolList[i];
					try {
						Field f = tic.getField(t.field);
						t.item = (Item)f.get(null);
						tools.put(t.item, t);
					}
					catch (NoSuchFieldException e) {
						DragonAPICore.logError(this.getMod()+" field not found! "+e.getMessage());
						e.printStackTrace();
						this.logFailure(e);
					}
					catch (SecurityException e) {
						DragonAPICore.logError("Cannot read "+this.getMod()+" (Security Exception)! "+e.getMessage());
						e.printStackTrace();
						this.logFailure(e);
					}
					catch (IllegalArgumentException e) {
						DragonAPICore.logError("Illegal argument for reading "+this.getMod()+"!");
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
				}

				for (int i = 0; i < ToolParts.partList.length; i++) {
					ToolParts t = ToolParts.partList[i];
					try {
						Field f = tic.getField(t.field);
						t.item = (Item)f.get(null);
					}
					catch (NoSuchFieldException e) {
						DragonAPICore.logError(this.getMod()+" field not found! "+e.getMessage());
						e.printStackTrace();
						this.logFailure(e);
					}
					catch (SecurityException e) {
						DragonAPICore.logError("Cannot read "+this.getMod()+" (Security Exception)! "+e.getMessage());
						e.printStackTrace();
						this.logFailure(e);
					}
					catch (IllegalArgumentException e) {
						DragonAPICore.logError("Illegal argument for reading "+this.getMod()+"!");
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
				}

				for (int i = 0; i < ToolBlocks.list.length; i++) {
					ToolBlocks t = ToolBlocks.list[i];
					try {
						Field f = tic.getField(t.field);
						t.item = (Block)f.get(null);
					}
					catch (NoSuchFieldException e) {
						DragonAPICore.logError(this.getMod()+" field not found! "+e.getMessage());
						e.printStackTrace();
						this.logFailure(e);
					}
					catch (SecurityException e) {
						DragonAPICore.logError("Cannot read "+this.getMod()+" (Security Exception)! "+e.getMessage());
						e.printStackTrace();
						this.logFailure(e);
					}
					catch (IllegalArgumentException e) {
						DragonAPICore.logError("Illegal argument for reading "+this.getMod()+"!");
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
				}

				init = true;
			}
			catch (ClassNotFoundException e) {
				DragonAPICore.logError(this.getMod()+" class not found! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}

			try {
				Class c = Class.forName("tconstruct.smeltery.TinkerSmeltery");
				cast_t = (Item)c.getField("metalPattern").get(null);
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
			catch (ClassNotFoundException e) {
				DragonAPICore.logError(this.getMod()+" class not found! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}

			try {
				Class c = Class.forName("tconstruct.tools.TinkerTools");
				pattern_t = (Item)c.getField("woodPattern").get(null);

				blank = (Item)c.getField("blankPattern").get(null);
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
			catch (ClassNotFoundException e) {
				DragonAPICore.logError(this.getMod()+" class not found! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}

			try {
				Class c = Class.forName("tconstruct.weaponry.TinkerWeaponry");

				for (int i = 0; i < Weapons.weaponList.length; i++) {
					Weapons t = Weapons.weaponList[i];
					try {
						Field f = c.getField(t.field);
						t.item = (Item)f.get(null);
						weapons.put(t.item, t);
					}
					catch (NoSuchFieldException e) {
						DragonAPICore.logError(this.getMod()+" field not found! "+e.getMessage());
						e.printStackTrace();
						this.logFailure(e);
					}
					catch (SecurityException e) {
						DragonAPICore.logError("Cannot read "+this.getMod()+" (Security Exception)! "+e.getMessage());
						e.printStackTrace();
						this.logFailure(e);
					}
					catch (IllegalArgumentException e) {
						DragonAPICore.logError("Illegal argument for reading "+this.getMod()+"!");
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
				}

				for (int i = 0; i < WeaponParts.partList.length; i++) {
					WeaponParts t = WeaponParts.partList[i];
					try {
						Field f = c.getField(t.field);
						t.item = (Item)f.get(null);
					}
					catch (NoSuchFieldException e) {
						DragonAPICore.logError(this.getMod()+" field not found! "+e.getMessage());
						e.printStackTrace();
						this.logFailure(e);
					}
					catch (SecurityException e) {
						DragonAPICore.logError("Cannot read "+this.getMod()+" (Security Exception)! "+e.getMessage());
						e.printStackTrace();
						this.logFailure(e);
					}
					catch (IllegalArgumentException e) {
						DragonAPICore.logError("Illegal argument for reading "+this.getMod()+"!");
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
				}

				cast_w = (Item)c.getField("metalPattern").get(null);
				pattern_w = (Item)c.getField("woodPattern").get(null);
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
			catch (ClassNotFoundException e) {
				DragonAPICore.logError(this.getMod()+" class not found! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}
		}
		else {
			this.noMod();
		}

		toolCastItem = cast_t;
		toolWoodPattern = pattern_t;

		weaponCastItem = cast_w;
		weaponWoodPattern = pattern_w;

		blankPattern = blank;
	}

	public static TinkerToolHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return init;
	}

	@Override
	public ModList getMod() {
		return ModList.TINKERER;
	}

	public boolean isPick(ItemStack is) {
		return is != null && is.getItem() == Tools.PICK.item;
	}

	public boolean isHammer(ItemStack is) {
		return is != null && is.getItem() == Tools.HAMMER.item;
	}

	public ItemStack getIngotCast() {
		return new ItemStack(toolCastItem, 1, 0);
	}

	public ItemStack getNuggetCast() {
		return new ItemStack(toolCastItem, 1, 27);
	}

	public ItemStack getBlankWoodPattern() {
		return new ItemStack(blankPattern, 1, 0);
	}

	public ItemStack getBlankAlubrassPattern() {
		return new ItemStack(blankPattern, 1, 1);
	}

	public ItemStack getBlankGoldPattern() {
		return new ItemStack(blankPattern, 1, 2);
	}

	public int getHarvestLevel(ItemStack is) {
		if (is.stackTagCompound == null)
			return 0;
		NBTTagCompound tag = is.stackTagCompound.getCompoundTag("InfiTool");
		return tag.getInteger("HarvestLevel");
	}

	public boolean isStoneOrBetter(ItemStack is) {
		return this.getHarvestLevel(is) >= 1;
	}

	public boolean isIronOrBetter(ItemStack is) {
		return this.getHarvestLevel(is) >= 2;
	}

	public boolean isDiamondOrBetter(ItemStack is) {
		return this.getHarvestLevel(is) >= 3;
	}

	public boolean isToolStation(Block b) {
		return b == ToolBlocks.TOOLSTATION.item || b == ToolBlocks.TOOLSTATION2.item;
	}

	public boolean isWorkbench(Block b) {
		return b == ToolBlocks.WORKBENCH.item || b == ToolBlocks.CRAFTSLAB.item;
	}

	public Tools getTool(Item item) {
		return tools.get(item);
	}

	public Weapons getWeapon(Item item) {
		return weapons.get(item);
	}

}
