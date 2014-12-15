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
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.oredict.OreDictionary;
import paulscode.sound.SoundSystemConfig;
import Reika.DragonAPI.Auxiliary.ChunkManager;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker;
import Reika.DragonAPI.Auxiliary.Trackers.BiomeCollisionTracker;
import Reika.DragonAPI.Auxiliary.Trackers.CommandableUpdateChecker;
import Reika.DragonAPI.Auxiliary.Trackers.CommandableUpdateChecker.CheckerDisableCommand;
import Reika.DragonAPI.Auxiliary.Trackers.CompatibilityTracker;
import Reika.DragonAPI.Auxiliary.Trackers.IntegrityChecker;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher.KeyTicker;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerHandler;
import Reika.DragonAPI.Auxiliary.Trackers.PotionCollisionTracker;
import Reika.DragonAPI.Auxiliary.Trackers.SuggestedModsTracker;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry;
import Reika.DragonAPI.Auxiliary.Trackers.VanillaIntegrityTracker;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Command.BlockReplaceCommand;
import Reika.DragonAPI.Command.DonatorCommand;
import Reika.DragonAPI.Command.EditNearbyInventoryCommand;
import Reika.DragonAPI.Command.GuideCommand;
import Reika.DragonAPI.Command.IDDumpCommand;
import Reika.DragonAPI.Command.LogControlCommand;
import Reika.DragonAPI.Command.SelectiveKillCommand;
import Reika.DragonAPI.Command.TestControlCommand;
import Reika.DragonAPI.Command.TileSyncCommand;
import Reika.DragonAPI.Extras.LoginHandler;
import Reika.DragonAPI.Instantiable.SyncPacket;
import Reika.DragonAPI.Instantiable.Event.GameFinishedLoadingEvent;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModInteract.AppEngHandler;
import Reika.DragonAPI.ModInteract.BCMachineHandler;
import Reika.DragonAPI.ModInteract.BCPipeHandler;
import Reika.DragonAPI.ModInteract.BannedItemReader;
import Reika.DragonAPI.ModInteract.BerryBushHandler;
import Reika.DragonAPI.ModInteract.DartItemHandler;
import Reika.DragonAPI.ModInteract.DartOreHandler;
import Reika.DragonAPI.ModInteract.ExtraUtilsHandler;
import Reika.DragonAPI.ModInteract.FactorizationHandler;
import Reika.DragonAPI.ModInteract.ForestryHandler;
import Reika.DragonAPI.ModInteract.ForestryRecipeHelper;
import Reika.DragonAPI.ModInteract.GalacticCraftHandler;
import Reika.DragonAPI.ModInteract.HarvestCraftHandler;
import Reika.DragonAPI.ModInteract.IC2Handler;
import Reika.DragonAPI.ModInteract.MFRHandler;
import Reika.DragonAPI.ModInteract.MagicCropHandler;
import Reika.DragonAPI.ModInteract.MagicaOreHandler;
import Reika.DragonAPI.ModInteract.MekToolHandler;
import Reika.DragonAPI.ModInteract.MekanismHandler;
import Reika.DragonAPI.ModInteract.MimicryHandler;
import Reika.DragonAPI.ModInteract.MystCraftHandler;
import Reika.DragonAPI.ModInteract.NEIIntercept;
import Reika.DragonAPI.ModInteract.OpenBlockHandler;
import Reika.DragonAPI.ModInteract.OreBerryBushHandler;
import Reika.DragonAPI.ModInteract.QuantumOreHandler;
import Reika.DragonAPI.ModInteract.RailcraftHandler;
import Reika.DragonAPI.ModInteract.RedstoneArsenalHandler;
import Reika.DragonAPI.ModInteract.ThaumBlockHandler;
import Reika.DragonAPI.ModInteract.ThaumOreHandler;
import Reika.DragonAPI.ModInteract.ThermalHandler;
import Reika.DragonAPI.ModInteract.TinkerBlockHandler;
import Reika.DragonAPI.ModInteract.TinkerToolHandler;
import Reika.DragonAPI.ModInteract.TransitionalOreHandler;
import Reika.DragonAPI.ModInteract.TwilightForestHandler;
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
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
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

	private ModLogger logger;

	@Override
	@EventHandler
	public void preload(FMLPreInitializationEvent evt) {
		this.verifyVersions();
		config.loadSubfolderedConfigFile(evt);
		config.initProps(evt);
		logger = new ModLogger(instance, false);
		logger.log("Active Classloader is: "+this.getClass().getClassLoader());
		logger.log("Initializing libraries with max recursion depth of "+ReikaJavaLibrary.getMaximumRecursiveDepth());

		proxy.registerSidedHandlers();

		this.registerTechnicalBlocks();

		ChunkManager.instance.register();

		OreDictionary.initVanillaEntries();
		ReikaJavaLibrary.initClass(ModList.class);

		this.increasePotionCount();
		//this.increaseBiomeCount(); world save stores biome as bytes, so 255 is cap

		BannedItemReader.instance.initWith("BanItem");
		BannedItemReader.instance.initWith("ItemBan");
		BannedItemReader.instance.initWith("TekkitCustomizerData");
		BannedItemReader.instance.initWith("TekkitCustomizer");

		this.basicSetup(evt);

		ReikaPacketHelper.registerPacketHandler(instance, packetChannel, new APIPacketHandler());

		int id = DragonOptions.SYNCPACKET.getValue();
		ReikaPacketHelper.registerVanillaPacketType(this, id, SyncPacket.class, Side.SERVER, EnumConnectionState.PLAY);
		//ReikaPacketWrapper.instance.registerPacket(SyncPacket.class);
	}

	/** Registers all the vanilla technical blocks (except air and block 36) to have items so as to avoid crashes when rendering them
	 * in the inventory. */
	private void registerTechnicalBlocks() {
		Block[] blocks = {
				Blocks.brewing_stand, Blocks.bed, Blocks.nether_wart, Blocks.cauldron, Blocks.flower_pot, Blocks.wheat, Blocks.reeds,
				Blocks.cake, Blocks.skull, Blocks.piston_head, Blocks.lit_redstone_ore, Blocks.powered_repeater, Blocks.pumpkin_stem,
				Blocks.standing_sign, Blocks.powered_comparator, Blocks.tripwire, Blocks.lit_redstone_lamp, Blocks.melon_stem,
				Blocks.unlit_redstone_torch, Blocks.unpowered_comparator, Blocks.redstone_wire, Blocks.wall_sign,
				Blocks.unpowered_repeater, Blocks.iron_door, Blocks.wooden_door
		};

		for (Block b : blocks) {
			ItemBlock ib = new ItemBlock(b);
			String s = Block.blockRegistry.getNameForObject(b)+"_technical";
			Item.itemRegistry.addObject(Block.getIdFromBlock(b), s, ib);
		}
	}

	/** Do not call unless biomes are no longer saved as bytes *//*
	private void increaseBiomeCount() {
		int count = BiomeGenBase.biomeList.length;
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
		proxy.registerSidedHandlersMain();

		PlayerHandler.instance.registerTracker(LoginHandler.instance);

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new APIGuiHandler());

		//ReikaPacketHelper.initPipelines();

		TickRegistry.instance.registerTickHandler(ProgressiveRecursiveBreaker.instance, Side.SERVER);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			TickRegistry.instance.registerTickHandler(KeyTicker.instance, Side.CLIENT);

		FMLInterModComms.sendMessage("Waila", "register", "Reika.DragonAPI.ModInteract.LegacyWailaHelper.registerObjects");

		if (DragonOptions.UNNERFOBSIDIAN.getState())
			Blocks.obsidian.setResistance(2000);

		if (ModList.COMPUTERCRAFT.isLoaded()) {
			try {
				Class handler = Class.forName("Reika.DragonAPI.ModInteract.PeripheralHandler");
				Object handlerObj = handler.newInstance();
				Class api = Class.forName("dan200.computercraft.api.ComputerCraftAPI");
				Method register = api.getDeclaredMethod("registerPeripheralProvider", handler);
				register.invoke(null, handlerObj);
				//ComputerCraftAPI.registerPeripheralProvider(new PeripheralHandler()); Nonreflective code crashes
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	@EventHandler
	public void postload(FMLPostInitializationEvent evt) {
		ReikaRegistryHelper.loadNames();

		this.loadHandlers();

		this.alCompat();

		//ReikaPacketHelper.postInitPipelines();

		BiomeCollisionTracker.instance.check();
		PotionCollisionTracker.instance.check();
		VanillaIntegrityTracker.instance.check();

		CompatibilityTracker.instance.test();

		IntegrityChecker.instance.testIntegrity();

		ReikaOreHelper.refreshAll();
		for (int i = 0; i < ModOreList.oreList.length; i++) {
			ModOreList ore = ModOreList.oreList[i];
			ore.reloadOreList();
		}

		SuggestedModsTracker.instance.printConsole();
		CommandableUpdateChecker.instance.checkAll();

		ReikaEntityHelper.loadMappings();
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
		if (ModList.NEI.isLoaded())
			NEIIntercept.instance.register();
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
			if (!ReikaItemHelper.listContainsItemStack(OreDictionary.getOres("ingotAluminum"), al.get(i)))
				OreDictionary.registerOre("ingotAluminum", al.get(i));
			if (!ReikaItemHelper.listContainsItemStack(OreDictionary.getOres("ingotAluminium"), al.get(i)))
				OreDictionary.registerOre("ingotAluminium", al.get(i));
		}

		al = new ArrayList(OreDictionary.getOres("ingotAluminum"));
		for (int i = 0; i < al.size(); i++) {
			if (!ReikaItemHelper.listContainsItemStack(OreDictionary.getOres("ingotNaturalAluminum"), al.get(i)))
				OreDictionary.registerOre("ingotNaturalAluminum", al.get(i));
			if (!ReikaItemHelper.listContainsItemStack(OreDictionary.getOres("ingotAluminium"), al.get(i)))
				OreDictionary.registerOre("ingotAluminium", al.get(i));
		}

		al = new ArrayList(OreDictionary.getOres("ingotAluminium"));
		for (int i = 0; i < al.size(); i++) {
			if (!ReikaItemHelper.listContainsItemStack(OreDictionary.getOres("ingotNaturalAluminum"), al.get(i)))
				OreDictionary.registerOre("ingotNaturalAluminum", al.get(i));
			if (!ReikaItemHelper.listContainsItemStack(OreDictionary.getOres("ingotAluminum"), al.get(i)))
				OreDictionary.registerOre("ingotAluminum", al.get(i));
		}


		al = new ArrayList(OreDictionary.getOres("oreAluminium"));
		for (int i = 0; i < al.size(); i++) {
			if (!ReikaItemHelper.listContainsItemStack(OreDictionary.getOres("oreAluminum"), al.get(i)))
				OreDictionary.registerOre("oreAluminum", al.get(i));
		}
	}

	private void loadHandlers() {
		this.initHandler(ModList.BCFACTORY, BCMachineHandler.class);
		this.initHandler(ModList.BCTRANSPORT, BCPipeHandler.class);
		this.initHandler(ModList.THAUMCRAFT, ThaumOreHandler.class);
		this.initHandler(ModList.THAUMCRAFT, ThaumBlockHandler.class);
		this.initHandler(ModList.DARTCRAFT, DartOreHandler.class);
		this.initHandler(ModList.DARTCRAFT, DartItemHandler.class);
		this.initHandler(ModList.TINKERER, TinkerToolHandler.class);
		this.initHandler(ModList.TINKERER, TinkerBlockHandler.class);
		this.initHandler(ModList.TWILIGHT, TwilightForestHandler.class);
		this.initHandler(ModList.MEKANISM, MekanismHandler.class);
		this.initHandler(ModList.MEKTOOLS, MekToolHandler.class);
		this.initHandler(ModList.TRANSITIONAL, TransitionalOreHandler.class);
		this.initHandler(ModList.IC2, IC2Handler.class);
		this.initHandler(ModList.ARSMAGICA, MagicaOreHandler.class);
		this.initHandler(ModList.APPENG, AppEngHandler.class);
		this.initHandler(ModList.FORESTRY, ForestryHandler.class);
		this.initHandler(ModList.FORESTRY, ForestryRecipeHelper.class);
		this.initHandler(ModList.THERMALFOUNDATION, ThermalHandler.class);
		this.initHandler(ModList.MIMICRY, MimicryHandler.class);
		this.initHandler(ModList.MAGICCROPS, MagicCropHandler.class);
		this.initHandler(ModList.QCRAFT, QuantumOreHandler.class);;
		this.initHandler(ModList.TINKERER, OreBerryBushHandler.class);
		this.initHandler(ModList.NATURA, BerryBushHandler.class);
		this.initHandler(ModList.OPENBLOCKS, OpenBlockHandler.class);
		this.initHandler(ModList.FACTORIZATION, FactorizationHandler.class);
		this.initHandler(ModList.HARVESTCRAFT, HarvestCraftHandler.class);
		this.initHandler(ModList.ARSENAL, RedstoneArsenalHandler.class);
		this.initHandler(ModList.RAILCRAFT, RailcraftHandler.class);
		this.initHandler(ModList.MINEFACTORY, MFRHandler.class);
		this.initHandler(ModList.GALACTICRAFT, GalacticCraftHandler.class);
		this.initHandler(ModList.EXTRAUTILS, ExtraUtilsHandler.class);
		this.initHandler(ModList.MYSTCRAFT, MystCraftHandler.class);

		ReikaJavaLibrary.initClass(ModOreList.class);
		ReikaJavaLibrary.initClass(ModWoodList.class);
		ReikaJavaLibrary.initClass(ModCropList.class);
		ReikaJavaLibrary.initClass(PowerTypes.class);
		ReikaJavaLibrary.initClass(InterfaceCache.class);
	}

	@EventHandler
	public void lastLoad(FMLServerAboutToStartEvent evt) {
		ModOreList.initializeAll();
	}

	private void initHandler(ModList mod, Class c) {
		if (mod.isLoaded()) {
			try {
				ReikaJavaLibrary.initClass(c);
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

}
