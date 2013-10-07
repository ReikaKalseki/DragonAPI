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

import java.awt.image.BufferedImage;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.IO.ReikaPNGLoader;
import Reika.DragonAPI.IO.TextureBinder;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ReikaTextureHelper {

	private static final HashMap<String, Integer> textures = new HashMap();
	private static final HashMap<String, ResourceLocation> maps = new HashMap();

	public static final TextureBinder binder = new TextureBinder();

	private static final ResourceLocation font = new ResourceLocation("textures/font/ascii.png");

	public static void bindTexture(Class root, String tex) {
		if (root == null) {
			throw new MisuseException("You cannot fetch a render texture with reference to a null class!");
		}
		if (tex.startsWith("/Reika/")) {
			StringBuilder sb = new StringBuilder();
			String[] path = tex.split("/");
			for (int i = 2; i < path.length; i++) {
				sb.append("/");
				sb.append(path[i]);
			}
		}
		bindDirectTexture(root, tex);
	}

	public static void bindDirectTexture(Class root, String tex) {
		Integer gl = textures.get(tex);
		if (gl == null) {
			BufferedImage img = ReikaPNGLoader.readTextureImage(root, tex);
			gl = new Integer(binder.allocateAndSetupTexture(img));
			textures.put(tex, gl);
		}
		if (gl != null)
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gl.intValue());
	}

	public static void bindPackTexture(String tex) {
		if (tex.equals("/terrain.png")) {
			bindTerrainTexture();
			return;
		}
		else if (tex.equals("/gui/items.png")) {
			bindItemTexture();
			return;
		}
		else if (tex.equals("/font/glyph_AA.png")) {
			bindFontTexture();
			return;
		}
		else {
			bindTexture(tex);
		}
	}

	private static void bindTexture(String tex) {
		ResourceLocation loc = maps.get(tex);
		if (loc == null) {
			loc = new ResourceLocation(tex);
			maps.put(tex, loc);
		}
		Minecraft.getMinecraft().renderEngine.bindTexture(loc);
	}

	public static int getGLID(String texture) {
		Integer gl = textures.get(texture);
		if (gl == null) {
			return -1;
		}
		return gl.intValue();
	}

	public static void bindTerrainTexture() {
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
	}

	public static void bindFontTexture() {
		Minecraft.getMinecraft().renderEngine.bindTexture(font);
	}

	public static void bindItemTexture() {
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);
	}

	public static int getIconHeight() {
		return 16;
	}

}
