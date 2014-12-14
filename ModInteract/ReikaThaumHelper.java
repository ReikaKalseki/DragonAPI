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
import java.lang.reflect.Method;
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
import net.minecraft.tileentity.TileEntity;
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

	private static Method addWandVis;
	private static Method getWandVis;
	private static Method maxWandVis;

	private static Method setWandInUse;
	private static Method clearWandInUse;

	private static Method researchComplete;

	private static Potion warpWard;

	private static Collection<Aspect> allAspects = new ArrayList();

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

	public static Collection<? extends Aspect> getAllAspects() {
		return Collections.unmodifiableCollection(allAspects);
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
		if (!ModList.THAUMCRAFT.isLoaded())
			return;
		ep.addPotionEffect(new PotionEffect(warpWard.id, time, 0));
	}

	public static int getWandSpaceFor(ItemStack wand, Aspect a) {
		if (!ModList.THAUMCRAFT.isLoaded())
			return 0;
		return getVisCapacityForWand(wand)-getVisInWand(wand).getAmount(a);
	}

	public static int getVisCapacityForWand(ItemStack wand) {
		if (!ModList.THAUMCRAFT.isLoaded())
			return 0;
		try {
			return (Integer)maxWandVis.invoke(wand.getItem(), wand);
		}
		catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static AspectList getVisInWand(ItemStack wand) {
		if (!ModList.THAUMCRAFT.isLoaded())
			return null;
		try {
			return (AspectList)getWandVis.invoke(wand.getItem(), wand);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int addVisToWand(ItemStack wand, Aspect a, int amt) {
		if (!ModList.THAUMCRAFT.isLoaded())
			return 0;
		try {
			return (Integer)addWandVis.invoke(wand.getItem(), wand, a, amt, true);
		}
		catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static void setWandInUse(ItemStack wand, TileEntity te) {
		if (!ModList.THAUMCRAFT.isLoaded())
			return;
		try {
			setWandInUse.invoke(wand.getItem(), wand, te.xCoord, te.yCoord, te.zCoord);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void clearWandInUse(ItemStack wand) {
		if (!ModList.THAUMCRAFT.isLoaded())
			return;
		try {
			clearWandInUse.invoke(wand.getItem(), wand);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Decomposes an AspectList down to its primal types. */
	public static AspectList decompose(AspectList complex) {
		AspectList al = new AspectList();
		for (Aspect a : complex.aspects.keySet()) {
			int amt = complex.getAmount(a);
			if (a.isPrimal()) {
				al.add(a, amt);
			}
			else {
				HashMap<Aspect, Integer> map = getAspectDecomposition(a);
				for (Aspect a2 : map.keySet()) {
					al.add(a2, amt*map.get(a2));
				}
			}
		}
		return al;
	}

	public static HashMap<Aspect, Integer> getAspectDecomposition(Aspect a) {
		HashMap<Aspect, Integer> map = new HashMap();
		addAspectDecomposition(a, map);
		return map;
	}

	private static void addAspectDecomposition(Aspect a, HashMap<Aspect, Integer> map) {
		Integer get = map.get(a);
		if (get == null)
			get = 0;
		if (a.isPrimal()) {
			map.put(a, get+1);
		}
		else {
			Aspect[] parents = a.getComponents();
			for (int i = 0; i < parents.length; i++) {
				addAspectDecomposition(parents[i], map);
			}
		}
	}

	public static boolean isResearchComplete(EntityPlayer ep, String research) {
		if (!ModList.THAUMCRAFT.isLoaded())
			return false;
		try {
			return (Boolean)researchComplete.invoke(null, ep.getCommandSenderName(), research);
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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

				Class wand = Class.forName("thaumcraft.common.items.wands.ItemWandCasting");
				addWandVis = wand.getMethod("addVis", ItemStack.class, Aspect.class, int.class, boolean.class);
				getWandVis = wand.getMethod("getAllVis", ItemStack.class);
				maxWandVis = wand.getMethod("getMaxVis", ItemStack.class);
				setWandInUse = wand.getMethod("setObjectInUse", ItemStack.class, int.class, int.class, int.class);
				clearWandInUse = wand.getMethod("clearObjectInUse", ItemStack.class);

				Class mgr = Class.forName("thaumcraft.common.lib.research.ResearchManager");
				researchComplete = mgr.getMethod("isResearchComplete", String.class, String.class);

				Field[] fds = Aspect.class.getDeclaredFields();
				for (int i = 0; i < fds.length; i++) {
					Field fd = fds[i];
					if (fd.getType() == Aspect.class) {
						Aspect a = (Aspect)fd.get(null);
						if (a != null) {
							allAspects.add(a);
						}
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
