/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary.Trackers;

import java.util.Collection;
import java.util.HashMap;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.IDConflictException;
import Reika.DragonAPI.Exception.StupidIDException;
import Reika.DragonAPI.Instantiable.Data.MultiMap;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class BiomeCollisionTracker {

	private MultiMap<DragonAPIMod, Integer> IDs = new MultiMap();
	private HashMap<Integer, Class> classes = new HashMap();

	public static final BiomeCollisionTracker instance = new BiomeCollisionTracker();

	private BiomeCollisionTracker() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public boolean isIDRegisteredToTracker(int id) {
		for (DragonAPIMod mod : IDs.keySet()) {
			Collection<Integer> ids = IDs.get(mod);
			if (ids.contains(id))
				return true;
		}
		return false;
	}

	private void addEntry(DragonAPIMod mod, int id, Class biome) {
		IDs.addValue(mod, id);
		classes.put(id, biome);
	}

	public void addBiomeID(DragonAPIMod mod, int id, Class biomeClass) {
		if (id < 0 || id >= 255) {
			throw new StupidIDException(mod, id);
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
		for (DragonAPIMod mod : IDs.keySet()) {
			Collection<Integer> ids = IDs.get(mod);
			for (int id : ids) {
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
