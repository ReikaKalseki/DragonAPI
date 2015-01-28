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

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class DelegateFontRenderer extends FontRenderer {

	private final FontRenderer fallback;
	private final HashMap<String, FontRenderer> renderers = new HashMap();
	private static int maxID = 0;

	public DelegateFontRenderer(FontRenderer fr) {
		super(Minecraft.getMinecraft().gameSettings, ReikaTextureHelper.font, Minecraft.getMinecraft().renderEngine, false);
		fallback = fr;
		((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
	}

	public String addRenderer(FontRenderer f) {
		String id = "\uFFFC"+maxID;
		renderers.put(id, f);
		maxID++;
		return id;
	}

	@Override
	public int drawString(String sg, int x, int y, int color, boolean shadow) {
		return this.getRenderer(sg).drawString(sg, x, y, color, shadow);
	}

	@Override
	public void drawSplitString(String sg, int x, int y, int space, int color) {
		this.getRenderer(sg).drawSplitString(sg, x, y, space, color);
	}

	private FontRenderer getRenderer(String sg) {
		String ref = sg;
		StringBuilder pre = new StringBuilder();
		while (!ref.isEmpty() && ref.charAt(0) == '\u00A7') {
			pre.append(ref.substring(0, 2));
			ref = ref.substring(2);
		}
		for (String c : renderers.keySet()) {
			if (ref.startsWith(c)) {
				return renderers.get(c);
			}
		}
		return fallback;
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
