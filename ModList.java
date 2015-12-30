/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Interfaces.Registry.ModEntry;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public enum ModList implements ModEntry {

	ROTARYCRAFT("RotaryCraft", "Reika.RotaryCraft.Registry.BlockRegistry", "Reika.RotaryCraft.Registry.ItemRegistry"),
	REACTORCRAFT("ReactorCraft", "Reika.ReactorCraft.Registry.ReactorBlocks", "Reika.ReactorCraft.Registry.ReactorItems"),
	EXPANDEDREDSTONE("ExpandedRedstone"),
	GEOSTRATA("GeoStrata"),
	FURRYKINGDOMS("FurryKingdoms"),
	CRITTERPET("CritterPet"),
	VOIDMONSTER("VoidMonster"),
	USEFULTNT("UsefulTNT"),
	METEORCRAFT("MeteorCraft"),
	JETPLANE("JetPlane"),
	CAVECONTROL("CaveControl"),
	LEGACYCRAFT("LegacyCraft"),
	ELECTRICRAFT("ElectriCraft", "Reika.ElectriCraft.Registry.ElectriBlocks", "Reika.ElectriCraft.Registry.ElectriItems"),
	CHROMATICRAFT("ChromatiCraft", "Reika.ChromatiCraft.Registry.ChromaBlocks", "Reika.ChromatiCraft.Registry.ChromaItems"),
	BUILDCRAFT("BuildCraft|Core", "buildcraft.BuildCraftCore"),
	BCENERGY("BuildCraft|Energy", "buildcraft.BuildCraftEnergy"),
	BCFACTORY("BuildCraft|Factory", "buildcraft.BuildCraftFactory"),
	BCTRANSPORT("BuildCraft|Transport", "buildcraft.BuildCraftTransport"),
	BCSILICON("BuildCraft|Silicon", "buildcraft.BuildCraftSilicon"),
	THAUMCRAFT("Thaumcraft", "thaumcraft.common.config.ConfigBlocks", "thaumcraft.common.config.ConfigItems"),
	IC2("IC2", "ic2.core.Ic2Items"),
	GREGTECH("gregtech"),
	FORESTRY("Forestry"),
	APPENG("appliedenergistics2"), //appeng.api.definitions
	MFFS("MFFS", "mffs.ModularForceFieldSystem"), //ensure still here
	REDPOWER("RedPower"),
	TWILIGHT("TwilightForest", "twilightforest.block.TFBlocks", "twilightforest.item.TFItems"),
	NATURA("Natura", "mods.natura.common.NContent"),
	BOP("BiomesOPlenty", "biomesoplenty.api.content.BOPCBlocks", "biomesoplenty.api.content.BOPCItems"),
	BXL("ExtraBiomesXL"),
	MINEFACTORY("MineFactoryReloaded", "powercrystals.minefactoryreloaded.setup.MFRThings"),
	DARTCRAFT("DartCraft", "bluedart.Blocks.DartBlock", "bluedart.Items.DartItem"), //ensure still here
	TINKERER("TConstruct", "tconstruct.world.TinkerWorld"), //tconstruct.library.TConstructRegistry.getBlock/Item
	THERMALEXPANSION("ThermalExpansion", new String[]{"thermalexpansion.block.TEBlocks", "cofh.thermalexpansion.block.TEBlocks"}, new String[]{"thermalexpansion.item.TEItems", "cofh.thermalexpansion.item.TEItems"}),
	THERMALFOUNDATION("ThermalFoundation", new String[]{"thermalfoundation.block.TFBlocks", "cofh.thermalfoundation.block.TFBlocks"}, new String[]{"thermalfoundation.item.TFItems", "cofh.thermalfoundation.item.TFItems"}),
	MEKANISM("Mekanism", "mekanism.common.MekanismBlocks", "mekanism.common.MekanismItems"),
	MEKTOOLS("MekanismTools", "mekanism.tools.common.ToolsItems"),
	RAILCRAFT("Railcraft", "mods.railcraft.common.blocks.RailcraftBlocks", new String[0]), //items spread over half a dozen classes
	ICBM("ICBM|Explosion"),
	ARSMAGICA("arsmagica2", "am2.blocks.BlocksCommonProxy", "am2.items.ItemsCommonProxy"), //ensure still here
	TRANSITIONAL("TransitionalAssistance", "modTA.Core.TACore"), //mod dead
	ENDERSTORAGE("EnderStorage"),
	TREECAPITATOR("TreeCapitator"),
	HARVESTCRAFT("harvestcraft", "com.pam.harvestcraft.BlockRegistry", "com.pam.harvestcraft.ItemRegistry"),
	MYSTCRAFT("Mystcraft", new String[]{"com.xcompwiz.mystcraft.api.MystObjects$Blocks", "com.xcompwiz.mystcraft.api.MystObjects"}, new String[]{"com.xcompwiz.mystcraft.api.MystObjects$Items", "com.xcompwiz.mystcraft.api.MystObjects"}),
	MAGICCROPS("magicalcrops", new String[]{"com.mark719.magicalcrops.MagicalCrops", "com.mark719.magicalcrops.handlers.MBlocks"}, new String[]{"com.mark719.magicalcrops.MagicalCrops", "com.mark719.magicalcrops.handlers.MItems"}),
	MIMICRY("Mimicry", "com.sparr.mimicry.block.MimicryBlock", "com.sparr.mimicry.item.MimicryItem"),
	QCRAFT("QuantumCraft", "dan200.QCraft"),
	OPENBLOCKS("OpenBlocks", "openblocks.OpenBlocks$Blocks", "openblocks.OpenBlocks$Items"),
	FACTORIZATION("factorization", "factorization.common.Registry"),
	UE("UniversalElectricity"),
	EXTRAUTILS("ExtraUtilities", "com.rwtema.extrautils.ExtraUtils"),
	POWERSUITS("powersuits", "net.machinemuse.powersuits.common.ModularPowersuits"), //ensure still here
	ARSENAL("RedstoneArsenal", new String[]{"redstonearsenal.item.RAItems", "cofh.redstonearsenal.item.RAItems"}),
	EMASHER("emashercore", "emasher.core.EmasherCore"), //ensure still here
	HIGHLANDS("Highlands", "highlands.api.HighlandsBlocks"),
	PROJRED("ProjRed|Core"),
	WITCHERY("witchery", "com.emoniph.witchery.WitcheryBlocks", "com.emoniph.witchery.WitcheryItems"),
	GALACTICRAFT("GalacticraftCore", "micdoodle8.mods.galacticraft.core.blocks.GCBlocks", "micdoodle8.mods.galacticraft.core.items.GCItems"),
	MULTIPART("ForgeMicroblock"),
	OPENCOMPUTERS("OpenComputers"),
	NEI("NotEnoughItems"),
	ATG("ATG"),
	WAILA("Waila"),
	BLUEPOWER("bluepower", "com.bluepowermod.init.BPBlocks", "com.bluepowermod.init.BPItems"),
	COLORLIGHT("easycoloredlights"),
	ENDERIO("EnderIO", "crazypants.enderio.EnderIO"),
	COMPUTERCRAFT("ComputerCraft", "dan200.ComputerCraft"),
	ROUTER("RouterReborn", "router.reborn.RouterReborn"),
	PNEUMATICRAFT("PneumaticCraft", "pneumaticCraft.common.block.Blockss", "pneumaticCraft.common.item.Itemss"),
	PROJECTE("ProjectE", "moze_intel.projecte.gameObjs.ObjHandler"),
	BLOODMAGIC("AWWayofTime", "WayofTime.alchemicalWizardry.ModBlocks", "WayofTime.alchemicalWizardry.ModItems"),
	LYCANITE("lycanitesmobs"),
	CRAFTMANAGER("zcraftingmanager"),
	MINECHEM("minechem"),
	TFC("terrafirmacraft"),
	BOTANIA("Botania", "vazkii.botania.common.block.ModBlocks", "vazkii.botania.common.item.ModItems"),
	GENDUSTRY("gendustry"),
	FLUXEDCRYSTALS("fluxedcrystals", "fluxedCrystals.init.FCBlocks", "fluxedCrystals.init.FCItems"),
	HUNGEROVERHAUL("HungerOverhaul"),
	CHISEL("chisel", "com.cricketcraft.chisel.init.ChiselBlocks", "com.cricketcraft.chisel.init.ChiselItems"),
	CARPENTER("CarpentersBlocks", "com.carpentersblocks.util.registry.BlockRegistry", "com.carpentersblocks.util.registry.ItemRegistry"),
	ENDEREXPANSION("HardcoreEnderExpansion"),
	AGRICRAFT("AgriCraft", "com.InfinityRaider.AgriCraft.init.Blocks", "com.InfinityRaider.AgriCraft.init.Items"),
	THAUMICTINKER("ThaumicTinkerer");

	private final boolean condition;
	public final String modLabel;
	private final String[] itemClass;
	private final String[] blockClass;

	private final HashMap<String, ModHandlerBase> handlers = new HashMap();

	//To save on repeated Class.forName
	private static final EnumMap<ModList, Class> blockClasses = new EnumMap(ModList.class);
	private static final EnumMap<ModList, Class> itemClasses = new EnumMap(ModList.class);
	private static final HashMap<String, ModList> modIDs = new HashMap();

	private static final Class liteClass;
	private static final Class optiClass;

	public static final ModList[] modList = values();

	private ModList(String label, String[] blocks, String[] items) {
		modLabel = label;
		condition = Loader.isModLoaded(modLabel);
		itemClass = items;
		blockClass = blocks;
		if (condition) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: "+this+" detected in the MC installation. Adjusting behavior accordingly.");
		}
		else
			ReikaJavaLibrary.pConsole("DRAGONAPI: "+this+" not detected in the MC installation. No special action taken.");

		if (condition) {
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

	private ModList(String label, String[] modClass) {
		this(label, modClass, modClass);
	}

	private ModList(String label, String blocks, String items) {
		this(label, blocks != null ? new String[]{blocks} : null, items != null ? new String[]{items} : null);
	}

	private ModList(String label, String[] blocks, String items) {
		this(label, blocks, items != null ? new String[]{items} : null);
	}

	private ModList(String label, String blocks, String[] items) {
		this(label, blocks != null ? new String[]{blocks} : null, items);
	}

	private ModList(String label) {
		this(label, (String)null);
	}

	private Class findClass(String s) {
		try {
			return Class.forName(s);
		}
		catch (ClassNotFoundException e) {
			return null;
		}
	}

	public Class getBlockClass() {
		if (blockClass == null || blockClass.length == 0) {
			DragonAPICore.logError("Could not load block class for "+this+". Null class provided.");
			ReikaJavaLibrary.dumpStack();
			return null;
		}
		Class c = blockClasses.get(this);
		if (c == null) {
			for (String s : blockClass) {
				c = this.findClass(s);
				if (c != null) {
					blockClasses.put(this, c);
					DragonAPICore.log("Found block class for "+this+": "+c);
					break;
				}
			}
			if (c == null) {
				String sgs = Arrays.toString(blockClass);
				DragonAPICore.logError("Could not load block class for "+this+". Not found: "+sgs);
				ReflectiveFailureTracker.instance.logModReflectiveFailure(this, new ClassNotFoundException(sgs));
				return null;
			}
		}
		return c;
	}

	public Class getItemClass() {
		if (itemClass == null || itemClass.length == 0) {
			DragonAPICore.logError("Could not load item class for "+this+". Null class provided.");
			ReikaJavaLibrary.dumpStack();
			return null;
		}
		Class c = itemClasses.get(this);
		if (c == null) {
			for (String s : itemClass) {
				c = this.findClass(s);
				if (c != null) {
					itemClasses.put(this, c);
					DragonAPICore.log("Found item class for "+this+": "+c);
					break;
				}
			}
			if (c == null) {
				String sgs = Arrays.toString(itemClass);
				DragonAPICore.logError("Could not load item class for "+this+". Not found: "+sgs);
				ReflectiveFailureTracker.instance.logModReflectiveFailure(this, new ClassNotFoundException(sgs));
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

	public ModContainer getModContainer() {
		return Loader.instance().getIndexedModList().get(modLabel);
	}

	public Object getModObject() {
		return this.getModContainer().getMod();
	}

	public String getVersion() {
		Object o = this.getModObject();
		return o instanceof DragonAPIMod ? ((DragonAPIMod)o).getModVersion().toString() : this.getModContainer().getVersion();
	}

	@Override
	public String toString() {
		return this.getModLabel();
	}

	public boolean isReikasMod() {
		return this.ordinal() <= CHROMATICRAFT.ordinal();
	}

	public void registerHandler(ModHandlerBase h, String id) {
		handlers.put(id, h);
	}

	public Collection<ModHandlerBase> getHandlers() {
		return handlers.values();
	}

	public ModHandlerBase getHandler(String id) {
		return handlers.get(id);
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

	public static boolean liteLoaderInstalled() {
		return liteClass != null;
	}

	public static boolean optifineInstalled() {
		return optiClass != null;
	}

	static {
		Class c = null;
		try {
			c = Class.forName("com.mumfrey.liteloader.core.LiteLoader");
			ReikaJavaLibrary.pConsole("DRAGONAPI: LiteLoader detected. Loading compatibility features.");
			ReikaJavaLibrary.pConsole("DRAGONAPI: \t\tNote that some parts of the game, especially sounds and textures, may error out.");
			ReikaJavaLibrary.pConsole("DRAGONAPI: \t\tTry reloading resources (F3+T) to fix this.");
		}
		catch (ClassNotFoundException e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: LiteLoader not detected.");
		}
		liteClass = c;

		c = null;
		try {
			c = Class.forName("optifine.OptiFineTweaker");
			ReikaJavaLibrary.pConsole("DRAGONAPI: Optifine detected. Loading compatibility features.");
			ReikaJavaLibrary.pConsole("DRAGONAPI: \t\tNote that some parts of the game, especially rendering and textures, may error out.");
			ReikaJavaLibrary.pConsole("DRAGONAPI: \t\tTry reloading resources (F3+T) to fix this.");
		}
		catch (ClassNotFoundException e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Optifine not detected.");
		}
		optiClass = c;
	}

}
