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
import Reika.DragonAPI.Auxiliary.ModList;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.ModLogger;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.BCMachineHandler;
import Reika.DragonAPI.ModInteract.DartItemHandler;
import Reika.DragonAPI.ModInteract.DartOreHandler;
import Reika.DragonAPI.ModInteract.MekToolHandler;
import Reika.DragonAPI.ModInteract.MekanismHandler;
import Reika.DragonAPI.ModInteract.ThaumBlockHandler;
import Reika.DragonAPI.ModInteract.ThaumOreHandler;
import Reika.DragonAPI.ModInteract.TinkerToolHandler;
import Reika.DragonAPI.ModInteract.TwilightForestHandler;
import Reika.DragonAPI.ModRegistry.ModCropList;
import Reika.DragonAPI.ModRegistry.ModOreList;
import Reika.DragonAPI.ModRegistry.ModSpiderList;
import Reika.DragonAPI.ModRegistry.ModWoodList;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod( modid = "DragonAPI", name="DragonAPI", version="release", certificateFingerprint = "@GET_FINGERPRINT@")
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class DragonAPIInit extends DragonAPIMod {

	@SidedProxy(clientSide="Reika.DragonAPI.APIProxyClient", serverSide="Reika.DragonAPI.APIProxy")
	public static APIProxy proxy;

	//@Instance
	public static DragonAPIInit instance = new DragonAPIInit();

	private ModLogger logger = new ModLogger(instance, true, false, false);

	@Override
	@PreInit
	public void preload(FMLPreInitializationEvent evt) {
		MinecraftForge.EVENT_BUS.register(RetroGenController.getInstance());
		OreDictionary.initVanillaEntries();
	}

	@Override
	@Init
	public void load(FMLInitializationEvent event) {

	}

	@Override
	@PostInit
	public void postload(FMLPostInitializationEvent evt) {
		this.loadHandlers();
		this.alCompat();
	}

	private static void alCompat() { //Why the hell are there three standards for aluminum?
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

	private static void loadHandlers() {
		ReikaJavaLibrary.initClass(ModList.class);
		ReikaJavaLibrary.initClass(ModOreList.class);
		ReikaJavaLibrary.initClass(ModWoodList.class);
		ReikaJavaLibrary.initClass(ModCropList.class);
		ReikaJavaLibrary.initClass(ModSpiderList.class);

		if (ModList.BUILDCRAFTFACTORY.isLoaded()) {
			ReikaJavaLibrary.initClass(BCMachineHandler.class);
		}
		if (ModList.THAUMCRAFT.isLoaded()) {
			ReikaJavaLibrary.initClass(ThaumOreHandler.class);
			ReikaJavaLibrary.initClass(ThaumBlockHandler.class);
		}
		if (ModList.DARTCRAFT.isLoaded()) {
			ReikaJavaLibrary.initClass(DartOreHandler.class);
			ReikaJavaLibrary.initClass(DartItemHandler.class);
		}
		if (ModList.TINKERER.isLoaded()) {
			ReikaJavaLibrary.initClass(TinkerToolHandler.class);
		}
		if (ModList.TWILIGHT.isLoaded()) {
			ReikaJavaLibrary.initClass(TwilightForestHandler.class);
		}
		if (ModList.MEKANISM.isLoaded()) {
			ReikaJavaLibrary.initClass(MekanismHandler.class);
		}
		if (ModList.MEKTOOLS.isLoaded()) {
			ReikaJavaLibrary.initClass(MekToolHandler.class);
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
		return null;
	}

}
