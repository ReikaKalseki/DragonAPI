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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
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
	ALUMINUM("Aluminum", "ingotAluminum", 1, "oreAluminum", "oreAluminium", "oreNaturalAluminum"), //...Why??
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
	NETHERPIGIRON("Pig Iron", "ingotSteel", 1, "oreNetherSteel"), //...
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
	MAGMANITE("Magmanite", "dropMagma", 1, "oreMagmanite"),
	MAGNETITE("Magnetite", "gemMagnetite", 1, "oreMagnetite"),
	ESSENCE("Essence", "itemEssence", 3, "oreEssence", "oreNetherEssence"),
	MIMICHITE("Mimichite", "gemMimichite", 1, "oreMimichite");

	private ArrayList<ItemStack> ores = new ArrayList<ItemStack>();
	private String name;
	private String[] oreLabel;
	private int dropCount;
	private String product;
	private HashMap<String, ArrayList<ItemStack>> perName = new HashMap();

	//private static final ArrayList<ItemStack> blocks = new ArrayList<ItemStack>();

	public static final ModOreList[] oreList = values();

	private ModOreList(String n, String prod, int count, String... ore) {
		if (!DragonAPIInit.canLoadHandlers())
			throw new MisuseException("Accessed registry enum too early! Wait until postInit!");
		dropCount = count;
		name = n;
		product = prod;
		oreLabel = new String[ore.length];
		for (int i = 0; i < ore.length; i++) {
			oreLabel[i] = ore[i];
		}

		ReikaJavaLibrary.pConsole("DRAGONAPI: Adding ore entries for "+this.toString()+" (Ore Names: "+Arrays.toString(ore)+")");

		for (int i = 0; i < ore.length; i++) {
			ArrayList<ItemStack> toadd = OreDictionary.getOres(ore[i]);
			if (!toadd.isEmpty()) {
				ReikaJavaLibrary.pConsole("\tDetected the following blocks for "+this+" from OreDict \""+ore[i]+"\": "+toadd.toString());
				for (int k = 0; k < toadd.size(); k++) {
					ItemStack is = toadd.get(k);
					if (ReikaItemHelper.isBlock(is)) {
						ores.add(is);
						ArrayList li = perName.get(ore[i]);
						if (li == null)
							perName.put(ore[i], new ArrayList());
						perName.get(ore[i]).add(is);
					}
					else {
						ReikaJavaLibrary.pConsole("\t"+is+" is not an ore block, but was OreDict fetched by \""+ore[i]+"\"!");
					}
				}
			}
			else
				ReikaJavaLibrary.pConsole("\tNo ore blocks detected for \""+ore[i]+"\"");
		}
		if (!this.existsInGame())
			ReikaJavaLibrary.pConsole("\tDRAGONAPI: No ore blocks detected for "+this);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getName());
		//sb.append(" (Ore Names: ");
		//for (int i = 0; i < oreLabel.length; i++) {
		//	sb.append(oreLabel[i]);
		//	if (i < oreLabel.length-1)
		//		sb.append(" ");
		//}
		//sb.append(")");
		if (this.isRare())
			sb.append(" (Rare)");
		return sb.toString();
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
			ArrayList<ItemStack> li = OreDictionary.getOres(oreLabel[i]);
			boolean flag = false;
			for (int k = 0; k < li.size(); k++) {
				ItemStack is = li.get(k);
				if (!ReikaItemHelper.listContainsItemStack(ores, is)) {
					ores.add(is);
					flag = true;
				}
			}
			if (flag) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Reloading ore listings for "+this);
				ReikaJavaLibrary.pConsole("DRAGONAPI: Found "+li);
			}
			ores.addAll(li);
		}
		//Thread.dumpStack();
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
			return MekanismHandler.getInstance().getModOre(is.itemID, is.getItemDamage());
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

	public ItemStack getFirstOreBlock() {
		if (!this.existsInGame())
			return null;
		return ores.get(0);
	}

	public ItemStack getRandomOreBlock() {
		if (!this.existsInGame())
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

	public boolean existsInGame() {
		return !ores.isEmpty();
	}

	public boolean isRare() {
		return this == ModOreList.PLATINUM || this == ModOreList.NETHERPLATINUM || this == ModOreList.IRIDIUM ||
				this == ModOreList.MOONSTONE;
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

	public boolean canGenerateIn(Block b) {
		if (this.isNetherOres())
			return b == Block.netherrack;

		return b == Block.stone;
	}

	public ItemStack getGennableIn(Block b) {
		for (int i = 0; i < ores.size(); i++) {
			ItemStack is = ores.get(i);
			Block ore = Block.blocksList[is.itemID];
			//Not done
		}
		return null;
	}

	public HashMap<String, ArrayList<ItemStack>> getOresByName() {
		HashMap<String, ArrayList<ItemStack>> map = new HashMap();
		map.putAll(perName);
		return map;
	}
}
