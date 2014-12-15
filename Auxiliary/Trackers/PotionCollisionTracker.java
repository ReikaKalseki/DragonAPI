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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.minecraft.potion.Potion;
import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.IDConflictException;
import Reika.DragonAPI.Exception.StupidIDException;
import Reika.DragonAPI.Extras.IDType;
import Reika.DragonAPI.Instantiable.Data.MultiMap;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class PotionCollisionTracker {

	private MultiMap<DragonAPIMod, Integer> IDs = new MultiMap();
	private ArrayList<DragonAPIMod> mods = new ArrayList();
	private HashMap<Integer, Class> classes = new HashMap();

	public static final PotionCollisionTracker instance = new PotionCollisionTracker();

	private PotionCollisionTracker() {
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

	private void addEntry(DragonAPIMod mod, int id, Class potion) {
		IDs.addValue(mod, id);
		classes.put(id, potion);
	}

	public void addPotionID(DragonAPIMod mod, int id, Class potionClass) {
		if (id < 0 || id >= Potion.potionTypes.length) {
			throw new StupidIDException(mod, id, IDType.POTION);
		}
		Potion potion = Potion.potionTypes[id];
		if (potion != null)
			this.onConflict(null, id);
		if (this.isIDRegisteredToTracker(id))
			this.onConflict(mod, id);
		else
			this.addEntry(mod, id, potionClass);
	}

	public final void check() {
		for (DragonAPIMod mod : IDs.keySet()) {
			Collection<Integer> ids = IDs.get(mod);
			for (int id : ids) {
				Potion potion = Potion.potionTypes[id];
				if (potion == null) {
					//this.onConflict(mod, id);
					ReikaJavaLibrary.pConsole("DRAGONAPI: Potion ID "+id+" ("+classes.get(id)+") was deleted post-registration!");
				}
				else {
					Class c = potion.getClass();
					Class c1 = classes.get(id);
					if (c1 != c)
						this.onConflict(mod, id);
				}
			}
		}
	}

	protected void onConflict(DragonAPIMod mod, int id) {
		String s = "Potion IDs: "+Potion.potionTypes[id]+" @ "+id;
		if (mod == null)
			throw new IDConflictException(s);
		throw new IDConflictException(mod, s);
	}

}
