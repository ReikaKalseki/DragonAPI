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

import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import cpw.mods.fml.common.Loader;

public enum ModList {

	ROTARYCRAFT("RotaryCraft", "Reika.RotaryCraft.RotaryCraft"),
	REACTORCRAFT("ReactorCraft"),
	DYETREES("DyeTrees"),
	EXPANDEDREDSTONE("ExpandedRedstone"),
	GEOSTRATA("GeoStrata"),
	REALBIOMES("RealBiomes"),
	FURRY("FurryKingdoms"),
	BUILDCRAFTENERGY("BuildCraft|Energy", "buildcraft.BuildCraftEnergy"),
	BUILDCRAFTFACTORY("BuildCraft|Factory", "buildcraft.BuildCraftFactory"),
	BUILDCRAFTTRANSPORT("BuildCraft|Transport", "buildcraft.BuildCraftTransport"),
	THAUMCRAFT("Thaumcraft", "thaumcraft.common.config.Config"),
	INDUSTRIALCRAFT("IC2", "ic2.core.Ic2Items"),
	GREGTECH("GregTech"),
	FORESTRY("Forestry", "forestry.core.config.ForestryBlock", "forestry.core.config.ForestryItem"),
	APPLIEDENERGISTICS("AppliedEnergistics", "appeng.common.AppEng"),
	MFFS("MFFS", "mffs.ModularForceFieldSystem"),
	REDPOWER("RedPower"),
	TWILIGHT("TwilightForest", "twilightforest.block.TFBlocks", "twilightforest.item.TFItems"),
	NATURA("Natura", "mods.natura.common.NContent"),
	BOP("BiomesOPlenty"),
	BXL("ExtraBiomesXL"),
	MINEFACTORY("MineFactoryReloaded", "powercrystals.minefactoryreloaded.MineFactoryReloadedCore"),
	DARTCRAFT("DartCraft", "bluedart.block.DartBlock", "bluedart.item.DartItem"),
	TINKERER("TConstruct", "tconstruct.common.TContent"),
	THERMALEXPANSION("ThermalExpansion", "thermalexpansion.block.TEBlocks", "thermalexpansion.item.TEItems"),
	MEKANISM("Mekanism", "mekanism.common.Mekanism"),
	MEKTOOLS("MekanismTools", "mekanism.tools.common.MekanismTools"),
	RAILCRAFT("Railcraft", "mods.railcraft.common.blocks", null), //items spread over half a dozen classes
	ICBM("ICBM|Explosion");

	private final boolean condition;
	private final String modlabel;
	private final String itemClass;
	private final String blockClass;

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
			ReikaJavaLibrary.pConsole("DRAGONAPI: Could not load block class for "+this+".");
			Thread.dumpStack();
			return null;
		}
		try {
			return Class.forName(blockClass);
		}
		catch (ClassNotFoundException e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Could not load block class for "+this+".");
			e.printStackTrace();
			return null;
		}
	}

	public Class getItemClass() {
		if (itemClass == null) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Could not load item class for "+this+".");
			Thread.dumpStack();
			return null;
		}
		try {
			return Class.forName(itemClass);
		}
		catch (ClassNotFoundException e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Could not load item class for "+this+".");
			e.printStackTrace();
			return null;
		}
	}

	public boolean isLoaded() {
		return condition;
	}

	public String getModLabel() {
		return modlabel;
	}

	@Override
	public String toString() {
		return this.getModLabel();
	}

	public boolean isReikasMod() {
		return this.ordinal() <= FURRY.ordinal();
	}

}
