package Reika.DragonAPI.Libraries.IO;


import java.awt.image.BufferedImage;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;

import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;

public class ImageToStringConverter {

	private static final Random rand = new Random();

	static {
		rand.nextBoolean();
		rand.nextBoolean();
	}

	public static BufferedImage encodeToImage(String img) {
		byte[] raw = img.getBytes(Charset.forName("UTF-8"));
		ArrayList<Integer> li = new ArrayList();
		li.add(raw.length);
		for (int idx = 0; idx < raw.length; idx += 4) {
			byte b1 = raw[idx];
			byte b2 = idx+1 < raw.length ? raw[idx+1] : 0;
			byte b3 = idx+2 < raw.length ? raw[idx+2] : 0;
			byte b4 = idx+3 < raw.length ? raw[idx+3] : 0;
			li.add(ReikaJavaLibrary.buildInt(b1, b2, b3, b4));
		}
		int side = (int)Math.ceil(Math.sqrt(li.size()))*2;
		BufferedImage image = new BufferedImage(side, side, BufferedImage.TYPE_INT_ARGB);
		for (int idx = 0; idx < li.size(); idx++) {
			int dat = li.get(idx);
			setColorsAt(image, idx, dat);
		}
		for (int idx = li.size(); idx < side*side/4; idx++) {
			setColorsAt(image, idx, rand.nextInt());
		}
		return image;
	}

	public static String decodeToString(BufferedImage image) {
		ArrayList<Byte> li = new ArrayList();
		int len = 0;
		int read = 0;
		boolean flag = true;
		for (int k = 0; k < image.getHeight(); k += 2) {
			if (!flag)
				break;
			for (int i = 0; i < image.getWidth(); i += 2) {
				if (!flag)
					break;
				int argb = getColorAt(image, i, k);
				int idx = getPixel(image, i, k);
				if (idx == 0) {
					len = argb;
				}
				else {
					byte[] vals = ReikaJavaLibrary.splitInt(argb);
					for (byte b : vals)
						li.add(b);
				}
				read += 4;
				if (read > len)
					flag = false;
			}
		}
		byte[] raw = new byte[len];
		for (int i = 0; i < len; i++) {
			if (i >= li.size())
				throw new RuntimeException("Byte out of bounds at "+i);
			raw[i] = li.get(i);
		}
		return new String(raw, Charset.forName("UTF-8"));
	}

	private static int getPixel(BufferedImage img, int col, int row) {
		return (col/2)+(row/2)*(img.getWidth()/2);
	}

	private static int[] getColRow(BufferedImage img, int px) {
		int side = img.getHeight()/2;
		int col = px%side;
		int row = px/side;
		return new int[]{col*2, row*2};
	}

	private static void setColorsAt(BufferedImage img, int px, int color) {
		int side = img.getHeight();
		int[] pos = getColRow(img, px);
		int col = pos[0];
		int row = pos[1];
		byte[] vals = ReikaJavaLibrary.splitInt(color);
		int c1 = ReikaColorAPI.RGBtoHex(rand.nextInt(255), rand.nextInt(255), vals[0], ReikaRandomHelper.getRandomBetween(64, 255));
		int c2 = ReikaColorAPI.RGBtoHex(rand.nextInt(255), vals[1], rand.nextInt(255), ReikaRandomHelper.getRandomBetween(64, 255));
		int c3 = ReikaColorAPI.RGBtoHex(vals[2], rand.nextInt(255), rand.nextInt(255), ReikaRandomHelper.getRandomBetween(64, 255));
		int c4 = ReikaColorAPI.RGBtoHex(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255), vals[3]);
		img.setRGB(col, row, c1);
		img.setRGB(col+1, row, c2);
		img.setRGB(col, row+1, c3);
		img.setRGB(col+1, row+1, c4);
	}

	private static int getColorAt(BufferedImage img, int col, int row) {
		int c1 = img.getRGB(col, row);
		int c2 = img.getRGB(col+1, row);
		int c3 = img.getRGB(col, row+1);
		int c4 = img.getRGB(col+1, row+1);
		byte b1 = (byte)ReikaColorAPI.getBlue(c1);
		byte b2 = (byte)ReikaColorAPI.getGreen(c2);
		byte b3 = (byte)ReikaColorAPI.getRed(c3);
		byte b4 = (byte)ReikaColorAPI.getAlpha(c4);
		return ReikaJavaLibrary.buildInt(b1, b2, b3, b4);
	}

}
