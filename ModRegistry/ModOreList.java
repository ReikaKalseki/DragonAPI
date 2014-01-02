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
import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.ModInteract.MekanismHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

public enum ModOreList {

	TIN("Tin", 0xB2D5E9, "ingotTin", 1, "oreTin"),
	COPPER("Copper", 0xBC6C01, "ingotCopper", 1, "oreCopper", "oreTetrahedrite"),
	LEAD("Lead", 0x697597, "ingotLead", 1, "oreLead"),
	FERROUS("Nickel", 0xD0CCAD, "ingotNickel", 1, "oreNickel"), //ask KingLemming, not me...
	SILVER("Silver", 0xA4D0DA, "ingotSilver", 1, "oreSilver"),
	GALENA("Galena", 0x7F6E95, "ingotGalena", 1, "oreGalena"),
	ALUMINUM("Aluminum", 0xF1F1F1, "ingotAluminum", 1, "oreAluminum", "oreAluminium", "oreNaturalAluminum"), //...Why??
	IRIDIUM("Iridium", 0xC1E2D3, "ingotIridium", 1, "oreIridium"),
	FIRESTONE("Firestone", 0xE19636, "shardFirestone", 1, "oreFirestone"),
	CERTUSQUARTZ("Certus Quartz", 0xC4CEFF, "crystalQuartz", 3, "oreCertusQuartz"),
	URANIUM("Uranium", 0x4CFF00, "ingotUranium", 1, "oreUranium", "oreYellorite"),
	CINNABAR("Mercury", 0x811A1A, "itemQuicksilver", 1, "oreCinnabar"),
	AMBER("Amber", 0xB17F17, "gemAmber", 1, "oreAmber"),
	INFUSEDAIR("Air Infused", 0xA88C32, "shardAir", 4, "oreInfusedAir"),
	INFUSEDFIRE("Fire Infused", 0xB52100, "shardFire", 4, "oreInfusedFire"),
	INFUSEDWATER("Water Infused", 0x007C99, "shardWater", 4, "oreInfusedWater"),
	INFUSEDEARTH("Earth Infused", 0x008200, "shardEarth", 4, "oreInfusedEarth"),
	INFUSEDENTROPY("Entropy Infused", 0x555576, "shardEntropy", 4, "oreInfusedEntropy"),
	INFUSEDORDER("Order Infused", 0xDCDCFF, "shardOrder", 4, "oreInfusedOrder"),
	APATITE("Apatite", 0x3296C5, "gemApatite", 3, "oreApatite"),
	SALTPETER("Saltpeter", 0xFFFFFF, "dustSaltpeter", 2, "oreSaltpeter"),
	TUNGSTEN("Tungsten", 0x1E1E1E, "ingotTungsten", 1, "oreTungsten", "oreTungstate"),
	NIKOLITE("Nikolite", 0x2DABB8, "dustNikolite", 5, "oreNikolite"),
	GREENSAPPHIRE("Green Sapphire", 0x00C416, "gemGreenSapphire", 1, "oreGreenSapphire", "oreNetherGreenSapphire"),
	RUBY("Ruby", 0xBC0000, "gemRuby", 1, "oreRuby"),
	SAPPHIRE("Sapphire", 0x0019AA, "gemSapphire", 1, "oreSapphire", "oreNetherSapphire"),
	MONAZIT("Monazit", 0x3C6E3C, "ForciciumItem", 4, "MonazitOre"),
	FORCE("Force", 0xFFC700, "gemForce", 3, "oreForce"),
	NETHERCOAL("Nether Coal", 0x262626, "itemCoal", 1, "oreNetherCoal"),
	NETHERIRON("Nether Iron", 0xD8AD91, "ingotIron", 1, "oreNetherIron"),
	NETHERGOLD("Nether Gold", 0xD6A400, "ingotGold", 1, "oreNetherGold"),
	NETHERREDSTONE("Nether Redstone", 0xBC0000, "dustRedstone", 4, "oreNetherRedstone"),
	NETHERLAPIS("Nether Lapis", 0x2B4BFF, "dyeBlue", 6, "oreNetherLapis"),
	NETHERDIAMOND("Nether Diamond", 0x68FFE3, "gemDiamond", 1, "oreNetherDiamond"),
	NETHEREMERALD("Nether Emerald", 0x12DB00, "gemEmerald", 1, "oreNetherEmerald"),
	NETHERTIN("Nether Tin", 0xA9CCE2, "ingotTin", 1, "oreNetherTin"),
	NETHERCOPPER("Nether Copper", 0xC8850C, "ingotCopper", 1, "oreNetherCopper"),
	NETHERLEAD("Nether Lead", 0x6F7DA3, "ingotLead", 1, "oreNetherLead"),
	NETHERNICKEL("Nether Nickel", 0xB6AE85, "ingotNickel", 1, "oreNetherNickel"),
	NETHERSILVER("Nether Silver", 0xA7E4EC, "ingotSilver", 1, "oreNetherSilver"),
	NETHERNIKOLITE("Nether Nikolite", 0xA7E4EC, "dustNikolite", 5, "oreNetherNikolite"),
	COBALT("Cobalt", 0x1C30A5, "ingotCobalt", 1, "oreCobalt"),
	ARDITE("Ardite", 0xF48A00, "ingotArdite", 1, "oreArdite"),
	PLATINUM("Platinum", 0x00A0DB, "ingotPlatinum", 1, "orePlatinum", "oreCooperite"), //WTF Greg..."Cooper" == "Sheldon"??
	NETHERPLATINUM("Nether Platinum", 0x0892DE, "ingotPlatinum", 1, "oreNetherPlatinum"),
	ZINC("Zinc", 0x9B9B9B, "ingotZinc", 1, "oreZinc", "oreSphalerite"),
	OSMIUM("Osmium", 0xA8B0E7, "ingotOsmium", 1, "oreOsmium", "oreNetherOsmium"),
	NETHERPIGIRON("Pig Iron", 0xC5B3AA, "ingotSteel", 1, "oreNetherSteel"), //...
	SULFUR("Sulfur", 0xFFFF00, "dustSulfur", 3, "oreSulfur"),
	PITCHBLENDE("Pitchblende", 0x4E4E7B, "ingotUranium", 1, "orePitchblende"),
	CADMIUM("Cadmium", 0x87A6E8, "ingotCadmium", 1, "oreCadmium"),
	INDIUM("Indium", 0xB3BFD8, "ingotIndium", 1, "oreIndium"),
	FLUORITE("Fluorite", 0xDFD4AA, "gemFluorite", 3, "oreFluorite"),
	BAUXITE("Bauxite", 0x72332B, "dustBauxite", 1, "oreBauxite"),
	SODALITE("Sodalite", 0x133CAD, "dyeBlue", 1, "oreSodalite"),
	PYRITE("Pyrite", 0xE1B531, "dustPyrite", 1, "orePyrite"),
	AMMONIUM("Ammonium Chloride", 0xFFF9E0, "dustAmmonium", 1, "oreAmmonium"),
	CALCITE("Calcite", 0xFF6A00, "gemCalcite", 1, "oreCalcite"),
	CHIMERITE("Chimerite", 0xCBE5F5, "gemChimerite", 2, "oreChimerite"),
	VINTEUM("Vinteum", 0x507BBC, "dustVinteum", 1, "oreVinteum"),
	BLUETOPAZ("Blue Topaz", 0x92B1F0, "gemBlueTopaz", 1, "oreBlueTopaz"),
	MOONSTONE("Moonstone", 0x6592CD, "gemMoonstone", 1, "oreMoonstone"),
	SUNSTONE("Sunstone", 0xD15106, "gemSunstone", 1, "oreSunstone"),
	TITANIUM("Titanium", 0x809797, "ingotTitanium", 1, "oreTitanium"),
	MAGMANITE("Magmanite", 0xF95D00, "dropMagma", 1, "oreMagmanite"),
	MAGNETITE("Magnetite", 0x44442D, "gemMagnetite", 1, "oreMagnetite"),
	ESSENCE("Essence", 0x158215, "itemEssence", 3, "oreEssence", "oreNetherEssence"),
	MIMICHITE("Mimichite", 0x8900FF, "gemMimichite", 1, "oreMimichite"),
	NETHERURANIUM("Nether Uranium", 0x00aa00, "ingotUranium", 1, "oreNetherUranium"),
	QUANTUM("Quantum", 0x5BA642, "dustQuantum", 2, "oreQuantum");

	private ArrayList<ItemStack> ores = new ArrayList<ItemStack>();
	public final String displayName;
	private String[] oreLabel;
	public final int dropCount;
	public final int oreColor;
	private String product;
	private boolean init;
	private HashMap<String, ArrayList<ItemStack>> perName = new HashMap();

	//private static final ArrayList<ItemStack> blocks = new ArrayList<ItemStack>();

	public static final ModOreList[] oreList = values();

	private ModOreList(String n, int color, String prod, int count, String... ore) {
		//if (!DragonAPIInit.canLoadHandlers())
		//	throw new MisuseException("Accessed registry enum too early! Wait until postInit!");
		dropCount = count;
		oreColor = color;
		displayName = n;
		product = prod;
		oreLabel = new String[ore.length];
		for (int i = 0; i < ore.length; i++) {
			oreLabel[i] = ore[i];
		}

		ReikaJavaLibrary.pConsole("DRAGONAPI: Adding ore entries for "+this.toString()+" (Ore Names: "+Arrays.toString(ore)+")");
	}

	public static void initializeAll() {
		if (!DragonAPIInit.canLoadHandlers())
			throw new MisuseException("Initialized registry enum too early! Wait until postInit!");
		for (int i = 0; i < oreList.length; i++) {
			oreList[i].initialize();
		}
	}

	private void initialize() {
		if (init) {
			ReikaJavaLibrary.pConsole(this+" is already initialized!");
			return;
		}
		init = true;
		for (int i = 0; i < oreLabel.length; i++) {
			ArrayList<ItemStack> toadd = OreDictionary.getOres(oreLabel[i]);
			if (!toadd.isEmpty()) {
				ReikaJavaLibrary.pConsole("\tDetected the following blocks for "+this+" from OreDict \""+oreLabel[i]+"\": "+toadd.toString());
				for (int k = 0; k < toadd.size(); k++) {
					ItemStack is = toadd.get(k);
					if (ReikaItemHelper.isBlock(is)) {
						ores.add(is);
						ArrayList li = perName.get(oreLabel[i]);
						if (li == null)
							perName.put(oreLabel[i], new ArrayList());
						perName.get(oreLabel[i]).add(is);
					}
					else {
						ReikaJavaLibrary.pConsole("\t"+is+" is not an ore block, but was OreDict fetched by \""+oreLabel[i]+"\"!");
					}
				}
			}
			else
				ReikaJavaLibrary.pConsole("\tNo ore blocks detected for \""+oreLabel[i]+"\"");
		}
		if (!this.existsInGame())
			ReikaJavaLibrary.pConsole("\tDRAGONAPI: No ore blocks detected for "+this);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(displayName);
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
			Iterator<ItemStack> it = li.iterator();
			while (it.hasNext()) {
				ItemStack is = it.next();
				if (is.itemID < 0) {
					ReikaJavaLibrary.pConsole("DRAGONAPI: Invalid item (ID = "+is.itemID+") registered as "+this);
					it.remove();
				}
				else {
					if (!ReikaItemHelper.listContainsItemStack(ores, is)) {
						ores.add(is);
						flag = true;
					}
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

	public static ModList getOreModFromItemStack(ItemStack is) {
		if (ReikaBlockHelper.isOre(is)) {
			if (ReikaItemHelper.isBlock(is)) {
				Block b = Block.blocksList[is.itemID];
				UniqueIdentifier dat = GameRegistry.findUniqueIdentifierFor(b);
				if (dat != null) {
					String modName = dat.name;
					String id = dat.modId;
					ModList mod = ModList.getModFromID(id);
					return mod;
				}
			}
			else {
				ReikaJavaLibrary.pConsole("\t"+is+" is not an ore block, but was registered as an ore block! This is a bug in its parent mod!");
			}
		}
		return null;
	}
}
