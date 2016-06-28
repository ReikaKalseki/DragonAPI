/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.OreDictionary;
import reika.dragonapi.DragonAPICore.DragonAPILoadWatcher;
import reika.dragonapi.auxiliary.ChunkManager;
import reika.dragonapi.auxiliary.DragonAPIEventWatcher;
import reika.dragonapi.auxiliary.LoggingFilters;
import reika.dragonapi.auxiliary.NEI_DragonAPI_Config;
import reika.dragonapi.auxiliary.ProgressiveRecursiveBreaker;
import reika.dragonapi.auxiliary.RebootScheduler;
import reika.dragonapi.auxiliary.LoggingFilters.LoggerType;
import reika.dragonapi.auxiliary.ModularLogger.ModularLoggerCommand;
import reika.dragonapi.auxiliary.trackers.BiomeCollisionTracker;
import reika.dragonapi.auxiliary.trackers.ChunkPregenerator;
import reika.dragonapi.auxiliary.trackers.CommandableUpdateChecker;
import reika.dragonapi.auxiliary.trackers.CompatibilityTracker;
import reika.dragonapi.auxiliary.trackers.CrashNotifications;
import reika.dragonapi.auxiliary.trackers.EnchantmentCollisionTracker;
import reika.dragonapi.auxiliary.trackers.EnvironmentSanityChecker;
import reika.dragonapi.auxiliary.trackers.FurnaceFuelRegistry;
import reika.dragonapi.auxiliary.trackers.IntegrityChecker;
import reika.dragonapi.auxiliary.trackers.PackModificationTracker;
import reika.dragonapi.auxiliary.trackers.PatreonController;
import reika.dragonapi.auxiliary.trackers.PlayerChunkTracker;
import reika.dragonapi.auxiliary.trackers.PlayerHandler;
import reika.dragonapi.auxiliary.trackers.PotionCollisionTracker;
import reika.dragonapi.auxiliary.trackers.RemoteAssetLoader;
import reika.dragonapi.auxiliary.trackers.SuggestedModsTracker;
import reika.dragonapi.auxiliary.trackers.TickRegistry;
import reika.dragonapi.auxiliary.trackers.TickScheduler;
import reika.dragonapi.auxiliary.trackers.VanillaIntegrityTracker;
import reika.dragonapi.auxiliary.trackers.CommandableUpdateChecker.CheckerDisableCommand;
import reika.dragonapi.auxiliary.trackers.KeyWatcher.KeyTicker;
import reika.dragonapi.base.DragonAPIMod;
import reika.dragonapi.base.ModHandlerBase;
import reika.dragonapi.base.DragonAPIMod.LoadProfiler.LoadPhase;
import reika.dragonapi.base.ModHandlerBase.SearchVersionHandler;
import reika.dragonapi.base.ModHandlerBase.VersionHandler;
import reika.dragonapi.base.ModHandlerBase.VersionIgnore;
import reika.dragonapi.command.BiomeMapCommand;
import reika.dragonapi.command.BlockReplaceCommand;
import reika.dragonapi.command.ChunkGenCommand;
import reika.dragonapi.command.ClassLoaderCommand;
import reika.dragonapi.command.ClearItemsCommand;
import reika.dragonapi.command.CommandOwnerCommand;
import reika.dragonapi.command.ConfigReloadCommand;
import reika.dragonapi.command.DonatorCommand;
import reika.dragonapi.command.EditNearbyInventoryCommand;
import reika.dragonapi.command.EntityCountCommand;
import reika.dragonapi.command.EntityListCommand;
import reika.dragonapi.command.FindBiomeCommand;
import reika.dragonapi.command.FindThreadCommand;
import reika.dragonapi.command.FindTilesCommand;
import reika.dragonapi.command.GuideCommand;
import reika.dragonapi.command.IDDumpCommand;
import reika.dragonapi.command.ItemNBTCommand;
import reika.dragonapi.command.LogControlCommand;
import reika.dragonapi.command.PlayerNBTCommand;
import reika.dragonapi.command.PopulateMinimapCommand;
import reika.dragonapi.command.SelectiveKillCommand;
import reika.dragonapi.command.SpawnMobsCommand;
import reika.dragonapi.command.StructureExportCommand;
import reika.dragonapi.command.TestControlCommand;
import reika.dragonapi.command.TileSyncCommand;
import reika.dragonapi.exception.InvalidBuildException;
import reika.dragonapi.extras.LoginHandler;
import reika.dragonapi.extras.SanityCheckNotification;
import reika.dragonapi.extras.TemporaryCodeCalls;
import reika.dragonapi.instantiable.EntityTumblingBlock;
import reika.dragonapi.instantiable.event.AddRecipeEvent;
import reika.dragonapi.instantiable.event.AddSmeltingEvent;
import reika.dragonapi.instantiable.event.client.SinglePlayerLogoutEvent;
import reika.dragonapi.instantiable.io.ControlledConfig;
import reika.dragonapi.instantiable.io.LagWarningFilter;
import reika.dragonapi.instantiable.io.ModLogger;
import reika.dragonapi.instantiable.io.SyncPacket;
import reika.dragonapi.libraries.ReikaPotionHelper;
import reika.dragonapi.libraries.ReikaRecipeHelper;
import reika.dragonapi.libraries.ReikaRegistryHelper;
import reika.dragonapi.libraries.io.ReikaCommandHelper;
import reika.dragonapi.libraries.io.ReikaPacketHelper;
import reika.dragonapi.libraries.io.ReikaRenderHelper;
import reika.dragonapi.libraries.java.ReikaJVMParser;
import reika.dragonapi.libraries.java.ReikaJavaLibrary;
import reika.dragonapi.libraries.java.ReikaObfuscationHelper;
import reika.dragonapi.libraries.registry.ReikaItemHelper;
import reika.dragonapi.libraries.registry.ReikaOreHelper;
import reika.dragonapi.mod.interact.BannedItemReader;
import reika.dragonapi.mod.interact.MinetweakerHooks;
import reika.dragonapi.mod.interact.WailaTechnicalOverride;
import reika.dragonapi.mod.interact.deepinteract.FrameBlacklist;
import reika.dragonapi.mod.interact.deepinteract.MTInteractionManager;
import reika.dragonapi.mod.interact.deepinteract.ReikaMystcraftHelper;
import reika.dragonapi.mod.interact.deepinteract.ReikaThaumHelper;
import reika.dragonapi.mod.interact.deepinteract.TwilightForestLootHooks;
import reika.dragonapi.mod.interact.itemhandlers.AppEngHandler;
import reika.dragonapi.mod.interact.itemhandlers.BCMachineHandler;
import reika.dragonapi.mod.interact.itemhandlers.BCPipeHandler;
import reika.dragonapi.mod.interact.itemhandlers.BerryBushHandler;
import reika.dragonapi.mod.interact.itemhandlers.BloodMagicHandler;
import reika.dragonapi.mod.interact.itemhandlers.BoPBlockHandler;
import reika.dragonapi.mod.interact.itemhandlers.CarpenterBlockHandler;
import reika.dragonapi.mod.interact.itemhandlers.ChiselBlockHandler;
import reika.dragonapi.mod.interact.itemhandlers.DartItemHandler;
import reika.dragonapi.mod.interact.itemhandlers.DartOreHandler;
import reika.dragonapi.mod.interact.itemhandlers.ExtraUtilsHandler;
import reika.dragonapi.mod.interact.itemhandlers.FactorizationHandler;
import reika.dragonapi.mod.interact.itemhandlers.ForestryHandler;
import reika.dragonapi.mod.interact.itemhandlers.GalacticCraftHandler;
import reika.dragonapi.mod.interact.itemhandlers.HarvestCraftHandler;
import reika.dragonapi.mod.interact.itemhandlers.HungerOverhaulHandler;
import reika.dragonapi.mod.interact.itemhandlers.IC2Handler;
import reika.dragonapi.mod.interact.itemhandlers.LegacyMagicCropHandler;
import reika.dragonapi.mod.interact.itemhandlers.MFRHandler;
import reika.dragonapi.mod.interact.itemhandlers.MagicCropHandler;
import reika.dragonapi.mod.interact.itemhandlers.MagicaOreHandler;
import reika.dragonapi.mod.interact.itemhandlers.MekToolHandler;
import reika.dragonapi.mod.interact.itemhandlers.MekanismHandler;
import reika.dragonapi.mod.interact.itemhandlers.MimicryHandler;
import reika.dragonapi.mod.interact.itemhandlers.MystCraftHandler;
import reika.dragonapi.mod.interact.itemhandlers.OpenBlockHandler;
import reika.dragonapi.mod.interact.itemhandlers.OreBerryBushHandler;
import reika.dragonapi.mod.interact.itemhandlers.PneumaticPlantHandler;
import reika.dragonapi.mod.interact.itemhandlers.QuantumOreHandler;
import reika.dragonapi.mod.interact.itemhandlers.RailcraftHandler;
import reika.dragonapi.mod.interact.itemhandlers.RedstoneArsenalHandler;
import reika.dragonapi.mod.interact.itemhandlers.ThaumIDHandler;
import reika.dragonapi.mod.interact.itemhandlers.ThaumOreHandler;
import reika.dragonapi.mod.interact.itemhandlers.ThermalHandler;
import reika.dragonapi.mod.interact.itemhandlers.TinkerBlockHandler;
import reika.dragonapi.mod.interact.itemhandlers.TinkerToolHandler;
import reika.dragonapi.mod.interact.itemhandlers.TransitionalOreHandler;
import reika.dragonapi.mod.interact.itemhandlers.TwilightForestHandler;
import reika.dragonapi.mod.interact.itemhandlers.VeryLegacyMagicCropHandler;
import reika.dragonapi.mod.interact.recipehandlers.ForestryRecipeHelper;
import reika.dragonapi.mod.interact.recipehandlers.SmelteryRecipeHandler;
import reika.dragonapi.mod.registry.InterfaceCache;
import reika.dragonapi.mod.registry.ModCropList;
import reika.dragonapi.mod.registry.ModOreList;
import reika.dragonapi.mod.registry.ModWoodList;
import reika.dragonapi.mod.registry.PowerTypes;
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
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = "DragonAPI", certificateFingerprint = "@GET_FINGERPRINT@", dependencies=DragonAPICore.dependencies)
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

		this.initalizeVanillaOreDict();

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

		if (!DragonOptions.CHECKSANITY.getState())
			CrashNotifications.instance.addNotification(null, new SanityCheckNotification());

		this.finishTiming();
	}

	@Override
	protected void postPreLoad() {
		this.rebuildAndRegisterVanillaRecipes();
	}

	private void rebuildAndRegisterVanillaRecipes() {
		AddRecipeEvent.isVanillaPass = true;
		AddSmeltingEvent.isVanillaPass = true;
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
			//if (!DragonAPIMod.EARLY_BUS.post(new AddSmeltingEvent(is, is2, xp, true)))
			ReikaRecipeHelper.addSmelting(is, is2, xp);
		}
		AddRecipeEvent.isVanillaPass = false;
		AddSmeltingEvent.isVanillaPass = false;
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

		//if (ModList.OPENCOMPUTERS.isLoaded() && !DragonOptions.DIRECTOC.getState()) {
		//	Driver.add(new PeripheralHandler());
		//}

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

		BiomeCollisionTracker.instance.check();
		PotionCollisionTracker.instance.check();
		EnchantmentCollisionTracker.instance.check();
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

		if (MTInteractionManager.isMTLoaded()) {
			MinetweakerHooks.instance.registerAll();
		}

		this.finishTiming();
	}

	private void initFlowerSeedsRegistration() {
		if (ModList.BOP.isLoaded() && BoPBlockHandler.getInstance().initializedProperly()) {
			for (int i = 0; i < BoPBlockHandler.flower1Types.length; i++) {
				OreDictionary.registerOre("flower", new ItemStack(BoPBlockHandler.getInstance().flower1, 1, i));
			}
			for (int i = 0; i < BoPBlockHandler.flower2Types.length; i++) {
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
	public void registerCommands(FMLServerStartingEvent evt) {
		DragonAPICore.log("Server Starting...");
		evt.registerServerCommand(new GuideCommand());
		evt.registerServerCommand(new DonatorCommand());
		evt.registerServerCommand(new LogControlCommand());
		evt.registerServerCommand(new TestControlCommand());
		evt.registerServerCommand(new CheckerDisableCommand());
		evt.registerServerCommand(new SelectiveKillCommand());
		evt.registerServerCommand(new EntityCountCommand());
		evt.registerServerCommand(new BlockReplaceCommand());
		evt.registerServerCommand(new EditNearbyInventoryCommand());
		evt.registerServerCommand(new TileSyncCommand());
		evt.registerServerCommand(new IDDumpCommand());
		evt.registerServerCommand(new EntityListCommand());
		evt.registerServerCommand(new FindTilesCommand());
		evt.registerServerCommand(new ClearItemsCommand());
		evt.registerServerCommand(new FindBiomeCommand());
		evt.registerServerCommand(new ClassLoaderCommand());
		evt.registerServerCommand(new ChunkGenCommand());
		evt.registerServerCommand(new FindThreadCommand());
		evt.registerServerCommand(new ModularLoggerCommand());
		evt.registerServerCommand(new SpawnMobsCommand());
		evt.registerServerCommand(new PopulateMinimapCommand());
		evt.registerServerCommand(new BiomeMapCommand());
		evt.registerServerCommand(new PlayerNBTCommand());
		evt.registerServerCommand(new ConfigReloadCommand());
		evt.registerServerCommand(new CommandOwnerCommand());
		evt.registerServerCommand(new StructureExportCommand());
		evt.registerServerCommand(new ItemNBTCommand());

		if (MTInteractionManager.isMTLoaded() && !DragonAPICore.isSinglePlayer())
			MTInteractionManager.instance.scanAndRevert();
	}

	@EventHandler
	public void overrideRecipes(FMLServerStartedEvent evt) {
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

	@EventHandler
	public void singlePlayerLogout(FMLServerStoppedEvent evt) {
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
