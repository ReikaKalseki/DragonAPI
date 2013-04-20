package Reika.DragonAPI;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ReikaPNGLoader {
	
	public static int textureMap;
	
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
     * Args: Filepath, Backup Direct FilePath (include C:\ or other letter drive) */
    public static BufferedImage readTextureImage(String name, String back)
    {
        BufferedImage bufferedimage = null;
		try {
			return ImageIO.read(new File(name));
		}
		catch (IOException e) {
			//ReikaJavaLibrary.pConsole("Default image filepath not found. Switching to backup.");
			try {
				return ImageIO.read(new File(back));
			} catch (IOException e1) {
				//ReikaJavaLibrary.pConsole("Backup image filepath not found. Loading \"MissingTexture\".");
				e1.printStackTrace();
			}
		}
		return new BufferedImage(64, 64, 2); //Missingtexture, copied from RenderEngine
    }
}
