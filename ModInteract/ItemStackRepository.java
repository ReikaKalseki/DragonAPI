/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import net.minecraft.item.ItemStack;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.DragonAPIMod;
import cpw.mods.fml.common.ModContainer;

public class ItemStackRepository {

	public static final ItemStackRepository instance = new ItemStackRepository();

	private final HashMap<String, Repository> data = new HashMap();

	private ItemStackRepository() {

	}

	public void registerClass(ModList mod, Class c) {
		this.registerClass(mod.modLabel, c);
	}

	public void registerClass(DragonAPIMod mod, Class c) {
		this.registerClass(mod.getModContainer(), c);
	}

	public void registerClass(ModContainer mc, Class c) {
		this.registerClass(mc.getModId(), c);
	}

	public void registerClass(String modid, Class c) {
		data.put(modid, new Repository(c));
	}

	public ItemStack getItem(ModList mod, String name) {
		return this.getItem(mod.modLabel, name);
	}

	public ItemStack getItem(DragonAPIMod mod, String name) {
		return this.getItem(mod.getModContainer(), name);
	}

	public ItemStack getItem(ModContainer mc, String name) {
		return this.getItem(mc.getModId(), name);
	}

	public ItemStack getItem(String modid, String name) {
		Repository r = data.get(modid);
		return r != null ? r.get(name) : null;
	}

	private static class Repository {

		private final HashMap<String, Field> fields = new HashMap();

		private Repository(Class c) {
			Field[] fds = c.getDeclaredFields();
			for (int i = 0; i < fds.length; i++) {
				Field f = fds[i];
				if (Modifier.isStatic(f.getModifiers()) && f.getType() == ItemStack.class) {
					String n = f.getName();
					f.setAccessible(true);
					fields.put(n, f);
				}
			}
		}

		private ItemStack get(String field) {
			try {
				return (ItemStack)fields.get(field).get(null);
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

	}

}
