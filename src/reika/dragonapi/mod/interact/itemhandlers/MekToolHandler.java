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

import java.lang.reflect.Field;
import java.util.EnumMap;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.ModList;
import reika.dragonapi.base.ModHandlerBase;

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
		EnumMap<Materials, Item> map1 = itemsTool.get(tool);
		if (map1 == null) {
			map1 = new EnumMap(Materials.class);
			itemsTool.put(t, map1);
		}
		itemsTool.put(t, map1);

		EnumMap<Tools, Item> map2 = itemsMaterial.get(tool);
		if (map2 == null) {
			map2 = new EnumMap(Tools.class);
			itemsMaterial.put(m, map2);
		}
		itemsMaterial.put(m, map2);
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
