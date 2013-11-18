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
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.ResourcePack;
import net.minecraft.client.resources.ResourcePackFileNotFoundException;
import net.minecraft.client.resources.ResourcePackRepositoryEntry;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.IO.ReikaImageLoader;
import Reika.DragonAPI.IO.ReikaTextureBinder;
import Reika.DragonAPI.Instantiable.ForcedResource;
import Reika.DragonAPI.Instantiable.PluralMap;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ReikaTextureHelper {

	/** Keys: Resource Pack, Path */
	private static final PluralMap textures = new PluralMap(2);
	private static final HashMap<String, ResourceLocation> maps = new HashMap();

	public static final ReikaTextureBinder binder = new ReikaTextureBinder();

	private static final ResourceLocation font = new ResourceLocation("textures/font/ascii.png");

	public static void bindTexture(Class root, String tex) {
		if (root == null) {
			throw new MisuseException("You cannot fetch a render texture with reference to a null class!");
		}
		String parent = root.getPackage().getName().replaceAll("\\.", "/")+"/";
		ResourcePack res = getCurrentResourcePack();
		if (res.equals(getDefaultResourcePack()))
			bindClassReferencedTexture(root, tex);
		else {
			if (tex.startsWith("/"))
				tex = tex.substring(1);
			String respath = tex.startsWith(parent) ? tex : parent+tex;
			boolean hasTex = bindPackTexture(respath, res);
			if (!hasTex)
				bindClassReferencedTexture(root, tex);
		}
	}

	/** To disallow resource packs to change it */
	public static void bindFinalTexture(Class root, String tex) {
		bindClassReferencedTexture(root, tex);
	}

	private static void bindClassReferencedTexture(Class root, String tex) {
		ResourcePack def = getDefaultResourcePack();
		Integer gl = (Integer) textures.get(def, tex);
		if (gl == null) {
			BufferedImage img = ReikaImageLoader.readImage(root, tex);
			gl = new Integer(binder.allocateAndSetupTexture(img));
			textures.put(gl, def, tex);
		}
		if (gl != null)
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gl.intValue());
	}

	public static void bindRawTexture(String tex) {
		ResourcePack def = getDefaultResourcePack();
		Integer gl = (Integer) textures.get(def, tex);
		if (gl == null) {
			BufferedImage img = ReikaImageLoader.readHardPathImage(tex);
			gl = new Integer(binder.allocateAndSetupTexture(img));
			textures.put(gl, def, tex);
		}
		if (gl != null)
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gl.intValue());
	}

	public static boolean bindPackTexture(String tex, ResourcePack res) {
		Integer gl = (Integer) textures.get(res, tex);
		boolean hasTex = false;
		if (gl == null) {
			BufferedImage img = ReikaImageLoader.getImageFromResourcePack(tex, res);
			hasTex = !ReikaImageLoader.missingtex.equals(img);
			gl = new Integer(binder.allocateAndSetupTexture(img));
			textures.put(gl, res, tex);
		}
		if (gl != null) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gl.intValue());
		}
		//return gl != null && hasTex;
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

	public static Icon getMissingIcon() {
		return ((TextureMap)Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationBlocksTexture)).getAtlasSprite("missingno");
	}

	/** Overrides the standard ResourceLocation system. Unfortunately not yet functional. */
	public static void forceArmorTexturePath(String tex) {
		ReikaJavaLibrary.pConsole("DRAGONAPI: Disabling ResourceLocation on armor texture "+tex);
		ForcedResource f = new ForcedResource(tex);
		Map map = getArmorTextureMappings();
		map.put(tex, f);
	}

	private static Map getArmorTextureMappings() {
		try {
			Class c = RenderBiped.class;
			Field f = c.getDeclaredField(DragonAPICore.isDeObfEnvironment() ? "field_110859_k" : "field_110859_k");
			f.setAccessible(true);
			return (Map)f.get(null);
		}
		catch (NoSuchFieldException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not load the Armor Textures!");
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not load the Armor Textures!");
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not load the Armor Textures!");
		}
		catch (SecurityException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not load the Armor Textures!");
		}
	}

	/** Returns the filename, including .zip if applicable, unless it is default, whereupon it returns "Default". */
	public static String getCurrentResourcePackFileName() {
		//return Minecraft.getMinecraft().gameSettings.skin;
		return Minecraft.getMinecraft().getResourcePackRepository().getResourcePackName();
	}

	/** Returns the display name */
	public static String getCurrentResourcePackName() {
		return getCurrentResourcePack().getPackName().replaceAll(".zip", "");
	}

	/** Requires an iteration over all loaded packs! Do NOT call this every tick or render tick! */
	public static ResourcePack getCurrentResourcePack() {
		List li = Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries();
		String skin = Minecraft.getMinecraft().gameSettings.skin;
		for (int i = 0; i < li.size(); i++) {
			ResourcePackRepositoryEntry e = (ResourcePackRepositoryEntry)li.get(i);
			if (e.getResourcePackName().equals(skin)) {
				return e.getResourcePack();
			}
		}
		return getDefaultResourcePack();
	}

	public static ResourcePack getDefaultResourcePack() {
		return Minecraft.getMinecraft().getResourcePackRepository().rprDefaultResourcePack;
	}

	public static InputStream getStreamFromTexturePack(String path, AbstractResourcePack pack) {
		Class c = pack.getClass();
		String sg = DragonAPICore.isDeObfEnvironment() ? "getInputStreamByName" : "func_110591_a";
		try {
			Method m = c.getDeclaredMethod(sg, String.class);
			m.setAccessible(true);
			Object o = m.invoke(pack, path);
			if (o == null)
				return null;
			InputStream in = (InputStream)o;
			m.setAccessible(false);
			return in;
		}
		catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		catch (SecurityException e) {
			e.printStackTrace();
		}
		catch (InvocationTargetException e) {
			if (e.getCause() instanceof ResourcePackFileNotFoundException) {

			}
			else
				e.printStackTrace();
		}
		return null;
	}

}
