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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.sound.SoundSetupEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerRegisterEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;
import paulscode.sound.SoundSystemConfig;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPICore.DragonAPILoadWatcher;
import Reika.DragonAPI.Auxiliary.ChunkManager;
import Reika.DragonAPI.Auxiliary.DragonAPIEventWatcher;
import Reika.DragonAPI.Auxiliary.FindTilesCommand;
import Reika.DragonAPI.Auxiliary.LoggingFilters;
import Reika.DragonAPI.Auxiliary.LoggingFilters.LoggerType;
import Reika.DragonAPI.Auxiliary.ModularLogger.ModularLoggerCommand;
import Reika.DragonAPI.Auxiliary.NEI_DragonAPI_Config;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker;
import Reika.DragonAPI.Auxiliary.Trackers.BiomeCollisionTracker;
import Reika.DragonAPI.Auxiliary.Trackers.ChunkPregenerator;
import Reika.DragonAPI.Auxiliary.Trackers.CommandableUpdateChecker;
import Reika.DragonAPI.Auxiliary.Trackers.CommandableUpdateChecker.CheckerDisableCommand;
import Reika.DragonAPI.Auxiliary.Trackers.CompatibilityTracker;
import Reika.DragonAPI.Auxiliary.Trackers.EnchantmentCollisionTracker;
import Reika.DragonAPI.Auxiliary.Trackers.EnvironmentSanityChecker;
import Reika.DragonAPI.Auxiliary.Trackers.FurnaceFuelRegistry;
import Reika.DragonAPI.Auxiliary.Trackers.IntegrityChecker;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher.KeyTicker;
import Reika.DragonAPI.Auxiliary.Trackers.PackModificationTracker;
import Reika.DragonAPI.Auxiliary.Trackers.PatreonController;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerHandler;
import Reika.DragonAPI.Auxiliary.Trackers.PotionCollisionTracker;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
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
import Reika.DragonAPI.Command.BiomeMapCommand;
import Reika.DragonAPI.Command.BlockReplaceCommand;
import Reika.DragonAPI.Command.ChunkGenCommand;
import Reika.DragonAPI.Command.ClassLoaderCommand;
import Reika.DragonAPI.Command.ClearItemsCommand;
import Reika.DragonAPI.Command.DonatorCommand;
import Reika.DragonAPI.Command.EditNearbyInventoryCommand;
import Reika.DragonAPI.Command.EntityCountCommand;
import Reika.DragonAPI.Command.EntityListCommand;
import Reika.DragonAPI.Command.FindBiomeCommand;
import Reika.DragonAPI.Command.FindThreadCommand;
import Reika.DragonAPI.Command.GuideCommand;
import Reika.DragonAPI.Command.IDDumpCommand;
import Reika.DragonAPI.Command.LogControlCommand;
import Reika.DragonAPI.Command.PlayerNBTCommand;
import Reika.DragonAPI.Command.PopulateMinimapCommand;
import Reika.DragonAPI.Command.SelectiveKillCommand;
import Reika.DragonAPI.Command.SpawnMobsCommand;
import Reika.DragonAPI.Command.TestControlCommand;
import Reika.DragonAPI.Command.TileSyncCommand;
import Reika.DragonAPI.Exception.InvalidBuildException;
import Reika.DragonAPI.Exception.WTFException;
import Reika.DragonAPI.Extras.LoginHandler;
import Reika.DragonAPI.Extras.TemporaryCodeCalls;
import Reika.DragonAPI.Instantiable.EntityTumblingBlock;
import Reika.DragonAPI.Instantiable.Event.AddRecipeEvent;
import Reika.DragonAPI.Instantiable.Event.AddSmeltingEvent;
import Reika.DragonAPI.Instantiable.Event.ItemUpdateEvent;
import Reika.DragonAPI.Instantiable.Event.Client.ChatEvent.ChatEventPost;
import Reika.DragonAPI.Instantiable.Event.Client.GameFinishedLoadingEvent;
import Reika.DragonAPI.Instantiable.Event.Client.SinglePlayerLogoutEvent;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Instantiable.IO.LagWarningFilter;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Instantiable.IO.SyncPacket;
import Reika.DragonAPI.Libraries.ReikaFluidHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.ReikaPotionHelper;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJVMParser;
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
import Reika.DragonAPI.ModInteract.ItemHandlers.CarpenterBlockHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.DartItemHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.DartOreHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ExtraUtilsHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.FactorizationHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ForestryHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.GalacticCraftHandler;
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
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
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

	public static final ControlledConfig config = new ControlledConfig(instance, DragonOptions.optionList, null);

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
		OreDictionary.initVanillaEntries();
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

		TickRegistry.instance.registerTickHandler(ProgressiveRecursiveBreaker.instance);
		TickRegistry.instance.registerTickHandler(TickScheduler.instance);
		TickRegistry.instance.registerTickHandler(ChunkPregenerator.instance);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			TickRegistry.instance.registerTickHandler(KeyTicker.instance);
			TickRegistry.instance.registerTickHandler(new ReikaRenderHelper.RenderTick());
		}
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
		PatreonController.instance.addPatron(this, "shobu", "6712dff7-a5d3-4a55-9c25-33b50e173ee1", 5);
		PatreonController.instance.addPatron(this, "Goof245", "79849e78-fe9a-4bb9-af6b-fb4c41fc8dd8", 20); //Aiden Young
		PatreonController.instance.addPatron(this, "Solego", "2c85a7d8-af77-4c5e-9416-47e4a281497f", 40);
		PatreonController.instance.addPatron(this, "Frazier", 25);
		PatreonController.instance.addPatron(this, "ReignOfMagic", "f1025e8b-6789-4591-b987-e318e61d7061", 10);
		PatreonController.instance.addPatron(this, "Iskandar", "b6fa35a3-8e74-499d-8cc6-ca83c912a14a", 10);
		PatreonController.instance.addPatron(this, "Yoogain", "5d937faf-7a4f-489a-85db-a1d95bb29657", 25);
		PatreonController.instance.addPatron(this, "foxxmike11", "a52ba32c-edd0-41e8-81dc-de9759716624", 25); //foxxmike
		PatreonController.instance.addPatron(this, "NihilistDandy", "3b29941b-924c-452a-abe5-2bfb6b94fecd", 40);
		PatreonController.instance.addPatron(this, "EQDanteon", "ec00d292-1447-4e99-922c-2653f423e0cb", 40);
		PatreonController.instance.addPatron(this, "Viper2g1", "3bf81769-1510-40b1-80d7-fea0c51aa304", 12); //Viperean
		PatreonController.instance.addPatron(this, "StrayanDropbear", "e95b2aba-35c9-40da-b4cc-62ee68dc2962", 10);

		logger.log("Credit to Techjar for hosting the version file and remote asset server.");

		CommandableUpdateChecker.instance.checkAll();

		this.finishTiming();
	}

	@Override
	@EventHandler
	public void postload(FMLPostInitializationEvent evt) {
		this.startTiming(LoadPhase.POSTLOAD);

		EnvironmentSanityChecker.instance.check();

		PackModificationTracker.instance.loadAll();

		this.loadHandlers();
		this.initFlowerRegistration();

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

	private void initFlowerRegistration() {
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
			DragonAPICore.log("Commands Loaded: "+MinecraftServer.getServer().getCommandManager().getCommands().size());
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
	public void onGameLoaded(GameFinishedLoadingEvent evt) throws InterruptedException {
		this.checkRemoteAssetDownload();
		//if (ModList.liteLoaderInstalled())
		Minecraft.getMinecraft().refreshResources();
		if (ModList.NEI.isLoaded()) {
			NEIIntercept.instance.register();
			//NEIFontRendererHandler.instance.register();
		}
		proxy.registerSidedHandlersGameLoaded();
		ReflectiveFailureTracker.instance.print();
	}

	private void checkRemoteAssetDownload() throws InterruptedException {
		long time = 0;
		long d = 100;
		while (!RemoteAssetLoader.instance.isDownloadComplete()) {
			if (time%5000 == 0) {
				String p = String.format("%.2f", 100*RemoteAssetLoader.instance.getDownloadProgress());
				String s = "Remote asset downloads not yet complete (current = "+p+"%). Pausing game load. Total delay: "+time+" ms.";
				logger.log(s);
			}
			Thread.sleep(d);
			time += d;
		}
	}

	@SubscribeEvent
	public void sendInteractToClient(PlayerInteractEvent evt) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && !ReikaPlayerAPI.isFake(evt.entityPlayer)) {
			ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.PLAYERINTERACT.ordinal(), new PacketTarget.PlayerTarget((EntityPlayerMP)evt.entityPlayer), evt.x, evt.y, evt.z, evt.face, evt.action.ordinal());
		}
	}

	@SubscribeEvent
	public void clearItems(ItemUpdateEvent evt) {
		if (ClearItemsCommand.clearItem(evt.entityItem)) {
			evt.entityItem.setDead();
		}
	}

	@SubscribeEvent
	public void tagDroppedItems(ItemTossEvent evt) {
		if (evt.player != null) {
			String s = evt.player.getUniqueID().toString();
			evt.entityItem.getEntityData().setString("dropper", s);
			//ReikaPacketHelper.sendStringIntPacket(packetChannel, PacketIDs.ITEMDROPPER.ordinal(), new PacketTarget.DimensionTarget(evt.entityItem.worldObj), s, evt.entityItem.getEntityId());
		}
	}

	@SubscribeEvent
	public void tagDroppedItems(EntityJoinWorldEvent evt) {
		if (evt.entity instanceof EntityItem && evt.world.isRemote) {
			//ReikaJavaLibrary.pConsole("Sending clientside request for Entity ID "+evt.entity.getEntityId());
			ReikaPacketHelper.sendDataPacket(packetChannel, PacketIDs.ITEMDROPPERREQUEST.ordinal(), new PacketTarget.ServerTarget(), evt.entity.getEntityId());
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void confirmNumericIDs(ChatEventPost evt) {
		if (evt.chatMessage.startsWith("Warning: Using numeric IDs will not be supported in the future")) {
			String item1 = EnumChatFormatting.GOLD+"/give item.forestry.apiculture.bee.template.root3";
			String item2 = EnumChatFormatting.GOLD+"/give item.gregtech.machine.primary.transformer.hv.ruby";
			String c = EnumChatFormatting.LIGHT_PURPLE.toString();
			ReikaChatHelper.writeString(c+"Numeric IDs will remain functional as long as I am here,");
			ReikaChatHelper.writeString(c+"because not everyone wants to type");
			ReikaChatHelper.writeString(c+"'"+item1+c+"'");
			ReikaChatHelper.writeString(c+"or");
			ReikaChatHelper.writeString(c+"'"+item2+c+"'.");
			ReikaChatHelper.writeString(c+"-DragonAPI");
		}
	}

	@SubscribeEvent
	public void verifyCraftingRecipe(AddRecipeEvent evt) {
		if (!evt.isVanillaPass) {
			try {
				if (!ReikaRecipeHelper.verifyRecipe(evt.recipe)) {
					logger.log("Invalid recipe, such as with nulled inputs, found. Removing to prevent crashes.");
					evt.setCanceled(true);
				}
			}
			catch (Exception e) {
				logger.logError("Could not parse crafting recipe");
				e.printStackTrace();
			}
		}
	}

	@SubscribeEvent
	public void verifySmeltingRecipe(AddSmeltingEvent evt) {
		if (!evt.isVanillaPass) {
			try {
				ItemStack in = evt.getInput();
				ItemStack out = evt.getOutput();
				if (in == null || in.getItem() == null) {
					logger.logError("Found a null-input (or null-item input) smelting recipe! "+null+" > "+out+"! This is invalid!");
					Thread.dumpStack();
					evt.setCanceled(true);
				}
				else if (out == null || out.getItem() == null) {
					logger.logError("Found a null-output (or null-item output) smelting recipe! "+in+" > "+null+"! This is invalid!");
					Thread.dumpStack();
					evt.setCanceled(true);
				}
				else if (!ReikaItemHelper.verifyItemStack(in)) {
					logger.logError("Found a smelting recipe with an invalid input!");
					Thread.dumpStack();
					evt.setCanceled(true);
				}
				else if (!ReikaItemHelper.verifyItemStack(out)) {
					logger.logError("Found a smelting recipe with an invalid output!");
					Thread.dumpStack();
					evt.setCanceled(true);
				}
			}
			catch (Exception e) {
				logger.logError("Could not parse smelting recipe");
				e.printStackTrace();
			}
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
