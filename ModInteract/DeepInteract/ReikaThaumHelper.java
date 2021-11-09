/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.DeepInteract;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.Data.Maps.TierMap;
import Reika.DragonAPI.Instantiable.Formula.MathExpression;
import Reika.DragonAPI.Instantiable.IO.XMLInterface;
import Reika.DragonAPI.Interfaces.ObjectToNBTSerializer;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.ModInteract.CustomThaumResearch;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumIDHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.entities.monster.EntityWisp;

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
	private static Method getData;
	private static Method createNote;
	private static Method getMix;

	private static Field dataKey;
	private static Field dataComplete;

	private static SimpleNetworkWrapper packetHandlerInstance;
	private static Constructor<IMessage> aspectPacketConstructor;

	private static Field wispTarget;

	private static Class alchemicalFurnaceClass;
	private static Field furnaceBurn;
	private static Field itemBurn;

	private static Object proxy; //auto-sides to correct side

	private static final TierMap<Aspect> aspectTiers = new TierMap();

	private static final HashSet<String> nativeCategories = new HashSet();

	public static final ObjectToNBTSerializer<Aspect> aspectSerializer = new ObjectToNBTSerializer<Aspect>() {

		@Override
		public NBTTagCompound save(Aspect obj) {
			NBTTagCompound ret = new NBTTagCompound();
			ret.setString("aspect", obj.getTag());
			return ret;
		}

		@Override
		public Aspect construct(NBTTagCompound tag) {
			return Aspect.getAspect(tag.getString("aspect"));
		}

	};

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
				Aspect a = null;
				Object seek = aspects[i];
				if (seek instanceof Aspect)
					a = (Aspect)seek;
				else if (seek instanceof String)
					a = Aspect.getAspect((String)seek);
				if (a != null)
					ot.add(a, (Integer)aspects[i+1]);
				else
					DragonAPICore.logError("Cannot generate aspect from input '"+seek+"'!");
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
		if (al == null)
			return "null";
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (Aspect a : al.aspects.keySet()) {
			if (a == null) {
				sb.append("<NULL>");
			}
			else {
				int amt = al.getAmount(a);
				sb.append(a.getTag()+"="+amt);
			}
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
		return Aspect.aspects.values();//Collections.unmodifiableCollection(allAspects);
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

	public static int getResearchPoolCount(EntityPlayer ep, Aspect a) {
		if (!ModList.THAUMCRAFT.isLoaded())
			return 0;
		AspectList al = aspects.get(ep.getCommandSenderName());
		return al.getAmount(a);
	}

	public static void giveResearchPoint(Aspect a, short amt, EntityPlayer ep) {
		if (!ModList.THAUMCRAFT.isLoaded())
			return;
		AspectList get = aspects.get(ep.getCommandSenderName());
		if (get == null) {
			get = new AspectList();
			aspects.put(ep.getCommandSenderName(), get);
		}
		get.add(a, amt);
		if (ep instanceof EntityPlayerMP)
			sendAspectGetPacket((EntityPlayerMP)ep, a, amt);
	}

	private static void sendAspectGetPacket(EntityPlayerMP ep, Aspect a, short amt) {
		try {
			short total = (short)getResearchPoolCount(ep, a);
			IMessage pkt = aspectPacketConstructor.newInstance(a.getTag(), amt, total);
			packetHandlerInstance.sendTo(pkt, ep);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
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

	public static void addPlayerWarp(EntityPlayer ep, int amt) {
		Integer has = playerWarp.get(ep.getCommandSenderName());
		int val = has != null ? has.intValue() : 0;
		playerWarp.put(ep.getCommandSenderName(), val+amt);
	}

	public static void addPlayerStickyWarp(EntityPlayer ep, int amt) {
		Integer has = playerStickyWarp.get(ep.getCommandSenderName());
		int val =  has != null ? has.intValue() : 0;
		playerStickyWarp.put(ep.getCommandSenderName(), val+amt);
	}

	public static void addPlayerTempWarp(EntityPlayer ep, int amt) {
		Integer has = playerTempWarp.get(ep.getCommandSenderName());
		int val =  has != null ? has.intValue() : 0;
		playerTempWarp.put(ep.getCommandSenderName(), val+amt);
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

	public static String getResearchForItem(ItemStack is) {
		try {
			Object data = getData.invoke(null, is);
			return (String)dataKey.get(data);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean isResearchComplete(EntityPlayer ep, String research) {
		return ThaumcraftApiHelper.isResearchComplete(ep.getCommandSenderName(), research);
	}

	public static boolean isNativeThaumResearch(ResearchItem ri) {
		return nativeCategories.contains(ri.category);
	}

	/** Your lang file will have to include an entry tc.research_category.[name]=[Localized Name] entry. */
	public static void addBookCategory(ResourceLocation icon, String name) {
		ResourceLocation rl2 = new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png");
		ResearchCategories.registerCategory(name, icon, rl2);
	}

	public static ResearchItem addBasicBookEntryViaXML(DragonAPIMod mod, String id, String name, String desc, String category, AspectList aspects, int row, int col, Class root, String path, ResourceLocation ico) {
		CustomThaumResearch res = new CustomThaumResearch(id, category, aspects, col, row, 0, ico).setName(name);
		res.setDescription(desc);
		XMLResearch xml = new XMLResearch(mod, id.toLowerCase(Locale.ENGLISH), root, path);
		res.setPages(xml.getPages());
		return res.registerResearchItem();
	}

	public static ResearchItem addInfusionRecipeBookEntryViaXML(DragonAPIMod mod, String id, String desc, String category, InfusionRecipe ir, MathExpression cost, int row, int col, Class root, String path) {
		ItemStack out = (ItemStack)ir.getRecipeOutput();
		AspectList aspects = new AspectList();
		for (Aspect a : ir.getAspects().aspects.keySet()) {
			int amt = cost != null ? (int)(cost.evaluate(ir.getAspects().getAmount(a))) : ir.getAspects().getAmount(a);
			aspects.add(a, Math.max(1, amt));
		}
		String name = out.getDisplayName();
		CustomThaumResearch res = new CustomThaumResearch(id, category, aspects, col, row, 0, out).setName(name);
		res.setDescription(desc);
		XMLResearch xml = new XMLResearch(mod, id.toLowerCase(Locale.ENGLISH), root, path, ir);
		res.setPages(xml.getPages());
		return res.registerResearchItem();
	}

	public static ResearchItem addCrucibleRecipeBookEntryViaXML(DragonAPIMod mod, String id, String desc, String category, CrucibleRecipe ir, MathExpression cost, int row, int col, Class root, String path) {
		ItemStack out = ir.getRecipeOutput();
		AspectList aspects = new AspectList();
		for (Aspect a : ir.aspects.aspects.keySet()) {
			aspects.add(a, Math.max(1, (int)(cost.evaluate(ir.aspects.getAmount(a)))));
		}
		String name = out.getDisplayName();
		CustomThaumResearch res = new CustomThaumResearch(id, category, aspects, col, row, 0, out).setName(name);
		res.setDescription(desc);
		XMLResearch xml = new XMLResearch(mod, id.toLowerCase(Locale.ENGLISH), root, path, ir);
		res.setPages(xml.getPages());
		return res.registerResearchItem();
	}

	public static ResearchItem addArcaneRecipeBookEntryViaXML(DragonAPIMod mod, String id, String desc, String category, IArcaneRecipe ir, MathExpression cost, int row, int col, Class root, String path) {
		ItemStack out = ir.getRecipeOutput();
		AspectList aspects = new AspectList();
		for (Aspect a : ir.getAspects().aspects.keySet()) {
			aspects.add(a, Math.max(1, (int)(cost.evaluate(ir.getAspects().getAmount(a)))));
		}
		String name = out.getDisplayName();
		CustomThaumResearch res = new CustomThaumResearch(id, category, aspects, col, row, 0, out).setName(name);
		res.setDescription(desc);
		XMLResearch xml = new XMLResearch(mod, id.toLowerCase(Locale.ENGLISH), root, path, ir);
		res.setPages(xml.getPages());
		return res.registerResearchItem();
	}

	public static ResearchItem addResearchForMultipleRecipesViaXML(DragonAPIMod mod, String name, ItemStack icon, String id, String desc, String category, Class root, String path, int row, int col, Object[] recipes, AspectList al) {
		XMLResearch xml = getResearchForMultipleRecipes(mod, id.toLowerCase(Locale.ENGLISH), root, path, recipes);
		CustomThaumResearch res = new CustomThaumResearch(id, category, al, col, row, 0, icon).setName(name);
		res.setDescription(desc);
		res.setPages(xml.getPages());
		return res.registerResearchItem();
	}

	private static XMLResearch getResearchForMultipleRecipes(DragonAPIMod mod, String name, Class root, String path, Object[] recipes) {
		XMLResearch res = new XMLResearch(mod, name, root, path);
		for (Object r : recipes) {
			res.addRecipePage(r);
		}
		return res;
	}

	public static class XMLResearch {

		private final DragonAPIMod mod;
		private final XMLInterface info;
		private final ArrayList<XMLPage> pages = new ArrayList();
		public final String name;

		private int maxPage = -1;
		private final HashSet<Integer> textPages = new HashSet();
		private final ArrayList<Integer> recipePages = new ArrayList();

		private static XMLInterface loadData(Class root, String path) {
			XMLInterface xml = new XMLInterface(root, path, !ReikaObfuscationHelper.isDeObfEnvironment());
			//xml.setFallback(getParent(false)+name+".xml");
			xml.init();
			return xml;
		}

		private XMLResearch(DragonAPIMod mod, String name, Class root, String path) {
			this.mod = mod;
			info = loadData(root, path);
			this.name = name;
			for (String key : info.getNodesWithin("researches:"+name)) {
				int id = Integer.parseInt(key.substring(key.lastIndexOf("page")+"page".length()));
				maxPage = Math.max(maxPage, id);
				pages.add(new XMLPage(this, id));
				textPages.add(id);
			}
			for (int i = 0; i <= maxPage; i++) {
				if (!textPages.contains(i))
					recipePages.add(i);
			}
		}

		private void addRecipePage(Object recipe) {
			int idx = -1;
			if (recipePages.isEmpty()) {
				maxPage++;
				idx = maxPage;
				//recipePages.add(idx);
			}
			else {
				idx = recipePages.remove(0);
			}
			pages.add(XMLPage.getPageForObject(this, recipe, idx));
			Collections.sort(pages);
		}

		private XMLResearch(DragonAPIMod mod, String name, Class root, String path, InfusionRecipe recipe) {
			this(mod, name, root, path);
			this.addRecipePage(recipe);
		}

		private XMLResearch(DragonAPIMod mod, String name, Class root, String path, IArcaneRecipe recipe) {
			this(mod, name, root, path);
			this.addRecipePage(recipe);
		}

		private XMLResearch(DragonAPIMod mod, String name, Class root, String path, CrucibleRecipe recipe) {
			this(mod, name, root, path);
			this.addRecipePage(recipe);
		}

		public ResearchPage[] getPages() {
			ResearchPage[] arr = new ResearchPage[pages.size()];
			for (int i = 0; i < arr.length; i++) {
				arr[i] = pages.get(i);
			}
			return arr;
		}
	}

	private static class XMLPage extends ResearchPage implements Comparable<XMLPage> {

		private final XMLResearch research;
		private final int page;

		private XMLPage(XMLResearch res, InfusionRecipe recipe, int id) {
			super(recipe);
			research = res;
			page = id;
		}

		private XMLPage(XMLResearch res, IArcaneRecipe recipe, int id) {
			super(recipe);
			research = res;
			page = id;
		}

		private XMLPage(XMLResearch res, CrucibleRecipe recipe, int id) {
			super(recipe);
			research = res;
			page = id;
		}

		private XMLPage(XMLResearch res, int id) {
			super("");
			research = res;
			page = id;
		}

		@Override
		public String getTranslatedText() {
			return research.info.getValueAtNode("researches:"+research.name.toLowerCase(Locale.ENGLISH)+":page"+page);
		}

		@Override
		public String toString() {
			return research.name+": "+this.getTranslatedText();
		}

		private static XMLPage getPageForObject(XMLResearch r, Object o, int id) {
			if (o instanceof InfusionRecipe) {
				return new XMLPage(r, (InfusionRecipe)o, id);
			}
			else if (o instanceof IArcaneRecipe) {
				return new XMLPage(r, (IArcaneRecipe)o, id);
			}
			else if (o instanceof CrucibleRecipe) {
				return new XMLPage(r, (CrucibleRecipe)o, id);
			}
			return new XMLPage(r, id);
		}

		@Override
		public int compareTo(XMLPage o) {
			return Integer.compare(page, o.page);
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
				Class c = Class.forName("thaumcraft.common.lib.network.PacketHandler");
				Field f = c.getField("INSTANCE");
				packetHandlerInstance = (SimpleNetworkWrapper)f.get(null);

				Class pkt = Class.forName("thaumcraft.common.lib.network.playerdata.PacketAspectPool");
				aspectPacketConstructor = pkt.getConstructor(String.class, Short.class, Short.class);
			}
			catch (Exception e) {
				DragonAPICore.logError("Could not load ThaumCraft Packet Handler!");
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
				getData = mgr.getMethod("getData", ItemStack.class);
				createNote = mgr.getMethod("createNote", ItemStack.class, String.class, World.class);
				getMix = mgr.getMethod("getCombinationResult", Aspect.class, Aspect.class);

				Class data = Class.forName("thaumcraft.common.lib.research.ResearchNoteData");
				dataKey = data.getField("key");
				dataComplete = data.getField("complete");
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
				alchemicalFurnaceClass = Class.forName("thaumcraft.common.tiles.TileAlchemyFurnace");
				furnaceBurn = alchemicalFurnaceClass.getDeclaredField("furnaceBurnTime");
				furnaceBurn.setAccessible(true);
				itemBurn = alchemicalFurnaceClass.getDeclaredField("currentItemBurnTime");
				itemBurn.setAccessible(true);
			}
			catch (Exception e) {
				DragonAPICore.logError("Could not load ThaumCraft Alchemical Furnace Handler!");
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

	public static int getAspectTier(Aspect a) {
		if (aspectTiers.isEmpty())
			buildAspectTiers();
		return aspectTiers.getTier(a);
	}

	public static Set<Aspect> getAspectsByTier(int t) {
		if (aspectTiers.isEmpty())
			buildAspectTiers();
		return aspectTiers.getByTier(t);
	}

	public static int getMaxAspectTier() {
		if (aspectTiers.isEmpty())
			buildAspectTiers();
		return aspectTiers.getMaxTier();
	}

	private static void buildAspectTiers() {
		for (Aspect a : getAllAspects()) {
			if (!aspectTiers.containsKey(a))
				aspectTiers.addObject(a, calculateAspectTier(a));
		}
	}

	private static int calculateAspectTier(Aspect a) {
		if (a.isPrimal())
			return 0;
		Aspect[] parents = a.getComponents();
		int maxt = 0;
		for (Aspect a2 : parents) {
			int t2 = aspectTiers.getTier(a2);
			if (t2 == -1) {
				t2 = calculateAspectTier(a2);
				aspectTiers.addObject(a2, t2);
			}
			maxt = Math.max(maxt, t2);
		}
		return maxt+1;
	}

	public static void programResearchNote(ItemStack is, String key, World world) {
		try {
			createNote.invoke(null, is, key, world);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Aspect getAspectCombinationResult(Aspect a1, Aspect a2) {
		try {
			return (Aspect)getMix.invoke(null, a1, a2);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ShapelessOreRecipe getShapelessArcaneAsShapelessRecipe(ShapelessArcaneRecipe r) {
		return new ShapelessOreRecipe(r.getRecipeOutput(), r.getInput());
	}

	public static ShapedOreRecipe getShapedArcaneAsShapedRecipe(ShapedArcaneRecipe r) {
		return new ShapedOreRecipe(r.getRecipeOutput(), ReikaRecipeHelper.decode1DArray(r.input, r.width, r.height));
	}

	public static boolean isAlchemicalFurnace(TileEntity te) {
		return te != null && te.getClass() == alchemicalFurnaceClass;
	}

	public static void setAlchemicalBurnTime(TileEntity te, int ticks) {
		try {
			furnaceBurn.setInt(te, ticks);
			itemBurn.setInt(te, ticks);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Bypasses his function because he ignores the stack size which is explicitly listed as modifiable  */
	public static void addSmeltingBonusWithStackSize(ItemStack in, ItemStack out) {
		try {
			Field f = ThaumcraftApi.class.getDeclaredField("smeltingBonus");
			f.setAccessible(true);
			HashMap<Object, ItemStack> map = (HashMap<Object, ItemStack>)f.get(null);
			List key = Arrays.asList(in.getItem(), in.getItemDamage());
			map.put(key, out.copy());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean aspectListContains(AspectList list, AspectList has) {
		for (Aspect a : has.aspects.keySet()) {
			if (list.getAmount(a) < has.getAmount(a))
				return false;
		}
		return true;
	}

	public static ItemFocusBasic getWandFocus(ItemStack is) { //easier than reflection
		if (is.stackTagCompound == null || !is.stackTagCompound.hasKey("focus"))
			return null;
		ItemStack stored = ItemStack.loadItemStackFromNBT(is.stackTagCompound.getCompoundTag("focus"));
		return stored != null && stored.getItem() instanceof ItemFocusBasic ? (ItemFocusBasic)stored.getItem() : null;
	}

	public static ItemStack getWandFocusStack(ItemStack is) { //easier than reflection
		if (is.stackTagCompound == null || !is.stackTagCompound.hasKey("focus"))
			return null;
		return ItemStack.loadItemStackFromNBT(is.stackTagCompound.getCompoundTag("focus"));
	}

	public static WandRod getWandRod(ItemStack is) { //easier than reflection
		String key = is != null && is.stackTagCompound != null && is.stackTagCompound.hasKey("rod") ? is.stackTagCompound.getString("rod") : "wood";
		return WandRod.rods.get(key);
	}

	public static WandCap getWandCap(ItemStack is) { //easier than reflection
		String key = is != null && is.stackTagCompound != null && is.stackTagCompound.hasKey("cap") ? is.stackTagCompound.getString("cap") : "iron";
		return WandCap.caps.get(key);
	}
}

