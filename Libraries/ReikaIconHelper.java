package Reika.DragonAPI.Libraries;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class ReikaIconHelper {

	public static IIcon clipFrom(IIcon tex, IIcon src, ResourceLocation loc, TextureMap map, String name) {
		return clipFrom(tex, src, loc, loc, map, name);
	}

	public static IIcon clipFrom(IIcon tex, IIcon src, ResourceLocation l1, ResourceLocation l2, TextureMap map, String name) {
		return getDiff(tex, src, l1, l2, true, map, name);
	}

	public static IIcon subtractFrom(IIcon sub, IIcon sum, ResourceLocation loc, TextureMap map, String name) {
		return subtractFrom(sub, sum, loc, loc, map, name);
	}

	public static IIcon subtractFrom(IIcon sub, IIcon sum, ResourceLocation l1, ResourceLocation l2, TextureMap map, String name) {
		return getDiff(sub, sum, l1, l2, false, map, name);
	}

	private static IIcon getDiff(IIcon i1, IIcon i2, ResourceLocation l1, ResourceLocation l2, boolean clip, TextureMap map, String name) {
		BufferedImage src = getIconImage(i2, l2);
		BufferedImage tgt = getIconImage(i1, l1);
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
				for (int y = 0; y < data[x].length; i++) {
					int c = out.getRGB(x, y);
					if (c != 0) {
						data[x][y] = c;
					}
				}
			}
		}
		return sp;
	}

	private static BufferedImage getIconImage(IIcon ico, ResourceLocation loc) {
		IResourceManager rm = Minecraft.getMinecraft().getResourceManager();
		try {
			IResource ir = rm.getResource(loc);
			BufferedImage img = ImageIO.read(ir.getInputStream());
			return img;
		}
		catch (IOException e) {
			throw new IllegalArgumentException("The RL "+loc+" does not contain "+ico+"!");
		}
	}

}
