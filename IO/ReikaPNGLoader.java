/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.IO;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public final class ReikaPNGLoader {

	private ReikaPNGLoader() {throw new RuntimeException("The class "+this.getClass()+" cannot be instantiated!");}

	public static int textureMap;
	public static BufferedImage missingtex = new BufferedImage(64, 64, 2);

	/** Returns a BufferedImage read off the provided filepath, or, failing that, a backup hard-coded path.
	 * Args: Root class, filepath, Backup Direct FilePath (include C:/ or other letter drive) */
	public static BufferedImage readTextureImage(Class root, String name)
	{
		ReikaJavaLibrary.pConsole("Pipelining texture from "+root.getCanonicalName()+" to "+name);
		InputStream inputfile = root.getResourceAsStream(name);

		if (inputfile == null) {
			ReikaJavaLibrary.pConsole("Image filepath at "+name+" not found. Loading \"MissingTexture\".");
			return missingtex;
		}
		BufferedImage bufferedimage = null;
		try {
			return ImageIO.read(inputfile);
		}
		catch (IOException e) {
			ReikaJavaLibrary.pConsole("Default image filepath at "+name+" not found.");
			return missingtex;
		}
	}
	/*
	public static BufferedImage readTexturePackImage(ITexturePack ip, String name) throws IOException
	{
		InputStream inputfile;
		inputfile = ip.getResourceAsStream(name);

		if (inputfile == null) {
			throw new IOException("IOException on loading texture pack image variant for texturepack "+ip.getTexturePackID()+". Loading default textures.");
		}
		return ImageIO.read(inputfile);
	}*/

	public static boolean imageFileExists(Class root, String name) {
		InputStream inputfile = root.getResourceAsStream(name);
		if (inputfile == null) {
			return false;
		}
		BufferedImage bufferedimage = null;
		try {
			bufferedimage = ImageIO.read(inputfile);
		}
		catch (IOException e) {
			return false;
		}
		return true;
	}

	public static BufferedImage getMissingTex() {
		return missingtex;
	}

	static {
		Graphics graphics = missingtex.getGraphics();
		graphics.setColor(Color.decode("0x2F0044"));
		graphics.fillRect(0, 0, 64, 64);
		graphics.setColor(Color.decode("0x7F6A00"));
		int i = 10;
		int j = 0;
		while (i < 64) {
			String s = j++ % 2 == 0 ? "missing" : "texture";
			graphics.drawString(s, 1, i);
			i += graphics.getFont().getSize();
			if (j % 2 == 0)
				i += 5;
		}
		graphics.dispose();
	}
}
