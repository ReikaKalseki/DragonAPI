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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.IO.ReikaImageLoader;
import Reika.DragonAPI.IO.ReikaTextureBinder;
import Reika.DragonAPI.Instantiable.Data.PluralMap;
import Reika.DragonAPI.Instantiable.IO.ForcedResource;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ReikaTextureHelper {

	/** Keys: Resource Pack, Path */
	private static final PluralMap textures = new PluralMap(2);
	private static final PluralMap packTextures = new PluralMap(2);

	private static final PluralMap<Integer> colorOverrides = new PluralMap(2);

	public static final ReikaTextureBinder binder = new ReikaTextureBinder();

	private static final ResourceLocation font = new ResourceLocation("textures/font/ascii.png");
	private static final ResourceLocation particle = new ResourceLocation("textures/particle/particles.png");
	private static final ResourceLocation gui = new ResourceLocation("textures/gui/widgets.png");
	private static final ResourceLocation hud = new ResourceLocation("textures/gui/icons.png");

	private static boolean reload() {
		return Keyboard.isKeyDown(Keyboard.KEY_F3) && Keyboard.isKeyDown(Keyboard.KEY_T);
	}

	public static void bindTexture(Class root, String tex) {
		if (reload()) {
			textures.clear();
			packTextures.clear();
		}
		else {
			if (root == null) {
				throw new MisuseException("You cannot fetch a render texture with reference to a null class!");
			}
			String oldtex = tex;
			//String parent = root.getPackage().getName().replaceAll("\\.", "/")+"/";
			String s = root.getCanonicalName();
			String parent = s.substring(0, s.length()-root.getSimpleName().length()-1).replaceAll("\\.", "/")+"/";
			ResourcePack res = getCurrentResourcePack();
			if (isDefaultResourcePack())
				bindClassReferencedTexture(root, tex);
			else {
				if (tex.startsWith("/"))
					tex = tex.substring(1);
				String respath = tex.startsWith(parent) ? tex : parent+tex;

				Boolean flag = (Boolean)packTextures.get(res, tex);
				if (flag == null || flag.booleanValue()) {
					boolean hasTex = bindPackTexture(respath, res);
					packTextures.put(hasTex, res, tex);
					if (!hasTex)
						bindClassReferencedTexture(root, oldtex);
				}
				else {
					bindClassReferencedTexture(root, oldtex);
				}
			}
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
			if (img == null) {
				ReikaJavaLibrary.pConsole("No image found for "+tex+"!");
				gl = new Integer(binder.allocateAndSetupTexture(ReikaImageLoader.getMissingTex()));
				textures.put(gl, def, tex);
			}
			else {
				gl = new Integer(binder.allocateAndSetupTexture(img));
				textures.put(gl, def, tex);
			}
		}
		if (gl != null)
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gl.intValue());
	}

	public static void bindRawTexture(String tex) {
		ResourcePack def = getDefaultResourcePack();
		Integer gl = (Integer) textures.get(def, tex);
		if (gl == null) {
			BufferedImage img = ReikaImageLoader.readHardPathImage(tex);
			if (img == null) {
				ReikaJavaLibrary.pConsole("No image found for "+tex+"!");
				gl = new Integer(binder.allocateAndSetupTexture(ReikaImageLoader.getMissingTex()));
				textures.put(gl, def, tex);
			}
			else {
				gl = new Integer(binder.allocateAndSetupTexture(img));
				textures.put(gl, def, tex);
			}
		}
		if (gl != null)
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gl.intValue());
	}

	public static boolean bindPackTexture(String tex, ResourcePack res) {
		Integer gl = (Integer) textures.get(res, tex);
		if (gl == null) {
			BufferedImage img = ReikaImageLoader.getImageFromResourcePack(tex, res);
			if (img == null) {
				return false;
			}
			gl = new Integer(binder.allocateAndSetupTexture(img));
			textures.put(gl, res, tex);
		}
		if (gl != null) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gl.intValue());
		}
		return gl != null;
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

	public static void bindGuiTexture() {
		Minecraft.getMinecraft().renderEngine.bindTexture(gui);
	}

	public static void bindHUDTexture() {
		Minecraft.getMinecraft().renderEngine.bindTexture(hud);
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
			Field f = ReikaObfuscationHelper.getField("field_110859_k");
			f.setAccessible(true);
			return (Map)f.get(null);
		}
		catch (IllegalAccessException e) {
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

	public static boolean isDefaultResourcePack() {
		return getCurrentResourcePack().equals(getDefaultResourcePack());
	}

	public static ResourcePack getDefaultResourcePack() {
		return Minecraft.getMinecraft().getResourcePackRepository().rprDefaultResourcePack;
	}

	public static InputStream getStreamFromTexturePack(String path, AbstractResourcePack pack) {
		try {
			Method m = ReikaObfuscationHelper.getMethod("getInputStreamByName");
			//m.setAccessible(true);
			Object o = m.invoke(pack, path);
			if (o == null)
				return null;
			InputStream in = (InputStream)o;
			//m.setAccessible(false);
			return in;
		}
		catch (IllegalAccessException e) {
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

	public static int getColorOverride(ReikaDyeHelper dye) {
		if (reload()) {
			colorOverrides.clear();
		}
		ResourcePack cur = getCurrentResourcePack();
		Integer color = colorOverrides.get(dye, cur);
		if (color == null) {
			initializeColorOverrides((AbstractResourcePack)cur);
			color = colorOverrides.get(dye, cur);
		}
		return color != null ? color.intValue() : dye.getDefaultColor();
	}

	private static void initializeColorOverrides(AbstractResourcePack pack) {
		try {
			String path = "Reika/DragonAPI/dyecolor.txt";
			InputStream in = getStreamFromTexturePack(path, pack);
			if (in == null) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Could not find color override text file. Using defaults.");
				for (int i = 0; i < 16; i++) {
					ReikaDyeHelper dye = ReikaDyeHelper.dyes[i];
					int c = dye.getDefaultColor();
					Integer color = new Integer(c);
					colorOverrides.put(color, dye, pack);
				}
				return;
			}
			BufferedReader p = new BufferedReader(new InputStreamReader(in));
			for (int i = 0; i < 16; i++) {
				String line = p.readLine();
				String[] s = line.split(":");
				int c = Color.decode(s[1]).getRGB();
				Integer color = new Integer(c);
				ReikaDyeHelper dye = ReikaDyeHelper.dyes[i];
				colorOverrides.put(color, dye, pack);
			}
			p.close();
			ReikaJavaLibrary.pConsole("DRAGONAPI: Found color override text file for texture pack "+getCurrentResourcePackName()+".");
		}
		catch (Exception e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Error reading color override text file for texture pack "+getCurrentResourcePackName()+".");
			e.printStackTrace();
		}
	}

	public static void bindParticleTexture() {
		Minecraft.getMinecraft().renderEngine.bindTexture(particle);
	}

}
