/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Effects;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AnimatedTexture {

	public final String texturePath;
	public final Class referenceClass;
	public final int numberFrames;
	/** In milliseconds between frames */
	public final int frameDelay;

	public final int xSize;
	public final int ySize;

	private static final Tessellator v5 = Tessellator.instance;

	public AnimatedTexture(Class ref, String path, int x, int y, int size, int delay) {
		referenceClass = ref;
		texturePath = path;
		numberFrames = size;
		frameDelay = delay;

		xSize = x;
		ySize = y;
	}

	public void render() {
		ReikaTextureHelper.bindTexture(referenceClass, texturePath);
		int frame = this.getCurrentFrame();
		float u = frame/(float)numberFrames;
		float du = u+xSize/(float)numberFrames;
		GL11.glEnable(GL11.GL_BLEND);
		v5.startDrawingQuads();
		v5.setColorOpaque(255, 255, 255);
		v5.addVertexWithUV(0, 0, 0, u, 0);
		v5.addVertexWithUV(1, 0, 0, du, 0);
		v5.addVertexWithUV(1, 1, 0, du, 1);
		v5.addVertexWithUV(0, 1, 0, u, 1);
		v5.draw();
	}

	public int getCurrentFrame() {
		long time = System.currentTimeMillis();
		int per = (int)(time/frameDelay);
		return per%numberFrames;
	}

}
