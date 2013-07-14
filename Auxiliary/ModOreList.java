/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Libraries.ReikaItemHelper;
import Reika.DragonAPI.Libraries.ReikaMathLibrary;

public enum ModOreList {

	TIN("Tin", "ingotTin", 1, "oreTin"),
	COPPER("Copper", "ingotCopper", 1, "oreCopper"),
	LEAD("Lead", "ingotLead", 1, "oreLead"),
	FERROUS("Nickel", "ingotNickel", 1, "oreNickel"), //ask KingLemming, not me...
	SILVER("Silver", "ingotSilver", 1, "oreSilver"),
	GALENA("Galena", "ingotGalena", 1, "oreGalena"),
	ALUMINUM("Aluminum", "ingotAluminum", 1, "oreAluminum", "oreBauxite"),
	IRIDIUM("Iridium", "ingotIridium", 1, "oreIridium"),
	PERIDOT("Peridot", "gemPeridot", 1, "orePeridot"),
	CERTUSQUARTZ("Certus Quartz", "quartz", 3, "oreCertus"),
	URANIUM("Uranium", "ingotUranium", 1, "oreUranium"),
	CINNABAR("Cinnabar", "cinnabar", 1, "oreCinnabar"),
	AMBER("Amber", "amber", 3, "oreAmber"),
	INFUSEDAIR("Air Infused", "infusedAir", 4, "oreInfusedAir"),
	INFUSEDFIRE("Fire Infused", "infusedFire", 4, "oreInfusedFire"),
	INFUSEDWATER("Water Infused", "infusedWater", 4, "oreInfusedWater"),
	INFUSEDEARTH("Earth Infused", "infusedEarth", 4, "oreInfusedEarth"),
	INFUSEDVIS("Vis Infused", "infusedVis", 4, "oreInfusedVis"),
	INFUSEDDULL("Dull Infused", "infusedDull", 4, "oreInfusedDull"),
	APATITE("Apatite", "gemApatite", 3, "oreApatite"),
	SALTPETER("Saltpeter", "dustSaltpeter", 2, "oreSaltpeter"),
	TUNGSTEN("Tungsten", "ingotTungsten", 1, "oreTungsten"),
	NIKOLITE("Nikolite", "dustNikolite", 5, "oreNikolite"),
	GREENSAPPHIRE("Green Sapphire", "gemGreenSapphire", 1, "oreGreenSapphire"),
	RUBY("Ruby", "gemRuby", 1, "oreRuby"),
	SAPPHIRE("Sapphire", "gemSapphire", 1, "oreSapphire");

	private ArrayList<ItemStack> ores;
	private String name;
	private String[] oreLabel;
	private int dropCount;
	private String product;

	public static final ModOreList[] oreList = ModOreList.values();

	private ModOreList(String n, String prod, int count, String... ore) {
		oreLabel = new String[ore.length];
		for (int i = 0; i < ore.length; i++) {
			oreLabel[i] = ore[i];
		}
		ores = new ArrayList<ItemStack>();
		for (int i = 0; i < ore.length; i++) {
			ores.addAll(OreDictionary.getOres(ore[i]));
		}
		dropCount = count;
		name = n;
		product = prod;
	}

	public static boolean isModOre(ItemStack is) {
		if (is == null)
			return false;
		for (int i = 0; i < oreList.length; i++) {
			if (ReikaItemHelper.listContainsItemStack(oreList[i].ores, is)) {
				return true;
			}
		}
		return false;
	}

	public static ModOreList getModOreFromOre(ItemStack is) {
		if (is == null)
			return null;
		for (int i = 0; i < oreList.length; i++) {
			if (ReikaItemHelper.listContainsItemStack(oreList[i].ores, is)) {
				return oreList[i];
			}
		}
		return null;
	}

	public static ModOreList getEntryFromDamage(int dmg) {
		if (ReikaMathLibrary.isValueInsideBoundsIncl(0, oreList.length-1, dmg))
			return oreList[dmg];
		return null;
	}

	public String[] getOreDictNames() {
		String[] arr = new String[oreLabel.length];
		System.arraycopy(oreLabel, 0, arr, 0, oreLabel.length);
		return arr;
	}

	public String[] getOreDictIngots() {
		return new String[]{product};
	}

	public static boolean isModOreIngot(ItemStack is) {
		if (is == null)
			return false;
		for (int i = 0; i < oreList.length; i++) {
			String[] ingots = oreList[i].getOreDictIngots();
			for (int j = 0; j < ingots.length; j++) {
				if (ReikaItemHelper.listContainsItemStack(OreDictionary.getOres(ingots[j]), is))
					return true;
			}
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public int getDropCount() {
		return dropCount;
	}

	public boolean isIngotType() {
		return product.contains("ingot");
	}

	public boolean isDustType() {
		return product.contains("dust");
	}

	public boolean isGemType() {
		return product.contains("gem");
	}

	public String getTypeName() {
		if (this.isIngotType())
			return "Ingot";
		if (this.isDustType())
			return "Dust";
		if (this.isGemType())
			return "Gem";
		return "Item";
	}
}
