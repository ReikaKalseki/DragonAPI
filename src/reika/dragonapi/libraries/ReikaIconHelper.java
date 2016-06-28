/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.libraries;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class ReikaIconHelper {

	public static IIcon clipFrom(IIcon tex, IIcon src, TextureMap map, String name) {
		try {
			return getDiff(tex, src, map, name, true);
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static IIcon subtractFrom(IIcon sub, IIcon sum, TextureMap map, String name) {
		try {
			return getDiff(sub, sum, map, name, false);
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static IIcon getDiff(IIcon i1, IIcon i2, TextureMap map, String name, boolean clip) throws IOException {
		BufferedImage src = getIconImage(i2, map);
		BufferedImage tgt = getIconImage(i1, map);
		int w = src.getWidth();
		int h = src.getHeight();
		int type = src.getType();
		BufferedImage out = new BufferedImage(w, h, type);
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int c1 = src.getRGB(x, y);
				int c2 = tgt.getRGB(x, y);
				boolean eq = c1 == c2;
				if (eq != clip) {
					out.setRGB(x, y, c2);
				}
				else {
					out.setRGB(x, y, 0); //fully transparent
				}
			}
		}
		return registerIcon(out, name, map);
	}

	private static IIcon registerIcon(BufferedImage out, String locName, TextureMap map) {
		IIcon ico = map.registerIcon(locName);
		TextureAtlasSprite sp = (TextureAtlasSprite)ico;
		for (int i = 0; i < sp.getFrameCount(); i++) {
			int[][] data = sp.getFrameTextureData(i);
			for (int x = 0; x < data.length; x++) {
				for (int y = 0; y < data[x].length; y++) {
					int c = out.getRGB(x, y);
					if (c != 0) {
						data[x][y] = c;
					}
				}
			}
		}
		return sp;
	}

	private static BufferedImage getIconImage(IIcon ico, TextureMap map) throws IOException {
		Map<String, TextureAtlasSprite> data = map.mapRegisteredSprites;
		for (String s : data.keySet()) {
			ResourceLocation loc = new ResourceLocation(s);
			TextureAtlasSprite spr = data.get(s);
			if (spr.getIconName().equals(ico.getIconName())) {
				IResource ir = Minecraft.getMinecraft().getResourceManager().getResource(loc);
				return ImageIO.read(ir.getInputStream());
			}
		}
		throw new IOException("No image found!");
	}

}
