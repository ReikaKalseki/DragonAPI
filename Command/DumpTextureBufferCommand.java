package Reika.DragonAPI.Command;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;

import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;


public class DumpTextureBufferCommand extends DragonClientCommand {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		switch(args[0].toLowerCase(Locale.ENGLISH)) {
			case "terrain":
				ReikaTextureHelper.bindTerrainTexture();
				break;
			case "items":
				ReikaTextureHelper.bindItemTexture();
				break;
			case "particles":
				ReikaTextureHelper.bindParticleTexture();
				break;
			case "enchant":
				ReikaTextureHelper.bindEnchantmentTexture();
				break;
		}
		int w = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
		int h = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
		int len = w * h;
		IntBuffer pixelBuffer = BufferUtils.createIntBuffer(len);
		int[] pixelValues = new int[len];

		GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

		GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);

		pixelBuffer.get(pixelValues);
		this.flipPixelArray(pixelValues, w, h);
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		for (int k = 0; k < h; k++) {
			for (int i = 0; i < w; i++) {
				img.setRGB(i, h-1-k, pixelValues[k*w+i]);
			}
		}
		try {
			File root = new File(Minecraft.getMinecraft().mcDataDir, "TextureExport");
			root.mkdirs();
			File f = new File(root, args[0]+".png");
			ImageIO.write(img, "png", f);
			this.sendChatToSender(ics, "Texture exported to "+f.getAbsolutePath());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void flipPixelArray(int[] data, int width, int height) {
		int[] temp = new int[width];
		int k = height / 2;

		for (int l = 0; l < k; ++l) {
			System.arraycopy(data, l * width, temp, 0, width);
			System.arraycopy(data, (height - 1 - l) * width, data, l * width, width);
			System.arraycopy(temp, 0, data, (height - 1 - l) * width, width);
		}
	}

	@Override
	public String getCommandString() {
		return "dumptexture";
	}

}
