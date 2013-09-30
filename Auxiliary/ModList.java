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

	ROTARYCRAFT("RotaryCraft"),
	REACTORCRAFT("ReactorCraft"),
	DYETREES("DyeTrees"),
	EXPANDEDREDSTONE("ExpandedRedstone"),
	GEOSTRATA("GeoStrata"),
	REALBIOMES("RealBiomes"),
	FURRY("FurryKingdoms"),
	BUILDCRAFTENERGY("BuildCraft|Energy"),
	BUILDCRAFTFACTORY("BuildCraft|Factory"),
	BUILDCRAFTTRANSPORT("BuildCraft|Transport"),
	THAUMCRAFT("Thaumcraft"),
	INDUSTRIALCRAFT("IC2"),
	GREGTECH("GregTech"),
	FORESTRY("Forestry"),
	APPLIEDENERGISTICS("AppliedEnergistics"),
	MFFS("ModularForceFieldSystem"),
	REDPOWER("RedPower"),
	TWILIGHT("TwilightForest"),
	NATURA("Natura"),
	BOP("BiomesOPlenty"),
	BXL("ExtraBiomesXL"),
	MINEFACTORY("MineFactoryReloaded"),
	DARTCRAFT("DartCraft"),
	TINKERER("TConstruct"),
	THERMAL("ThermalExpansion"),
	MEKANISM("Mekanism"),
	MEKTOOLS("MekanismTools"),
	RAILCRAFT("Railcraft");

	private boolean condition;
	private boolean preset = false;
	private String modlabel;
	private Class modClass;

	public static final ModList[] modList = ModList.values();

	private ModList(String s) {
		modlabel = s;
		boolean c = Loader.isModLoaded(modlabel);
		condition = c;
		preset = true;
		if (c)
			ReikaJavaLibrary.pConsole("DRAGONAPI: "+this+" detected in the MC installation. Adjusting behavior accordingly.");
		else
			ReikaJavaLibrary.pConsole("DRAGONAPI: "+this+" not detected in the MC installation. No special action taken.");
	}

	public boolean isLoaded() {
		if (preset)
			return condition;
		return false;
	}

	public String getModLabel() {
		return modlabel;
	}

	@Override
	public String toString() {
		return this.getModLabel();
	}

}
