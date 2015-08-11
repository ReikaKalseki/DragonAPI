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

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.potion.Potion;
import net.minecraftforge.client.event.sound.SoundSetupEvent;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerRegisterEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;
import paulscode.sound.SoundSystemConfig;
import Reika.DragonAPI.DragonAPICore.DragonAPILoadWatcher;
import Reika.DragonAPI.Auxiliary.ChunkManager;
import Reika.DragonAPI.Auxiliary.DragonAPIEventWatcher;
import Reika.DragonAPI.Auxiliary.FindTilesCommand;
import Reika.DragonAPI.Auxiliary.LoggingFilters;
import Reika.DragonAPI.Auxiliary.NEI_DragonAPI_Config;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker;
import Reika.DragonAPI.Auxiliary.Trackers.BiomeCollisionTracker;
import Reika.DragonAPI.Auxiliary.Trackers.CommandableUpdateChecker;
import Reika.DragonAPI.Auxiliary.Trackers.CommandableUpdateChecker.CheckerDisableCommand;
import Reika.DragonAPI.Auxiliary.Trackers.CompatibilityTracker;
import Reika.DragonAPI.Auxiliary.Trackers.FurnaceFuelRegistry;
import Reika.DragonAPI.Auxiliary.Trackers.IntegrityChecker;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher.KeyTicker;
import Reika.DragonAPI.Auxiliary.Trackers.PackModificationTracker;
import Reika.DragonAPI.Auxiliary.Trackers.PatreonController;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerHandler;
import Reika.DragonAPI.Auxiliary.Trackers.PotionCollisionTracker;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Auxiliary.Trackers.SuggestedModsTracker;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry;
import Reika.DragonAPI.Auxiliary.Trackers.TickScheduler;
import Reika.DragonAPI.Auxiliary.Trackers.VanillaIntegrityTracker;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Base.DragonAPIMod.LoadProfiler.LoadPhase;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Base.ModHandlerBase.SearchVersionHandler;
import Reika.DragonAPI.Base.ModHandlerBase.VersionHandler;
import Reika.DragonAPI.Base.ModHandlerBase.VersionIgnore;
import Reika.DragonAPI.Command.BlockReplaceCommand;
import Reika.DragonAPI.Command.ClearItemsCommand;
import Reika.DragonAPI.Command.DonatorCommand;
import Reika.DragonAPI.Command.EditNearbyInventoryCommand;
import Reika.DragonAPI.Command.EntityListCommand;
import Reika.DragonAPI.Command.GuideCommand;
import Reika.DragonAPI.Command.IDDumpCommand;
import Reika.DragonAPI.Command.LogControlCommand;
import Reika.DragonAPI.Command.SelectiveKillCommand;
import Reika.DragonAPI.Command.TestControlCommand;
import Reika.DragonAPI.Command.TileSyncCommand;
import Reika.DragonAPI.Exception.InvalidBuildException;
import Reika.DragonAPI.Exception.WTFException;
import Reika.DragonAPI.Extras.LoginHandler;
import Reika.DragonAPI.Extras.TemporaryCodeCalls;
import Reika.DragonAPI.Instantiable.Event.ItemUpdateEvent;
import Reika.DragonAPI.Instantiable.Event.Client.GameFinishedLoadingEvent;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Instantiable.IO.SyncPacket;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaFluidHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.ReikaPotionHelper;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModInteract.BannedItemReader;
import Reika.DragonAPI.ModInteract.MinetweakerHooks;
import Reika.DragonAPI.ModInteract.WailaTechnicalOverride;
import Reika.DragonAPI.ModInteract.DeepInteract.FrameBlacklist;
import Reika.DragonAPI.ModInteract.DeepInteract.MTInteractionManager;
import Reika.DragonAPI.ModInteract.DeepInteract.NEIIntercept;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaMystcraftHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.TwilightForestLootHooks;
import Reika.DragonAPI.ModInteract.ItemHandlers.AppEngHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.BCMachineHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.BCPipeHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.BerryBushHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.BloodMagicHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.BoPBlockHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.DartItemHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.DartOreHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ExtraUtilsHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.FactorizationHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ForestryHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.GalacticCraftHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.HarvestCraftHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.HungerOverhaulHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.IC2Handler;
import Reika.DragonAPI.ModInteract.ItemHandlers.MFRHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.MagicCropHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.MagicaOreHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.MekToolHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.MekanismHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.MimicryHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.MystCraftHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.OpenBlockHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.OreBerryBushHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.PneumaticPlantHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.QuantumOreHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.RailcraftHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.RedstoneArsenalHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumBiomeHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumOreHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThermalHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerBlockHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TransitionalOreHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TwilightForestHandler;
import Reika.DragonAPI.ModInteract.RecipeHandlers.ForestryRecipeHelper;
import Reika.DragonAPI.ModInteract.RecipeHandlers.SmelteryRecipeHandler;
import Reika.DragonAPI.ModRegistry.InterfaceCache;
import Reika.DragonAPI.ModRegistry.ModCropList;
import Reika.DragonAPI.ModRegistry.ModOreList;
import Reika.DragonAPI.ModRegistry.ModWoodList;
import Reika.DragonAPI.ModRegistry.PowerTypes;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLFingerprintViolationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = "DragonAPI", certificateFingerprint = "@GET_FINGERPRINT@", dependencies=DragonAPICore.dependencies)
public class DragonAPIInit extends DragonAPIMod {

	public static final String packetChannel = "DragonAPIData";

	@SidedProxy(clientSide="Reika.DragonAPI.APIProxyClient", serverSide="Reika.DragonAPI.APIProxy")
	public static APIProxy proxy;

	@Instance("DragonAPI")
	public static DragonAPIInit instance = new DragonAPIInit();

	public static final ControlledConfig config = new ControlledConfig(instance, DragonOptions.optionList, null, 0);

	private static ModLogger logger;

	public DragonAPIInit() {
		super();

	}

	@EventHandler
	public void invalidSignature(FMLFingerprintViolationEvent evt) {
		if (!ReikaObfuscationHelper.isDeObfEnvironment()) {
			if (!evt.fingerprints.contains(evt.expectedFingerprint.toLowerCase().replaceAll(":", ""))) {
				throw new InvalidBuildException(this, evt.source);
			}
		}
	}

	@Override
	@EventHandler
	public void preload(FMLPreInitializationEvent evt) {
		this.startTiming(LoadPhase.PRELOAD);
		this.verifyInstallation();
		config.loadSubfolderedConfigFile(evt);
		config.initProps(evt);

		logger = new ModLogger(instance, false);
		if (DragonOptions.FILELOG.getState())
			logger.setOutput("**_Loading_Log.log");

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			MinecraftForge.EVENT_BUS.register(DragonAPILoadWatcher.instance);
		MinecraftForge.EVENT_BUS.register(DragonAPIEventWatcher.instance);

		logger.log("Initializing libraries with max recursion depth of "+ReikaJavaLibrary.getMaximumRecursiveDepth());

		proxy.registerSidedHandlers();

		this.registerTechnicalBlocks();

		ChunkManager.instance.register();

		OreDictionary.initVanillaEntries();
		ReikaJavaLibrary.initClass(ModList.class);

		this.increasePotionCount();
		this.increaseChunkCap();
		//this.increaseBiomeCount(); world save stores biome as bytes, so 255 is cap

		if (ReikaObfuscationHelper.isDeObfEnvironment())
			TemporaryCodeCalls.preload(evt);

		BannedItemReader.instance.initWith("BanItem");
		BannedItemReader.instance.initWith("ItemBan");
		BannedItemReader.instance.initWith("TekkitCustomizerData");
		BannedItemReader.instance.initWith("TekkitCustomizer");

		this.basicSetup(evt);

		ReikaPacketHelper.registerPacketHandler(instance, packetChannel, new APIPacketHandler());

		int id = DragonOptions.SYNCPACKET.getValue();
		ReikaPacketHelper.registerVanillaPacketType(this, id, SyncPacket.class, Side.SERVER, EnumConnectionState.PLAY);
		//if (DragonOptions.COMPOUNDSYNC.getState())
		//	ReikaPacketHelper.registerVanillaPacketType(this, id+1, CompoundSyncPacket.class, Side.SERVER, EnumConnectionState.PLAY);
		this.finishTiming();
	}

	private void increaseChunkCap() {
		Configuration cfg = ForgeChunkManager.getConfig();
		Property modTC = cfg.get(this.getModContainer().getModId(), "maximumTicketCount", 1000);
		Property modCPT = cfg.get(this.getModContainer().getModId(), "maximumChunksPerTicket", 2000);
		cfg.save();
	}

	private static final Block[] technicalBlocks = {
		Blocks.brewing_stand, Blocks.bed, Blocks.nether_wart, Blocks.cauldron, Blocks.flower_pot, Blocks.wheat, Blocks.reeds,
		Blocks.cake, Blocks.skull, Blocks.piston_head, Blocks.lit_redstone_ore, Blocks.powered_repeater, Blocks.pumpkin_stem,
		Blocks.standing_sign, Blocks.powered_comparator, Blocks.tripwire, Blocks.lit_redstone_lamp, Blocks.melon_stem,
		Blocks.unlit_redstone_torch, Blocks.unpowered_comparator, Blocks.redstone_wire, Blocks.wall_sign,
		Blocks.unpowered_repeater, Blocks.iron_door, Blocks.wooden_door
	};

	/** Registers all the vanilla technical blocks (except air and block 36) to have items so as to avoid crashes when rendering them
	 * in the inventory. */
	private void registerTechnicalBlocks() {
		for (int i = 0; i < technicalBlocks.length; i++) {
			Block b = technicalBlocks[i];
			ItemBlock ib = new ItemBlock(b);
			String s = Block.blockRegistry.getNameForObject(b)+"_technical";
			Item.itemRegistry.addObject(Block.getIdFromBlock(b), s, ib);

			if (ModList.WAILA.isLoaded()) {
				WailaTechnicalOverride.instance.addBlock(b);
			}

			if (ModList.NEI.isLoaded()) {
				NEI_DragonAPI_Config.hideBlock(b);
			}
		}
	}

	/*
	@EventHandler
	public void preventMappingCorruption(FMLMissingMappingsEvent evt) {
		for (MissingMapping m : evt.getAll()) {
			if (m.type == Type.ITEM && (m.id <= 164 || ReikaMathLibrary.isValueInsideBounds(170, 175, m.id))) {
				ReikaJavaLibrary.pConsole("FIRED");
				//m.ignore();
			}
		}
	}
	 *//*
	/** Do not call unless biomes are no longer saved as bytes *//*
	private void increaseBiomeCount() {
		int count = BiomeGenBase.biomeL	ist.length;
		int newsize = 1024;
		BiomeGenBase[] newBiomes = new BiomeGenBase[newsize];
		System.arraycopy(BiomeGenBase.biomeList, 0, newBiomes, 0, count);
		BiomeGenBase.biomeList = newBiomes;
		if (BiomeGenBase.biomeList.length == newsize)
			logger.log("Overriding the vanilla BiomeList array to allow for biome IDs up to "+(newsize-1)+" (up from "+(count-1)+").");
		else
			logger.logError("Could not increase biome ID limit from "+count+" to "+newsize+", but no exception was thrown!");
	}*/

	private void increasePotionCount() {
		int count = Potion.potionTypes.length;
		int newsize = 256;
		if (count > newsize) {
			logger.log("Did not increase potion size array, as some other mod already did.");
			return;
		}
		Potion[] newPotions = new Potion[newsize];
		System.arraycopy(Potion.potionTypes, 0, newPotions, 0, count);
		Potion.potionTypes = newPotions;
		if (Potion.potionTypes.length == newsize)
			logger.log("Overriding the vanilla PotionTypes array to allow for potion IDs up to "+(newsize-1)+" (up from "+(count-1)+").");
		else
			logger.logError("Could not increase potion ID limit from "+count+" to "+newsize+", but no exception was thrown!");
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		this.startTiming(LoadPhase.LOAD);
		proxy.registerSidedHandlersMain();

		if (ReikaObfuscationHelper.isDeObfEnvironment())
			TemporaryCodeCalls.load(event);

		ReikaRegistryHelper.loadNames();

		PlayerHandler.instance.registerTracker(LoginHandler.instance);

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new APIGuiHandler());

		//ReikaPacketHelper.initPipelines();

		TickRegistry.instance.registerTickHandler(ProgressiveRecursiveBreaker.instance);
		TickRegistry.instance.registerTickHandler(TickScheduler.instance);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			TickRegistry.instance.registerTickHandler(KeyTicker.instance);
		//if (DragonOptions.COMPOUNDSYNC.getState())
		//	TickRegistry.instance.registerTickHandler(CompoundSyncPacketTracker.instance, Side.SERVER);

		FMLInterModComms.sendMessage("Waila", "register", "Reika.DragonAPI.ModInteract.LegacyWailaHelper.registerObjects");
		FMLInterModComms.sendMessage("Waila", "register", "Reika.DragonAPI.ModInteract.WailaTechnicalOverride.registerOverride");

		if (DragonOptions.UNNERFOBSIDIAN.getState())
			Blocks.obsidian.setResistance(2000);

		if (ModList.COMPUTERCRAFT.isLoaded()) {
			try {
				Class interf = Class.forName("dan200.computercraft.api.peripheral.IPeripheralProvider");
				Class handler = Class.forName("Reika.DragonAPI.ModInteract.PeripheralHandler");
				Object handlerObj = handler.newInstance();
				Class api = Class.forName("dan200.computercraft.api.ComputerCraftAPI");
				Method register = api.getDeclaredMethod("registerPeripheralProvider", interf);
				register.invoke(null, handlerObj);
				//ComputerCraftAPI.registerPeripheralProvider(new PeripheralHandler()); Nonreflective code crashes
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		PatreonController.instance.addPatron(this, "Dale Mahalko / Plawerth", 1);
		PatreonController.instance.addPatron(this, "Lasse Knudsen", 40);
		PatreonController.instance.addPatron(this, "Jason Watson", 10);
		PatreonController.instance.addPatron(this, "frogfigther the derp", "a52abe43-ee2a-482a-a99c-51d8203e49d3", 10);
		PatreonController.instance.addPatron(this, "Fizyxnrd", 2);
		PatreonController.instance.addPatron(this, "Jakbruce2012", "8286f5c2-3062-4034-a72b-21dde6dcaa1a", 10);
		PatreonController.instance.addPatron(this, "Michael Luke", 5);
		PatreonController.instance.addPatron(this, "Andrew Jones", 10);
		PatreonController.instance.addPatron(this, "Steven Kane", 40);
		PatreonController.instance.addPatron(this, "motsop", 1);
		PatreonController.instance.addPatron(this, "Demethan", "d0ffe97b-3370-4135-8218-4399e7ec5184", 5); //Luc Levesque
		PatreonController.instance.addPatron(this, "David Harris", 1);
		PatreonController.instance.addPatron(this, "Michael Vaarning", 5);
		PatreonController.instance.addPatron(this, "Renato", 1);
		PatreonController.instance.addPatron(this, "BevoLJ", "1d6a73e8-cf9e-45e9-aada-c2e1609e1f77", 5);
		PatreonController.instance.addPatron(this, "brian allred", 1);
		PatreonController.instance.addPatron(this, "Joshua Kubiak", 5);
		PatreonController.instance.addPatron(this, "Paul Luhman", 1);
		PatreonController.instance.addPatron(this, "John Paul Douglass", 10);
		PatreonController.instance.addPatron(this, "Jon M", "3c1c793a-a29d-4822-8e35-6be630315277", 5);
		PatreonController.instance.addPatron(this, "Christhereaper", 5);
		PatreonController.instance.addPatron(this, "Havgood", 10);
		PatreonController.instance.addPatron(this, "David Carlson", 10);
		PatreonController.instance.addPatron(this, "Frederik", 10);
		PatreonController.instance.addPatron(this, "Tommy Miller", 10);
		PatreonController.instance.addPatron(this, "Josh O'Connor-Chen", 10);
		PatreonController.instance.addPatron(this, "Ariaxis", "bef0a130-c4f5-4239-bd6b-a19e24802120", 30);
		PatreonController.instance.addPatron(this, "Dawson Dimmick", 25);
		PatreonController.instance.addPatron(this, "Polymorph", "474c622e-80cb-4daa-b2c2-562c5d85aa4c", 10);
		PatreonController.instance.addPatron(this, "SourC00lguy", "c0f67a07-a8aa-4044-a223-fb5640778c41", 1);
		PatreonController.instance.addPatron(this, "DorinnB", "1a4c37c8-de99-4960-8157-90dc28ef4c65", 1);
		PatreonController.instance.addPatron(this, "Kotaro_MC", "4e40b5a3-fa82-4496-acf2-d2fadeb5bf5d", 40);
		PatreonController.instance.addPatron(this, "AnotherDeadBard", "147aac9c-c0d2-4273-a6cc-f272f5b2ae13", 5);
		PatreonController.instance.addPatron(this, "Haggle1996", "bb7c2ac3-72aa-4ad8-8e00-4e0fb67a51ec", 10);
		PatreonController.instance.addPatron(this, "acnotalpha", "dc7496be-2408-4c7e-a65e-6beb53355fa7", 10); //Jeremiah Winsley
		PatreonController.instance.addPatron(this, "Lavious", "7fb32de9-4d98-4d1f-9264-43bd1edf0ae0", 1);
		PatreonController.instance.addPatron(this, "quok98", "f573f6a0-9e08-482a-9985-29c5bb89c4f4", 10); //Rich Edelman
		PatreonController.instance.addPatron(this, "rxiv", "1cb1da91-d3ed-4c10-9506-ca27fd480634", 5);
		//PatreonController.instance.addPatron(this, "shobu", "6712dff7-a5d3-4a55-9c25-33b50e173ee1", 5);
		PatreonController.instance.addPatron(this, "Goof245", "79849e78-fe9a-4bb9-af6b-fb4c41fc8dd8", 20); //Aiden Young
		PatreonController.instance.addPatron(this, "Solego", "2c85a7d8-af77-4c5e-9416-47e4a281497f", 40); //Aiden Young


		CommandableUpdateChecker.instance.checkAll();

		this.finishTiming();
	}

	@Override
	@EventHandler
	public void postload(FMLPostInitializationEvent evt) {
		this.startTiming(LoadPhase.POSTLOAD);

		PackModificationTracker.instance.loadAll();

		this.loadHandlers();

		if (ReikaObfuscationHelper.isDeObfEnvironment())
			TemporaryCodeCalls.postload(evt);

		this.alCompat();

		GameRegistry.registerFuelHandler(FurnaceFuelRegistry.instance);

		//ReikaPacketHelper.postInitPipelines();

		BiomeCollisionTracker.instance.check();
		PotionCollisionTracker.instance.check();
		VanillaIntegrityTracker.instance.check();

		CompatibilityTracker.instance.test();

		ReikaPotionHelper.loadBadPotions();

		IntegrityChecker.instance.testIntegrity();

		ReikaOreHelper.refreshAll();
		ModOreList.initializeAll();

		SuggestedModsTracker.instance.printConsole();

		ReikaEntityHelper.loadMappings();

		//CreativeTabSorter.instance.sortTabs(); //frequently messes up

		ReikaJavaLibrary.initClass(FrameBlacklist.class);
		ReikaJavaLibrary.initClass(ReikaMystcraftHelper.class);
		ReikaJavaLibrary.initClass(ReikaThaumHelper.class);
		ReikaJavaLibrary.initClass(SmelteryRecipeHandler.class);
		ReikaJavaLibrary.initClass(TwilightForestLootHooks.class);

		if (MTInteractionManager.isMTLoaded()) {
			MinetweakerHooks.instance.registerAll();
		}

		this.finishTiming();
	}

	private void sortCreativeTabs() {

	}

	@EventHandler
	public void registerCommands(FMLServerStartingEvent evt) {
		evt.registerServerCommand(new GuideCommand());
		evt.registerServerCommand(new DonatorCommand());
		evt.registerServerCommand(new LogControlCommand());
		evt.registerServerCommand(new TestControlCommand());
		evt.registerServerCommand(new CheckerDisableCommand());
		evt.registerServerCommand(new SelectiveKillCommand());
		evt.registerServerCommand(new BlockReplaceCommand());
		evt.registerServerCommand(new EditNearbyInventoryCommand());
		evt.registerServerCommand(new TileSyncCommand());
		evt.registerServerCommand(new IDDumpCommand());
		evt.registerServerCommand(new EntityListCommand());
		evt.registerServerCommand(new FindTilesCommand());
		evt.registerServerCommand(new ClearItemsCommand());

		if (MTInteractionManager.isMTLoaded())
			MTInteractionManager.instance.scanAndRevert();
	}

	@SubscribeEvent
	public void catchNullOreDict(OreRegisterEvent evt) {
		if (evt.Ore == null || evt.Ore.getItem() == null)
			throw new WTFException("Someone registered null to the OreDictionary under the name '"+evt.Name+"'!", true);
		else {
			logger.log("Logged OreDict registration of "+evt.Ore+" as '"+evt.Name+"'.");
		}
	}

	@SubscribeEvent
	public void mapFluidContainers(FluidContainerRegisterEvent evt) {
		Fluid f = evt.data.fluid.getFluid();
		ItemStack fill = evt.data.filledContainer;
		ItemStack empty = evt.data.emptyContainer;
		StringBuilder sb = new StringBuilder();
		sb.append("Logged FluidContainer registration of ");
		sb.append(f.getName());
		sb.append(" with filled '");
		sb.append(fill != null ? fill.getDisplayName() : "[null]");
		sb.append("' and empty '");
		sb.append(empty != null ? empty.getDisplayName() : "[null]");
		sb.append("'.");
		logger.log(sb.toString());
		ReikaFluidHelper.mapContainerToFluid(f, empty, fill);
	}

	@SubscribeEvent
	public void onClose(WorldEvent.Unload evt) {

	}

	@SubscribeEvent
	public void onLoad(WorldEvent.Load evt) {

	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void increaseChannels(SoundSetupEvent evt) {
		if (DragonOptions.SOUNDCHANNELS.getState()) {
			SoundSystemConfig.setNumberNormalChannels(256);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onGameLoaded(GameFinishedLoadingEvent evt) {
		if (ModList.liteLoaderInstalled())
			Minecraft.getMinecraft().refreshResources();
		if (ModList.NEI.isLoaded()) {
			NEIIntercept.instance.register();
			//NEIFontRendererHandler.instance.register();
		}
		proxy.registerSidedHandlersGameLoaded();
		ReflectiveFailureTracker.instance.print();
	}

	@SubscribeEvent
	public void clearItems(ItemUpdateEvent evt) {
		if (ClearItemsCommand.clearItem(evt.entityItem)) {
			evt.entityItem.setDead();
		}
	}

	@SubscribeEvent
	public void addGuideGUI(PlayerInteractEvent evt) {
		EntityPlayer ep = evt.entityPlayer;
		ItemStack is = ep.getCurrentEquippedItem();
		if (is != null && is.getItem() == Items.enchanted_book) {
			if (is.stackTagCompound != null) {
				NBTTagCompound disp = is.stackTagCompound.getCompoundTag("display");
				if (disp != null) {
					NBTTagList list = disp.getTagList("Lore", NBTTypes.STRING.ID);
					if (list != null && list.tagCount() > 0) {
						String sg = list.getStringTagAt(0);
						if (sg != null && sg.equals("Reika's Mods Guide")) {
							ep.openGui(instance, 0, ep.worldObj, 0, 0, 0);
							evt.setResult(Result.ALLOW);
						}
					}
				}
			}
		}
	}

	private void alCompat() { //Why the hell are there three standards for aluminum?
		logger.log("Repairing compatibility between Alumin(i)um OreDictionary Names.");
		List<ItemStack> al = new ArrayList(OreDictionary.getOres("ingotNaturalAluminum"));
		for (int i = 0; i < al.size(); i++) {
			if (!ReikaItemHelper.collectionContainsItemStack(OreDictionary.getOres("ingotAluminum"), al.get(i)))
				OreDictionary.registerOre("ingotAluminum", al.get(i));
			if (!ReikaItemHelper.collectionContainsItemStack(OreDictionary.getOres("ingotAluminium"), al.get(i)))
				OreDictionary.registerOre("ingotAluminium", al.get(i));
		}

		al = new ArrayList(OreDictionary.getOres("ingotAluminum"));
		for (int i = 0; i < al.size(); i++) {
			if (!ReikaItemHelper.collectionContainsItemStack(OreDictionary.getOres("ingotNaturalAluminum"), al.get(i)))
				OreDictionary.registerOre("ingotNaturalAluminum", al.get(i));
			if (!ReikaItemHelper.collectionContainsItemStack(OreDictionary.getOres("ingotAluminium"), al.get(i)))
				OreDictionary.registerOre("ingotAluminium", al.get(i));
		}

		al = new ArrayList(OreDictionary.getOres("ingotAluminium"));
		for (int i = 0; i < al.size(); i++) {
			if (!ReikaItemHelper.collectionContainsItemStack(OreDictionary.getOres("ingotNaturalAluminum"), al.get(i)))
				OreDictionary.registerOre("ingotNaturalAluminum", al.get(i));
			if (!ReikaItemHelper.collectionContainsItemStack(OreDictionary.getOres("ingotAluminum"), al.get(i)))
				OreDictionary.registerOre("ingotAluminum", al.get(i));
		}


		al = new ArrayList(OreDictionary.getOres("oreAluminium"));
		for (int i = 0; i < al.size(); i++) {
			if (!ReikaItemHelper.collectionContainsItemStack(OreDictionary.getOres("oreAluminum"), al.get(i)))
				OreDictionary.registerOre("oreAluminum", al.get(i));
		}
	}

	private void loadHandlers() {
		this.registerHandler(ModList.BCFACTORY, BCMachineHandler.class);
		this.registerHandler(ModList.BCTRANSPORT, BCPipeHandler.class);
		this.registerHandler(ModList.THAUMCRAFT, ThaumOreHandler.class);
		this.registerHandler(ModList.THAUMCRAFT, ThaumBiomeHandler.class);
		this.registerHandler(ModList.DARTCRAFT, DartOreHandler.class);
		this.registerHandler(ModList.DARTCRAFT, DartItemHandler.class);
		this.registerHandler(ModList.TINKERER, TinkerToolHandler.class);
		this.registerHandler(ModList.TINKERER, TinkerBlockHandler.class);
		this.registerHandler(ModList.TWILIGHT, TwilightForestHandler.class);
		this.registerHandler(ModList.MEKANISM, MekanismHandler.class);
		this.registerHandler(ModList.MEKTOOLS, MekToolHandler.class);
		this.registerHandler(ModList.TRANSITIONAL, TransitionalOreHandler.class);
		this.registerHandler(ModList.IC2, IC2Handler.class);
		this.registerHandler(ModList.ARSMAGICA, MagicaOreHandler.class);
		this.registerHandler(ModList.APPENG, AppEngHandler.class);
		this.registerHandler(ModList.FORESTRY, ForestryHandler.class);
		this.registerHandler(ModList.FORESTRY, ForestryRecipeHelper.class);
		this.registerHandler(ModList.THERMALFOUNDATION, ThermalHandler.class);
		this.registerHandler(ModList.MIMICRY, MimicryHandler.class);
		this.registerHandler(ModList.MAGICCROPS, MagicCropHandler.class, new SearchVersionHandler("4.0.0_PUBLIC_BETA")); //Newest
		//this.registerHandler(ModList.MAGICCROPS, LegacyMagicCropHandler.class, new SearchVersionHandler("4.0.0_BETA")); //Private Beta
		//this.registerHandler(ModList.MAGICCROPS, VeryLegacyMagicCropHandler.class, new SearchVersionHandler("1.7.2 - 0.1 ALPHA")); //1.7.10 alpha
		this.registerHandler(ModList.QCRAFT, QuantumOreHandler.class);;
		this.registerHandler(ModList.TINKERER, OreBerryBushHandler.class);
		this.registerHandler(ModList.NATURA, BerryBushHandler.class);
		this.registerHandler(ModList.OPENBLOCKS, OpenBlockHandler.class);
		this.registerHandler(ModList.FACTORIZATION, FactorizationHandler.class);
		this.registerHandler(ModList.HARVESTCRAFT, HarvestCraftHandler.class);
		this.registerHandler(ModList.ARSENAL, RedstoneArsenalHandler.class);
		this.registerHandler(ModList.RAILCRAFT, RailcraftHandler.class);
		this.registerHandler(ModList.MINEFACTORY, MFRHandler.class);
		this.registerHandler(ModList.GALACTICRAFT, GalacticCraftHandler.class);
		this.registerHandler(ModList.EXTRAUTILS, ExtraUtilsHandler.class);
		this.registerHandler(ModList.MYSTCRAFT, MystCraftHandler.class);
		this.registerHandler(ModList.BLOODMAGIC, BloodMagicHandler.class);
		this.registerHandler(ModList.PNEUMATICRAFT, PneumaticPlantHandler.class);
		this.registerHandler(ModList.BOP, BoPBlockHandler.class);
		this.registerHandler(ModList.HUNGEROVERHAUL, HungerOverhaulHandler.class);

		ReikaJavaLibrary.initClass(ModOreList.class);
		ReikaJavaLibrary.initClass(ModWoodList.class);
		ReikaJavaLibrary.initClass(ModCropList.class);
		ReikaJavaLibrary.initClass(PowerTypes.class);
		ReikaJavaLibrary.initClass(InterfaceCache.class);
	}

	@EventHandler
	public void lastLoad(FMLServerAboutToStartEvent evt) {
		ReikaOreHelper.refreshAll();
		ModOreList.initializeAll();
	}

	private void registerHandler(ModList mod, Class<? extends ModHandlerBase> c) {
		this.registerHandler(mod, c, new VersionIgnore());
	}

	private void registerHandler(ModList mod, Class<? extends ModHandlerBase> c, VersionHandler vh) {
		if (mod.isLoaded()) {
			try {
				String ver = mod.getVersion();
				if (vh.acceptVersion(ver)) {
					this.initHandler(mod, c);
					logger.log("Loading handler "+c+" for mod "+mod+" "+ver+".");
				}
				else {
					logger.log("Not loading handler "+c+" for "+mod.getDisplayName()+"; Version "+ver+" not compatible with "+vh.toString()+".");
				}
			}
			catch (Exception e) {
				logger.logError("Could not load handler for "+mod.name());
				e.printStackTrace();
			}
			catch (LinkageError e) {
				logger.logError("Class version mismatch error! Could not load handler for "+mod.name());
				e.printStackTrace();
			}
		}
		else {
			logger.log("Not loading handler for "+mod.getDisplayName()+"; Mod not present.");
		}
	}

	private void initHandler(ModList mod, Class<? extends ModHandlerBase> c) throws Exception {
		ReikaJavaLibrary.initClass(c);
		Method inst = c.getMethod("getInstance", null);
		ModHandlerBase h = (ModHandlerBase)inst.invoke(null);
		mod.registerHandler(h);
	}

	public static boolean canLoadHandlers() {
		return Loader.instance().hasReachedState(LoaderState.INITIALIZATION);
	}

	@Override
	public String getDisplayName() {
		return "DragonAPI";
	}

	@Override
	public String getModAuthorName() {
		return "Reika";
	}

	@Override
	public URL getDocumentationSite() {
		return DragonAPICore.getReikaForumPage();
	}

	@Override
	public String getWiki() {
		return null;
	}

	@Override
	public String getUpdateCheckURL() {
		return CommandableUpdateChecker.reikaURL;
	}

	@Override
	public ModLogger getModLogger() {
		return logger;
	}

	static {
		loadLogParsers();
	}

	private static void loadLogParsers() {
		LoggingFilters.registerCoreFilters();
	}

}
