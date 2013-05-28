/*******************************************************************************
 * @author Reika
 * 
 * This code is the property of and owned and copyrighted by Reika.
 * This code is provided under a modified visible-source license that is as follows:
 * 
 * Any and all users are permitted to use the source for educational purposes, or to create other mods that call
 * parts of this code and use DragonAPI as a dependency.
 * 
 * Unless given explicit written permission - electronic writing is acceptable - no user may redistribute this
 * source code nor any derivative works. These pre-approved works must prominently contain this copyright notice.
 * 
 * Additionally, no attempt may be made to achieve monetary gain from this code by anyone except the original author.
 * In the case of pre-approved derivative works, any monetary gains made will be shared between the original author
 * and the other developer(s), proportional to the ratio of derived to original code.
 * 
 * Finally, any and all displays, duplicates or derivatives of this code must be prominently marked as such, and must
 * contain attribution to the original author, including a link to the original source. Any attempts to claim credit
 * for this code will be treated as intentional theft.
 * 
 * Due to the Mojang and Minecraft Mod Terms of Service and Licensing Restrictions, compiled versions of this code
 * must be provided for free. However, with the exception of pre-approved derivative works, only the original author
 * may distribute compiled binary versions of this code.
 * 
 * Failure to comply with these restrictions is a violation of copyright law and will be dealt with accordingly.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.awt.Color;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
