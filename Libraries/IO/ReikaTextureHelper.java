/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.IO;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.texturepacks.ITexturePack;
import net.minecraft.client.texturepacks.TexturePackList;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ReikaTextureHelper {

	public static ITexturePack getVanillaTexturePack() {
		Minecraft mc = Minecraft.getMinecraft();
		TexturePackList tx = mc.texturePackList;
		List<ITexturePack> li = tx.availableTexturePacks();
		for (int i = 0; i < li.size(); i++) {
			if (isVanillaTexturePack(li.get(i)))
				return li.get(i);
		}
		return null; //never happens
	}

	public static boolean isVanillaTexturePack(ITexturePack ip) {
		return "default".equals(ip.getTexturePackID());
	}

	public static boolean isUsingDefaultTexturePack() {
		return isVanillaTexturePack(Minecraft.getMinecraft().texturePackList.getSelectedTexturePack());
	}

	public static ITexturePack getCurrentTexturePack() {
		return (Minecraft.getMinecraft().texturePackList.getSelectedTexturePack());
	}

	public static int getIconWidth(Icon ico) {
		return (int)Math.ceil(ico.getSheetWidth()*(ico.getMaxU()-ico.getMinU()));
	}

	public static int getIconHeight(Icon ico) {
		return (int)Math.ceil(ico.getSheetHeight()*(ico.getMaxV()-ico.getMinV()));
	}

}
