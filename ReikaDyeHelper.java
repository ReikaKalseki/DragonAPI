package Reika.DragonAPI;

import java.awt.Color;

import net.minecraft.block.Block;
import net.minecraft.item.*;

public enum ReikaDyeHelper {

	BLACK(0x191919),
	RED(0xCC4C4C),
	GREEN(0x667F33),
	BROWN(0x7F664C),
	BLUE(0x3366CC),
	PURPLE(0xB266E5),
	CYAN(0x4C99B2),
	LIGHTGRAY(0x999999),
	GRAY(0x4C4C4C),
	PINK(0xF2B2CC),
	LIME(0x7FCC19),
	YELLOW(0xE5E533),
	LIGHTBLUE(0x99B2F2),
	MAGNETA(0xE57FD8),
	ORANGE(0xF2B233),
	WHITE(0xFFFFFF);

	private int color;

	public static final ReikaDyeHelper[] dyes = ReikaDyeHelper.values();

	private ReikaDyeHelper(int c) {
		color = c;
	}

	public static ReikaDyeHelper getColorFromDamage(int damage) {
		return dyes[damage];
	}

	public int getDamage() {
		return this.ordinal();
	}

	public int getColor() {
		return color;
	}

	public Color getJavaColor() {
		return Color.decode(String.valueOf(color));
	}

	public int getRed() {
		return this.getJavaColor().getRed();
	}

	public int getBlue() {
		return this.getJavaColor().getBlue();
	}

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

}
