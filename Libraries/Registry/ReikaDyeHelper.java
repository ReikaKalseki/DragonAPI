/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Registry;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum ReikaDyeHelper {

	BLACK(0x191919),
	RED(0x993333),
	GREEN(0x667F33),
	BROWN(0x664C33),
	BLUE(0x334CB2),
	PURPLE(0x7F3FB2),
	CYAN(0x4C7F99),
	LIGHTGRAY(0x999999, "Light Gray"),
	GRAY(0x4C4C4C),
	PINK(0xF27FA5),
	LIME(0x7FCC19),
	YELLOW(0xE5E533),
	LIGHTBLUE(0x6699D8, "Light Blue"),
	MAGENTA(0xB24CD8),
	ORANGE(0xD87F33),
	WHITE(0xFFFFFF);

	public final int color;
	public final String colorName;
	public final String colorNameNoSpaces;
	private static final Random rand = new Random();

	public static final ReikaDyeHelper[] dyes = ReikaDyeHelper.values();
	private static final HashMap<ReikaDyeHelper, ArrayList<ItemStack>> oreDict = new HashMap();
	private static final HashMap<List<Integer>, ReikaDyeHelper> oreDict2 = new HashMap();

	private ReikaDyeHelper(int c) {
		color = c;
		colorName = ReikaStringParser.capFirstChar(this.name());
		colorNameNoSpaces = ReikaStringParser.stripSpaces(colorName);
	}

	private ReikaDyeHelper(int c, String n) {
		color = c;
		colorName = n;
		colorNameNoSpaces = ReikaStringParser.stripSpaces(n);
	}

	public static boolean isDyeItem(ItemStack is) {
		return getColorFromItem(is) != null;
	}

	public static ReikaDyeHelper getColorFromItem(ItemStack is) {
		if (is == null)
			return null;
		if (is.itemID == Item.dyePowder.itemID)
			return getColorFromDamage(is.getItemDamage());
		return getDyeByOreDictionary(is);
	}

	private static ReikaDyeHelper getDyeByOreDictionary(ItemStack is) {
		List<Integer> key = Arrays.asList(is.itemID, is.getItemDamage());
		ReikaDyeHelper color = oreDict2.get(key);
		if (color != null)
			return color;
		for (int i = 0; i < dyes.length; i++) {
			ReikaDyeHelper dye = dyes[i];
			String name = dye.getOreDictName();
			ArrayList<ItemStack> li = OreDictionary.getOres(name);
			if (ReikaItemHelper.listContainsItemStack(li, is)) {
				addItemMapping(dye, is);
				return dye;
			}
		}
		return null;
	}

	private static void addItemMapping(ReikaDyeHelper dye, ItemStack is) {
		ArrayList<ItemStack> li = oreDict.get(dye);
		if (li == null) {
			li = new ArrayList();
			oreDict.put(dye, li);
		}
		li.add(is);
		List<Integer> key = Arrays.asList(is.itemID, is.getItemDamage());
		oreDict2.put(key, dye);
	}

	public static ReikaDyeHelper getColorFromDamage(int damage) {
		return damage >= 0 && damage < dyes.length ? dyes[damage] : BLACK;
	}

	public static ReikaDyeHelper getRandomColor() {
		return getColorFromDamage(rand.nextInt(16));
	}

	public int getDamage() {
		return this.ordinal();
	}

	@SideOnly(Side.CLIENT)
	public int getColor() {
		return ReikaTextureHelper.isDefaultResourcePack() ? color : this.getColorOverride();
	}

	@SideOnly(Side.CLIENT)
	public int getDefaultColor() {
		return color;
	}

	@SideOnly(Side.CLIENT)
	private int getColorOverride() {
		return ReikaTextureHelper.getColorOverride(this);
	}

	@SideOnly(Side.CLIENT)
	public Color getJavaColor() {
		return Color.decode(String.valueOf(this.getColor()));
	}

	@SideOnly(Side.CLIENT)
	public int getRed() {
		return this.getJavaColor().getRed();
	}

	@SideOnly(Side.CLIENT)
	public int getBlue() {
		return this.getJavaColor().getBlue();
	}

	@SideOnly(Side.CLIENT)
	public int getGreen() {
		return this.getJavaColor().getGreen();
	}

	public int getWoolMeta() {
		return 15-this.getDamage();
	}

	public ItemStack getStackOf() {
		return new ItemStack(Item.dyePowder.itemID, 1, this.getDamage());
	}

	public ItemStack getWoolStack() {
		return new ItemStack(Block.cloth.blockID, 1, this.getWoolMeta());
	}

	@SideOnly(Side.CLIENT)
	public double[] getRedstoneParticleVelocityForColor() {
		if (this == WHITE)
			return new double[]{20,20,20};
		double[] c = new double[]{this.getRed()/255D, this.getGreen()/255D, this.getBlue()/255D};
		return c;
	}

	public String getOreDictName() {
		return "dye"+ReikaStringParser.stripSpaces(colorName);
	}

	@SideOnly(Side.CLIENT)
	public void setGLColorBlend() {
		Color c = this.getJavaColor();
		GL11.glColor3d(this.getRed()/255D, this.getGreen()/255D, this.getBlue()/255D);
	}
}
