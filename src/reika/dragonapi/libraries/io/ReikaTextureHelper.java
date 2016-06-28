/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.libraries.io;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackFileNotFoundException;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.exception.MisuseException;
import reika.dragonapi.instantiable.data.maps.PluralMap;
import reika.dragonapi.instantiable.event.client.TextureReloadEvent;
import reika.dragonapi.io.ReikaImageLoader;
import reika.dragonapi.io.ReikaTextureBinder;
import reika.dragonapi.io.ReikaImageLoader.ImageEditor;
import reika.dragonapi.libraries.java.ReikaJavaLibrary;
import reika.dragonapi.libraries.java.ReikaObfuscationHelper;
import reika.dragonapi.libraries.registry.ReikaDyeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ReikaTextureHelper {

	/** Keys: Class, Path */
	private static final PluralMap textures = new PluralMap(2);

	private static final HashMap<ReikaDyeHelper, Integer> colorOverrides = new HashMap();
	private static boolean noColorPacks = false;

	public static final TextureMap dummyTextureMap = new TextureMap(-1, "");

	public static final ReikaTextureBinder binder = new ReikaTextureBinder();

	public static final ResourceLocation font = new ResourceLocation("textures/font/ascii.png");
	private static final ResourceLocation particle = new ResourceLocation("textures/particle/particles.png");
	private static final ResourceLocation gui = new ResourceLocation("textures/gui/widgets.png");
	private static final ResourceLocation hud = new ResourceLocation("textures/gui/icons.png");
	private static final ResourceLocation ench = new ResourceLocation("textures/misc/enchanted_item_glint.png");

	private static boolean reload() {
		return Keyboard.isKeyDown(Keyboard.KEY_F3) && Keyboard.isKeyDown(Keyboard.KEY_T);
	}

	public static void bindTexture(Class root, String tex) {
		bindTexture(root, tex, null);
	}

	public static void bindTexture(Class root, String tex, ImageEditor img) {
		if (reload()) {
			textures.clear();
			colorOverrides.clear();
			MinecraftForge.EVENT_BUS.post(new TextureReloadEvent());
		}
		else {
			if (root == null) {
				throw new MisuseException("You cannot fetch a render texture with reference to a null class!");
			}
			String oldtex = tex;
			//String parent = root.getPackage().getName().replaceAll("\\.", "/")+"/";
			String s = root.getCanonicalName();
			String parent = s.substring(0, s.length()-root.getSimpleName().length()-1).replaceAll("\\.", "/")+"/";
			if (tex.startsWith("/"))
				tex = tex.substring(1);
			String respath = tex.startsWith(parent) ? tex : parent+tex;

			Integer gl = (Integer)textures.get(root, tex);
			if (gl == null) {
				ArrayList<IResourcePack> li = getCurrentResourcePacks();
				Collections.reverse(li); //because Mojang
				for (IResourcePack res : li) {
					gl = bindPackTexture(root, respath, res, img);
					if (gl != null) {
						textures.put(gl, root, tex);
						DragonAPICore.log("Texture Pack "+res.getPackName()+" contains an image for "+tex+".");
						break;
					}
				}
			}
			if (gl == null) {
				DragonAPICore.log("No texture packs contain an image for "+tex+". Loading default.");
				gl = bindClassReferencedTexture(root, oldtex, img);
				textures.put(gl, root, tex);
			}
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gl.intValue());
		}
	}

	/** To disallow resource packs to change it */
	public static void bindFinalTexture(Class root, String tex) {
		Integer gl = (Integer)textures.get(root, tex);
		if (gl == null) {
			gl = bindClassReferencedTexture(root, tex, null);
			textures.put(gl, root, tex);
		}
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gl.intValue());
	}

	private static Integer bindClassReferencedTexture(Class root, String tex, ImageEditor editor) {
		BufferedImage img = ReikaImageLoader.readImage(root, tex, editor);
		if (img == null) {
			DragonAPICore.logError("No image found for "+tex+"!");
			return new Integer(binder.allocateAndSetupTexture(ReikaImageLoader.getMissingTex()));
		}
		else {
			return new Integer(binder.allocateAndSetupTexture(img));
		}
	}

	public static void bindRawTexture(String tex) {
		Integer gl = (Integer)textures.get(null, tex);
		if (gl == null) {
			BufferedImage img = ReikaImageLoader.readHardPathImage(tex);
			if (img == null) {
				DragonAPICore.logError("No image found for "+tex+"!");
				gl = new Integer(binder.allocateAndSetupTexture(ReikaImageLoader.getMissingTex()));
				textures.put(gl, null, tex);
			}
			else {
				gl = new Integer(binder.allocateAndSetupTexture(img));
				textures.put(gl, null, tex);
			}
		}
		if (gl != null)
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gl.intValue());
	}

	private static Integer bindPackTexture(Class root, String tex, IResourcePack res, ImageEditor editor) {
		BufferedImage img = ReikaImageLoader.getImageFromResourcePack(tex, res, editor);
		return img != null ? new Integer(binder.allocateAndSetupTexture(img)) : null;
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

	public static void bindEnchantmentTexture() {
		Minecraft.getMinecraft().renderEngine.bindTexture(ench);
	}

	public static void bindHUDTexture() {
		Minecraft.getMinecraft().renderEngine.bindTexture(hud);
	}

	public static int getIconHeight() {
		return 16;
	}

	public static IIcon getMissingIcon() {
		TextureMap tex = (TextureMap)Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationBlocksTexture);
		return tex.getAtlasSprite("missingno");
	}

	/** Overrides the standard ResourceLocation system. Unfortunately not yet functional. *//*
	public static void forceArmorTexturePath(String tex) {
		DragonAPICore.log("DRAGONAPI: Disabling ResourceLocation on armor texture "+tex);
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
	}*/

	/** Returns a list of the selected resource packs, in the order they appear in the selection screen. */
	public static ArrayList<IResourcePack> getCurrentResourcePacks() {
		ArrayList<IResourcePack> packs = new ArrayList();
		List<ResourcePackRepository.Entry> li = Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries();
		for (int i = 0; i < li.size(); i++) {
			ResourcePackRepository.Entry e = li.get(i);
			packs.add(e.getResourcePack());
		}
		return packs;
	}

	public static boolean isDefaultResourcePack() {
		return Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries().isEmpty();
	}

	public static IResourcePack getDefaultResourcePack() {
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
			noColorPacks = false;
		}
		Integer color = colorOverrides.get(dye);
		if (color == null && !noColorPacks) {
			initializeColorOverrides();
			color = colorOverrides.get(dye);
		}
		return color != null ? color.intValue() : dye.getDefaultColor();
	}

	private static void initializeColorOverrides() {
		ArrayList<IResourcePack> li = getCurrentResourcePacks();
		boolean loaded = false;
		for (int k = 0; k < li.size(); k++) {
			AbstractResourcePack pack = (AbstractResourcePack)li.get(k);
			try {
				String path = "Reika/DragonAPI/dyecolor.txt";
				InputStream in = getStreamFromTexturePack(path, pack);
				if (in != null) {
					BufferedReader p = new BufferedReader(new InputStreamReader(in));
					for (int i = 0; i < 16; i++) {
						String line = p.readLine();
						String[] s = line.split(":");
						int c = Color.decode(s[1]).getRGB();
						Integer color = new Integer(c);
						ReikaDyeHelper dye = ReikaDyeHelper.dyes[i];
						colorOverrides.put(dye, color);
					}
					p.close();
					DragonAPICore.log("Found color override text file for texture pack "+pack.getPackName()+".");
					loaded = true;
				}
				else {
					DragonAPICore.log("No color override found for texture pack "+pack.getPackName()+".");
				}
			}
			catch (Exception e) {
				DragonAPICore.logError("Error reading color override text file for texture pack "+pack.getPackName()+".");
				e.printStackTrace();
			}
		}
		if (!loaded) {
			DragonAPICore.log("Could not find color override text file in any resource packs. Using defaults.");
			for (int i = 0; i < 16; i++) {
				ReikaDyeHelper dye = ReikaDyeHelper.dyes[i];
				int c = dye.getDefaultColor();
				Integer color = new Integer(c);
				colorOverrides.put(dye, color);
			}
			noColorPacks = true;
			return;
		}
	}

	public static void bindParticleTexture() {
		Minecraft.getMinecraft().renderEngine.bindTexture(particle);
	}

	public static int[][] getTextureData(IIcon ico, TextureMap map) {
		return map.getAtlasSprite(ico.getIconName()).getFrameTextureData(0);
	}

	public static void generateSpriteTextureData(String name, TextureMap map, int[][]... data) {
		TextureAtlasSprite tex = new GenSprite(name);
		tex.setFramesTextureData(ReikaJavaLibrary.makeListFrom(data));
		map.mapRegisteredSprites.put(name, tex);
	}

	private static class GenSprite extends TextureAtlasSprite {

		protected GenSprite(String s) {
			super(s);
		}

	}

}
