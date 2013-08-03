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

import Reika.DragonAPI.Libraries.ReikaJavaLibrary;
import cpw.mods.fml.common.Loader;

public enum APIRegistry {

	BUILDCRAFTENERGY("BuildCraft|Energy"),
	BUILDCRAFTFACTORY("BuildCraft|Factory"),
	BUILDCRAFTTRANSPORT("BuildCraft|Transport"),
	THAUMCRAFT("Thaumcraft"),
	INDUSTRIALCRAFT("IC2"),
	GREGTECH("Gregtech"),
	FORESTRY("Forestry"),
	APPLIEDENERGISTICS("AppliedEnergistics"),
	MFFS("ModularForceFieldSystem"),
	REDPOWER("RedPower"),
	TWILIGHT("TwilightForest"),
	NATURA("Natura"),
	BOP("Biomes O'Plenty"),
	BXL("Biomes XL"),
	MINEFACTORY("MineFactoryReloaded"),
	DARTCRAFT("DartCraft");

	private boolean condition;
	private boolean preset = false;
	private String modlabel;
	private Class modClass;

	public static final APIRegistry[] apiList = APIRegistry.values();

	private APIRegistry(String s) {
		modlabel = s;
		boolean c = Loader.isModLoaded(modlabel);
		condition = c;
		preset = true;
		if (c)
			ReikaJavaLibrary.pConsole("DRAGONAPI: "+this+" detected in the MC installation. Adjusting behavior accordingly.");
		else
			ReikaJavaLibrary.pConsole("DRAGONAPI: "+this+" not detected in the MC installation. No special action taken.");
	}

	public boolean conditionsMet() {
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
