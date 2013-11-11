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

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Auxiliary.BiomeCollisionTracker;
import Reika.DragonAPI.Auxiliary.LoginHandler;
import Reika.DragonAPI.Auxiliary.RetroGenController;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.ModLogger;
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
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

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
		OreDictionary.initVanillaEntries();
		ReikaJavaLibrary.initClass(ModList.class);

		ReikaRegistryHelper.setupModData(instance, evt);
		ReikaRegistryHelper.setupVersionChecking(evt);
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		GameRegistry.registerPlayerTracker(LoginHandler.instance);
	}

	@Override
	@EventHandler
	public void postload(FMLPostInitializationEvent evt) {
		this.loadHandlers();
		this.alCompat();
		BiomeCollisionTracker.instance.check();
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

		this.initHandler(ModList.BUILDCRAFTFACTORY, BCMachineHandler.class);
		this.initHandler(ModList.BUILDCRAFTTRANSPORT, BCPipeHandler.class);
		this.initHandler(ModList.THAUMCRAFT, ThaumOreHandler.class);
		this.initHandler(ModList.THAUMCRAFT, ThaumBlockHandler.class);
		this.initHandler(ModList.DARTCRAFT, DartOreHandler.class);
		this.initHandler(ModList.DARTCRAFT, DartItemHandler.class);
		this.initHandler(ModList.TINKERER, TinkerToolHandler.class);
		this.initHandler(ModList.TWILIGHT, TwilightForestHandler.class);
		this.initHandler(ModList.MEKANISM, MekanismHandler.class);
		this.initHandler(ModList.MEKTOOLS, MekToolHandler.class);
		this.initHandler(ModList.TRANSITIONAL, TransitionalOreHandler.class);
		this.initHandler(ModList.INDUSTRIALCRAFT, IC2Handler.class);
		this.initHandler(ModList.ARSMAGICA, MagicaOreHandler.class);
		this.initHandler(ModList.APPLIEDENERGISTICS, AppEngHandler.class);
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
