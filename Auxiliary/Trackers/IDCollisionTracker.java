/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary.Trackers;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.potion.Potion;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.IDConflictException;
import Reika.DragonAPI.Exception.StupidIDException;
import Reika.DragonAPI.Extras.IDType;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;

public final class IDCollisionTracker {

	public static final IDCollisionTracker instance = new IDCollisionTracker();

	private final EnumMap<IDType, IDData> data = new EnumMap(IDType.class);

	private IDCollisionTracker() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	private IDData getOrCreateData(IDType type) {
		IDData entry = data.get(type);
		if (entry == null) {
			entry = new IDData(type);
			data.put(type, entry);
		}
		return entry;
	}

	private void addEntry(DragonAPIMod mod, IDType type, int id, Class c) {
		IDData entry = this.getOrCreateData(type);
		entry.IDs.addValue(mod, id);
		entry.classes.put(id, c);
	}

	public void addBiomeID(DragonAPIMod mod, int id, Class<? extends BiomeGenBase> biomeClass) {
		this.addID(mod, id, IDType.BIOME, biomeClass);
	}

	public void addEnchantmentID(DragonAPIMod mod, int id, Class<? extends Enchantment> enchClass) {
		this.addID(mod, id, IDType.ENCHANTMENT, enchClass);
	}

	public void addPotionID(DragonAPIMod mod, int id, Class<? extends Potion> potionClass) {
		this.addID(mod, id, IDType.POTION, potionClass);
	}

	private void addID(DragonAPIMod mod, int id, IDType type, Class c) {
		if (id < 0 || id > type.maxValue) {
			throw new StupidIDException(mod, id, type);
		}
		Object obj = type.getValue(id);
		IDData entry = this.getOrCreateData(type);
		if (obj != null)
			this.onConflict(null, type, id, obj.getClass(), c);
		else if (entry.classes.containsKey(id))
			this.onConflict(mod, type, id, entry.classes.get(id), c);
		else
			this.addEntry(mod, type, id, c);
	}

	public final void check() {
		HashSet<String> conflicts = new HashSet();
		for (IDData dat : data.values()) {
			IDType type = dat.type;
			for (DragonAPIMod mod : dat.IDs.keySet()) {
				Collection<Integer> ids = dat.IDs.get(mod);
				for (int id : ids) {
					Object obj = type.getValue(id);
					if (obj == null) {
						//conflicts.add(this.onConflict(mod, id));
						DragonAPICore.logError(type.getName()+" ID "+id+" ("+dat.classes.get(id)+") was deleted post-registration!");
					}
					else {
						Class c = obj.getClass();
						Class c1 = dat.classes.get(id);
						if (c1 != c)
							conflicts.add(this.onConflict(mod, type, id, c, c1));
					}
				}
			}
		}
		if (!conflicts.isEmpty()) {
			throw new IDConflictException(conflicts.toArray(new String[conflicts.size()]));
		}
	}

	protected String onConflict(DragonAPIMod mod, IDType type, int id, Class c, Class c1) {
		String s = type.getName()+" IDs: "+type.getValue(id)+" @ ID "+id+" ("+c.getSimpleName()+" & "+c1.getSimpleName()+")";
		return mod == null ? s : mod.getDisplayName()+" - "+s;
	}

	private static class IDData {

		public final IDType type;

		private final MultiMap<DragonAPIMod, Integer> IDs = new MultiMap();
		private final HashMap<Integer, Class> classes = new HashMap();

		private IDData(IDType type) {
			this.type = type;
		}

	}

}
