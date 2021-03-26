/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import Reika.DragonAPI.DragonAPICore;

public final class ReikaPotionHelper extends DragonAPICore {

	public static final int SPLASH_BIT = 16384;
	public static final int EXTENDED_BIT = 64;
	public static final int BOOST_BIT = 32;
	public static final int WATER_META = 0;
	public static final int AWKWARD_META = 16;
	public static final int POTION_BIT = 8192;

	private static final HashSet<Integer> badPotions = new HashSet();
	private static HashMap<Potion, Integer> potionDamageValues = new HashMap();

	public static final Comparator effectSorter = new PotionEffectSorter();

	/** Returns a potion ID from the damage value. Returns -1 if invalid damage value. */
	public static int getPotionID(int dmg) {
		List effects = Items.potionitem.getEffects(dmg);
		if (effects != null && !effects.isEmpty()) {
			Iterator potioneffects = effects.iterator();
			while (potioneffects.hasNext()) {
				PotionEffect effect = (PotionEffect)potioneffects.next();
				return effect.getPotionID();
			}
		}
		return -1;
	}

	public static boolean isBadEffect(Potion pot) {
		return badPotions.contains(pot.id);
	}

	public static boolean isSplashPotion(int dmg) {
		return (dmg & SPLASH_BIT) != 0;
	}

	public static boolean isBoosted(int dmg) {
		return (dmg & BOOST_BIT) != 0;
	}

	public static boolean isExtended(int dmg) {
		return (dmg & EXTENDED_BIT) != 0;
	}

	public static boolean isActualPotion(int dmg) {
		return (dmg & POTION_BIT) != 0;
	}

	static {
		badPotions.add(Potion.confusion.id);
		badPotions.add(Potion.wither.id);
		badPotions.add(Potion.moveSlowdown.id);
		badPotions.add(Potion.digSlowdown.id);
		badPotions.add(Potion.weakness.id);
		badPotions.add(Potion.blindness.id);
		badPotions.add(Potion.harm.id);
		badPotions.add(Potion.hunger.id);
		badPotions.add(Potion.poison.id);

		potionDamageValues.put(Potion.regeneration, 8193);
		potionDamageValues.put(Potion.moveSpeed, 8194);
		potionDamageValues.put(Potion.fireResistance, 8227);
		potionDamageValues.put(Potion.poison, 8196);
		potionDamageValues.put(Potion.heal, 8261);
		potionDamageValues.put(Potion.nightVision, 8230);
		potionDamageValues.put(Potion.weakness, 8232);
		potionDamageValues.put(Potion.damageBoost, 8201);
		potionDamageValues.put(Potion.moveSlowdown, 8234);
		potionDamageValues.put(Potion.harm, 8268);
		potionDamageValues.put(Potion.waterBreathing, 8237);
		potionDamageValues.put(Potion.invisibility, 8238);
	}

	public static void loadBadPotions() {
		for (int i = 0; i < Potion.potionTypes.length; i++) {
			Potion p = Potion.potionTypes[i];
			if (p != null && p.isBadEffect)
				badPotions.add(p.id);
		}
	}

	public static void addBadPotion(Potion p) {
		badPotions.add(p.id);
	}

	public static Map<Potion, Integer> getPotionValues() {
		return Collections.unmodifiableMap(potionDamageValues);
	}

	public static ArrayList<ItemStack> getBasePotionItems() {
		ArrayList<ItemStack> li = new ArrayList();
		for (int meta : potionDamageValues.values()) {
			li.add(new ItemStack(Items.potionitem, 1, meta));
		}
		return li;
	}

	public static ItemStack getPotionItem(Potion potion, boolean extended, boolean levelII, boolean splash) {
		int dmg = getPotionDamageValue(potion);
		if (extended)
			dmg += EXTENDED_BIT;
		if (levelII)
			dmg += BOOST_BIT;
		if (splash)
			dmg += SPLASH_BIT;
		ItemStack is = new ItemStack(Items.potionitem, 1, dmg);
		return is;
	}

	public static int getPotionDamageValue(Potion potion) {
		return potionDamageValues.containsKey(potion) ? potionDamageValues.get(potion) : 0;
	}
	/*
	public static void clearPotionsExceptSome(EntityLivingBase e, Potion... potions) {
		Collection<PotionEffect> c = e.getActivePotionEffects();
		Iterator<PotionEffect> it = c.iterator();
		ArrayList<PotionEffect> keep = new ArrayList();
		while (it.hasNext()) {
			PotionEffect pot = it.next();
			int id = pot.getPotionID();
			boolean remove = true;
			for (int i = 0; i < potions.length; i++) {
				if (id == potions[i].id) {
					remove = false;
					break;
				}
			}
			if (remove) {
				e.removePotionEffect(id);
				if (e.worldObj.isRemote)
					e.removePotionEffectClient(id);
			}
		}
		for (int i = 0; i < keep.size(); i++) {
			PotionEffect pot = keep.get(i);
			e.addPotionEffect(new PotionEffect(pot.getPotionID(), pot.getDuration(), pot.getAmplifier()));
		}
	}

	public static void clearPotionsExceptPerma(EntityLivingBase e) {
		Collection<PotionEffect> c = e.getActivePotionEffects();
		Iterator<PotionEffect> it = c.iterator();
		ArrayList<PotionEffect> keep = new ArrayList();
		while (it.hasNext()) {
			PotionEffect pot = it.next();
			int id = pot.getPotionID();
			Potion p = Potion.potionTypes[id];
			boolean remove = true;
			if (p instanceof PermaPotion) {
				remove = ((PermaPotion)p).canBeCleared(e, pot);
			}
			if (remove) {
				e.removePotionEffect(id);
				if (e.worldObj.isRemote)
					e.removePotionEffectClient(id);
			}
		}
		for (int i = 0; i < keep.size(); i++) {
			PotionEffect pot = keep.get(i);
			e.addPotionEffect(new PotionEffect(pot.getPotionID(), pot.getDuration(), pot.getAmplifier()));
		}
	}
	 */

	public static void clearBadPotions(EntityLivingBase player) {
		clearBadPotions(player, null);
	}

	public static void clearBadPotions(EntityLivingBase player, Set<Integer> ignore) {
		for (int id : badPotions) {
			if (ignore == null || !ignore.contains(id))
				player.removePotionEffect(id);
		}
	}

	private static class PotionEffectSorter implements Comparator<PotionEffect> {

		private PotionEffectSorter() {

		}

		@Override
		public int compare(PotionEffect o1, PotionEffect o2) {
			return this.getFlags(o1)-this.getFlags(o2);
		}

		private int getFlags(PotionEffect o) {
			int flags = 0;

			flags += o.getPotionID()*1000000;
			flags += o.getDuration()*100;
			flags += o.getAmplifier();

			return flags;
		}

	}
}
