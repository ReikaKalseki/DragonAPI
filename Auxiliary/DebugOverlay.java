/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class DebugOverlay {

	public static final DebugOverlay instance = new DebugOverlay();

	private DebugOverlay() {

	}

	@ForgeSubscribe
	public void eventHandler(RenderGameOverlayEvent event) {
		if (DragonAPICore.debugtest) {
			Minecraft mc = Minecraft.getMinecraft();
			FontRenderer f = mc.fontRenderer;

			double d = 3;
			GL11.glScaled(d, d, d);
			String s = "Debug Mode Enabled!";
			f.drawString(s, 1, 1, 0xffffff);
			GL11.glScaled(1/d, 1/d, 1/d);
			ReikaTextureHelper.bindHUDTexture();
		}
	}

}
