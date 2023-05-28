/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Interfaces.Registry.Dependency;
import Reika.DragonAPI.Interfaces.Registry.ModEntry;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public enum ModList implements ModEntry, Dependency {

	ROTARYCRAFT("RotaryCraft", "Reika.RotaryCraft.Registry.BlockRegistry", "Reika.RotaryCraft.Registry.ItemRegistry"),
	REACTORCRAFT("ReactorCraft", "Reika.ReactorCraft.Registry.ReactorBlocks", "Reika.ReactorCraft.Registry.ReactorItems"),
	EXPANDEDREDSTONE("ExpandedRedstone"),
	GEOSTRATA("GeoStrata"),
	//FURRYKINGDOMS("FurryKingdoms"),
	CRITTERPET("CritterPet", "Reika.CritterPet.CritterPet"),
	VOIDMONSTER("VoidMonster"),
	USEFULTNT("UsefulTNT"),
	METEORCRAFT("MeteorCraft"),
	//JETPLANE("JetPlane"),
	CAVECONTROL("CaveControl"),
	LEGACYCRAFT("LegacyCraft"),
	ELECTRICRAFT("ElectriCraft", "Reika.ElectriCraft.Registry.ElectriBlocks", "Reika.ElectriCraft.Registry.ElectriItems"),
	CHROMATICRAFT("ChromatiCraft", "Reika.ChromatiCraft.Registry.ChromaBlocks", "Reika.ChromatiCraft.Registry.ChromaItems"),
	TERRITORYZONE("TerritoryZone"),
	CONDENSEDORES("CondensedOres"),
	TREECLIMBING("TreeClimbing"),
	ARCHISECTIONS("ArchiSections"),
	LOOTTWEAKS("LootTweaks"),
	SATISFORESTRY("Satisforestry", "Reika.Satisforestry.Registry.SFBlocks"),
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
	THERMALDYNAMICS("ThermalDynamics", "cofh.thermaldynamics.ThermalDynamics", new String[]{"thermaldynamics.item.TDItems", "cofh.thermaldynamics.item.TDItems"}),
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
	CHISEL("chisel", new String[]{"com.cricketcraft.chisel.init.ChiselBlocks", "team.chisel.init.ChiselBlocks"}, new String[]{"com.cricketcraft.chisel.init.ChiselItems", "team.chisel.init.ChiselItems"}),
	CARPENTER("CarpentersBlocks", "com.carpentersblocks.util.registry.BlockRegistry", "com.carpentersblocks.util.registry.ItemRegistry"),
	ENDEREXPANSION("HardcoreEnderExpansion"),
	AGRICRAFT("AgriCraft", "com.InfinityRaider.AgriCraft.init.Blocks", "com.InfinityRaider.AgriCraft.init.Items"),
	THAUMICTINKER("ThaumicTinkerer"),
	RFTOOLS("rftools"), //classes scattered
	DRACONICEVO("DraconicEvolution"),
	MAGICBEES("MagicBees", "magicbees.main.Config"),
	IMMERSIVEENG("ImmersiveEngineering", "blusunrize.immersiveengineering.common.IEContent"),
	FORBIDDENMAGIC("ForbiddenMagic", "fox.spiteful.forbidden.blocks.ForbiddenBlocks", "fox.spiteful.forbidden.items.ForbiddenItems"),
	ADVROCKET("advancedRocketry"),
	HEXCRAFT("hexcraft", "com.celestek.hexcraft.HexBlocks", "com.celestek.hexcraft.init.HexItems"),
	TROPICRAFT("tropicraft", "net.tropicraft.registry.TCBlockRegistry", "net.tropicraft.registry.TCItemRegistry"),
	EREBUS("erebus", "erebus.ModBlocks", "erebus.ModItems"),
	BETWEENLANDS("thebetweenlands", "thebetweenlands.blocks.BLBlockRegistry", "thebetweenlands.items.BLItemRegistry"),
	NETHERPAM("harvestthenether", "com.pam.harvestthenether.BlockRegistry", "com.pam.harvestthenether.ItemRegistry"),
	AETHER("aether", "net.aetherteam.aether.blocks.AetherBlocks", "net.aetherteam.aether.items.AetherItems"),
	CLIMATECONTROL("climatecontrol"),
	STORAGEDRAWERS("StorageDrawers"),
	;

	private final boolean condition;
	public final String modLabel;
	private final String[] itemClasses;
	private final String[] blockClasses;

	private final HashMap<String, ModHandlerBase> handlers = new HashMap();

	//To save on repeated Class.forName
	private Class blockClass;
	private Class itemClass;
	private static final HashMap<String, ModList> modIDs = new HashMap();

	public static final ModList[] modList = values();

	private ModList(String label, String[] blocks, String[] items) {
		modLabel = label;
		condition = Loader.isModLoaded(modLabel);
		itemClasses = items;
		blockClasses = blocks;
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
		if (blockClasses == null || blockClasses.length == 0) {
			DragonAPICore.logError("Could not load block class for "+this+". Null class provided.");
			ReikaJavaLibrary.dumpStack();
			return null;
		}
		if (blockClass == null) {
			for (String s : blockClasses) {
				blockClass = this.findClass(s);
				if (blockClass != null) {
					DragonAPICore.log("Found block class for "+this+": "+blockClass);
					break;
				}
			}
			if (blockClass == null) {
				String sgs = Arrays.toString(blockClasses);
				DragonAPICore.logError("Could not load block class for "+this+". Not found: "+sgs);
				ReflectiveFailureTracker.instance.logModReflectiveFailure(this, new ClassNotFoundException(sgs));
				return null;
			}
		}
		return blockClass;
	}

	public Class getItemClass() {
		if (itemClasses == null || itemClasses.length == 0) {
			DragonAPICore.logError("Could not load item class for "+this+". Null class provided.");
			ReikaJavaLibrary.dumpStack();
			return null;
		}
		if (itemClass == null) {
			for (String s : itemClasses) {
				itemClass = this.findClass(s);
				if (itemClass != null) {
					DragonAPICore.log("Found item class for "+this+": "+itemClass);
					break;
				}
			}
			if (itemClass == null) {
				String sgs = Arrays.toString(itemClasses);
				DragonAPICore.logError("Could not load item class for "+this+". Not found: "+sgs);
				ReflectiveFailureTracker.instance.logModReflectiveFailure(this, new ClassNotFoundException(sgs));
				return null;
			}
		}
		return itemClass;
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
		if (this == TWILIGHT)
			return "Twilight Forest";
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
		return this.ordinal() <= SATISFORESTRY.ordinal();
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

	public String getRegisteredName() {
		return Loader.instance().getIndexedModList().get(modLabel).getName();
	}

}
