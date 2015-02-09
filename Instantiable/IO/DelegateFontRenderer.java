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

import gnu.trove.map.hash.TCharIntHashMap;
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

	private static final TCharIntHashMap charMap = new TCharIntHashMap(256);

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
			//ReikaJavaLibrary.pConsole(index+"/"+sg.length()+"@"+sg, index >= 0);
			if (index >= 0 && index < sg.length()-1) {
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

	public static int getCharGridIndex(char c) {
		return charMap.get(c);
	}

	static {
		String key = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000";
		char[] chars = key.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			charMap.put(chars[i], i);
		}
	}


}
