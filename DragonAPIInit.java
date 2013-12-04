/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI;

import java.net.URL;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Auxiliary.BiomeCollisionTracker;
import Reika.DragonAPI.Auxiliary.ChatWatcher;
import Reika.DragonAPI.Auxiliary.CompatibilityTracker;
import Reika.DragonAPI.Auxiliary.CustomSoundHandler;
import Reika.DragonAPI.Auxiliary.ItemOverwriteTracker;
import Reika.DragonAPI.Auxiliary.LoginHandler;
import Reika.DragonAPI.Auxiliary.PlayerModelRenderer;
import Reika.DragonAPI.Auxiliary.RetroGenController;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Extras.GuideCommand;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.AppEngHandler;
import Reika.DragonAPI.ModInteract.BCMachineHandler;
import Reika.DragonAPI.ModInteract.BCPipeHandler;
import Reika.DragonAPI.ModInteract.DartItemHandler;
import Reika.DragonAPI.ModInteract.DartOreHandler;
import Reika.DragonAPI.ModInteract.ForestryHandler;
import Reika.DragonAPI.ModInteract.IC2Handler;
import Reika.DragonAPI.ModInteract.MagicaOreHandler;
import Reika.DragonAPI.ModInteract.MekToolHandler;
import Reika.DragonAPI.ModInteract.MekanismHandler;
import Reika.DragonAPI.ModInteract.ThaumBlockHandler;
import Reika.DragonAPI.ModInteract.ThaumOreHandler;
import Reika.DragonAPI.ModInteract.ThermalHandler;
import Reika.DragonAPI.ModInteract.TinkerToolHandler;
import Reika.DragonAPI.ModInteract.TransitionalOreHandler;
import Reika.DragonAPI.ModInteract.TwilightForestHandler;
import Reika.DragonAPI.ModRegistry.ModCropList;
import Reika.DragonAPI.ModRegistry.ModOreList;
import Reika.DragonAPI.ModRegistry.ModWoodList;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod( modid = "DragonAPI", name="DragonAPI", version="release", certificateFingerprint = "@GET_FINGERPRINT@")
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class DragonAPIInit extends DragonAPIMod {

	@SidedProxy(clientSide="Reika.DragonAPI.APIProxyClient", serverSide="Reika.DragonAPI.APIProxy")
	public static APIProxy proxy;

	@Instance("DragonAPI")
	public static DragonAPIInit instance = new DragonAPIInit();

	private ModLogger logger;

	@Override
	@EventHandler
	public void preload(FMLPreInitializationEvent evt) {
		logger = new ModLogger(instance, true, false, false);
		MinecraftForge.EVENT_BUS.register(RetroGenController.getInstance());
		MinecraftForge.EVENT_BUS.register(this);
		OreDictionary.initVanillaEntries();
		ReikaJavaLibrary.initClass(ModList.class);

		ReikaRegistryHelper.setupModData(instance, evt);
		ReikaRegistryHelper.setupVersionChecking(evt);

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			MinecraftForge.EVENT_BUS.register(PlayerModelRenderer.instance);
			MinecraftForge.EVENT_BUS.register(CustomSoundHandler.instance);
		}
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		GameRegistry.registerPlayerTracker(LoginHandler.instance);

		NetworkRegistry.instance().registerGuiHandler(instance, new APIGuiHandler());

		NetworkRegistry.instance().registerChatListener(ChatWatcher.instance);
	}

	@Override
	@EventHandler
	public void postload(FMLPostInitializationEvent evt) {
		this.loadHandlers();

		this.alCompat();

		BiomeCollisionTracker.instance.check();
		ItemOverwriteTracker.instance.check();

		if (DragonAPICore.isOnActualServer())
			;//this.licenseTest();
		CompatibilityTracker.instance.test();
	}

	@EventHandler
	public void registerCommands(FMLServerStartingEvent evt) {
		evt.registerServerCommand(new GuideCommand());
	}

	@ForgeSubscribe
	public void addGuideGUI(PlayerInteractEvent evt) {
		EntityPlayer ep = evt.entityPlayer;
		ItemStack is = ep.getCurrentEquippedItem();
		if (is != null && is.itemID == Item.enchantedBook.itemID) {
			if (is.stackTagCompound != null) {
				NBTTagCompound disp = is.stackTagCompound.getCompoundTag("display");
				if (disp != null) {
					NBTTagList list = disp.getTagList("Lore");
					if (list != null && list.tagCount() > 0) {
						String sg = ((NBTTagString)list.tagAt(0)).data;
						if (sg != null && sg.equals("Reika's Mods Guide")) {
							ep.openGui(instance, 0, ep.worldObj, 0, 0, 0);
							evt.setResult(Result.ALLOW);
						}
					}
				}
			}
		}
	}

	private void licenseTest() {
		MinecraftServer server = MinecraftServer.getServer();
		String name = server.getServerHostname();
		ReikaJavaLibrary.spamConsole(name);
	}

	private void alCompat() { //Why the hell are there three standards for aluminum?
		logger.log("Repairing compatibility between Alumin(i)um OreDictionary Names.");
		List<ItemStack> al = OreDictionary.getOres("ingotNaturalAluminum");
		for (int i = 0; i < al.size(); i++) {
			if (!ReikaItemHelper.listContainsItemStack(OreDictionary.getOres("ingotAluminum"), al.get(i)))
				OreDictionary.registerOre("ingotAluminum", al.get(i));
			if (!ReikaItemHelper.listContainsItemStack(OreDictionary.getOres("ingotAluminium"), al.get(i)))
				OreDictionary.registerOre("ingotAluminium", al.get(i));
		}

		al = OreDictionary.getOres("ingotAluminum");
		for (int i = 0; i < al.size(); i++) {
			if (!ReikaItemHelper.listContainsItemStack(OreDictionary.getOres("ingotNaturalAluminum"), al.get(i)))
				OreDictionary.registerOre("ingotNaturalAluminum", al.get(i));
			if (!ReikaItemHelper.listContainsItemStack(OreDictionary.getOres("ingotAluminium"), al.get(i)))
				OreDictionary.registerOre("ingotAluminium", al.get(i));
		}

		al = OreDictionary.getOres("ingotAluminium");
		for (int i = 0; i < al.size(); i++) {
			if (!ReikaItemHelper.listContainsItemStack(OreDictionary.getOres("ingotNaturalAluminum"), al.get(i)))
				OreDictionary.registerOre("ingotNaturalAluminum", al.get(i));
			if (!ReikaItemHelper.listContainsItemStack(OreDictionary.getOres("ingotAluminum"), al.get(i)))
				OreDictionary.registerOre("ingotAluminum", al.get(i));
		}
	}

	private void loadHandlers() {
		ReikaJavaLibrary.initClass(ModOreList.class);
		ReikaJavaLibrary.initClass(ModWoodList.class);
		ReikaJavaLibrary.initClass(ModCropList.class);

		this.initHandler(ModList.BCFACTORY, BCMachineHandler.class);
		this.initHandler(ModList.BCTRANSPORT, BCPipeHandler.class);
		this.initHandler(ModList.THAUMCRAFT, ThaumOreHandler.class);
		this.initHandler(ModList.THAUMCRAFT, ThaumBlockHandler.class);
		this.initHandler(ModList.DARTCRAFT, DartOreHandler.class);
		this.initHandler(ModList.DARTCRAFT, DartItemHandler.class);
		this.initHandler(ModList.TINKERER, TinkerToolHandler.class);
		this.initHandler(ModList.TWILIGHT, TwilightForestHandler.class);
		this.initHandler(ModList.MEKANISM, MekanismHandler.class);
		this.initHandler(ModList.MEKTOOLS, MekToolHandler.class);
		this.initHandler(ModList.TRANSITIONAL, TransitionalOreHandler.class);
		this.initHandler(ModList.IC2, IC2Handler.class);
		this.initHandler(ModList.ARSMAGICA, MagicaOreHandler.class);
		this.initHandler(ModList.APPENG, AppEngHandler.class);
		this.initHandler(ModList.FORESTRY, ForestryHandler.class);
		this.initHandler(ModList.THERMALEXPANSION, ThermalHandler.class);
	}

	private void initHandler(ModList mod, Class c) {
		if (!mod.isLoaded())
			return;
		try {
			ReikaJavaLibrary.initClass(c);
		}
		catch (Exception e) {
			logger.logError("Could not load handler for "+mod.name());
			e.printStackTrace();
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
		return DragonAPICore.getReikaForumPage(instance);
	}

	@Override
	public boolean hasWiki() {
		return false;
	}

	@Override
	public URL getWiki() {
		return null;
	}

	@Override
	public boolean hasVersion() {
		return false;
	}

	@Override
	public String getVersionName() {
		return "";
	}

	@Override
	public ModLogger getModLogger() {
		return logger;
	}

}
