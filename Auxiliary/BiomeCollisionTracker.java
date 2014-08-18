/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.IDConflictException;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;

public class BiomeCollisionTracker {

	private HashMap<DragonAPIMod, ArrayList<Integer>> IDs = new HashMap();
	private ArrayList<DragonAPIMod> mods = new ArrayList();
	private HashMap<Integer, Class> classes = new HashMap();

	public static final BiomeCollisionTracker instance = new BiomeCollisionTracker();

	private BiomeCollisionTracker() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public boolean isIDRegisteredToTracker(int id) {
		for (int i = 0; i < mods.size(); i++) {
			DragonAPIMod mod = mods.get(i);
			ArrayList<Integer> ids = IDs.get(mod);
			if (ids.contains(id))
				return true;
		}
		return false;
	}

	private void addEntry(DragonAPIMod mod, int id, Class biome) {
		ArrayList<Integer> ids = IDs.get(mod);
		if (ids == null) {
			ids = new ArrayList();
		}
		ids.add(id);
		IDs.put(mod, ids);
		if (!mods.contains(mod))
			mods.add(mod);
		classes.put(id, biome);
	}

	public void addBiomeID(DragonAPIMod mod, int id, Class biomeClass) {
		if (id < 0 || id == 255) {
			throw new IllegalArgumentException("Illegal Biome ID "+id);
		}
		BiomeGenBase biome = BiomeGenBase.biomeList[id];
		if (biome != null)
			this.onConflict(null, id);
		if (this.isIDRegisteredToTracker(id))
			this.onConflict(mod, id);
		else
			this.addEntry(mod, id, biomeClass);
	}

	public final void check() {
		for (int i = 0; i < mods.size(); i++) {
			DragonAPIMod mod = mods.get(i);
			ArrayList<Integer> ids = IDs.get(mod);
			for (int k = 0; k < ids.size(); k++) {
				int id = ids.get(k);
				BiomeGenBase biome = BiomeGenBase.biomeList[id];
				if (biome == null) {
					//this.onConflict(mod, id);
					ReikaJavaLibrary.pConsole("DRAGONAPI: Biome ID "+id+" ("+classes.get(id)+") was deleted post-registration!");
				}
				else {
					Class c = biome.getClass();
					Class c1 = classes.get(id);
					if (c1 != c)
						this.onConflict(mod, id);
				}
			}
		}
	}

	protected void onConflict(DragonAPIMod mod, int id) {
		String s = "Biome IDs: "+BiomeGenBase.biomeList[id]+" @ ID "+id;
		if (mod == null)
			throw new IDConflictException(s);
		throw new IDConflictException(mod, s);
	}

}
