/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.auxiliary.trackers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.base.DragonAPIMod;
import reika.dragonapi.exception.IDConflictException;
import reika.dragonapi.exception.StupidIDException;
import reika.dragonapi.extras.IDType;
import reika.dragonapi.instantiable.data.maps.MultiMap;

public final class EnchantmentCollisionTracker {

	private MultiMap<DragonAPIMod, Integer> IDs = new MultiMap();
	private ArrayList<DragonAPIMod> mods = new ArrayList();
	private HashMap<Integer, Class> classes = new HashMap();

	public static final EnchantmentCollisionTracker instance = new EnchantmentCollisionTracker();

	private EnchantmentCollisionTracker() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void addEntry(DragonAPIMod mod, int id, Class Enchantment) {
		IDs.addValue(mod, id);
		classes.put(id, Enchantment);
	}

	public void addEnchantmentID(DragonAPIMod mod, int id, Class EnchantmentClass) {
		if (id < 0 || id >= Enchantment.enchantmentsList.length) {
			throw new StupidIDException(mod, id, IDType.ENCHANTMENT);
		}
		Enchantment e = Enchantment.enchantmentsList[id];
		if (e != null)
			this.onConflict(null, id, e.getClass(), EnchantmentClass);
		if (classes.containsKey(id))
			this.onConflict(mod, id, classes.get(id), EnchantmentClass);
		else
			this.addEntry(mod, id, EnchantmentClass);
	}

	public final void check() {
		for (DragonAPIMod mod : IDs.keySet()) {
			Collection<Integer> ids = IDs.get(mod);
			for (int id : ids) {
				Enchantment e = Enchantment.enchantmentsList[id];
				if (e == null) {
					//this.onConflict(mod, id);
					DragonAPICore.logError("Enchantment ID "+id+" ("+classes.get(id)+") was deleted post-registration!");
				}
				else {
					Class c = e.getClass();
					Class c1 = classes.get(id);
					if (c1 != c)
						this.onConflict(mod, id, c, c1);
				}
			}
		}
	}

	protected void onConflict(DragonAPIMod mod, int id, Class c, Class c1) {
		String s = "Enchantment IDs: "+Enchantment.enchantmentsList[id]+" @ "+id+" ("+c.getSimpleName()+" & "+c1.getSimpleName()+")";
		if (mod == null)
			throw new IDConflictException(s);
		throw new IDConflictException(mod, s);
	}

}
