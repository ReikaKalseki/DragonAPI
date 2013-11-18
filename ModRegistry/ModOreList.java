/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModRegistry;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.MekanismHandler;

public enum ModOreList {

	TIN("Tin", "ingotTin", 1, "oreTin"),
	COPPER("Copper", "ingotCopper", 1, "oreCopper", "oreTetrahedrite"),
	LEAD("Lead", "ingotLead", 1, "oreLead"),
	FERROUS("Nickel", "ingotNickel", 1, "oreNickel"), //ask KingLemming, not me...
	SILVER("Silver", "ingotSilver", 1, "oreSilver"),
	GALENA("Galena", "ingotGalena", 1, "oreGalena"),
	ALUMINUM("Aluminum", "ingotAluminum", 1, "oreAluminum", "oreAluminium", "naturalAluminum", "oreNaturalAluminum"), //...Why??
	IRIDIUM("Iridium", "ingotIridium", 1, "oreIridium"),
	FIRESTONE("Firestone", "shardFirestone", 1, "oreFirestone"),
	CERTUSQUARTZ("Certus Quartz", "crystalQuartz", 3, "oreCertusQuartz"),
	URANIUM("Uranium", "ingotUranium", 1, "oreUranium"),
	CINNABAR("Mercury", "itemQuicksilver", 1, "oreCinnabar"),
	AMBER("Amber", "gemAmber", 1, "oreAmber"),
	INFUSEDAIR("Air Infused", "shardAir", 4, "oreInfusedAir"),
	INFUSEDFIRE("Fire Infused", "shardFire", 4, "oreInfusedFire"),
	INFUSEDWATER("Water Infused", "shardWater", 4, "oreInfusedWater"),
	INFUSEDEARTH("Earth Infused", "shardEarth", 4, "oreInfusedEarth"),
	INFUSEDENTROPY("Entropy Infused", "shardEntropy", 4, "oreInfusedEntropy"),
	INFUSEDORDER("Order Infused", "shardOrder", 4, "oreInfusedOrder"),
	APATITE("Apatite", "gemApatite", 3, "oreApatite"),
	SALTPETER("Saltpeter", "dustSaltpeter", 2, "oreSaltpeter"),
	TUNGSTEN("Tungsten", "ingotTungsten", 1, "oreTungsten", "oreTungstate"),
	NIKOLITE("Nikolite", "dustNikolite", 5, "oreNikolite"),
	GREENSAPPHIRE("Green Sapphire", "gemGreenSapphire", 1, "oreGreenSapphire", "oreNetherGreenSapphire"),
	RUBY("Ruby", "gemRuby", 1, "oreRuby"),
	SAPPHIRE("Sapphire", "gemSapphire", 1, "oreSapphire", "oreNetherSapphire"),
	MONAZIT("Monazit", "ForciciumItem", 4, "MonazitOre"),
	FORCE("Force", "gemForce", 3, "oreForce"),
	NETHERCOAL("Nether Coal", "itemCoal", 1, "oreNetherCoal"),
	NETHERIRON("Nether Iron", "ingotIron", 1, "oreNetherIron"),
	NETHERGOLD("Nether Gold", "ingotGold", 1, "oreNetherGold"),
	NETHERREDSTONE("Nether Redstone", "dustRedstone", 4, "oreNetherRedstone"),
	NETHERLAPIS("Nether Lapis", "dyeBlue", 6, "oreNetherLapis"),
	NETHERDIAMOND("Nether Diamond", "gemDiamond", 1, "oreNetherDiamond"),
	NETHEREMERALD("Nether Emerald", "gemEmerald", 1, "oreNetherEmerald"),
	NETHERTIN("Nether Tin", "ingotTin", 1, "oreNetherTin"),
	NETHERCOPPER("Nether Copper", "ingotCopper", 1, "oreNetherCopper"),
	NETHERLEAD("Nether Lead", "ingotLead", 1, "oreNetherLead"),
	NETHERNICKEL("Nether Nickel", "ingotNickel", 1, "oreNetherNickel"),
	NETHERSILVER("Nether Silver", "ingotSilver", 1, "oreNetherSilver"),
	NETHERNIKOLITE("Nether Nikolite", "dustNikolite", 5, "oreNetherNikolite"),
	COBALT("Cobalt", "ingotCobalt", 1, "oreCobalt"),
	ARDITE("Ardite", "ingotArdite", 1, "oreArdite"),
	PLATINUM("Platinum", "ingotPlatinum", 1, "orePlatinum", "oreCooperite"), //WTF Greg..."Cooper" == "Sheldon"??
	NETHERPLATINUM("Nether Platinum", "ingotPlatinum", 1, "oreNetherPlatinum"),
	ZINC("Zinc", "ingotZinc", 1, "oreZinc", "oreSphalerite"),
	OSMIUM("Osmium", "ingotOsmium", 1, "oreOsmium", "oreNetherOsmium"),
	NETHERPIGIRON("Steel", "ingotSteel", 1, "oreNetherSteel"), //...
	SULFUR("Sulfur", "dustSulfur", 3, "oreSulfur"),
	PITCHBLENDE("Pitchblende", "ingotUranium", 1, "orePitchblende"),
	CADMIUM("Cadmium", "ingotCadmium", 1, "oreCadmium"),
	INDIUM("Indium", "ingotIndium", 1, "oreIndium"),
	FLUORITE("Fluorite", "gemFluorite", 3, "oreFluorite"),
	BAUXITE("Bauxite", "dustBauxite", 1, "oreBauxite"),
	SODALITE("Sodalite", "dyeBlue", 1, "oreSodalite"),
	PYRITE("Pyrite", "dustPyrite", 1, "orePyrite"),
	AMMONIUM("Ammonium Chloride", "dustAmmonium", 1, "oreAmmonium"),
	CALCITE("Calcite", "gemCalcite", 1, "oreCalcite"),
	CHIMERITE("Chimerite", "gemChimerite", 2, "oreChimerite"),
	VINTEUM("Vinteum", "dustVinteum", 1, "oreVinteum"),
	BLUETOPAZ("Blue Topaz", "gemBlueTopaz", 1, "oreBlueTopaz"),
	MOONSTONE("Moonstone", "gemMoonstone", 1, "oreMoonstone"),
	SUNSTONE("Sunstone", "gemSunstone", 1, "oreSunstone"),
	TITANIUM("Titanium", "ingotTitanium", 1, "oreTitanium"),
	MAGMANITE("Magmanite", "dropMagma", 1, "oreMagmanite");

	private ArrayList<ItemStack> ores;
	private String name;
	private String[] oreLabel;
	private int dropCount;
	private String product;

	//private static final ArrayList<ItemStack> blocks = new ArrayList<ItemStack>();

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

	public boolean isThaumcraft() {
		return this.name().contains("INFUSED") || this.name().equals("CINNABAR") || this.name().equals("AMBER");
	}

	public boolean isNetherOres() {
		return this.name().startsWith("NETHER");
	}

	public boolean isGregtech() {
		switch(this) {
		case BAUXITE:
		case GALENA:
		case PYRITE:
		case SODALITE:
		case TUNGSTEN:
		case ZINC:
			return true;
		default:
			return false;
		}
	}

	public void reloadOreList() {
		ores = new ArrayList<ItemStack>();
		for (int i = 0; i < oreLabel.length; i++) {
			ores.addAll(OreDictionary.getOres(oreLabel[i]));
		}
	}

	public static boolean isModOre(ItemStack is) {
		if (is == null)
			return false;
		if (is.itemID == MekanismHandler.getInstance().oreID)
			return true;
		for (int i = 0; i < oreList.length; i++) {
			if (oreList[i].ores.isEmpty()) {
				oreList[i].reloadOreList();
			}
			if (ReikaItemHelper.listContainsItemStack(oreList[i].ores, is)) {
				return true;
			}
		}
		return false;
	}

	public static ModOreList getModOreFromOre(ItemStack is) {
		if (is == null)
			return null;
		if (is.itemID == MekanismHandler.getInstance().oreID)
			return OSMIUM;
		for (int i = 0; i < oreList.length; i++) {
			if (oreList[i].ores.isEmpty()) {
				oreList[i].reloadOreList();
			}
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

	public String getProductLabel() {
		return product;
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
		return product.contains("gem") || product.contains("crystal");
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

	public static ArrayList<ItemStack> getAllRegisteredOreBlocks() {
		ArrayList<ItemStack> li = new ArrayList<ItemStack>();
		for (int i = 0; i < oreList.length; i++) {
			li.addAll(oreList[i].ores);
		}
		return li;
	}

	public boolean hasRegisteredBlock() {
		return ores.size() > 0;
	}

	public ItemStack getFirstOreBlock() {
		if (!this.hasRegisteredBlock())
			return null;
		return ores.get(0);
	}

	public ItemStack getRandomOreBlock() {
		if (!this.hasRegisteredBlock())
			return null;
		int s = ores.size();
		int index = new Random().nextInt(s);
		return ores.get(index);
	}

	public ArrayList<ItemStack> getAllOreBlocks() {
		ArrayList<ItemStack> li = new ArrayList<ItemStack>();
		li.addAll(ores);
		return li;
	}

	public boolean hasLoadedOres() {
		return !ores.isEmpty();
	}

	public boolean isRare() {
		return this == ModOreList.PLATINUM || this == ModOreList.NETHERPLATINUM || this == ModOreList.IRIDIUM || this == ModOreList.MOONSTONE;
	}

	public boolean isArsMagica() {
		switch(this) {
		case CHIMERITE:
		case VINTEUM:
		case BLUETOPAZ:
		case MOONSTONE:
		case SUNSTONE:
			return true;
		default:
			return false;
		}
	}
}
