/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.IO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

import com.google.common.collect.HashBiMap;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class DelegateFontRenderer extends FontRenderer {

	private final FontRenderer fallback;
	private final HashBiMap<String, FontRenderer> renderers = HashBiMap.create();
	private static int currentID = 161; //char 161 = '¡'; everything from here is a foreign-language char or diacritical
	private static final int maxID = 0xFFFF;
	private static final String keyChar = "\uFFFC";

	public DelegateFontRenderer(FontRenderer fr) {
		super(Minecraft.getMinecraft().gameSettings, ReikaTextureHelper.font, Minecraft.getMinecraft().renderEngine, false);
		fallback = fr;
		((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
	}

	public String addRenderer(FontRenderer f) {
		if (currentID >= maxID) {
			throw new MisuseException("Delegate Font Renderer has run out of IDs! All "+maxID+" IDs occupied!");
		}
		String id = keyChar+String.valueOf((char)currentID);
		renderers.put(id, f);
		currentID++;
		return id;
	}

	@Override
	public int drawString(String sg, int x, int y, int color, boolean shadow) {
		FontKey f = this.getRenderer(sg);
		return f.renderer.drawString(f.text, x, y, color, shadow);
	}

	@Override
	public void drawSplitString(String sg, int x, int y, int space, int color) {
		FontKey f = this.getRenderer(sg);
		f.renderer.drawSplitString(f.text, x, y, space, color);
	}

	private FontKey getRenderer(String sg) {
		if (sg.length() > 2) {
			int index = sg.indexOf(keyChar);
			if (index >= 0) {
				String key = sg.substring(index, index+2);
				FontRenderer f = renderers.get(key);
				if (f != null) {
					return new FontKey(f, sg.substring(0, index)+sg.substring(index+2, sg.length()));
				}
			}
		}
		return new FontKey(fallback, sg);
	}

	private static class FontKey {

		private final FontRenderer renderer;
		private final String text;

		private FontKey(FontRenderer f, String s) {
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

	public static DelegateFontRenderer getRegisteredInstance() {
		if (!Loader.instance().hasReachedState(LoaderState.LOADING))
			throw new MisuseException("Tried to access the delegate font renderer too early!");
		return (DelegateFontRenderer)Minecraft.getMinecraft().fontRenderer;
	}


}
