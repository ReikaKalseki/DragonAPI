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

import java.util.HashMap;
import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Libraries.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.ReikaReflectionHelper;
import Reika.DragonAPI.Resources.TabDragonAPI;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class DragonAPICore {

	private static boolean resourcesLoaded = false;

	protected DragonAPICore() {throw new MisuseException("The class "+this.getClass()+" cannot be instantiated!");}

	protected static final Random rand = new Random();

	public static CreativeTabs tab = new TabDragonAPI(CreativeTabs.getNextID(),"DragonAPI");

	private static final HashMap<String,Item> items = new HashMap<String, Item>();
	private static final HashMap<String,Integer> IDs = new HashMap<String, Integer>();
	private static final HashMap<String,IItemRenderer> renders = new HashMap<String, IItemRenderer>();

	public static final boolean hasAllClasses() {
		return true;
	}

	//TODO Add handler for custom death messages

	public static void addIDMapping(String name, int id) {
		IDs.put(name, id);
	}
	/*
	public static void loadResources() {
		if (resourcesLoaded) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Resources already loaded!");
			return;
		}
		ReikaJavaLibrary.pConsole("DRAGONAPI: Loading Resources");
		resourcesLoaded = true;
		addItem("Spawner", "Monster Spawner", new ItemSpawner(IDs.get("Spawner")).setUnlocalizedName("spawner"));
	}*/

	private static void addItem(String name, String unloc, Item i) {
		if (!items.containsKey(unloc)) {
			items.put(unloc, i);
			LanguageRegistry.addName(i, name);
		}
	}

	public static void addItem(DragonAPIMod mod, Class<? extends Item> cl, String name, String unloc) {
		Item i = ReikaReflectionHelper.createBasicItemInstance(cl, IDs.get(unloc), unloc);
		addItem(name, unloc, i);
		ReikaJavaLibrary.pConsole("DRAGONAPI: "+mod.getTechnicalName()+"is adding item "+name+" with system name "+unloc+" to id "+i.itemID);
	}

	public static Item getItem(String name) {
		return items.get(name);
	}

	public static void addRenderer(String unloc, IItemRenderer render) {
		if (renders.containsKey(unloc))
			throw new MisuseException(unloc+" already has a specified render! "+renders.get(unloc));
		MinecraftForgeClient.registerItemRenderer(getItem(unloc).itemID, render);
		ReikaJavaLibrary.pConsole("DRAGONAPI: Adding item render "+render+" for system name "+unloc);
	}
}
