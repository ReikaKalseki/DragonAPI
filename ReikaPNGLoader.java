package Reika.DragonAPI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ReikaPNGLoader {
	
	public static int textureMap;
	public static BufferedImage missingtex = new BufferedImage(64, 64, 2);
	
	/** Call this to return an int for OpenGL texture binding. Args: Root class, Filepath
	 * (in the same directory as the one that contains your image subdirectory
	 * Eg. If folder contains mod.class and folder names "Textures", which has images in a subfolder "Img", use (mod.class, Textures/Img)
	 * This requires the PNGDecoder Jar! *//*
	public static int setupTextures(Class root, String file) {
		String filename = root.getResource(file).getPath();
	    IntBuffer tmp = BufferUtils.createIntBuffer(1);
	    GL11.glGenTextures(tmp);
	    tmp.rewind();
	    try {
	        InputStream in = new FileInputStream(filename);
	        PNGDecoder decoder = new PNGDecoder(in);

	        ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
	        decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
	        buf.flip();
	        GL11.glBindTexture(GL11.GL_TEXTURE_2D, tmp.get(0));
	        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
	        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
	        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 4);
	        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
	        int unsigned = (buf.get(0) & 0xff);/*
	        System.out.println(unsigned);
	        System.out.println(buf.get(1));
	        System.out.println(buf.get(2));
	        System.out.println(buf.get(3));*//*
	    }
	    catch (java.io.FileNotFoundException ex) {
	        System.out.println("Error " + filename + " not found");
	    }
	    catch (java.io.IOException e) {
	        System.out.println("Error decoding " + filename);
	    }
	    tmp.rewind();
	    return tmp.get(0);
	}*/
	
    /** Returns a BufferedImage read off the provided filepath, or, failing that, a backup hard-coded path.
     * Args: Root class, filepath, Backup Direct FilePath (include C:/ or other letter drive) */
    public static BufferedImage readTextureImage(Class root, String name, String back)
    {
    	InputStream inputfile = root.getResourceAsStream(name);
    	InputStream inputback = root.getResourceAsStream(back);
    	setMissingTex();
    	if (inputfile == null && inputback == null) {
    		ReikaJavaLibrary.pConsole("Neither default image filepath at "+name+" or backup at "+back+" found. Loading \"MissingTexture\".");
    		return missingtex;
    	}
    	if (inputfile == null && inputback != null) {
    		ReikaJavaLibrary.pConsole("Default image filepath at "+name+" does not exist. Switching to backup at "+back+".");
			try {
				return ImageIO.read(inputback);
			} catch (IOException e1) {
				ReikaJavaLibrary.pConsole("Backup image filepath at "+back+" not found. Loading \"MissingTexture\".");
				//e1.printStackTrace();
			}
			return missingtex;
    	}
        BufferedImage bufferedimage = null;
		try {
			return ImageIO.read(inputfile);
		}
		catch (IOException e) {
			if (back == null) {
				ReikaJavaLibrary.pConsole("Backup image filepath at "+back+" does not exist. Loading \"MissingTexture\".");
				return missingtex;
			}
			ReikaJavaLibrary.pConsole("Default image filepath at "+name+" not found. Switching to backup at "+back+".");
			try {
				return ImageIO.read(inputback);
			} catch (IOException e1) {
				ReikaJavaLibrary.pConsole("Backup image filepath at "+back+" not found. Loading \"MissingTexture\".");
				//e1.printStackTrace();
				return missingtex;
			}
		}
    }
    
    public static BufferedImage getMissingTex() {
    	setMissingTex();
    	return missingtex;
    }
    
    private static void setMissingTex() {
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
