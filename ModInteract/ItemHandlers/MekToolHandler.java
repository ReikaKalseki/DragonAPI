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
import java.util.EnumMap;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public final class MekToolHandler extends ModHandlerBase {

	public static enum Materials {
		WOOD("Wood"),
		STONE("Stone"),
		IRON("Iron"),
		DIAMOND("Diamond"),
		GOLD("Gold"),
		GLOWSTONE("Glowstone"),
		BRONZE("Bronze"),
		OSMIUM("Osmium"),
		OBSIDIAN("Obsidian"),
		LAPIS("Lazuli"),
		STEEL("Steel");

		private final String id;

		private static final Materials[] list = values();

		private Materials(String s) {
			id = s;
		}

		public boolean isVanillaMaterial() {
			switch(this) {
				case WOOD:
				case STONE:
				case IRON:
				case DIAMOND:
				case GOLD:
					return true;
				default:
					return false;
			}
		}

		public Item getItem(Tools t) {
			return instance.initializedProperly() ? instance.itemsMaterial.get(this).get(t) : null;
		}

		public ItemStack getRawMaterial() {
			switch(this) {
				case BRONZE:
					return new ItemStack(MekanismHandler.getInstance().ingotID, 1, MekanismHandler.bronzeIngotMeta);
				case GLOWSTONE:
					return new ItemStack(MekanismHandler.getInstance().ingotID, 1, MekanismHandler.glowstoneIngotMeta);
				case OBSIDIAN:
					return new ItemStack(MekanismHandler.getInstance().ingotID, 1, MekanismHandler.obsidianIngotMeta);
				case OSMIUM:
					return new ItemStack(MekanismHandler.getInstance().ingotID, 1, MekanismHandler.osmiumIngotMeta);
				case STEEL:
					return new ItemStack(MekanismHandler.getInstance().ingotID, 1, MekanismHandler.steelIngotMeta);
				case STONE:
					return new ItemStack(Blocks.cobblestone);
				case WOOD:
					return new ItemStack(Blocks.planks, 1, OreDictionary.WILDCARD_VALUE);
				case GOLD:
					return new ItemStack(Items.gold_ingot);
				case IRON:
					return new ItemStack(Items.iron_ingot);
				case LAPIS:
					return ReikaItemHelper.lapisDye.copy();
				case DIAMOND:
					return new ItemStack(Items.diamond);
			}
			return null;
		}
	}

	public static enum Tools {
		PICK("Pickaxe"),
		PAXEL("Paxel"),
		SHOVEL("Shovel"),
		AXE("Axe"),
		HOE("Hoe"),
		SWORD("Sword"),
		HELMET("Helmet"),
		CHEST("Chestplate"),
		LEGS("Leggings"),
		BOOTS("Boots");

		private final String id;

		private static final Tools[] list = values();

		private Tools(String s) {
			id = s;
		}

		public boolean isCombineableWith(Materials m) {
			return this == PAXEL ? true : !m.isVanillaMaterial();
		}

		public Item getItem(Materials m) {
			return instance.initializedProperly() ? instance.itemsTool.get(this).get(m) : null;
		}

		public int getNumberIngots(Materials m) {
			switch(this) {
				case AXE:
					return 3;
				case HOE:
					return 2;
				case PAXEL:
					return 6;
				case PICK:
					return 3;
				case SHOVEL:
					return 1;
				case SWORD:
					return 2;
				case BOOTS:
					return m == Materials.STEEL ? 2 : 4;
				case CHEST:
					return m == Materials.STEEL ? 5 : 8;
				case HELMET:
					return m == Materials.STEEL ? 3 : 5;
				case LEGS:
					return m == Materials.STEEL ? 5 : 7;
			}
			return 0;
		}
	}

	private static final MekToolHandler instance = new MekToolHandler();

	private final EnumMap<Tools, EnumMap<Materials, Item>> itemsTool = new EnumMap(Tools.class);
	private final EnumMap<Materials, EnumMap<Tools, Item>> itemsMaterial = new EnumMap(Materials.class);

	private MekToolHandler() {
		super();
		if (this.hasMod()) {
			Class item = this.getMod().getItemClass();

			for (int i = 0; i < Materials.list.length; i++) {
				Materials m = Materials.list[i];
				for (int k = 0; k < Tools.list.length; k++) {
					Tools t = Tools.list[k];
					if (t.isCombineableWith(m)) {
						String varname = this.getField(m, t);
						Item tool = this.getID(item, varname);
						this.addEntry(tool, m, t);
					}
				}
			}

		}
		else {
			this.noMod();
		}
	}

	private void addEntry(Item tool, Materials m, Tools t) {
		EnumMap<Materials, Item> map1 = itemsTool.get(t);
		if (map1 == null) {
			map1 = new EnumMap(Materials.class);
			itemsTool.put(t, map1);
		}
		map1.put(m, tool);

		EnumMap<Tools, Item> map2 = itemsMaterial.get(t);
		if (map2 == null) {
			map2 = new EnumMap(Tools.class);
			itemsMaterial.put(m, map2);
		}
		map2.put(t, tool);
	}

	private String getField(Materials m, Tools t) {
		return m.id+t.id;
	}

	private Item getID(Class c, String varname) {
		try {
			Field f = c.getField(varname);
			Item id = ((Item)f.get(null));
			return id;
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
		return null;
	}

	public static MekToolHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return !itemsMaterial.isEmpty() && !itemsTool.isEmpty();
	}

	@Override
	public ModList getMod() {
		return ModList.MEKTOOLS;
	}

	public boolean isArmor(ItemStack is) {
		if (is == null)
			return false;
		if (!this.initializedProperly())
			return false;
		if (itemsTool.get(Tools.HELMET).containsValue(is.getItem()))
			return false;
		if (itemsTool.get(Tools.CHEST).containsValue(is.getItem()))
			return false;
		if (itemsTool.get(Tools.LEGS).containsValue(is.getItem()))
			return false;
		if (itemsTool.get(Tools.BOOTS).containsValue(is.getItem()))
			return false;
		return false;
	}

	public boolean isPickTypeTool(ItemStack held) {
		if (held == null)
			return false;
		if (!this.initializedProperly())
			return false;
		return itemsTool.get(Tools.PAXEL).containsValue(held.getItem()) || itemsTool.get(Tools.PICK).containsValue(held.getItem());
	}

	public boolean isWood(ItemStack held) {
		if (held == null)
			return false;
		if (!this.initializedProperly())
			return false;
		return itemsMaterial.get(Materials.WOOD).containsValue(held.getItem());
	}

}
