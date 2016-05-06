/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.DeepInteract;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.entities.monster.EntityWisp;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Instantiable.Formula.MathExpression;
import Reika.DragonAPI.Instantiable.IO.XMLInterface;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.ModInteract.CustomThaumResearch;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumIDHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ReikaThaumHelper {

	//keep watch for if these change to uuid
	private static Map<String, ArrayList<String>> research;
	private static Map<String, AspectList> aspects;
	private static Map<String, ArrayList<String>> scannedObjects;
	private static Map<String, ArrayList<String>> scannedEntities;
	private static Map<String, ArrayList<String>> scannedPhenomena;
	private static Map<String, Integer> playerWarp;
	private static Map<String, Integer> playerStickyWarp;
	private static Map<String, Integer> playerTempWarp;

	private static Method addWandVis;
	private static Method getWandVis;
	private static Method maxWandVis;

	private static Method setWandInUse;
	private static Method clearWandInUse;

	private static Method researchComplete;

	private static Field wispTarget;

	private static Object proxy; //auto-sides to correct side

	private static final Collection<Aspect> allAspects = new ArrayList();

	private static final HashSet<String> nativeCategories = new HashSet();

	public static void addAspects(ItemStack is, AspectList aspects) {
		AspectList has = ThaumcraftApi.objectTags.get(Arrays.asList(is.getItem(), is.getItemDamage()));

		if (has != null) {
			for (Aspect as : has.aspects.keySet()) {
				aspects.merge(as, has.getAmount(as));
			}
		}
		clearNullAspects(aspects);
		ThaumcraftApi.registerObjectTag(is, new int[]{is.getItemDamage()}, aspects);
		DragonAPICore.log("Registering "+is+" aspects "+aspectsToString(aspects));
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
			DragonAPICore.logError("Could not add aspects to "+is+": You must specify a level for every aspect!");
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
		clearNullAspects(ot);
		ThaumcraftApi.registerObjectTag(is, ot);
		DragonAPICore.log("Registering "+is+" aspects "+aspectsToString(ot));
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
			DragonAPICore.logError("Invalid parameters! Could not generate aspect list from "+Arrays.toString(aspects)+"!");
			e.printStackTrace();
		}
		return ot;
	}

	public static void clearNullAspects(AspectList al) {
		al.aspects.remove(null);
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
		ep.addPotionEffect(new PotionEffect(ThaumIDHandler.Potions.WARPWARD.getID(), time, 0));
	}

	public static void removeWarp(EntityPlayer ep) {
		playerWarp.remove(ep.getCommandSenderName());
		playerTempWarp.remove(ep.getCommandSenderName());
	}

	public static void removeWarp(EntityPlayer ep, int amt) {
		if (!ModList.THAUMCRAFT.isLoaded())
			return;
		String s = ep.getCommandSenderName();
		int temphas = getPlayerTempWarp(ep);
		int rem = 0;
		if (temphas > 0) {
			rem = Math.min(temphas, amt);
			if (temphas-rem > 0) {
				playerTempWarp.put(s, temphas-rem);
			}
			else {
				playerTempWarp.remove(s);
			}
		}
		int left = amt-rem;
		if (left > 0) {
			int stickhas = getPlayerStickyWarp(ep);
			if (stickhas > 0) {
				int rem2 = Math.min(stickhas, left);
				if (stickhas-rem2 > 0) {
					playerStickyWarp.put(s, stickhas-rem2);
				}
				else {
					playerStickyWarp.remove(s);
				}
				left -= rem2;
			}
			if (left > 0) {
				int has = getPlayerWarp(ep);
				if (has > 0) {
					int rem2 = Math.min(has, left);
					if (has-rem2 > 0) {
						playerWarp.put(s, has-rem2);
					}
					else {
						playerWarp.remove(s);
					}
				}
			}
		}
	}

	public static int getPlayerWarp(EntityPlayer ep) {
		Integer has = playerWarp.get(ep.getCommandSenderName());
		return has != null ? has.intValue() : 0;
	}

	public static int getPlayerStickyWarp(EntityPlayer ep) {
		Integer has = playerStickyWarp.get(ep.getCommandSenderName());
		return has != null ? has.intValue() : 0;
	}

	public static int getPlayerTempWarp(EntityPlayer ep) {
		Integer has = playerTempWarp.get(ep.getCommandSenderName());
		return has != null ? has.intValue() : 0;
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

	/** Decomposes an Aspect down to its primal types. */
	public static AspectList decompose(Aspect a) {
		AspectList al = new AspectList();
		if (a.isPrimal()) {
			al.add(a, 1);
		}
		else {
			HashMap<Aspect, Integer> map = getAspectDecomposition(a);
			for (Aspect a2 : map.keySet()) {
				al.add(a2, map.get(a2));
			}
		}
		return al;
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

	public static boolean isNativeThaumResearch(ResearchItem ri) {
		return nativeCategories.contains(ri.category);
	}

	/** Your lang file will have to include an entry tc.research_category.[name]=[Localized Name] entry. */
	public static void addBookCategory(ResourceLocation icon, String name) {
		ResourceLocation rl2 = new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png");
		ResearchCategories.registerCategory(name, icon, rl2);
	}

	public static void addInfusionRecipeBookEntryViaXML(String id, String desc, String category, InfusionRecipe ir, MathExpression cost, int row, int col, Class root, String path) {
		ItemStack out = (ItemStack)ir.getRecipeOutput();
		AspectList aspects = new AspectList();
		for (Aspect a : ir.getAspects().aspects.keySet()) {
			aspects.add(a, Math.max(1, (int)(cost.evaluate(ir.getAspects().getAmount(a)))));
		}
		String name = out.getDisplayName();
		CustomThaumResearch res = new CustomThaumResearch(id, category, aspects, col, row, 0, out).setName(name);
		res.setDescription(desc);
		XMLResearch xml = new XMLResearch(id.toLowerCase(Locale.ENGLISH), root, path, ir, 2);
		res.setPages(xml.getPages());
		res.registerResearchItem();
	}

	public static void addCrucibleRecipeBookEntryViaXML(String id, String desc, String category, CrucibleRecipe ir, MathExpression cost, int row, int col, Class root, String path) {
		ItemStack out = ir.getRecipeOutput();
		AspectList aspects = new AspectList();
		for (Aspect a : ir.aspects.aspects.keySet()) {
			aspects.add(a, Math.max(1, (int)(cost.evaluate(ir.aspects.getAmount(a)))));
		}
		String name = out.getDisplayName();
		CustomThaumResearch res = new CustomThaumResearch(id, category, aspects, col, row, 0, out).setName(name);
		res.setDescription(desc);
		XMLResearch xml = new XMLResearch(id.toLowerCase(Locale.ENGLISH), root, path, ir, 2);
		res.setPages(xml.getPages());
		res.registerResearchItem();
	}

	public static void addArcaneRecipeBookEntryViaXML(String id, String desc, String category, IArcaneRecipe ir, MathExpression cost, int row, int col, Class root, String path) {
		ItemStack out = ir.getRecipeOutput();
		AspectList aspects = new AspectList();
		for (Aspect a : ir.getAspects().aspects.keySet()) {
			aspects.add(a, Math.max(1, (int)(cost.evaluate(ir.getAspects().getAmount(a)))));
		}
		String name = out.getDisplayName();
		CustomThaumResearch res = new CustomThaumResearch(id, category, aspects, col, row, 0, out).setName(name);
		res.setDescription(desc);
		XMLResearch xml = new XMLResearch(id.toLowerCase(Locale.ENGLISH), root, path, ir, 2);
		res.setPages(xml.getPages());
		res.registerResearchItem();
	}

	public static class XMLResearch {

		private final XMLInterface info;
		private final ArrayList<ResearchPage> pages = new ArrayList();
		public final String name;

		private XMLResearch(String name, Class root, String path, InfusionRecipe recipe, int num) {
			info = new XMLInterface(root, path);
			this.name = name;
			XMLPage page = new XMLPage(recipe);
			pages.add(page);
			for (int i = 1; i < num; i++) {
				pages.add(new XMLPage(i));
			}
		}

		private XMLResearch(String name, Class root, String path, IArcaneRecipe recipe, int num) {
			info = new XMLInterface(root, path);
			this.name = name;
			XMLPage page = new XMLPage(recipe);
			pages.add(page);
			for (int i = 1; i < num; i++) {
				pages.add(new XMLPage(i));
			}
		}

		private XMLResearch(String name, Class root, String path, CrucibleRecipe recipe, int num) {
			info = new XMLInterface(root, path);
			this.name = name;
			XMLPage page = new XMLPage(recipe);
			pages.add(page);
			for (int i = 1; i < num; i++) {
				pages.add(new XMLPage(i));
			}
		}

		public void addPage() {
			int num = pages.size();
			pages.add(new XMLPage(num));
		}

		public ResearchPage[] getPages() {
			ResearchPage[] arr = new ResearchPage[pages.size()];
			for (int i = 0; i < arr.length; i++) {
				arr[i] = pages.get(i);
			}
			return arr;
		}

		private class XMLPage extends ResearchPage {

			private final int page;

			private XMLPage(InfusionRecipe recipe) {
				super(recipe);
				page = 0;
			}

			private XMLPage(IArcaneRecipe recipe) {
				super(recipe);
				page = 0;
			}

			private XMLPage(CrucibleRecipe recipe) {
				super(recipe);
				page = 0;
			}

			private XMLPage(int id) {
				super("");
				page = id;
			}

			@Override
			public String getTranslatedText() {
				return info.getValueAtNode("researches:"+name.toLowerCase(Locale.ENGLISH)+":page"+page);
			}

			@Override
			public String toString() {
				return name+": "+this.getTranslatedText();
			}
		}
	}

	static {
		if (ModList.THAUMCRAFT.isLoaded()) {

			nativeCategories.add("BASICS");
			nativeCategories.add("THAUMATURGY");
			nativeCategories.add("ALCHEMY");
			nativeCategories.add("ARTIFICE");
			nativeCategories.add("GOLEMANCY");
			nativeCategories.add("ELDRITCH");

			try {
				Class c = Class.forName("thaumcraft.common.Thaumcraft");
				Field f = c.getField("proxy");
				proxy = f.get(null);

				Class cp = Class.forName("thaumcraft.common.CommonProxy");
				Field kn = cp.getField("playerKnowledge");
				Object knowledge = kn.get(proxy);
				Class ck = Class.forName("thaumcraft.common.lib.research.PlayerKnowledge");
				Field res = ck.getField("researchCompleted");
				Field objs = ck.getField("objectsScanned");
				Field ents = ck.getField("entitiesScanned");
				Field phen = ck.getField("phenomenaScanned");
				Field asp = ck.getField("aspectsDiscovered");
				Field warp = ck.getField("warp");
				Field warpsticky = ck.getField("warpSticky");
				Field warptemp = ck.getField("warpTemp");

				aspects = (Map)asp.get(knowledge);
				research = (Map)res.get(knowledge);
				scannedObjects = (Map)objs.get(knowledge);
				scannedEntities = (Map)ents.get(knowledge);
				scannedPhenomena = (Map)phen.get(knowledge);
				playerWarp = (Map)warp.get(knowledge);
				playerStickyWarp = (Map)warpsticky.get(knowledge);
				playerTempWarp = (Map)warptemp.get(knowledge);
			}
			catch (Exception e) {
				DragonAPICore.logError("Could not load ThaumCraft PlayerKnowledge Handler!");
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.THAUMCRAFT, e);
			}

			try {
				Class wand = Class.forName("thaumcraft.common.items.wands.ItemWandCasting");
				addWandVis = wand.getMethod("addVis", ItemStack.class, Aspect.class, int.class, boolean.class);
				getWandVis = wand.getMethod("getAllVis", ItemStack.class);
				maxWandVis = wand.getMethod("getMaxVis", ItemStack.class);
				setWandInUse = wand.getMethod("setObjectInUse", ItemStack.class, int.class, int.class, int.class);
				clearWandInUse = wand.getMethod("clearObjectInUse", ItemStack.class);
			}
			catch (Exception e) {
				DragonAPICore.logError("Could not load ThaumCraft Wand Handler!");
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.THAUMCRAFT, e);
			}

			try {
				Class mgr = Class.forName("thaumcraft.common.lib.research.ResearchManager");
				researchComplete = mgr.getMethod("isResearchComplete", String.class, String.class);
			}
			catch (Exception e) {
				DragonAPICore.logError("Could not load ThaumCraft Research Handler!");
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.THAUMCRAFT, e);
			}

			try {
				Class wisp = Class.forName("thaumcraft.common.entities.monster.EntityWisp");
				wispTarget = wisp.getDeclaredField("targetedEntity");
				wispTarget.setAccessible(true);
			}
			catch (Exception e) {
				DragonAPICore.logError("Could not load ThaumCraft Mob Handler!");
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.THAUMCRAFT, e);
			}

			try {
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
				DragonAPICore.logError("Could not load ThaumCraft Aspect Handler!");
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.THAUMCRAFT, e);
			}

			if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
				try {
					Class clip = Class.forName("thaumcraft.client.ClientProxy");

					for (int i = 0; i < EffectType.list.length; i++) {
						EffectType type = EffectType.list[i];
						type.call = clip.getMethod(type.name, type.arguments);
					}
				}
				catch (Exception e) {
					DragonAPICore.logError("Could not load ThaumCraft Effect Handler!");
					e.printStackTrace();
					ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.THAUMCRAFT, e);
				}
			}
		}
	}

	public static void setWispHostility(EntityWisp e, EntityLivingBase tg) {
		try {
			wispTarget.set(e, tg);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Entity getWispHostility(EntityWisp e) {
		try {
			return (Entity)wispTarget.get(e);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/** Triggers one of TC's native FX. It is your responsibility to ensure the correct parameters are chosen for the relevant Effect Type. */
	@SideOnly(Side.CLIENT)
	public static void triggerEffect(EffectType type, Object... data) {
		try {
			type.call.invoke(proxy, data);
		}
		catch (Exception e) {
			DragonAPICore.logError("Error Triggering ThaumCraft Effect: "+e.getMessage());
			e.printStackTrace();
		}
	}

	public static enum EffectType {
		NODEBURST("burst", World.class, double.class, double.class, double.class, float.class), //Node break/wisp spawn sparkle
		WISP("wispFX", World.class, double.class, double.class, double.class, float.class, float.class, float.class, float.class), //Wisp particles
		WISP2("wispFX2", World.class, double.class, double.class, double.class, float.class, int.class, boolean.class, boolean.class, float.class),
		WISP3("wispFX3", World.class, double.class, double.class, double.class, double.class, double.class, double.class, float.class, int.class, boolean.class, float.class),
		WISP4("wispFX4", World.class, double.class, double.class, double.class, Entity.class, int.class, boolean.class, float.class),
		BOLT("bolt", World.class, Entity.class, Entity.class), //Wisp attack
		SOURCESTREAM("sourceStreamFX", World.class, double.class, double.class, double.class, float.class, float.class, float.class, int.class), //Vis from jar during infusion
		RUNES("blockRunes", World.class, double.class, double.class, double.class, float.class, float.class, float.class, int.class, float.class),
		NODEBOLT("nodeBolt", World.class, float.class, float.class, float.class, float.class, float.class, float.class), //node interaction
		NODEBOLT_ENTITY("nodeBolt", World.class, float.class, float.class, float.class, Entity.class);

		private static final EffectType[] list = values();

		private final String name;
		private final Class[] arguments;

		private Method call;

		private EffectType(String s, Class... args) {
			name = s;
			arguments = args;
		}
	}
	/*
	/** Note dat[0] is the length. *//*
	public static int[] aspectToIntsForPacket(Aspect a) {
		String s = a.getName().toLowerCase();
		int[] dat = new int[1+s.length()];
		dat[0] = s.length();
		for (int i = 0; i < dat.length; i++) {
			dat[1+i] = s.charAt(i);
		}
		return dat;
	}

	public static Aspect intsToAspectFromPacket(int[] dat) {
		int len = dat[0];
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < dat.length; i++) {
			sb.append((char)dat[i]);
		}
		String s = sb.toString();
		Aspect a = Aspect.getAspect(s);
		if (a == null) {
			DragonAPICore.logError("Packet Error: Aspect from String '"+s+"' (encoded by "+Arrays.toString(dat)+") returned a null Aspect!");
		}
		else
			return a;
	}
	 */
}

