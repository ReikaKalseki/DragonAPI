/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.IO;

import java.awt.image.BufferedImage;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.settings.GameSettings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ReikaTextureBinder {

	/** Stores the image data for the texture during loading. */
	private IntBuffer imageData = GLAllocation.createDirectIntBuffer(16777216); //8388608//was 4194304 of raw ARGB; doubled to 8MB to allow for bigger images
	private long totalBytesLoaded = 0;
	private int boundTexture;

	/** Copy the supplied image onto a newly-allocated OpenGL texture, returning the allocated texture ID */
	public int allocateAndSetupTexture(BufferedImage buf) {
		int i = GL11.glGenTextures();
		this.loadImageOntoTexture(buf, i);
		return i;
	}

	public void loadImageOntoTexture(BufferedImage buf, int texID) {
		this.setupTextureExt(buf, texID, false, false);
	}

	private void bindTexture(int GL_ID) {
		if (GL_ID != boundTexture) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, GL_ID);
			boundTexture = GL_ID;
		}
	}

	private void setupTextureExt(BufferedImage buf, int texID, boolean mipmap, boolean clampEdge) {
		this.bindTexture(texID);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

		if (mipmap) {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		}

		if (clampEdge) {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		}
		else {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		}

		int j = buf.getWidth();
		int k = buf.getHeight();

		int[] aint = new int[j * k];
		buf.getRGB(0, 0, j, k, aint, 0, j);

		GameSettings options = Minecraft.getMinecraft().gameSettings;
		if (options != null && options.anaglyph) {
			aint = this.colorToAnaglyph(aint);
		}

		imageData.clear();
		imageData.put(aint);
		totalBytesLoaded += aint.length*4;
		imageData.position(0).limit(aint.length);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, j, k, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, imageData);
		imageData.clear();
	}

	public long getTotalBytesLoaded() {
		return totalBytesLoaded;
	}

	private int[] colorToAnaglyph(int[] arr) {
		int[] aint1 = new int[arr.length];

		for (int i = 0; i < arr.length; i++) {
			int j = (arr[i] >> 24) & 255;
			int k = (arr[i] >> 16) & 255;
			int l = (arr[i] >> 8) & 255;
			int i1 = (arr[i]) & 255;
			int j1 = (k * 30 + l * 59 + i1 * 11) / 100;
			int k1 = (k * 30 + l * 70) / 100;
			int l1 = (k * 30 + i1 * 70) / 100;
			aint1[i] = j << 24 | j1 << 16 | k1 << 8 | l1;
		}

		return aint1;
	}
}
