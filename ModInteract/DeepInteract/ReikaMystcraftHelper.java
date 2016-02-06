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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.fluids.Fluid;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.ModInteract.ReikaTwilightHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ExtraUtilsHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.MystCraftHandler;

import com.xcompwiz.mystcraft.api.APIInstanceProvider;
import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.api.exception.APIUndefined;
import com.xcompwiz.mystcraft.api.exception.APIVersionRemoved;
import com.xcompwiz.mystcraft.api.exception.APIVersionUndefined;
import com.xcompwiz.mystcraft.api.hook.PageAPI;
import com.xcompwiz.mystcraft.api.hook.SymbolAPI;
import com.xcompwiz.mystcraft.api.hook.SymbolValuesAPI;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;

import cpw.mods.fml.common.event.FMLInterModComms;


public class ReikaMystcraftHelper {

	private static final Random rand = new Random();

	private static final HashMap<Integer, AgeInterface> ageData = new HashMap();

	private static final Method getTile;
	private static final Method getBook;
	private static final Method getLink;

	private static APIInstanceProvider apiProvider;

	public static void disableFluidPage(Fluid f) {
		FMLInterModComms.sendMessage(ModList.MYSTCRAFT.modLabel, "blacklistfluid", f.getName());
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
			ILinkInfo info = (ILinkInfo)getLink.invoke(book.getItem(), book);
			return info;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int getTargetDimensionIDFromPortalBlock(World world, int x, int y, int z) {
		ILinkInfo info = getPortalInfo(world, x, y, z);
		return info != null ? info.getDimensionUID() : Integer.MIN_VALUE;
	}

	public static boolean isMystAge(World world) {
		int id = world.provider.dimensionId;
		if (id == 0 || id == 1 || id == -1 || id == ReikaTwilightHelper.getDimensionID() || id == ExtraUtilsHandler.getInstance().darkID)
			return false;
		return world.provider.getClass().getSimpleName().equals("WorldProviderMyst");
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

	public static boolean isSymbolPresent(World world, String sym) {
		if (AgeInterface.loadedCorrectly && isMystAge(world)) {
			return getOrCreateInterface(world).symbolExists(sym);
		}
		return false;
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

		private static boolean loadedCorrectly;

		public final int dimensionID;
		private final WorldProvider provider;
		private Object ageController; //AgeController class
		private Object instabilityController; //InstabilityController
		private Object ageData; //AgeData class
		private HashSet<String> ageSymbols;

		private AgeInterface(World world) {
			if (!isMystAge(world))
				throw new IllegalArgumentException("Dimension "+world.provider.dimensionId+" is not a MystCraft age!");
			provider = world.provider;
			dimensionID = world.provider.dimensionId;
			try {
				ageController = age_controller.get(provider);
				instabilityController = instability_controller.get(ageController);
				ageData = data.get(ageController);
				ageSymbols = new HashSet((List<String>)symbolList.get(ageData));
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

		public boolean symbolExists(String s) {
			return ageSymbols.contains(s);
		}

		public boolean symbolExists(IAgeSymbol s) {
			return ageSymbols.contains(s.identifier());
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
			boolean load = true;
			if (ModList.MYSTCRAFT.isLoaded()) {
				try {
					Class prov = Class.forName("com.xcompwiz.mystcraft.world.WorldProviderMyst");
					cont = prov.getDeclaredField("controller");
					cont.setAccessible(true);
					Class age = Class.forName("com.xcompwiz.mystcraft.world.AgeController");
					insta = age.getDeclaredField("instabilityController");
					insta.setAccessible(true);
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
		}

	}

	public static ArrayList<IAgeSymbol> getAllSymbols() {
		ArrayList<IAgeSymbol> c = new ArrayList();
		SymbolAPI api = getAPI(APISegment.SYMBOL, 1);
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

	public static ItemStack getSymbolPage(IAgeSymbol a) {
		PageAPI api = getAPI(APISegment.PAGE, 1);
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

	public static IAgeSymbol getRandomPage() {
		ArrayList<IAgeSymbol> c = getAllSymbols();
		return c.get(rand.nextInt(c.size()));
	}

	/** Ranges from 0-1, lower is rarer; direct linear affect on page loot rarity */
	public static float getPageWeight(IAgeSymbol a) {
		SymbolValuesAPI api = getAPI(APISegment.SYMBOLVALUES, 1);
		if (api != null) {
			return api.getSymbolItemWeight(a.identifier());
		}
		return 0;
	}

	public static void setPageRank(IAgeSymbol a, int rank) {
		SymbolValuesAPI api = getAPI(APISegment.SYMBOLVALUES, 1);
		if (api != null) {
			api.setSymbolCardRank(a, rank);
		}
	}

	public static void setRandomAgeWeight(IAgeSymbol a, float weight) {
		//TODO
	}

	public static void registerAgeSymbol(IAgeSymbol a) {
		SymbolAPI api = getAPI(APISegment.SYMBOL, 1);
		if (api != null) {
			api.registerSymbol(a, false);
		}
	}

	@ModDependent(ModList.MYSTCRAFT)
	public static <A> A getAPI(APISegment type, int version) {
		try {
			return apiProvider != null ? (A)apiProvider.getAPIInstance(type.getTag(version)) : null;
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
	}

	public static enum APISegment {
		SYMBOL("symbol"), //SymbolAPI
		WORD("word"), //WordAPI
		GRAMMAR("grammar"), //GrammarAPI
		INSTABILITY("instability"), //InstabilityAPI
		LINKING("linking"), //LinkingAPI
		LINKPROPERTY("linkproperty"), //LinkPropertyAPI
		PAGE("page"), //PageAPI
		SYMBOLVALUES("symbolvalues"), //SymbolValuesAPI
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
	}

}
