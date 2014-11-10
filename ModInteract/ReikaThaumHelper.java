/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class ReikaThaumHelper {

	private static Map<String, ArrayList<String>> research;
	private static Map<String, AspectList> aspects;
	private static Map<String, ArrayList<String>> scannedObjects;
	private static Map<String, ArrayList<String>> scannedEntities;
	private static Map<String, ArrayList<String>> scannedPhenomena;

	private static Potion warpWard;

	public static void addAspects(ItemStack is, AspectList aspects) {
		AspectList has = ThaumcraftApi.objectTags.get(Arrays.asList(is.getItem(), is.getItemDamage()));

		if (has != null) {
			for (Aspect as : has.aspects.keySet()) {
				aspects.merge(as, has.getAmount(as));
			}
		}
		ThaumcraftApi.registerObjectTag(is, new int[]{is.getItemDamage()}, aspects);
	}

	public static void addAspectsToBlock(Block b, AspectList aspects) {
		addAspects(new ItemStack(b), aspects);
	}

	public static void addAspectsToBlockMeta(Block b, int meta, AspectList aspects) {
		addAspects(new ItemStack(b, 1, meta), aspects);
	}

	public static void addAspectsToItem(Item i, AspectList aspects) {
		addAspects(new ItemStack(i), aspects);
	}

	public static void addAspectsToItemMeta(Item id, int meta, AspectList aspects) {
		addAspects(new ItemStack(id, 1, meta), aspects);
	}

	/** Contains a helper function to avoid overwriting existing aspects. */
	public static void addAspects(ItemStack is, Object... aspects) {
		if (aspects.length%2 != 0) {
			ReikaJavaLibrary.pConsole("Could not add aspects to "+is+": You must specify a level for every aspect!");
			ReikaJavaLibrary.dumpStack();
			return;
		}
		AspectList has = ThaumcraftApi.objectTags.get(Arrays.asList(is.getItem(), is.getItemDamage()));
		AspectList ot = getAspectList(aspects);

		if (has != null) {
			for (Aspect as : has.aspects.keySet()) {
				ot.merge(as, has.getAmount(as));
			}
		}
		ReikaJavaLibrary.pConsole(Block.getBlockFromItem(is.getItem())+"_"+is+" > "+ot.aspects.toString()+" W "+has);
		ThaumcraftApi.registerObjectTag(is, ot);
	}

	public static void addAspectsToBlock(Block b, Object... aspects) {
		addAspects(new ItemStack(b), aspects);
	}

	public static void addAspectsToBlockMeta(Block b, int meta, Object... aspects) {
		addAspects(new ItemStack(b, 1, meta), aspects);
	}

	public static void addAspectsToItem(Item i, Object... aspects) {
		addAspects(new ItemStack(i), aspects);
	}

	public static void addAspectsToItemMeta(Item id, int meta, Object... aspects) {
		addAspects(new ItemStack(id, 1, meta), aspects);
	}

	public static void clearAspects(ItemStack is) {
		AspectList ot = new AspectList();
		ThaumcraftApi.registerObjectTag(is, new int[]{is.getItemDamage()}, ot);
	}

	public static void addAspects(Class<? extends Entity> entity, Object... aspects) {
		AspectList ot = getAspectList(aspects);
		String name = (String)EntityList.classToStringMapping.get(entity);
		ThaumcraftApi.registerEntityTag(name, ot);
	}

	private static AspectList getAspectList(Object... aspects) {
		AspectList ot = new AspectList();
		try {
			for (int i = 0; i < aspects.length; i += 2) {
				ot.add((Aspect)aspects[i], (Integer)aspects[i+1]);
			}
		}
		catch (ClassCastException e) {
			ReikaJavaLibrary.pConsole("Invalid parameters! Could not generate aspect list from "+Arrays.toString(aspects)+"!");
			e.printStackTrace();
		}
		return ot;
	}

	public static String aspectsToString(AspectList al) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (Aspect a : al.aspects.keySet()) {
			int amt = al.getAmount(a);
			sb.append(a.getTag()+"="+amt);
			sb.append(", ");
		}
		sb.append("}");
		return sb.toString();
	}

	private static final class AspectSorter implements Comparator<Aspect> {

		private static final HashMap<Aspect, Integer> map = new HashMap();

		@Override
		public int compare(Aspect o1, Aspect o2) {
			if (o1.isPrimal() && o2.isPrimal()) {
				return map.get(o1)-map.get(o2);
			}
			else if (o1.isPrimal()) {
				return Integer.MIN_VALUE;
			}
			else if (o2.isPrimal()) {
				return Integer.MAX_VALUE;
			}
			else {
				return String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
			}
		}

		static {
			map.put(Aspect.AIR, 0);
			map.put(Aspect.EARTH, 1);
			map.put(Aspect.FIRE, 2);
			map.put(Aspect.WATER, 3);
			map.put(Aspect.ORDER, 4);
			map.put(Aspect.ENTROPY, 5);
		}

	}

	public static boolean hasPlayerDiscoveredAspect(EntityPlayer ep, Aspect a) {
		if (!ModList.THAUMCRAFT.isLoaded())
			return false;
		if (a.isPrimal())
			return true;
		AspectList al = aspects.get(ep.getCommandSenderName());
		return al != null && al.aspects.containsKey(a);
	}

	public static Collection<Aspect> getAllDiscoveredAspects(EntityPlayer ep) {
		Collection<Aspect> li = new ArrayList();
		if (!ModList.THAUMCRAFT.isLoaded())
			return li;
		AspectList al = aspects.get(ep.getCommandSenderName());
		if (al != null) {
			li.addAll(al.aspects.keySet());
		}
		return li;
	}

	public static void sortAspectList(ArrayList<Aspect> list) {
		Collections.sort(list, new AspectSorter());
	}

	public static void clearScannedObjects(EntityPlayer ep) {
		if (!ModList.THAUMCRAFT.isLoaded())
			return;
		String s = ep.getCommandSenderName();
		scannedObjects.remove(s);
		scannedEntities.remove(s);
		scannedPhenomena.remove(s);
	}

	public static void clearResearch(EntityPlayer ep) {
		if (!ModList.THAUMCRAFT.isLoaded())
			return;
		research.remove(ep.getCommandSenderName());
	}

	public static void clearDiscoveredAspects(EntityPlayer ep) {
		if (!ModList.THAUMCRAFT.isLoaded())
			return;
		aspects.remove(ep.getCommandSenderName());
	}

	public static void giveWarpProtection(EntityPlayer ep, int time) {
		ep.addPotionEffect(new PotionEffect(warpWard.id, time, 0));
	}

	static {
		if (ModList.THAUMCRAFT.isLoaded()) {
			try {
				Class c = Class.forName("thaumcraft.common.Thaumcraft");
				Field f = c.getField("proxy");
				Object proxy = f.get(null);
				Class cp = Class.forName("thaumcraft.common.CommonProxy");
				Field kn = cp.getField("playerKnowledge");
				Object knowledge = kn.get(proxy);
				Class ck = Class.forName("thaumcraft.common.lib.research.PlayerKnowledge");
				Field res = ck.getField("researchCompleted");
				Field objs = ck.getField("objectsScanned");
				Field ents = ck.getField("entitiesScanned");
				Field phen = ck.getField("phenomenaScanned");
				Field asp = ck.getField("aspectsDiscovered");

				aspects = (Map)asp.get(knowledge);
				research = (Map)res.get(knowledge);
				scannedObjects = (Map)objs.get(knowledge);
				scannedEntities = (Map)ents.get(knowledge);
				scannedPhenomena = (Map)phen.get(knowledge);

				Class pot = Class.forName("thaumcraft.common.lib.potions.PotionWarpWard");
				Field ins = pot.getDeclaredField("instance");
				warpWard = (Potion)ins.get(null);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
