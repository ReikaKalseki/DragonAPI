/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.GUI;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;


public class PianoKeyboard extends Gui {

	public final int guiX;
	public final int guiY;
	private final MusicGui guiInstance;

	private final ArrayList<PianoKey> keyList = new ArrayList();

	private static final KeyShape[] shapeList = {
		KeyShape.LEFT,
		KeyShape.BLACK,
		KeyShape.MIDDLE,
		KeyShape.BLACK,
		KeyShape.MIDDLE,
		KeyShape.BLACK,
		KeyShape.RIGHT,
		KeyShape.LEFT,
		KeyShape.BLACK,
		KeyShape.MIDDLE,
		KeyShape.BLACK,
		KeyShape.RIGHT
	};

	public PianoKeyboard(int x, int y, MusicGui gui) {
		guiX = x;
		guiY = y;
		guiInstance = gui;

		int dx = 6;
		for (int m = 0; m < 4; m++) {
			for (int i = 0; i <= 4; i += 2) {
				int id = i+1+m*12;
				keyList.add(new PianoKey(id, x+dx+i*4, y+1, this.getShapeFromIndex(id), guiInstance));
			}
			dx += 32;
			for (int i = 0; i <= 2; i += 2) {
				int id = i+8+m*12;
				keyList.add(new PianoKey(id, x+dx+i*4, y+1, this.getShapeFromIndex(id), guiInstance));
			}
			dx += 24;
		}

		for (int i = 0; i <= 56; i += 2) {
			int id = i;
			if (id >= 8)
				id--;
			if (id >= 13)
				id--;
			if (id >= 20)
				id--;
			if (id >= 25)
				id--;
			if (id >= 32)
				id--;
			if (id >= 37)
				id--;
			if (id >= 44)
				id--;
			if (id >= 49)
				id--;

			//buttonList.add(new InvisibleButton(id, x, k+150, w, 37, ""));
			keyList.add(new PianoKey(id, x+i*4, y+1, this.getShapeFromIndex(id), guiInstance));
		}

	}

	private KeyShape getShapeFromIndex(int i) {
		if (i == 48)
			return KeyShape.WHITE;
		return shapeList[i%shapeList.length];
	}

	public void mouseClicked(int x, int y, int button) {
		for (int i = 0; i < keyList.size(); i++) {
			PianoKey key = keyList.get(i);
			if (key.mousePressed(Minecraft.getMinecraft(), x, y)) {
				guiInstance.onKeyPressed(key);
				return;
			}
		}
	}

	public void drawKeys() {
		guiInstance.bindKeyboardTexture();
		this.drawTexturedModalRect(guiX, guiY, 0, 64, 232, 37);

		Minecraft mc = Minecraft.getMinecraft();
		GL11.glEnable(GL11.GL_BLEND);
		for (int i = 0; i < keyList.size(); i++) {
			PianoKey key = keyList.get(i);
			key.drawButton(mc, 0, 0);
		}
		GL11.glDisable(GL11.GL_BLEND);

		mc.fontRenderer.drawString("F", guiX-6, guiY+28, 0);
		mc.fontRenderer.drawString("F", guiX+233, guiY+28, 0);
	}

	public static class PianoKey extends GuiButton {

		public final KeyShape hitbox;
		private int alpha = 0;
		private final MusicGui guiInstance;

		public PianoKey(int note, int x, int y, KeyShape shape, MusicGui gui) {
			super(note, x, y, 0, 0, "");
			hitbox = shape;
			guiInstance = gui;
		}

		@Override
		public void drawButton(Minecraft mc, int x, int y)
		{
			int c = guiInstance.getColorForChannel(guiInstance.getActiveChannel());
			int rgb = (c & 0xffffff) | (alpha << 24);
			if (alpha > 0) {
				switch(hitbox) {
					case BLACK:
						this.drawRect(xPosition+1, yPosition+1, xPosition+3, yPosition+20, rgb);
						break;
					case LEFT:
						this.drawRect(xPosition+1, yPosition, xPosition+6, yPosition+35, rgb);
						this.drawRect(xPosition+6, yPosition+21, xPosition+7, yPosition+35, rgb);
						break;
					case MIDDLE:
						this.drawRect(xPosition+2, yPosition, xPosition+6, yPosition+21, rgb);
						this.drawRect(xPosition+1, yPosition+21, xPosition+7, yPosition+35, rgb);
						break;
					case RIGHT:
						this.drawRect(xPosition+2, yPosition, xPosition+7, yPosition+35, rgb);
						this.drawRect(xPosition+1, yPosition+21, xPosition+2, yPosition+35, rgb);
						break;
					case WHITE:
						this.drawRect(xPosition+1, yPosition, xPosition+7, yPosition+35, rgb);
						break;
					default:
						break;
				}
				alpha--;
			}
		}

		@Override
		public boolean mousePressed(Minecraft mc, int x, int y)
		{
			ReikaGuiAPI api = ReikaGuiAPI.instance;
			boolean flag = false;
			switch(hitbox) {
				case BLACK:
					if (api.isMouseInBox(xPosition, xPosition+4, yPosition, yPosition+21))
						flag = true;
					break;
				case LEFT:
					if (api.isMouseInBox(xPosition+1, xPosition+6, yPosition, yPosition+35))
						flag = true;
					if (api.isMouseInBox(xPosition+5, xPosition+7, yPosition+21, yPosition+35))
						flag = true;
					break;
				case MIDDLE:
					if (api.isMouseInBox(xPosition+2, xPosition+6, yPosition, yPosition+35))
						flag = true;
					if (api.isMouseInBox(xPosition+1, xPosition+7, yPosition+21, yPosition+35))
						flag = true;
					break;
				case RIGHT:
					if (api.isMouseInBox(xPosition+2, xPosition+7, yPosition, yPosition+35))
						flag = true;
					if (api.isMouseInBox(xPosition+1, xPosition+7, yPosition+21, yPosition+35))
						flag = true;
					break;
				case WHITE:
					if (api.isMouseInBox(xPosition+1, xPosition+7, yPosition, yPosition+35))
						flag = true;
					break;
				default:
					break;
			}

			if (flag)
				alpha = 255;
			//ReikaJavaLibrary.pConsole(alpha);
			return flag;
		}

	}

	private static enum KeyShape {
		WHITE(), //keyboard end
		BLACK(), //accidentals
		LEFT(), //C, F
		RIGHT(), //E, B
		MIDDLE(); //D, G, A

		private KeyShape() {

		}
	}

	public static interface MusicGui {

		public int getActiveChannel();

		public void bindKeyboardTexture();

		public void onKeyPressed(PianoKey key);

		public int getColorForChannel(int channel);

	}
}
