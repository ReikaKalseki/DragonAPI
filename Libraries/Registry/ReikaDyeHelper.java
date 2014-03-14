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

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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

	public static final ReikaDyeHelper[] dyes = ReikaDyeHelper.values();

	private ReikaDyeHelper(int c) {
		color = c;
		colorName = ReikaStringParser.capFirstChar(this.name());
	}

	private ReikaDyeHelper(int c, String n) {
		color = c;
		colorName = n;
	}

	public static boolean isDyeItem(ItemStack is) {
		return is != null && is.itemID == Item.dyePowder.itemID;
	}

	public static ReikaDyeHelper getColorFromDamage(int damage) {
		return damage >= 0 && damage < dyes.length ? dyes[damage] : BLACK;
	}

	public static ReikaDyeHelper getColorFromItem(ItemStack is) {
		return getColorFromDamage(is.getItemDamage());
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
