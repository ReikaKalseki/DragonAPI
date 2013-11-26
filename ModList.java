/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import cpw.mods.fml.common.Loader;

public enum ModList {

	ROTARYCRAFT("RotaryCraft", "Reika.RotaryCraft.RotaryCraft"),
	REACTORCRAFT("ReactorCraft"),
	DYETREES("DyeTrees"),
	EXPANDEDREDSTONE("ExpandedRedstone"),
	GEOSTRATA("GeoStrata"),
	REALBIOMES("RealBiomes"),
	FURRYKINGDOMS("FurryKingdoms"),
	SPIDERPET("SpiderPet"),
	ENDERFOREST("EnderForest"),
	VOIDMONSTER("VoidMonster"),
	USEFULTNT("UsefulTNT"),
	BCENERGY("BuildCraft|Energy", "buildcraft.BuildCraftEnergy"),
	BCFACTORY("BuildCraft|Factory", "buildcraft.BuildCraftFactory"),
	BCTRANSPORT("BuildCraft|Transport", "buildcraft.BuildCraftTransport"),
	THAUMCRAFT("Thaumcraft", "thaumcraft.common.config.ConfigBlocks", "thaumcraft.common.config.ConfigItems"),
	IC2("IC2", "ic2.core.Ic2Items"),
	GREGTECH("GregTech"),
	FORESTRY("Forestry", "forestry.core.config.ForestryBlock", "forestry.core.config.ForestryItem"),
	APPENG("AppliedEnergistics", "appeng.common.AppEng"),
	MFFS("MFFS", "mffs.ModularForceFieldSystem"),
	REDPOWER("RedPower"),
	TWILIGHT("TwilightForest", "twilightforest.block.TFBlocks", "twilightforest.item.TFItems"),
	NATURA("Natura", "mods.natura.common.NContent"),
	BOP("BiomesOPlenty", "biomesoplenty.configuration.configfile.BOPConfigurationIDs"),
	BXL("ExtraBiomesXL"),
	MINEFACTORY("MineFactoryReloaded", "powercrystals.minefactoryreloaded.MineFactoryReloadedCore"),
	DARTCRAFT("DartCraft", "bluedart.block.DartBlock", "bluedart.item.DartItem"),
	TINKERER("TConstruct", "tconstruct.common.TContent"),
	THERMALEXPANSION("ThermalExpansion", "thermalexpansion.block.TEBlocks", "thermalexpansion.item.TEItems"),
	MEKANISM("Mekanism", "mekanism.common.Mekanism"),
	MEKTOOLS("MekanismTools", "mekanism.tools.common.MekanismTools"),
	RAILCRAFT("Railcraft", "mods.railcraft.common.blocks", null), //items spread over half a dozen classes
	ICBM("ICBM|Explosion"),
	ARSMAGICA("arsmagica2", "am2.blocks.BlocksCommonProxy", "am2.items.ItemsCommonProxy"),
	TRANSITIONAL("TransitionalAssistance", "modTA.Core.TACore"),
	ENDERSTORAGE("EnderStorage"),
	OPTIFINE("Optifine");

	private final boolean condition;
	private final String modlabel;
	private final String itemClass;
	private final String blockClass;

	//To save on repeated Class.forName
	private static final HashMap<ModList, Class> blockClasses = new HashMap();
	private static final HashMap<ModList, Class> itemClasses = new HashMap();

	public static final ModList[] modList = ModList.values();

	private ModList(String label, String blocks, String items) {
		modlabel = label;
		boolean c = Loader.isModLoaded(modlabel);
		condition = c;
		itemClass = items;
		blockClass = blocks;
		if (c) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: "+this+" detected in the MC installation. Adjusting behavior accordingly.");
		}
		else
			ReikaJavaLibrary.pConsole("DRAGONAPI: "+this+" not detected in the MC installation. No special action taken.");

		if (c) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Attempting to load data from "+this);
			if (blocks == null)
				ReikaJavaLibrary.pConsole("DRAGONAPI: Could not block class for "+this+": Specified class was null. This may not be an error.");
			if (items == null)
				ReikaJavaLibrary.pConsole("DRAGONAPI: Could not item class for "+this+": Specified class was null. This may not be an error.");
		}
	}

	private ModList(String label, String modClass) {
		this(label, modClass, modClass);
	}

	private ModList(String label) {
		this(label, null);
	}

	public Class getBlockClass() {
		if (blockClass == null) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Could not load block class for "+this+". Null class provided.");
			Thread.dumpStack();
			return null;
		}
		Class c = blockClasses.get(this);
		if (c == null) {
			try {
				c = Class.forName(blockClass);
				blockClasses.put(this, c);
				return c;
			}
			catch (ClassNotFoundException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Could not load block class for "+this+".");
				e.printStackTrace();
				return null;
			}
		}
		return c;
	}

	public Class getItemClass() {
		if (itemClass == null) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Could not load item class for "+this+". Null class provided.");
			Thread.dumpStack();
			return null;
		}
		Class c = itemClasses.get(this);
		if (c == null) {
			try {
				c = Class.forName(itemClass);
				itemClasses.put(this, c);
				return c;
			}
			catch (ClassNotFoundException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Could not load item class for "+this+".");
				e.printStackTrace();
				return null;
			}
		}
		return c;
	}

	public boolean isLoaded() {
		return condition;
	}

	public String getModLabel() {
		return modlabel;
	}

	public String getDisplayName() {
		return ReikaStringParser.capFirstChar(this.name());
	}

	@Override
	public String toString() {
		return this.getModLabel();
	}

	public boolean isReikasMod() {
		return this.ordinal() <= USEFULTNT.ordinal();
	}

	public static List<ModList> getReikasMods() {
		List<ModList> li = new ArrayList();
		for (int i = 0; i < modList.length; i++) {
			ModList mod = modList[i];
			if (mod.isReikasMod())
				li.add(mod);
		}
		return li;
	}

}
