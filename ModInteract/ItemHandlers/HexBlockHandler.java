/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.ItemHandlers;

import java.lang.reflect.Field;

import com.celestek.hexcraft.api.HexColor;
import com.celestek.hexcraft.api.HexVariant;
import com.celestek.hexcraft.api.IBlockHexColorDynamic;
import com.celestek.hexcraft.api.IBlockHexColorSimple;
import com.celestek.hexcraft.api.IBlockHexVariantDynamic;
import com.celestek.hexcraft.api.IBlockHexVariantSimple;
import com.celestek.hexcraft.api.WorldGenColors;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class HexBlockHandler extends ModHandlerBase {

	private static final HexBlockHandler instance = new HexBlockHandler();

	private Class monolithBaseClass;
	private Class colorizedSimpleInterface;
	private Class variantSimpleInterface;

	public static enum BasicHexColors {
		RED,
		GREEN,
		BLUE,
		WHITE,
		BLACK,
		;

		private Item crystal;
		private WorldGenColors color;

		public WorldGenColors getValue() {
			return color;
		}

		public Item getCrystal() {
			return crystal;
		}
	}

	private HexBlockHandler() {
		super();

		if (this.hasMod()) {
			try {
				/*
				Class blocks = this.getMod().getBlockClass();
				Field ore = blocks.getField("MimichiteOre");
				idstone = ((Block)ore.get(null));
				 */

				WorldGenColors[] list = WorldGenColors.getList();
				Class items = this.getMod().getItemClass();
				for (BasicHexColors hex : BasicHexColors.values()) {
					Field item = items.getField("itemHexoriumCrystal"+ReikaStringParser.capFirstChar(hex.name()));
					item.setAccessible(true);
					hex.crystal = ((Item)item.get(null));

					hex.color = list[hex.ordinal()];
				}

				monolithBaseClass = Class.forName("com.celestek.hexcraft.block.BlockHexoriumMonolithBase");

				colorizedSimpleInterface = Class.forName("com.celestek.hexcraft.api.IBlockHexColorSimple");
				variantSimpleInterface = Class.forName("com.celestek.hexcraft.api.IBlockHexVariantSimple");
			}
			catch (ClassNotFoundException e) {
				DragonAPICore.logError(this.getMod()+" class not found! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (NoSuchFieldException e) {
				DragonAPICore.logError(this.getMod()+" field not found! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (SecurityException e) {
				DragonAPICore.logError("Cannot read "+this.getMod()+" (Security Exception)! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (IllegalArgumentException e) {
				DragonAPICore.logError("Illegal argument for reading "+this.getMod()+"!");
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (IllegalAccessException e) {
				DragonAPICore.logError("Illegal access exception for reading "+this.getMod()+"!");
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (NullPointerException e) {
				DragonAPICore.logError("Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
				e.printStackTrace();
				this.logFailure(e);
			}
		}
		else {
			this.noMod();
		}
	}

	public static HexBlockHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return monolithBaseClass != null && colorizedSimpleInterface != null && variantSimpleInterface != null;
	}

	@Override
	public ModList getMod() {
		return ModList.HEXCRAFT;
	}

	public boolean isMonolith(Block b) {
		return monolithBaseClass != null && monolithBaseClass.isAssignableFrom(b.getClass());
	}

	public boolean isColorized(Block b) {
		return colorizedSimpleInterface != null && colorizedSimpleInterface.isAssignableFrom(b.getClass());
	}

	public boolean hasVariants(Block b) {
		return variantSimpleInterface != null && variantSimpleInterface.isAssignableFrom(b.getClass());
	}

	@SideOnly(Side.CLIENT)
	@ModDependent(ModList.HEXCRAFT)
	public HexColor getColor(Block b, IBlockAccess iba, int x, int y, int z) {
		if (b instanceof IBlockHexColorDynamic) {
			return ((IBlockHexColorDynamic)b).getColor(iba, x, y, z);
		}
		else if (b instanceof IBlockHexColorSimple) {
			return ((IBlockHexColorSimple)b).getColor();
		}
		else {
			return null;
		}
	}

	@SideOnly(Side.CLIENT)
	@ModDependent(ModList.HEXCRAFT)
	public HexVariant getVariant(Block b, IBlockAccess iba, int x, int y, int z) {
		if (b instanceof IBlockHexVariantDynamic) {
			return ((IBlockHexVariantDynamic)b).getVariant(iba, x, y, z);
		}
		else if (b instanceof IBlockHexVariantSimple) {
			return ((IBlockHexVariantSimple)b).getVariant();
		}
		else {
			return null;
		}
	}

}
