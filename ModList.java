/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI;

import java.util.ArrayList;
import java.util.EnumMap;
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
	METEORCRAFT("MeteorCraft"),
	JETPLANE("JetPlane"),
	CAVECONTROL("CaveControl"),
	LEGACYCRAFT("LegacyCraft"),
	ELECTRICRAFT("ElectriCraft"),
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
	BOP("BiomesOPlenty", "biomesoplenty.configuration.BOPConfigurationIDs"),
	BXL("ExtraBiomesXL"),
	MINEFACTORY("MineFactoryReloaded", "powercrystals.minefactoryreloaded.MineFactoryReloadedCore"),
	DARTCRAFT("DartCraft", "bluedart.block.DartBlock", "bluedart.item.DartItem"),
	TINKERER("TConstruct", "tconstruct.common.TContent"), //"TRepo" in 1.7
	THERMALEXPANSION("ThermalExpansion", "thermalexpansion.block.TEBlocks", "thermalexpansion.item.TEItems"),
	MEKANISM("Mekanism", "mekanism.common.Mekanism"),
	MEKTOOLS("MekanismTools", "mekanism.tools.common.MekanismTools"),
	RAILCRAFT("Railcraft", "mods.railcraft.common.blocks", null), //items spread over half a dozen classes
	ICBM("ICBM|Explosion"),
	ARSMAGICA("arsmagica2", "am2.blocks.BlocksCommonProxy", "am2.items.ItemsCommonProxy"),
	TRANSITIONAL("TransitionalAssistance", "modTA.Core.TACore"),
	ENDERSTORAGE("EnderStorage"),
	OPTIFINE("Optifine"),
	TREECAPITATOR("TreeCapitator"),
	HARVESTCRAFT("pamharvestcraft", "assets.pamharvestcraft.PamHarvestCraft"),
	MYSTCRAFT("Mystcraft"),
	MAGICCROPS("magicalcrops", "magicalcrops.mod_mCrops"),
	MIMICRY("Mimicry", "modMimicry.Block.MimicryBlock", "modMimicry.Item.MimicryItem"),
	QCRAFT("QuantumCraft", "dan200.QCraft"),
	OPENBLOCKS("OpenBlocks", "openblocks.OpenBlocks$Blocks", "openblocks.OpenBlocks$Items"),
	FACTORIZATION("factorization", "factorization.common.Registry"),
	UE("UniversalElectricity"),
	EXTRAUTILS("ExtraUtilities", "extrautils.ExtraUtils"),
	POWERSUITS("powersuits", "net.machinemuse.powersuits.common.ModularPowersuits"),
	ARSENAL("Redstone Arsenal", "redstonearsenal.item.RAItems"),
	EMASHER("emashercore", "emasher.core.EmasherCore"),
	HIGHLANDS("Highlands", "highlands.api.HighlandsBlocks"),
	PROJRED("ProjRed|Core");

	private final boolean condition;
	public final String modLabel;
	private final String itemClass;
	private final String blockClass;

	//To save on repeated Class.forName
	private static final EnumMap<ModList, Class> blockClasses = new EnumMap(ModList.class);
	private static final EnumMap<ModList, Class> itemClasses = new EnumMap(ModList.class);
	private static final HashMap<String, ModList> modIDs = new HashMap();

	public static final ModList[] modList = values();

	private ModList(String label, String blocks, String items) {
		modLabel = label;
		boolean c = Loader.isModLoaded(modLabel);
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
		return modLabel;
	}

	public String getDisplayName() {
		if (this.isReikasMod())
			return modLabel;
		return ReikaStringParser.capFirstChar(this.name());
	}

	@Override
	public String toString() {
		return this.getModLabel();
	}

	public boolean isReikasMod() {
		return this.ordinal() <= ELECTRICRAFT.ordinal();
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

	public static ModList getModFromID(String id) {
		if (modIDs.containsKey(id))
			return modIDs.get(id);
		else {
			for (int i = 0; i < modList.length; i++) {
				ModList mod = modList[i];
				if (mod.modLabel.equals(id)) {
					modIDs.put(id, mod);
					return mod;
				}
			}
			modIDs.put(id, null);
			return null;
		}
	}

}
