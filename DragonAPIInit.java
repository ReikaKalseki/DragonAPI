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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

import Reika.DragonAPI.DragonAPICore.DragonAPILoadWatcher;
import Reika.DragonAPI.ASM.DragonAPIClassTransformer;
import Reika.DragonAPI.Auxiliary.ChunkManager;
import Reika.DragonAPI.Auxiliary.DragonAPIEventWatcher;
import Reika.DragonAPI.Auxiliary.DynamicRetrogenSettings;
import Reika.DragonAPI.Auxiliary.LoggingFilters;
import Reika.DragonAPI.Auxiliary.LoggingFilters.LoggerType;
import Reika.DragonAPI.Auxiliary.ModularLogger.ModularLoggerCommand;
import Reika.DragonAPI.Auxiliary.NEI_DragonAPI_Config;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker;
import Reika.DragonAPI.Auxiliary.RainTicker;
import Reika.DragonAPI.Auxiliary.RebootScheduler;
import Reika.DragonAPI.Auxiliary.Trackers.ChunkPregenerator;
import Reika.DragonAPI.Auxiliary.Trackers.CommandableUpdateChecker;
import Reika.DragonAPI.Auxiliary.Trackers.CommandableUpdateChecker.CheckerDisableCommand;
import Reika.DragonAPI.Auxiliary.Trackers.CompatibilityTracker;
import Reika.DragonAPI.Auxiliary.Trackers.CrashNotifications;
import Reika.DragonAPI.Auxiliary.Trackers.EnvironmentSanityChecker;
import Reika.DragonAPI.Auxiliary.Trackers.FurnaceFuelRegistry;
import Reika.DragonAPI.Auxiliary.Trackers.IDCollisionTracker;
import Reika.DragonAPI.Auxiliary.Trackers.IntegrityChecker;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher.KeyTicker;
import Reika.DragonAPI.Auxiliary.Trackers.PackModificationTracker;
import Reika.DragonAPI.Auxiliary.Trackers.PatreonController;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerChunkTracker;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerHandler;
import Reika.DragonAPI.Auxiliary.Trackers.RemoteAssetLoader;
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
import Reika.DragonAPI.Command.ClearParticlesCommand;
import Reika.DragonAPI.Command.DragonClientCommand;
import Reika.DragonAPI.Command.DragonCommandBase;
import Reika.DragonAPI.Command.ExportEnvironmentCommand;
import Reika.DragonAPI.Command.GetLatencyCommand;
import Reika.DragonAPI.Command.ToggleBlockChangePacketCommand;
import Reika.DragonAPI.Exception.InvalidBuildException;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Extras.IconPrefabs;
import Reika.DragonAPI.Extras.LoginHandler;
import Reika.DragonAPI.Extras.ReplacementCraftingHandler;
import Reika.DragonAPI.Extras.ReplacementSmeltingHandler;
import Reika.DragonAPI.Extras.SanityCheckNotification;
import Reika.DragonAPI.Extras.TemporaryCodeCalls;
import Reika.DragonAPI.Instantiable.EntityTumblingBlock;
import Reika.DragonAPI.Instantiable.Data.Collections.EventRecipeList;
import Reika.DragonAPI.Instantiable.Event.Client.SinglePlayerLogoutEvent;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Instantiable.IO.LagWarningFilter;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Instantiable.IO.SyncPacket;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.ReikaPotionHelper;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaCommandHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJVMParser;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaReflectionHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModInteract.BannedItemReader;
import Reika.DragonAPI.ModInteract.MinetweakerHooks;
import Reika.DragonAPI.ModInteract.WailaTechnicalOverride;
import Reika.DragonAPI.ModInteract.Bees.ReikaBeeHelper;
import Reika.DragonAPI.ModInteract.Computers.PeripheralHandlerRelay;
import Reika.DragonAPI.ModInteract.DeepInteract.FrameBlacklist;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader;
import Reika.DragonAPI.ModInteract.DeepInteract.MTInteractionManager;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaMystcraftHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.TwilightForestLootHooks;
import Reika.DragonAPI.ModInteract.ItemHandlers.AppEngHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.BCMachineHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.BCPipeHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.BerryBushHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.BloodMagicHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.BoPBlockHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.CarpenterBlockHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ChiselBlockHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.DartItemHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.DartOreHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ExtraUtilsHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.FactorizationHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ForestryHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.GalacticCraftHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.GregOreHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.HarvestCraftHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.HungerOverhaulHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.IC2Handler;
import Reika.DragonAPI.ModInteract.ItemHandlers.LegacyMagicCropHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.MFRHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.MagicCropHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.MagicaOreHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.MekToolHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.MekanismHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.MimicryHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.MystCraftHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.NaturaBlockHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.OpenBlockHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.OreBerryBushHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.PneumaticPlantHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.QuantumOreHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.RailcraftHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.RedstoneArsenalHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumIDHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumOreHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThermalHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerBlockHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TransitionalOreHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TwilightForestHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.VeryLegacyMagicCropHandler;
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
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = "DragonAPI", version = "v@MAJOR_VERSION@@MINOR_VERSION@", certificateFingerprint = "@GET_FINGERPRINT@", dependencies=DragonAPICore.dependencies)
public class DragonAPIInit extends DragonAPIMod {

	public static final String packetChannel = "DragonAPIData";

	@SidedProxy(clientSide="Reika.DragonAPI.APIProxyClient", serverSide="Reika.DragonAPI.APIProxy")
	public static APIProxy proxy;

	@Instance("DragonAPI")
	public static DragonAPIInit instance = new DragonAPIInit();

	public static final ControlledConfig config = new ControlledConfig(instance, DragonOptions.optionList, null);

	private static ModLogger logger;

	public DragonAPIInit() {
		super();

	}

	@EventHandler
	public void invalidSignature(FMLFingerprintViolationEvent evt) {
		if (!ReikaObfuscationHelper.isDeObfEnvironment()) {
			if (!evt.fingerprints.contains(evt.expectedFingerprint.toLowerCase(Locale.ENGLISH).replaceAll(":", ""))) {
				throw new InvalidBuildException(this, evt.source, "Invalid mod jarsign fingerprint!");
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

		int val = DragonOptions.LAGWARNING.getValue();
		if (val > 0) {
			LoggingFilters.registerFilter(new LagWarningFilter(val), LoggerType.SERVER);
			logger.log("Preparing to filter unneeded and/or trivial 'can't keep up' server log messages with delays of less than "+val+" ms.");
		}

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			MinecraftForge.EVENT_BUS.register(DragonAPILoadWatcher.instance);
		MinecraftForge.EVENT_BUS.register(DragonAPIEventWatcher.instance);

		logger.log("Initializing libraries with max recursion depth of "+this.getRecursionDepth());

		proxy.registerSidedHandlers();

		this.registerTechnicalBlocks();

		ChunkManager.instance.register();
		DragonOptions.getCollectKey(); //just to init

		this.initalizeVanillaOreDict();

		ReikaJavaLibrary.initClass(ModList.class);

		this.increasePotionCount();
		this.increaseChunkCap();
		//this.increaseBiomeCount(); world save stores biome as bytes, so 255 is cap

		if (ReikaObfuscationHelper.isDeObfEnvironment())
			TemporaryCodeCalls.preload(evt);

		this.basicSetup(evt);

		ReikaPacketHelper.registerPacketHandler(instance, packetChannel, new APIPacketHandler());

		int id = DragonOptions.SYNCPACKET.getValue();
		ReikaPacketHelper.registerVanillaPacketType(this, id, SyncPacket.class, Side.SERVER, EnumConnectionState.PLAY);
		//if (DragonOptions.COMPOUNDSYNC.getState())
		//	ReikaPacketHelper.registerVanillaPacketType(this, id+1, CompoundSyncPacket.class, Side.SERVER, EnumConnectionState.PLAY);

		if (!DragonOptions.CHECKSANITY.getState())
			CrashNotifications.instance.addNotification(null, new SanityCheckNotification());

		this.finishTiming();
	}

	@Override
	protected void postPreLoad() {
		this.rebuildAndRegisterVanillaRecipes();

		BannedItemReader.instance.initWith(BannedItemReader.PLUGIN_PATH+"/BanItem", "*");
		BannedItemReader.instance.initWith(BannedItemReader.PLUGIN_PATH+"/ItemBan", "*");
		BannedItemReader.instance.initWith(BannedItemReader.PLUGIN_PATH+"/TekkitCustomizerData", "*");
		BannedItemReader.instance.initWith(BannedItemReader.PLUGIN_PATH+"/TekkitCustomizer", "*");
		BannedItemReader.instance.initWith(DragonAPICore.getServerRootFolder(), "ItemBlacklist.json");
		BannedItemReader.instance.initWith(Loader.instance().getConfigDir().getAbsolutePath(), "ItemBlacklist.json");
	}

	private void rebuildAndRegisterVanillaRecipes() {
		/*
		AddRecipeEvent.isVanillaPass = true;
		//AddSmeltingEvent.isVanillaPass = true;
		ArrayList<IRecipe> li = new ArrayList(CraftingManager.getInstance().getRecipeList());
		CraftingManager.getInstance().getRecipeList().clear();
		for (IRecipe r : li) {
			if (ReikaRecipeHelper.verifyRecipe(r)) {
				if (ReikaRecipeHelper.isNonVForgeRecipeClass(r)) {
					DragonAPICore.log("Found a modded recipe registered in pre-init! This is a design error, as it can trigger a Forge bug and break the recipe! Recipe="+ReikaRecipeHelper.toString(r));
				}
				//if (!DragonAPIMod.EARLY_BUS.post(new AddRecipeEvent(r, true)))
				CraftingManager.getInstance().getRecipeList().add(r);
			}
			else {
				DragonAPICore.logError("Found an invalid recipe in the list, with either nulled inputs or outputs! This is invalid! Class="+r.getClass());
			}
		}
		 */
		ReplacementCraftingHandler.fireEventsForVanillaRecipes();
		((EventRecipeList)CraftingManager.getInstance().getRecipeList()).filterVanillaRecipes();
		ReplacementSmeltingHandler.fireEventsForVanillaRecipes();
		/* Not needed anymore since overwrite of vanilla system
		HashMap<ItemStack, Object[]> map = new HashMap();
		for (Object o : FurnaceRecipes.smelting().getSmeltingList().keySet()) {
			ItemStack is = (ItemStack)o;
			ItemStack is2 = (ItemStack)FurnaceRecipes.smelting().getSmeltingList().get(is);
			Object[] dat = {is2, FurnaceRecipes.smelting().func_151398_b(is2)};
			map.put(is, dat);
		}
		FurnaceRecipes.smelting().getSmeltingList().clear();
		for (ItemStack is : map.keySet()) {
			Object[] dat = map.get(is);
			ItemStack is2 = (ItemStack)dat[0];
			float xp = (float)dat[1];
			ReikaRecipeHelper.addSmelting(is, is2, xp);
		}
		 */

		//AddRecipeEvent.isVanillaPass = false;
		//AddSmeltingEvent.isVanillaPass = false;
	}

	private int getRecursionDepth() {
		int config = ReikaJVMParser.getArgumentInteger("-DragonAPI_RecursionLimit");//DragonOptions.RECURSE.getValue();
		if (config == -1)
			config = 0;
		int min = 500;
		int max = 50000;
		if (config < min && config != 0) {
			logger.logError("Specified recursion limit of "+config+" is far too low, and has been clamped to "+min+".");
			config = min;
		}
		if (config > max && config != 0) {
			logger.logError("Specified recursion limit of "+config+" is far too high, and has been clamped to "+max+".");
			config = max;
		}
		return config > 0 ? config : ReikaJavaLibrary.getMaximumRecursiveDepth();
	}

	private void initalizeVanillaOreDict() {
		OreDictionary.registerOre("netherrack", Blocks.netherrack);
		OreDictionary.registerOre("soulsand", Blocks.soul_sand);
		OreDictionary.registerOre("flower", Blocks.yellow_flower);
		for (int i = 0; i < BlockFlower.field_149859_a.length; i++) {
			OreDictionary.registerOre("flower", new ItemStack(Blocks.red_flower, 1, i));
		}
	}

	private void increaseChunkCap() {
		Configuration cfg = ForgeChunkManager.getConfig();
		Property modTC = cfg.get(this.getModContainer().getModId(), "maximumTicketCount", 1000);
		Property modCPT = cfg.get(this.getModContainer().getModId(), "maximumChunksPerTicket", 2000);
		cfg.save();
	}

	/** Registers all the vanilla technical blocks (except air and block 36) to have items so as to avoid crashes when rendering them
	 * in the inventory. */
	private void registerTechnicalBlocks() {
		Block[] technicalBlocks = {
				Blocks.brewing_stand, Blocks.bed, Blocks.nether_wart, Blocks.cauldron, Blocks.flower_pot, Blocks.wheat, Blocks.reeds,
				Blocks.cake, Blocks.skull, Blocks.piston_head, Blocks.lit_redstone_ore, Blocks.powered_repeater, Blocks.pumpkin_stem,
				Blocks.standing_sign, Blocks.powered_comparator, Blocks.tripwire, Blocks.lit_redstone_lamp, Blocks.melon_stem,
				Blocks.unlit_redstone_torch, Blocks.unpowered_comparator, Blocks.redstone_wire, Blocks.wall_sign,
				Blocks.unpowered_repeater, Blocks.iron_door, Blocks.wooden_door
		};

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

		DragonAPICore.log("Added technical blocks for "+Arrays.toString(technicalBlocks));
		DragonAPICore.log("Note that if you remove DragonAPI, you will need to install the patch from Reika's website for the first launch afterwards.");
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

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			RemoteAssetLoader.instance.checkAndStartDownloads();

		if (ReikaObfuscationHelper.isDeObfEnvironment())
			TemporaryCodeCalls.load(event);

		ReikaRegistryHelper.loadNames();

		PlayerHandler.instance.registerTracker(LoginHandler.instance);

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new APIGuiHandler());

		ReikaRegistryHelper.registerModEntity(this, EntityTumblingBlock.class, "Tumbling Block", true, 128);

		//ReikaPacketHelper.initPipelines();

		BiomeGenBase.ocean.rainfall = Math.max(1, BiomeGenBase.ocean.rainfall);
		BiomeGenBase.deepOcean.rainfall = Math.max(1, BiomeGenBase.deepOcean.rainfall);

		TickRegistry.instance.registerTickHandler(ProgressiveRecursiveBreaker.instance);
		TickRegistry.instance.registerTickHandler(TickScheduler.instance);
		TickRegistry.instance.registerTickHandler(ChunkPregenerator.instance);
		if (DragonOptions.RAINTICK.getState())
			TickRegistry.instance.registerTickHandler(RainTicker.instance);
		if (DragonOptions.AUTOREBOOT.getValue() > 0)
			TickRegistry.instance.registerTickHandler(RebootScheduler.instance);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			TickRegistry.instance.registerTickHandler(KeyTicker.instance);
			TickRegistry.instance.registerTickHandler(new ReikaRenderHelper.RenderTick());
		}
		TickRegistry.instance.registerTickHandler(PlayerChunkTracker.instance);

		//if (DragonOptions.COMPOUNDSYNC.getState())
		//	TickRegistry.instance.registerTickHandler(CompoundSyncPacketTracker.instance, Side.SERVER);

		FMLInterModComms.sendMessage("Waila", "register", "Reika.DragonAPI.ModInteract.LegacyWailaHelper.registerObjects");
		FMLInterModComms.sendMessage("Waila", "register", "Reika.DragonAPI.ModInteract.WailaTechnicalOverride.registerOverride");

		FMLInterModComms.sendMessage("Mystcraft", "API", "Reika.DragonAPI.ModInteract.DeepInteract.ReikaMystcraftHelper.receiveAPI");

		FMLInterModComms.sendMessage("OpenBlocks", "donateUrl", "https://sites.google.com/site/reikasminecraft/support-me");

		if (DragonOptions.UNNERFOBSIDIAN.getState())
			Blocks.obsidian.setResistance(2000);

		if (ModList.COMPUTERCRAFT.isLoaded()) {
			PeripheralHandlerRelay.registerCCHandler();
		}

		if (ModList.OPENCOMPUTERS.isLoaded() && !DragonOptions.DIRECTOC.getState()) {
			PeripheralHandlerRelay.registerOCHandler();
		}

		PatreonController.instance.registerMod("Reika", PatreonController.reikaURL);

		logger.log("Credit to Techjar for hosting the version file and remote asset server.");

		CommandableUpdateChecker.instance.checkAll();

		this.finishTiming();
	}

	@Override
	@EventHandler
	public void postload(FMLPostInitializationEvent evt) {
		this.startTiming(LoadPhase.POSTLOAD);

		if (DragonOptions.CHECKSANITY.getState())
			EnvironmentSanityChecker.instance.check();

		PackModificationTracker.instance.loadAll();

		this.loadHandlers();
		this.initFlowerSeedsRegistration();

		if (ReikaObfuscationHelper.isDeObfEnvironment())
			TemporaryCodeCalls.postload(evt);

		this.alCompat();

		GameRegistry.registerFuelHandler(FurnaceFuelRegistry.instance);

		//ReikaPacketHelper.postInitPipelines();

		IDCollisionTracker.instance.check();
		VanillaIntegrityTracker.instance.check();

		CompatibilityTracker.instance.test();

		ReikaPotionHelper.loadBadPotions();

		proxy.postLoad();

		IntegrityChecker.instance.testIntegrity();

		ReikaOreHelper.refreshAll();
		ModOreList.initializeAll();

		SuggestedModsTracker.instance.printConsole();

		//CreativeTabSorter.instance.sortTabs(); //frequently messes up

		ReikaJavaLibrary.initClass(FrameBlacklist.class);
		ReikaJavaLibrary.initClass(ReikaMystcraftHelper.class);
		ReikaJavaLibrary.initClass(ReikaThaumHelper.class);
		ReikaJavaLibrary.initClass(SmelteryRecipeHandler.class);
		ReikaJavaLibrary.initClass(TwilightForestLootHooks.class);

		if (ModList.APPENG.isLoaded()) {
			MESystemReader.registerEffectHandler();
		}

		if (MTInteractionManager.isMTLoaded()) {
			MinetweakerHooks.instance.registerAll();
		}

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			ClientCommandHandler.instance.registerCommand(new ToggleBlockChangePacketCommand());
			ClientCommandHandler.instance.registerCommand(new GetLatencyCommand());
			ClientCommandHandler.instance.registerCommand(new ClearParticlesCommand());
			ClientCommandHandler.instance.registerCommand(new ExportEnvironmentCommand());
		}

		if (DragonOptions.BIOMEFIRE.getState()) {
			BiomeGenBase.desert.rainfall = BiomeGenBase.desertHills.rainfall; //to differentiate hell and desert
			BiomeGenBase.ocean.rainfall = BiomeGenBase.deepOcean.rainfall = 1F;
		}

		if (DragonOptions.PLAYERMOBCAP.getState()) {
			String f = ReikaObfuscationHelper.isDeObfEnvironment() ? "maxNumberOfCreature" : "field_75606_e";
			try {
				ReikaReflectionHelper.setFinalField(EnumCreatureType.class, f, EnumCreatureType.monster, Integer.MAX_VALUE);
			}
			catch (Exception e) {
				throw new RegistrationException(this, "Could not set mob spawn cap!", e);
			}
		}

		FluidRegistry.WATER.setBlock(Blocks.water); //needs to be re-set due to CoFH overwriting

		this.finishTiming();
	}

	private void initFlowerSeedsRegistration() {
		if (ModList.BOP.isLoaded() && BoPBlockHandler.getInstance().initializedProperly()) {
			for (int i = 0; i < BoPBlockHandler.Flower1Types.values().length; i++) {
				OreDictionary.registerOre("flower", new ItemStack(BoPBlockHandler.getInstance().flower1, 1, i));
			}
			for (int i = 0; i < BoPBlockHandler.Flower2Types.values().length; i++) {
				OreDictionary.registerOre("flower", new ItemStack(BoPBlockHandler.getInstance().flower2, 1, i));
			}
		}
		if (ModList.BOTANIA.isLoaded()) {
			Block flower = GameRegistry.findBlock(ModList.BOTANIA.modLabel, "flower");
			Block tallflower1 = GameRegistry.findBlock(ModList.BOTANIA.modLabel, "doubleFlower1");
			Block tallflower2 = GameRegistry.findBlock(ModList.BOTANIA.modLabel, "doubleFlower2");
			for (int i = 0; i < 16; i++) {
				Block tall = i >= 8 ? tallflower2 : tallflower1;
				int tallm = i%8;
				OreDictionary.registerOre("flower", new ItemStack(flower, 1, i));
				OreDictionary.registerOre("flower", new ItemStack(tall, 1, tallm));
			}
		}
		if (ModList.PROJRED.isLoaded()) {
			Block flower = GameRegistry.findBlock("ProjRed|Exploration", "projectred.exploration.lily");
			if (flower != null) {
				OreDictionary.registerOre("flower", flower);
			}
			ItemStack seed = ReikaItemHelper.lookupItem("ProjRed|Exploration:projectred.exploration.lilyseed");
			if (seed != null) {
				OreDictionary.registerOre("seed", seed);
			}
		}
		OreDictionary.registerOre("seed", Items.wheat_seeds);
		OreDictionary.registerOre("seed", Items.pumpkin_seeds);
		OreDictionary.registerOre("seed", Items.melon_seeds);
		OreDictionary.registerOre("seed", Items.nether_wart);
		for (int i = 0; i < ModCropList.cropList.length; i++) {
			ModCropList mod = ModCropList.cropList[i];
			if (mod.seedID != null) {
				OreDictionary.registerOre("seed", new ItemStack(mod.seedID, 1, mod.seedMeta));
			}
		}
		if (ModList.AGRICRAFT.isLoaded()) {
			ItemStack pseed = ReikaItemHelper.lookupItem("AgriCraft:seedPotato");
			if (pseed != null) {
				OreDictionary.registerOre("seed", pseed);
			}
			ItemStack cseed = ReikaItemHelper.lookupItem("AgriCraft:seedCarrot");
			if (cseed != null) {
				OreDictionary.registerOre("seed", cseed);
			}
		}
	}

	private void sortCreativeTabs() {

	}

	@EventHandler
	public void sortRecipes(FMLLoadCompleteEvent evt) {
		if (ForgeModContainer.shouldSortRecipies)
			ReplacementCraftingHandler.sortRecipes();

		DynamicRetrogenSettings.instance.loadConfig();
	}

	@EventHandler
	public void registerCommands(FMLServerStartingEvent evt) {
		DragonAPICore.log("Server Starting...");
		try {
			for (Class c : ReikaJavaLibrary.getAllClassesFromPackage("Reika.DragonAPI.Command", DragonCommandBase.class, true, true)) {
				if (DragonClientCommand.class.isAssignableFrom(c))
					continue;
				try {
					evt.registerServerCommand((DragonCommandBase)c.newInstance());
				}
				catch (Exception e) {
					throw new RuntimeException("Could not construct command '"+c+"'!");
				}
			}
		}
		catch (IOException e) {
			throw new RuntimeException("Could not find DragonAPI commands!", e);
		}

		evt.registerServerCommand(new CheckerDisableCommand());
		evt.registerServerCommand(new ModularLoggerCommand());

		if (MTInteractionManager.isMTLoaded() && !DragonAPICore.isSinglePlayer())
			MTInteractionManager.instance.scanAndRevert();

		DragonAPICore.setGameLoaded();
	}

	@EventHandler
	public void printPackData(FMLServerStartedEvent evt) {
		DragonAPICore.log("Server Started.");
		DragonAPICore.log("Total Crafting Recipes: "+CraftingManager.getInstance().getRecipeList().size());
		DragonAPICore.log("Dimensions Present: "+Arrays.toString(DimensionManager.getStaticDimensionIDs()));
		DragonAPICore.log("Mods Present: "+Loader.instance().getActiveModList().size());
		DragonAPICore.log("ASM Transformers Loaded: "+Launch.classLoader.getTransformers().size());
		if (MinecraftServer.getServer() != null)
			DragonAPICore.log("Commands Loaded: "+ReikaCommandHelper.getCommandList().size());
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
		this.registerHandler(ModList.BCFACTORY, BCMachineHandler.class, "Block Handler");
		this.registerHandler(ModList.BCTRANSPORT, BCPipeHandler.class, "Pipe Handler");
		this.registerHandler(ModList.THAUMCRAFT, ThaumOreHandler.class, "Ore Handler");
		this.registerHandler(ModList.THAUMCRAFT, ThaumIDHandler.class, "Biome Handler");
		this.registerHandler(ModList.DARTCRAFT, DartOreHandler.class, "Ore Handler");
		this.registerHandler(ModList.DARTCRAFT, DartItemHandler.class, "Item Handler");
		this.registerHandler(ModList.TINKERER, TinkerToolHandler.class, "Tool Handler");
		this.registerHandler(ModList.TINKERER, TinkerBlockHandler.class, "Block Handler");
		this.registerHandler(ModList.TWILIGHT, TwilightForestHandler.class, "Handler");
		this.registerHandler(ModList.MEKANISM, MekanismHandler.class, "Block Handler");
		this.registerHandler(ModList.MEKTOOLS, MekToolHandler.class, "Tool Handler");
		this.registerHandler(ModList.TRANSITIONAL, TransitionalOreHandler.class, "Handler");
		this.registerHandler(ModList.IC2, IC2Handler.class, "Handler");
		this.registerHandler(ModList.ARSMAGICA, MagicaOreHandler.class, "Ore Handler");
		this.registerHandler(ModList.APPENG, AppEngHandler.class, "Handler");
		this.registerHandler(ModList.FORESTRY, ForestryHandler.class, "Item Handler");
		this.registerHandler(ModList.FORESTRY, ForestryRecipeHelper.class, "Recipe Handler");
		this.registerHandler(ModList.THERMALFOUNDATION, ThermalHandler.class, "Handler");
		this.registerHandler(ModList.MIMICRY, MimicryHandler.class, "Handler");
		this.registerHandler(ModList.MAGICCROPS, MagicCropHandler.class, "Handler", new SearchVersionHandler("4.0.0_PUBLIC_BETA")); //Newest
		this.registerHandler(ModList.MAGICCROPS, LegacyMagicCropHandler.class, "Handler", new SearchVersionHandler("4.0.0_BETA")); //Private Beta
		this.registerHandler(ModList.MAGICCROPS, VeryLegacyMagicCropHandler.class, "Handler", new SearchVersionHandler("1.7.2 - 0.1 ALPHA")); //1.7.10 alpha
		this.registerHandler(ModList.QCRAFT, QuantumOreHandler.class, "Handler");
		this.registerHandler(ModList.TINKERER, OreBerryBushHandler.class, "Handler");
		this.registerHandler(ModList.NATURA, BerryBushHandler.class, "Handler");
		this.registerHandler(ModList.OPENBLOCKS, OpenBlockHandler.class, "Handler");
		this.registerHandler(ModList.FACTORIZATION, FactorizationHandler.class, "Handler");
		this.registerHandler(ModList.HARVESTCRAFT, HarvestCraftHandler.class, "Handler");
		this.registerHandler(ModList.ARSENAL, RedstoneArsenalHandler.class, "Handler");
		this.registerHandler(ModList.RAILCRAFT, RailcraftHandler.class, "Handler");
		this.registerHandler(ModList.MINEFACTORY, MFRHandler.class, "Handler");
		this.registerHandler(ModList.GALACTICRAFT, GalacticCraftHandler.class, "Handler");
		this.registerHandler(ModList.EXTRAUTILS, ExtraUtilsHandler.class, "Handler");
		this.registerHandler(ModList.MYSTCRAFT, MystCraftHandler.class, "Block Handler");
		this.registerHandler(ModList.BLOODMAGIC, BloodMagicHandler.class, "Handler");
		this.registerHandler(ModList.PNEUMATICRAFT, PneumaticPlantHandler.class, "Handler");
		this.registerHandler(ModList.BOP, BoPBlockHandler.class, "Handler");
		this.registerHandler(ModList.HUNGEROVERHAUL, HungerOverhaulHandler.class, "Handler");
		this.registerHandler(ModList.CARPENTER, CarpenterBlockHandler.class, "Handler");
		this.registerHandler(ModList.CHISEL, ChiselBlockHandler.class, "Handler");
		this.registerHandler(ModList.GREGTECH, GregOreHandler.class, "Ore Handler");
		this.registerHandler(ModList.NATURA, NaturaBlockHandler.class, "Block Handler");

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
		ReplacementSmeltingHandler.build();
		ReikaDyeHelper.buildItemCache();
		if (ModList.FORESTRY.isLoaded()) {
			ReikaBeeHelper.buildSpeciesList();
		}
	}

	@EventHandler
	public void singlePlayerLogout(FMLServerStoppedEvent evt) {
		ReikaPlayerAPI.clearHeadCache();
		if (evt.getSide() == Side.CLIENT) {
			MinecraftForge.EVENT_BUS.post(new SinglePlayerLogoutEvent());
		}
	}

	private void registerHandler(ModList mod, Class<? extends ModHandlerBase> c, String id) {
		this.registerHandler(mod, c, id, new VersionIgnore());
	}

	private void registerHandler(ModList mod, Class<? extends ModHandlerBase> c, String id, VersionHandler vh) {
		if (mod.isLoaded()) {
			try {
				String ver = mod.getVersion();
				if (vh.acceptVersion(ver)) {
					this.initHandler(mod, c, id);
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

	private void initHandler(ModList mod, Class<? extends ModHandlerBase> c, String id) throws Exception {
		ReikaJavaLibrary.initClass(c);
		Method inst = c.getMethod("getInstance", null);
		ModHandlerBase h = (ModHandlerBase)inst.invoke(null);
		mod.registerHandler(h, id);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void setupExtraIcons(TextureStitchEvent.Pre event) {
		logger.log("Loading Additional Icons");

		if (event.map.getTextureType() == 0) {
			IconPrefabs.registerAll(event.map);
		}

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

	@Override
	public File getConfigFolder() {
		return config.getConfigFolder();
	}

	@Override
	protected Class<? extends IClassTransformer> getASMClass() {
		return DragonAPIClassTransformer.class;
	}

	static {
		loadLogParsers();
	}

	private static void loadLogParsers() {
		LoggingFilters.registerCoreFilters();

		/*
		if (Loader.isModLoaded("endercore")) {
			Logger enderCore = (Logger)LogManager.getLogger("EnderCore");
			if (enderCore != null) {
				enderCore.addFilter(new ReplyFilter(enderCore, "Removed 0 missing texture stacktraces. Tada!", "DRAGONAPI: Congratulations. Have 0 cookies."));
			}
		}
		 */
	}

}
