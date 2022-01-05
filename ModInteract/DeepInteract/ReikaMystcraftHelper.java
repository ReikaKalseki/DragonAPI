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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import com.xcompwiz.mystcraft.api.APIInstanceProvider;
import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.api.exception.APIUndefined;
import com.xcompwiz.mystcraft.api.exception.APIVersionRemoved;
import com.xcompwiz.mystcraft.api.exception.APIVersionUndefined;
import com.xcompwiz.mystcraft.api.hook.DimensionAPI;
import com.xcompwiz.mystcraft.api.hook.PageAPI;
import com.xcompwiz.mystcraft.api.hook.SymbolAPI;
import com.xcompwiz.mystcraft.api.hook.SymbolValuesAPI;
import com.xcompwiz.mystcraft.api.item.IItemOrderablePageProvider;
import com.xcompwiz.mystcraft.api.item.IItemPageProvider;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.api.word.WordData;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IPopulate;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fluids.Fluid;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Extras.NeedsImplementation;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.ModInteract.ReikaTwilightHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ExtraUtilsHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.MystCraftHandler;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.event.FMLInterModComms;


public class ReikaMystcraftHelper {

	private static final Random rand = new Random();

	private static final HashMap<Integer, AgeInterface> ageData = new HashMap();

	private static final Method getTile;
	private static final Method getBook;
	private static final Method getLink;

	private static final Class biomeWrapper;
	private static final Field parentBiome;

	private static final Field pagesField;

	private static APIInstanceProvider apiProvider;
	private static final int API_VERSION = 1;

	private static final ArrayList<MystcraftPageRegistry> registries = new ArrayList();

	public static void disableFluidPage(Fluid f) {
		FMLInterModComms.sendMessage(ModList.MYSTCRAFT.modLabel, "blacklistfluid", f.getName());
	}

	public static void disableBiomePage(BiomeGenBase b) {
		FMLInterModComms.sendMessage(ModList.MYSTCRAFT.modLabel, "blacklist", "Biome"+b.biomeID);
	}

	private static ILinkInfo getPortalInfo(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (b != MystCraftHandler.getInstance().portalID)
			return null;
		try {
			TileEntity te = (TileEntity)getTile.invoke(MystCraftHandler.getInstance().portalID, world, x, y, z);
			ItemStack book = (ItemStack)getBook.invoke(te);
			if (book == null)
				return null;
			ILinkInfo info = getLinkbookLink(book);
			return info;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ILinkInfo getLinkbookLink(ItemStack book) {
		try {
			return (ILinkInfo)getLink.invoke(book.getItem(), book);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ArrayList<IAgeSymbol> getPagesInBook(ItemStack is, boolean readBookDirectly) {
		ArrayList<IAgeSymbol> li = new ArrayList();
		if (readBookDirectly) {
			/*
			IItemPageProvider ii = (IItemPageProvider)is.getItem();
			List<ItemStack> ret = ii.getPageList(null, is);
			for (ItemStack in : ret) {
				IAgeSymbol ia = getSymbolFromPage(in);
				if (ia != null) {
					li.add(ia);
				}
			}*/
			if (is.stackTagCompound == null)
				return li;
			NBTTagList list = is.stackTagCompound.getTagList("Pages", NBTTypes.COMPOUND.ID);
			for (Object o : list.tagList) {
				NBTTagCompound tag = (NBTTagCompound)o;
				ItemStack page = ItemStack.loadItemStackFromNBT(tag);
				IAgeSymbol ia = getSymbolFromPage(page);
				if (ia != null) {
					li.add(ia);
				}
			}
		}
		else {
			ILinkInfo link = getLinkbookLink(is);
			if (link != null) {
				Integer dim = link.getDimensionUID();
				if (dim != null && DimensionManager.isDimensionRegistered(dim)) {
					DimensionManager.initDimension(dim);
					WorldServer age = DimensionManager.getWorld(dim);
					if (age != null) {
						for (String s : getAgeSymbolsOrdered(age)) {
							IAgeSymbol ia = ((SymbolAPI)getAPI(APISegment.SYMBOL)).getSymbol(s);
							//ReikaJavaLibrary.pConsole(s+" > "+ia);
							if (ia != null) {
								li.add(ia);
							}
						}
					}
				}
			}
		}
		return li;
	}

	public static int getTargetDimensionIDFromPortalBlock(World world, int x, int y, int z) {
		ILinkInfo info = getPortalInfo(world, x, y, z);
		return info != null ? info.getDimensionUID() : Integer.MIN_VALUE;
	}

	public static boolean isMystAge(World world) {
		int id = world.provider.dimensionId;
		if (id == 0 || id == 1 || id == -1 || (id == ReikaTwilightHelper.getDimensionID() && ModList.TWILIGHT.isLoaded()) || (id == ExtraUtilsHandler.getInstance().darkID && ModList.EXTRAUTILS.isLoaded()))
			return false;
		DimensionAPI d = (DimensionAPI)getAPI(APISegment.DIMENSION);
		return world.provider.getClass().getSimpleName().equals("WorldProviderMyst");//d != null && d.isMystcraftAge(id);
	}
	/*
	public static int getStabilityForAge(World world) {
		if (!loadedCorrectly)
			return 0;
		return isMystAge(world) ? getOrCreateInterface(world).getStabilizationParameter() : 0;
	}
	 */
	public static int getInstabilityScoreForAge(World world) {
		if (!AgeInterface.loadedCorrectly)
			return 0;
		return isMystAge(world) ? getOrCreateInterface(world).getInstabilityScore() : 0;
	}

	public static int getBlockInstabilityForAge(World world) {
		if (!AgeInterface.loadedCorrectly)
			return 0;
		return isMystAge(world) ? getOrCreateInterface(world).getBlockInstability() : 0;
	}

	public static int getSymbolInstabilityForAge(World world) {
		if (!AgeInterface.loadedCorrectly)
			return 0;
		return isMystAge(world) ? getOrCreateInterface(world).getSymbolInstability() : 0;
	}

	public static short getBaseInstabilityForAge(World world) {
		if (!AgeInterface.loadedCorrectly)
			return 0;
		return isMystAge(world) ? getOrCreateInterface(world).getBaseInstability() : 0;
	}

	public static int decrInstabilityForAge(World world, int amt) {
		if (!AgeInterface.loadedCorrectly)
			return 0;
		return isMystAge(world) ? getOrCreateInterface(world).decrInstability(amt) : 0;
	}

	public static void addInstabilityForAge(World world, short amt) {
		if (AgeInterface.loadedCorrectly && isMystAge(world)) {
			getOrCreateInterface(world).addBaseInstability(amt);
		}
	}

	public static void removeInstabilityForAge(World world) {
		if (AgeInterface.loadedCorrectly && isMystAge(world))
			getOrCreateInterface(world).setZeroInstability();
	}

	public static boolean isSymbolPresent(World world, BasicPages sym) {
		return isSymbolPresent(world, sym.ID);
	}

	public static boolean isSymbolPresent(World world, String sym) {
		if (AgeInterface.loadedCorrectly && isMystAge(world)) {
			return getOrCreateInterface(world).symbolExists(sym);
		}
		return false;
	}

	public static Set<String> getAgeSymbols(World world) {
		if (AgeInterface.loadedCorrectly && isMystAge(world)) {
			return getOrCreateInterface(world).getSymbols();
		}
		return new HashSet();
	}

	public static List<String> getAgeSymbolsOrdered(World world) {
		if (AgeInterface.loadedCorrectly && isMystAge(world)) {
			return getOrCreateInterface(world).getSymbolsOrdered();
		}
		return new ArrayList();
	}

	/*
	public static boolean setStabilityForAge(World world, int stability) {
		if (!loadedCorrectly)
			return false;
		if (isMystAge(world)) {
			InstabilityInterface ii = getOrCreateInterface(world);
			return ii.setStabilization(stability);
		}
		else {
			return false;
		}
	}

	public static boolean addStabilityForAge(World world, int toAdd) {
		if (!loadedCorrectly)
			return false;
		if (isMystAge(world)) {
			InstabilityInterface ii = getOrCreateInterface(world);
			int stable = ii.getStabilizationParameter();
			int newstable = stable+toAdd;
			return ii.setStabilization(newstable);
		}
		else {
			return false;
		}
	}
	 */

	private static AgeInterface getOrCreateInterface(World world) {
		if (!AgeInterface.loadedCorrectly)
			return null;
		AgeInterface ii = ageData.get(world.provider.dimensionId);
		if (ii == null) {
			ii = new AgeInterface(world);
			ageData.put(world.provider.dimensionId, ii);
		}
		return ii;
	}

	public static void clearCache() {
		ageData.clear();
	}

	public static ArrayList<IAgeSymbol> getAllSymbols() {
		ArrayList<IAgeSymbol> c = new ArrayList();
		SymbolAPI api = getAPI(APISegment.SYMBOL);
		if (api != null) {
			c.addAll(api.getAllRegisteredSymbols());
		}
		return c;
	}

	public static ArrayList<ItemStack> getAllAgePages() {
		ArrayList<ItemStack> li = new ArrayList();
		ArrayList<IAgeSymbol> c = getAllSymbols();
		for (IAgeSymbol a : c) {
			li.add(getSymbolPage(a));
		}
		return li;
	}

	public static String getSymbolIdFromPage(ItemStack is) {
		PageAPI api = getAPI(APISegment.PAGE);
		if (api != null) {
			return api.getPageSymbol(is);
		}
		return null;
	}

	public static IAgeSymbol getSymbolFromPage(ItemStack is) {
		String id = getSymbolIdFromPage(is);
		if (id == null)
			return null;
		SymbolAPI api = getAPI(APISegment.SYMBOL);
		if (api != null) {
			return api.getSymbol(id);
		}
		return null;
	}

	public static ItemStack getSymbolPage(IAgeSymbol a) {
		PageAPI api = getAPI(APISegment.PAGE);
		if (api != null) {
			ItemStack is = new ItemStack((Item)Item.itemRegistry.getObject(ModList.MYSTCRAFT.modLabel+":"+MystObjects.Items.page));
			if (is != null && is.getItem() != null) {
				is.stackTagCompound = new NBTTagCompound();
				//is.stackTagCompound.setTag("symbol", new NBTTagCompound());
				api.setPageSymbol(is, a.identifier());
				return is;
			}
		}
		return null;
	}

	public static ItemStack getSymbolPage(String id) {
		PageAPI api = getAPI(APISegment.PAGE);
		if (api != null) {
			SymbolAPI api2 = getAPI(APISegment.SYMBOL);
			IAgeSymbol ias = api2.getSymbol(id);
			if (ias == null)
				throw new IllegalArgumentException("No such symbol '"+id+"'!");
			ItemStack is = new ItemStack((Item)Item.itemRegistry.getObject(ModList.MYSTCRAFT.modLabel+":"+MystObjects.Items.page));
			if (is != null && is.getItem() != null) {
				is.stackTagCompound = new NBTTagCompound();
				//is.stackTagCompound.setTag("symbol", new NBTTagCompound());
				api.setPageSymbol(is, id);
				return is;
			}
		}
		return null;
	}

	public static int getSymbolRank(IAgeSymbol ia) {
		return (int)((SymbolValuesAPI)getAPI(APISegment.SYMBOLVALUES)).getSymbolItemWeight(ia.identifier());
	}

	public static IAgeSymbol getRandomPage() {
		ArrayList<IAgeSymbol> c = getAllSymbols();
		return c.get(rand.nextInt(c.size()));
	}

	/** Ranges from 0-1, lower is rarer; direct linear affect on page loot rarity */
	public static float getPageWeight(IAgeSymbol a) {
		SymbolValuesAPI api = getAPI(APISegment.SYMBOLVALUES);
		if (api != null) {
			return api.getSymbolItemWeight(a.identifier());
		}
		return 0;
	}

	public static void setPageRank(IAgeSymbol a, int rank) {
		SymbolValuesAPI api = getAPI(APISegment.SYMBOLVALUES);
		if (api != null) {
			api.setSymbolCardRank(a, rank);
		}
	}

	@NeedsImplementation
	public static void setRandomAgeWeight(IAgeSymbol a, float weight) {
		//TODO incomplete abandoned method
	}

	public static void registerAgeSymbol(IAgeSymbol a) {
		SymbolAPI api = getAPI(APISegment.SYMBOL);
		if (api != null) {
			boolean flag = api.registerSymbol(a, false);
			if (flag) {
				DragonAPICore.log("Registering MystCraft page '"+a.displayName()+"' ("+a.getClass()+"')");
			}
			else {
				DragonAPICore.logError("Could not register MystCraft page '"+a.displayName()+"' ("+a.getClass()+"')");
			}
		}
		else {
			DragonAPICore.logError("Could not register MystCraft page '"+a.displayName()+"' ("+a.getClass()+"'); API object was null.");
		}
	}

	@ModDependent(ModList.MYSTCRAFT)
	public static <A> A getAPI(APISegment type) {
		try {
			return apiProvider != null ? (A)apiProvider.getAPIInstance(type.getTag(API_VERSION)) : null;
		}
		catch (APIUndefined e) {
			throw new RuntimeException("Invalid API type coded into DragonAPI! This is a serious error!");
		}
		catch (APIVersionUndefined e) {
			e.printStackTrace();
		}
		catch (APIVersionRemoved e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void receiveAPI(APIInstanceProvider provider) {
		apiProvider = provider;
		for (MystcraftPageRegistry p : registries) {
			p.register();
		}
	}

	public static void registerPageRegistry(MystcraftPageRegistry p) {
		if (registries.contains(p))
			throw new MisuseException("You cannot register a MystCraft page provider twice!");
		registries.add(p);
	}

	public static List<ItemStack> getPagesInFolder(EntityPlayer ep, ItemStack is, boolean clear) {
		List<ItemStack> li = ((IItemPageProvider)is.getItem()).getPageList(ep, is);
		if (clear) {
			for (int i = 0; i < li.size(); i++) {
				((IItemOrderablePageProvider)is.getItem()).removePage(ep, is, i);
			}
		}
		return li;
	}

	public static int getFlatWorldThickness(World world) {
		AgeInterface a = getOrCreateInterface(world);
		return a != null ? a.getGroundLevel() : world.provider.getAverageGroundLevel();
	}

	public static BiomeGenBase getMystParentBiome(BiomeGenBase b) {
		if (b.getClass() != biomeWrapper) {
			return b;
		}
		try {
			return (BiomeGenBase)parentBiome.get(b);
		}
		catch (Exception e) {
			e.printStackTrace();
			return b;
		}
	}

	public static boolean setBookBinderPages(TileEntity te, ArrayList<IAgeSymbol> li) {
		return setBookBinderItemPages(te, getPagesAsItems(li));
	}

	private static ArrayList<ItemStack> getPagesAsItems(ArrayList<IAgeSymbol> li) {
		ArrayList<ItemStack> ret = new ArrayList();
		for (IAgeSymbol ia : li) {
			ret.add(getSymbolPage(ia));
		}
		return ret;
	}

	private static boolean setBookBinderItemPages(TileEntity te, ArrayList<ItemStack> pages) {
		List<ItemStack> li;
		try {
			li = (List<ItemStack>)pagesField.get(te);
			if (li == null || !li.isEmpty())
				return false;
			li.addAll(pages);
			te.worldObj.markBlockForUpdate(te.xCoord, te.yCoord, te.zCoord);
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static interface MystcraftPageRegistry {

		public void register();

	}

	public static enum APISegment {
		SYMBOL("symbol"), //SymbolAPI
		WORD("word"), //WordAPI
		GRAMMAR("grammar"), //GrammarAPI
		INSTABILITY("instability"), //InstabilityAPI
		LINKING("linking"), //LinkingAPI
		LINKPROPERTY("linkingprop"), //LinkPropertyAPI
		PAGE("page"), //PageAPI
		SYMBOLVALUES("symbolvals"), //SymbolValuesAPI
		RENDER("render"), //RenderAPI
		DIMENSION("dimension"), //DimensionAPI
		;

		private final String tag;

		private APISegment(String n) {
			tag = n;
		}

		public String getTag(int version) {
			return tag+"-"+version;
		}
	}

	static {
		Method tile = null;
		Method book = null;
		Method link = null;
		Class biome = null;
		Field base = null;
		Field pages = null;

		boolean load = true;

		if (ModList.MYSTCRAFT.isLoaded()) {
			try {
				Class portal = Class.forName("com.xcompwiz.mystcraft.portal.PortalUtils");
				tile = portal.getDeclaredMethod("getTileEntity", IBlockAccess.class, int.class, int.class, int.class);
				tile.setAccessible(true);
				Class booktile = Class.forName("com.xcompwiz.mystcraft.tileentity.TileEntityBook");
				book = booktile.getDeclaredMethod("getBook");
				book.setAccessible(true);
				Class item = Class.forName("com.xcompwiz.mystcraft.item.ItemLinking");
				link = item.getDeclaredMethod("getLinkInfo", ItemStack.class);
				link.setAccessible(true);
				biome = Class.forName("com.xcompwiz.mystcraft.world.biome.BiomeWrapperMyst");
				base = biome.getDeclaredField("baseBiome");
				base.setAccessible(true);
				Class binder = Class.forName("com.xcompwiz.mystcraft.tileentity.TileEntityBookBinder");
				pages = binder.getDeclaredField("pages");
				pages.setAccessible(true);
			}
			catch (Exception e) {
				DragonAPICore.logError("Error loading Mystcraft linkbook interfacing!");
				e.printStackTrace();
				load = false;
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.MYSTCRAFT, e);
			}
		}
		else {
			load = false;
		}

		getTile = tile;
		getBook = book;
		getLink = link;
		biomeWrapper = biome;
		parentBiome = base;
		pagesField = pages;
	}

	public static IAgeSymbol createIWorldGeneratorPage(IWorldGenerator gen, String[] poem, int instability) {
		return new IWGSymbol(gen, poem, instability);
	}

	private static final class IWGSymbol extends BasicAgeSymbol {

		private final IWorldGenerator generator;

		public IWGSymbol(IWorldGenerator gen, String[] poem, int instability) {
			super(gen.getClass().getName().toLowerCase(Locale.ENGLISH).replaceAll("\\.", "_"), gen.getClass().getSimpleName(), poem, instability);
			generator = gen;
		}

		@Override
		public void registerLogic(AgeDirector age, long seed) {
			age.registerInterface(new IWGRelay(generator));
		}

	}

	private static final class IWGRelay implements IPopulate {

		private final IWorldGenerator generator;

		public IWGRelay(IWorldGenerator gen) {
			generator = gen;
		}

		@Override
		public boolean populate(World world, Random rand, int x, int y, boolean flag) {
			IChunkProvider prov = world.getChunkProvider();
			IChunkProvider gen = ((ChunkProviderServer)prov).currentChunkProvider;
			generator.generate(rand, x, y, world, gen, prov);
			return true;
		}

	}

	public static class BasicAgeSymbol implements IAgeSymbol {

		public final String id;
		public final String name;
		private final String[] words;
		public final int instability;

		public BasicAgeSymbol(String id, String n, String[] poem) {
			this(id, n, poem, 0);
		}

		public BasicAgeSymbol(String id, String n, String[] poem, int inst) {
			this.id = id;
			name = n;
			words = poem;
			instability = inst;
		}

		@Override
		public void registerLogic(AgeDirector controller, long seed) {

		}

		@Override
		public int instabilityModifier(int count) {
			return instability;
		}

		@Override
		public final String identifier() {
			return id;
		}

		@Override
		public final String displayName() {
			return name;
		}

		@Override
		public final String[] getPoem() {
			return words;
		}

	}

	private static final class AgeInterface {

		private static final Field age_controller;
		private static final Field instability_controller;
		//private static final Field stabilization;
		private static final Field data;
		private static final Field instabilityNumber;
		private static final Field blockInstabilityNumber;
		private static final Field baseInstability;
		private static final Field symbolList;
		private static final Method getScore;
		private static final Method getGroundLevel;

		private static boolean loadedCorrectly;

		public final int dimensionID;
		private final WorldProvider provider;
		private Object ageController; //AgeController class
		private Object instabilityController; //InstabilityController
		private Object ageData; //AgeData class
		private HashSet<String> ageSymbols;
		private ArrayList<String> ageSymbolsOrdered;

		private AgeInterface(World world) {
			if (!isMystAge(world))
				throw new IllegalArgumentException("Dimension "+world.provider.dimensionId+" is not a MystCraft age!");
			provider = world.provider;
			dimensionID = world.provider.dimensionId;
			try {
				ageController = age_controller.get(provider);
				instabilityController = instability_controller.get(ageController);
				ageData = data.get(ageController);
				ageSymbolsOrdered = new ArrayList((List<String>)symbolList.get(ageData));
				ageSymbols = new HashSet(ageSymbolsOrdered);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		public int getBlockInstability() {
			try {
				Integer get = (Integer)blockInstabilityNumber.get(ageController);
				return get != null ? get.intValue() : 0;
			}
			catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}

		public int getSymbolInstability() {
			try {
				return instabilityNumber.getInt(ageController);
			}
			catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}

		public short getBaseInstability() {
			try {
				return baseInstability.getShort(ageData);
			}
			catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}

		public int getInstabilityScore() {
			try {
				return (Integer)getScore.invoke(ageController);
			}
			catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}

		public void addBaseInstability(short amt) {
			short base = this.getBaseInstability();
			this.setBaseInstability((short)(amt+base));
		}

		public void setZeroInstability() {
			this.setSymbolInstability(0);
			this.setBlockInstability(0);
			this.setBaseInstability((short)0);
		}

		public int decrInstability(int amt) {
			int symbol = this.getSymbolInstability();
			if (symbol >= amt) {
				this.setSymbolInstability(symbol-amt);
				return 0;
			}
			else {
				this.setSymbolInstability(0);
				int rem = amt-symbol;
				int block = this.getBlockInstability();
				if (block >= rem) {
					this.setBlockInstability(block-rem);
					return 0;
				}
				else {
					int rem2 = rem-block;
					this.setBlockInstability(0);
					short base = this.getBaseInstability();
					if (base >= rem2) {
						this.setBaseInstability((short)(base-rem2));
						return 0;
					}
					else {
						this.setBaseInstability((short)0);
						return rem2-base;
					}
				}
			}
		}

		private void setBaseInstability(short amt) {
			if (amt < 0)
				amt = 0;
			try {
				baseInstability.set(ageData, amt);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void setBlockInstability(int amt) {
			if (amt < 0)
				amt = 0;
			try {
				blockInstabilityNumber.set(ageController, Integer.valueOf(amt));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void setSymbolInstability(int amt) {
			if (amt < 0)
				amt = 0;
			try {
				instabilityNumber.set(ageController, amt);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		public Set<String> getSymbols() {
			return Collections.unmodifiableSet(ageSymbols);
		}

		public List<String> getSymbolsOrdered() {
			return Collections.unmodifiableList(ageSymbolsOrdered);
		}

		public boolean symbolExists(String s) {
			return ageSymbols.contains(s);
		}

		public boolean symbolExists(IAgeSymbol s) {
			return ageSymbols.contains(s.identifier());
		}

		public int getGroundLevel() {
			try {
				return (int)getGroundLevel.invoke(ageController);
			}
			catch (Exception e) {
				e.printStackTrace();
				return 64;
			}
		}

		static {
			Field cont = null;
			Field insta = null;
			//Field stable = null;
			Field num = null;
			Field numblock = null;
			Field base = null;
			Field adata = null;
			Field sym = null;
			Method score = null;
			Method level = null;
			boolean load = true;
			if (ModList.MYSTCRAFT.isLoaded()) {
				try {
					Class prov = Class.forName("com.xcompwiz.mystcraft.world.WorldProviderMyst");
					cont = prov.getDeclaredField("controller");
					cont.setAccessible(true);
					Class age = Class.forName("com.xcompwiz.mystcraft.world.AgeController");
					insta = age.getDeclaredField("instabilityController");
					insta.setAccessible(true);
					level = age.getDeclaredMethod("getAverageGroundLevel");
					level.setAccessible(true);
					Class controller = Class.forName("com.xcompwiz.mystcraft.instability.InstabilityController");
					//stable = controller.getDeclaredField("stabilization");*
					//stable.setAccessible(true);
					num = age.getDeclaredField("symbolinstability");
					num.setAccessible(true);
					numblock = age.getDeclaredField("blockinstability");
					numblock.setAccessible(true);
					Class data = Class.forName("com.xcompwiz.mystcraft.world.agedata.AgeData");
					base = data.getDeclaredField("instability");
					base.setAccessible(true);
					sym = data.getDeclaredField("symbols");
					sym.setAccessible(true);
					score = age.getDeclaredMethod("getInstabilityScore");
					score.setAccessible(true);
					adata = age.getDeclaredField("agedata");
					adata.setAccessible(true);
					loadedCorrectly = true;
				}
				catch (Exception e) {
					DragonAPICore.logError("Error loading Mystcraft instability interfacing!");
					e.printStackTrace();
					load = false;
					ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.MYSTCRAFT, e);
				}
			}
			else {
				load = false;
			}
			age_controller = cont;
			instability_controller = insta;
			//stabilization = stable;
			instabilityNumber = num;
			blockInstabilityNumber = numblock;
			getScore = score;
			baseInstability = base;
			symbolList = sym;
			data = adata;
			getGroundLevel = level;
		}

	}

	/** This is copied from com.xcompwiz.mystcraft.data.ModSymbols.initialize() */
	public static enum BasicPages {
		ColorCloud("ColorCloud", 1, WordData.Image, WordData.Entropy, WordData.Believe, WordData.Weave),
		ColorCloudNatural("ColorCloudNat", 1, WordData.Image, WordData.Entropy, WordData.Believe, WordData.Nature),
		ColorFog("ColorFog", 1, WordData.Image, WordData.Entropy, WordData.Explore, WordData.Weave),
		ColorFogNatural("ColorFogNat", 1, WordData.Image, WordData.Entropy, WordData.Explore, WordData.Nature),
		ColorFoliage("ColorFoliage", 1, WordData.Image, WordData.Growth, WordData.Elevate, WordData.Weave),
		ColorFoliageNatural("ColorFoliageNat", 1, WordData.Image, WordData.Growth, WordData.Elevate, WordData.Nature),
		ColorGrass("ColorGrass", 1, WordData.Image, WordData.Growth, WordData.Resilience, WordData.Weave),
		ColorGrassNatural("ColorGrassNat", 1, WordData.Image, WordData.Growth, WordData.Resilience, WordData.Nature),
		ColorSky("ColorSky", 1, WordData.Image, WordData.Celestial, WordData.Harmony, WordData.Weave),
		ColorSkyNatural("ColorSkyNat", 1, WordData.Image, WordData.Celestial, WordData.Harmony, WordData.Nature),
		ColorSkyNight("ColorSkyNight", 1, WordData.Image, WordData.Celestial, WordData.Contradict, WordData.Weave),
		ColorWater("ColorWater", 1, WordData.Image, WordData.Flow, WordData.Constraint, WordData.Weave),
		ColorWaterNatural("ColorWaterNat", 1, WordData.Image, WordData.Flow, WordData.Constraint, WordData.Nature),
		DoodadRainbow("Rainbow", 1, WordData.Celestial, WordData.Image, WordData.Harmony, WordData.Balance),
		HideHorizon("NoHorizon", 1, WordData.Celestial, WordData.Inhibit, WordData.Image, WordData.Void),
		DarkMoon("MoonDark", 1, WordData.Celestial, WordData.Void, WordData.Inhibit, WordData.Wisdom),
		MoonNormal("MoonNormal", 1, WordData.Celestial, WordData.Image, WordData.Cycle, WordData.Wisdom),
		DarkStars("StarsDark", 1, WordData.Celestial, WordData.Void, WordData.Inhibit, WordData.Order),
		StarsEndSky("StarsEndSky", 1, WordData.Celestial, WordData.Image, WordData.Chaos, WordData.Weave),
		StarsNormal("StarsNormal", 1, WordData.Celestial, WordData.Harmony, WordData.Ethereal, WordData.Order),
		StarsTwinkle("StarsTwinkle", 1, WordData.Celestial, WordData.Harmony, WordData.Ethereal, WordData.Entropy),
		DarkSun("SunDark", 1, WordData.Celestial, WordData.Void, WordData.Inhibit, WordData.Energy),
		SunNormal("SunNormal", 2, WordData.Celestial, WordData.Image, WordData.Stimulate, WordData.Energy),
		BiomeControllerGrid("BioConGrid", 3, WordData.Constraint, WordData.Nature, WordData.Chain, WordData.Mutual),
		BiomeControllerNative("BioConNative", 3, WordData.Constraint, WordData.Nature, WordData.Tradition, WordData.Sustain),
		BiomeControllerSingle("BioConSingle", 3, WordData.Constraint, WordData.Nature, WordData.Infinite, WordData.Static),
		BiomeControllerTiled("BioConTiled", 3, WordData.Constraint, WordData.Nature, WordData.Chain, WordData.Contradict),
		BiomeControllerHuge("BioConHuge", 3, WordData.Constraint, WordData.Nature, WordData.Weave, "Huge"),
		BiomeControllerLarge("BioConLarge", 3, WordData.Constraint, WordData.Nature, WordData.Weave, "Large"),
		BiomeControllerMedium("BioConMedium", 3, WordData.Constraint, WordData.Nature, WordData.Weave, "Medium"),
		BiomeControllerSmall("BioConSmall", 3, WordData.Constraint, WordData.Nature, WordData.Weave, "Small"),
		BiomeControllerTiny("BioConTiny", 3, WordData.Constraint, WordData.Nature, WordData.Weave, "Tiny"),
		NoSea("NoSea", 2, WordData.Transform, WordData.Constraint, WordData.Flow, WordData.Inhibit),
		AntiPvP("PvPOff", -1, WordData.Chain, WordData.Chaos, WordData.Encourage, WordData.Harmony),
		EnvAccelerated("EnvAccel", 3, WordData.Survival, WordData.Dynamic, WordData.Change, WordData.Spur),
		EnvExplosions("EnvExplosions", 3, WordData.Survival, WordData.Sacrifice, WordData.Power, WordData.Force),
		EnvLightning("EnvLightning", 3, WordData.Survival, WordData.Sacrifice, WordData.Power, WordData.Energy),
		EnvMeteor("EnvMeteor", 3, WordData.Survival, WordData.Sacrifice, WordData.Power, WordData.Momentum),
		EnvScorched("EnvScorch", 3, WordData.Survival, WordData.Sacrifice, WordData.Power, WordData.Chaos),
		LightingBright("LightingBright", 3, WordData.Ethereal, WordData.Power, WordData.Infinite, WordData.Spur),
		LightingDark("LightingDark", 3, WordData.Ethereal, WordData.Void, WordData.Constraint, WordData.Inhibit),
		LightingNormal("LightingNormal", 2, WordData.Ethereal, WordData.Dynamic, WordData.Cycle, WordData.Balance),
		North("ModNorth", 0, WordData.Transform, WordData.Flow, WordData.Motion, WordData.Control),
		East("ModEast", 0, WordData.Transform, WordData.Flow, WordData.Motion, WordData.Tradition),
		South("ModSouth", 0, WordData.Transform, WordData.Flow, WordData.Motion, WordData.Chaos),
		West("ModWest", 0, WordData.Transform, WordData.Flow, WordData.Motion, WordData.Change),
		Clear("ModClear", 0, WordData.Contradict, WordData.Transform, WordData.Change, WordData.Void),
		Gradient("ModGradient", 0, WordData.Transform, WordData.Image, WordData.Merge, WordData.Weave),
		HorizonColor("ColorHorizon", 0, WordData.Transform, WordData.Image, WordData.Celestial, WordData.Change),
		ZeroLength("ModZero", 0, WordData.Transform, WordData.Time, WordData.System, WordData.Inhibit),
		HalfLength("ModHalf", 0, WordData.Transform, WordData.Time, WordData.System, WordData.Stimulate),
		FullLength("ModFull", 0, WordData.Transform, WordData.Time, WordData.System, WordData.Balance),
		DoubleLength("ModDouble",  0, WordData.Transform, WordData.Time, WordData.System, WordData.Sacrifice),
		NadirPhase("ModEnd", 0, WordData.Transform, WordData.Cycle, WordData.System, WordData.Rebirth),
		RisingPhase("ModRising", 0, WordData.Transform, WordData.Cycle, WordData.System, WordData.Growth),
		Noon("ModNoon", 0, WordData.Transform, WordData.Cycle, WordData.System, WordData.Harmony),
		SettingPhase("ModSetting", 0, WordData.Transform, WordData.Cycle, WordData.System, WordData.Future),
		Caves("Caves", 2, WordData.Terrain, WordData.Transform, WordData.Void, WordData.Flow),
		Dungeons("Dungeons", 2, WordData.Civilization, WordData.Constraint, WordData.Chain, WordData.Resurrect),
		FloatingIslands("FloatIslands", 3, WordData.Terrain, WordData.Transform, WordData.Form, WordData.Celestial),
		NoLargeFeature("FeatureLargeDummy", 0, 4, WordData.Contradict, WordData.Chaos, WordData.Exist, WordData.Terrain),
		NoMediumFeature("FeatureMediumDummy", 1000, 4, WordData.Contradict, WordData.Chaos, WordData.Exist, WordData.Balance),
		NoSmallFeature("FeatureSmallDummy", 2000, 5, WordData.Contradict, WordData.Chaos, WordData.Exist, WordData.Form),
		HugeTrees("HugeTrees", 2, WordData.Nature, WordData.Stimulate, WordData.Spur, WordData.Elevate),
		LakesDeep("LakesDeep", 3, WordData.Nature, WordData.Flow, WordData.Static, WordData.Explore),
		LakesSurface("LakesSurface", 3, WordData.Nature, WordData.Flow, WordData.Static, WordData.Elevate),
		Mineshafts("Mineshafts", 3, WordData.Civilization, WordData.Machine, WordData.Motion, WordData.Tradition),
		NetherFort("NetherFort", 3, WordData.Civilization, WordData.Machine, WordData.Power, WordData.Entropy),
		Obelisks("Obelisks", 3, WordData.Civilization, WordData.Resilience, WordData.Static, WordData.Form),
		Ravines("Ravines", 2, WordData.Terrain, WordData.Transform, WordData.Void, WordData.Weave),
		Spheres("TerModSpheres", 2, WordData.Terrain, WordData.Transform, WordData.Form, WordData.Cycle),
		Spikes("GenSpikes", 3, WordData.Nature, WordData.Encourage, WordData.Entropy, WordData.Static),
		Strongholds("Strongholds", 3, WordData.Civilization, WordData.Wisdom, WordData.Future, WordData.Honor),
		Tendrils("Tendrils", 3, WordData.Terrain, WordData.Transform, WordData.Growth, WordData.Flow),
		Villages("Villages", 3, WordData.Civilization, WordData.Society, WordData.Harmony, WordData.Nurture),
		CrystalFormation("CryForm", 3, WordData.Nature, WordData.Encourage, WordData.Growth, WordData.Static),
		Skylands("Skylands", 3, WordData.Terrain, WordData.Transform, WordData.Void, WordData.Elevate),
		StarFissure("StarFissure", 3, WordData.Nature, WordData.Harmony, WordData.Mutual, WordData.Void),
		DenseOres("DenseOres", 5, WordData.Survival, WordData.Stimulate, WordData.Machine, WordData.Chaos),
		WeatherAlways("WeatherOn", 3, WordData.Sustain, WordData.Static, WordData.Tradition, WordData.Stimulate),
		WeatherCloudy("WeatherCloudy", 3, WordData.Sustain, WordData.Static, WordData.Believe, WordData.Motion),
		WeatherFast("WeatherFast", 3, WordData.Sustain, WordData.Dynamic, WordData.Tradition, WordData.Spur),
		WeatherNormal("WeatherNorm", 2, WordData.Sustain, WordData.Dynamic, WordData.Tradition, WordData.Balance),
		WeatherOff("WeatherOff", 3, WordData.Sustain, WordData.Static, WordData.Stimulate, WordData.Energy),
		WeatherRain("WeatherRain", 3, WordData.Sustain, WordData.Static, WordData.Rebirth, WordData.Growth),
		WeatherSlow("WeatherSlow", 3, WordData.Sustain, WordData.Dynamic, WordData.Tradition, WordData.Inhibit),
		WeatherSnow("WeatherSnow", 3, WordData.Sustain, WordData.Static, WordData.Inhibit, WordData.Energy),
		WeatherStorm("WeatherStorm", 3, WordData.Sustain, WordData.Static, WordData.Nature, WordData.Power),
		TerrainGenAmplified("TerrainAmplified", 3, WordData.Terrain, WordData.Form, WordData.Tradition, WordData.Spur),
		TerrainGenEnd("TerrainEnd", 4, WordData.Terrain, WordData.Form, WordData.Ethereal, WordData.Flow),
		TerrainGenFlat("TerrainFlat", 3, WordData.Terrain, WordData.Form, WordData.Inhibit, WordData.Motion),
		TerrainGenNether("TerrainNether", 4, WordData.Terrain, WordData.Form, WordData.Constraint, WordData.Entropy),
		TerrainGenNormal("TerrainNormal", 2, WordData.Terrain, WordData.Form, WordData.Tradition, WordData.Flow),
		TerrainGenVoid("TerrainVoid", 4, WordData.Terrain, WordData.Form, WordData.Infinite, WordData.Void);

		public final String ID;
		public final String word1;
		public final String word2;
		public final String word3;
		public final String word4;
		public final int instability;

		private BasicPages(String id, int rank, String w1, String w2, String w3, String w4) {
			this(id, 0, rank, w1, w2, w3, w4);
		}

		private BasicPages(String id, int inst, int rank, String w1, String w2, String w3, String w4) {
			ID = id;
			word1 = w1;
			word2 = w2;
			word3 = w3;
			word4 = w4;
			instability = inst;
		}
	}

}
