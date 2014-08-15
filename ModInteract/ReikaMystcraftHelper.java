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

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;

import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;

import cpw.mods.fml.common.event.FMLInterModComms;


public class ReikaMystcraftHelper {

	private static final HashMap<Integer, InstabilityInterface> ageData = new HashMap();

	private static final Field controller;
	private static final Field instability;
	private static final Field stabilization;
	private static final Field data;
	private static final Field instabilityNumber;
	private static final Field baseInstability;

	private static final Method getTile;
	private static final Method getBook;
	private static final Method getLink;

	public static final boolean loadedCorrectly;

	public static void disableFluidPage(String name) {
		NBTTagCompound NBTMsg = new NBTTagCompound();
		NBTMsg.setTag("fluidsymbol", new NBTTagCompound());
		NBTMsg.getCompoundTag("fluidsymbol").setFloat("rarity", 0.0F);
		NBTMsg.getCompoundTag("fluidsymbol").setFloat("grammarweight", 0.0F);
		NBTMsg.getCompoundTag("fluidsymbol").setFloat("instabilityPerBlock", Float.MAX_VALUE);
		NBTMsg.getCompoundTag("fluidsymbol").setString("fluidname", name);
		FMLInterModComms.sendMessage("Mystcraft", "fluidsymbol", NBTMsg);
	}

	private static ILinkInfo getPortalInfo(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (b != MystObjects.portal)
			return null;
		try {
			TileEntity te = (TileEntity)getTile.invoke(MystObjects.portal, world, x, y, z);
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

	public static int getStabilityForAge(World world) {
		if (!loadedCorrectly)
			return 0;
		return isMystAge(world) ? getOrCreateInterface(world).getStabilizationParameter() : 0;
	}

	public static int getInstabilityForAge(World world) {
		if (!loadedCorrectly)
			return 0;
		return isMystAge(world) ? getOrCreateInterface(world).getTotalInstability() : 0;
	}

	public static int getBonusInstabilityForAge(World world) {
		if (!loadedCorrectly)
			return 0;
		return isMystAge(world) ? getOrCreateInterface(world).getBonusInstability() : 0;
	}

	public static int getBaseInstabilityForAge(World world) {
		if (!loadedCorrectly)
			return 0;
		return isMystAge(world) ? getOrCreateInterface(world).getBaseInstability() : 0;
	}

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

	public static boolean addBaseInstabilityForAge(World world, short toAdd) {
		if (!loadedCorrectly)
			return false;
		if (isMystAge(world)) {
			InstabilityInterface ii = getOrCreateInterface(world);
			short unstable = ii.getBaseInstability();
			short newunstable = (short)(unstable+toAdd);
			return ii.setBaseInstability(newunstable);
		}
		else {
			return false;
		}
	}

	public static boolean addBonusInstabilityForAge(World world, int toAdd) {
		if (!loadedCorrectly)
			return false;
		if (isMystAge(world)) {
			InstabilityInterface ii = getOrCreateInterface(world);
			int unstable = ii.getBonusInstability();
			int newunstable = unstable+toAdd;
			return ii.setBonusInstability(newunstable);
		}
		else {
			return false;
		}
	}

	private static InstabilityInterface getOrCreateInterface(World world) {
		if (!loadedCorrectly)
			return null;
		InstabilityInterface ii = ageData.get(world.provider.dimensionId);
		if (ii == null) {
			ii = new InstabilityInterface(world);
			ageData.put(world.provider.dimensionId, ii);
		}
		return ii;
	}

	private static final class InstabilityInterface {

		public final int dimensionID;
		private final WorldProvider provider;
		private Object ageController; //AgeController class
		private Object instabilityController; //InstabilityController
		private Object ageData; //AgeData class

		InstabilityInterface(World world) {
			if (!isMystAge(world))
				throw new IllegalArgumentException("Dimension "+world.provider.dimensionId+" is not a MystCraft age!");
			provider = world.provider;
			dimensionID = world.provider.dimensionId;
			try {
				ageController = controller.get(provider);
				instabilityController = instability.get(ageController);
				ageData = data.get(ageController);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		public int getStabilizationParameter() {
			try {
				return stabilization.getInt(instabilityController);
			}
			catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}

		public boolean setStabilization(int stable) {
			try {
				stabilization.set(instabilityController, stable);
				return true;
			}
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		public boolean setBonusInstability(int amount) {
			try {
				instabilityNumber.set(ageController, amount);
				return true;
			}
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		public boolean setBaseInstability(int amount) {
			try {
				baseInstability.set(instabilityController, amount);
				return true;
			}
			catch (Exception e) {
				e.printStackTrace();
				return false;
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

		public int getBonusInstability() {
			try {
				return instabilityNumber.getInt(ageController);
			}
			catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}

		public int getTotalInstability() {
			return this.getBaseInstability()+this.getBonusInstability();
		}

		public boolean addBaseInstability(short amount) {
			short current = this.getBaseInstability();
			short newshort = (short)(current+amount);
			if (newshort < 0)
				newshort = 0;
			try {
				baseInstability.set(ageData, newshort);
				return true;
			}
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		public boolean addBonusInstability(int amount) {
			int current = this.getBonusInstability();
			try {
				instabilityNumber.set(ageController, current+amount);
				return true;
			}
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

	}

	static {
		Field cont = null;
		Field insta = null;
		Field stable = null;
		Field num = null;
		Field base = null;
		Field adata = null;
		Method tile = null;
		Method book = null;
		Method link = null;
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
				stable = controller.getDeclaredField("stabilization");
				stable.setAccessible(true);
				num = age.getDeclaredField("instability");
				num.setAccessible(true);
				Class data = Class.forName("com.xcompwiz.mystcraft.world.agedata.AgeData");
				base = data.getDeclaredField("instability");
				base.setAccessible(true);
				adata = age.getDeclaredField("agedata");
				adata.setAccessible(true);
				Class portal = Class.forName("com.xcompwiz.mystcraft.Blocks.BlockBookReceptacle");
				tile = portal.getDeclaredMethod("getTileEntity", IBlockAccess.class, int.class, int.class, int.class);
				tile.setAccessible(true);
				Class booktile = Class.forName("com.xcompwiz.mystcraft.tileentity.TileEntityBook");
				book = booktile.getDeclaredMethod("getBook");
				book.setAccessible(true);
				Class item = Class.forName("com.xcompwiz.mystcraft.Items.ItemLinking");
				link = item.getDeclaredMethod("getLinkInfo", ItemStack.class);
				link.setAccessible(true);
			}
			catch (Exception e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading Mystcraft instability interfacing!");
				e.printStackTrace();
				load = false;
			}
		}
		else {
			load = false;
		}
		controller = cont;
		instability = insta;
		stabilization = stable;
		instabilityNumber = num;
		baseInstability = base;
		data = adata;
		getTile = tile;
		getBook = book;
		getLink = link;
		loadedCorrectly = load;
	}

}