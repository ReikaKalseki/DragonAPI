/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.io;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.common.MinecraftForge;
import reika.dragonapi.exception.MisuseException;
import reika.dragonapi.instantiable.rendering.BasicFontRenderer;
import reika.dragonapi.libraries.io.ReikaTextureHelper;
import com.google.common.collect.HashBiMap;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class DelegateFontRenderer extends FontRenderer {

	private final FontRenderer fallback;
	private final HashBiMap<String, BasicFontRenderer> renderers = HashBiMap.create();
	private static int currentID = 512; //char 512; everything from here is a foreign-language char or diacritic
	private static final int maxID = 0xFFFF;
	private static final char keyChar = '\uFFFC';

	public DelegateFontRenderer(FontRenderer fr) {
		super(Minecraft.getMinecraft().gameSettings, ReikaTextureHelper.font, Minecraft.getMinecraft().renderEngine, false);
		fallback = fr;
		this.setUnicodeFlag(fr.getUnicodeFlag());
		this.setBidiFlag(fr.getBidiFlag());
		((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
	}

	@Override
	public void setUnicodeFlag(boolean flag) {
		super.setUnicodeFlag(flag);
		fallback.setUnicodeFlag(flag);
	}

	@Override
	public void setBidiFlag(boolean flag) {
		super.setBidiFlag(flag);
		fallback.setBidiFlag(flag);
	}

	public String addRenderer(BasicFontRenderer f) {
		if (currentID >= maxID) {
			throw new MisuseException("Delegate Font Renderer has run out of IDs! All "+maxID+" IDs occupied!");
		}
		String id = keyChar+String.valueOf((char)currentID);
		renderers.put(id, f);
		((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(f);
		MinecraftForge.EVENT_BUS.register(f);
		currentID++;
		return id;
	}

	@Override
	public int drawString(String sg, int x, int y, int color, boolean shadow) {
		FontKey f = this.getRenderer(sg);
		if (f != null)
			return f.renderer.drawString(f.text, x, y, color, shadow);
		else
			return fallback.drawString(sg, x, y, color, shadow);
	}

	@Override
	public void drawSplitString(String sg, int x, int y, int space, int color) {
		FontKey f = this.getRenderer(sg);
		if (f != null)
			f.renderer.drawSplitString(f.text, x, y, space, color);
		else
			fallback.drawSplitString(sg, x, y, space, color);
	}

	private FontKey getRenderer(String sg) {
		if (sg != null && sg.length() > 2) {
			int index = sg.indexOf(keyChar);
			//ReikaJavaLibrary.pConsole(index+"/"+sg.length()+"@"+sg, index >= 0);
			if (index >= 0 && index < sg.length()-1) {
				String key = sg.substring(index, index+2);
				BasicFontRenderer f = renderers.get(key);
				if (f != null) {
					return new FontKey(f, sg.substring(0, index)+sg.substring(index+2, sg.length()));
				}
			}
		}
		return null;
	}

	private static class FontKey {

		private final BasicFontRenderer renderer;
		private final String text;

		private FontKey(BasicFontRenderer f, String s) {
			renderer = f;
			text = s;
		}

	}

	@Override
	public void onResourceManagerReload(IResourceManager irm) {
		/*
		fallback.onResourceManagerReload(irm);
		for (FontRenderer f : renderers.values())
			f.onResourceManagerReload(irm);
		 */
		super.onResourceManagerReload(irm);
	}

	public FontRenderer getFallback() {
		return fallback;
	}

	public static DelegateFontRenderer getRegisteredInstance() {
		if (!Loader.instance().hasReachedState(LoaderState.LOADING))
			throw new MisuseException("Tried to access the delegate font renderer too early!");
		return (DelegateFontRenderer)Minecraft.getMinecraft().fontRenderer;
	}

	public static String stripFlags(String sg) {
		int idx = sg.indexOf(keyChar);
		while (idx >= 0) {
			sg = sg.replaceAll(String.valueOf(keyChar)+String.valueOf(sg.charAt(idx+1)), "");
			idx = sg.indexOf(keyChar);
		}
		return sg;
	}


}
