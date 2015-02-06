/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.GUI;

import java.awt.Color;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Instantiable.Data.BarGraphData;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;

public class BarGraph {

	private final BarGraphData data;
	private final List<Integer> values;

	public final int xSize;
	public final int ySize;

	public final int barWidth;

	public final int maxY;

	public BarGraph(BarGraphData dat, int w, int h) {
		data = dat;

		xSize = w;
		ySize = h;

		values = data.getXValues();

		barWidth = xSize/data.getNumberEntries();

		int maxy = -1;
		for (int i = 0; i < values.size(); i++) {
			int dx = values.get(i);
			int dy = data.getYOfX(dx);
			if (dy > maxy)
				maxy = dy;
		}
		maxY = maxy;
	}

	private int getBarHeight(int i) {
		int x = values.get(i);
		return data.getYOfX(x)*ySize/maxY;
	}

	public void render(int x, int y, Color barColor) {
		int w = barWidth;
		ReikaRenderHelper.prepareGeoDraw(false);
		int n = data.getNumberEntries();
		for (int i = 0; i < n; i++) {
			int h = this.getBarHeight(i);
			int v = values.get(i);
			GL11.glColor4f(1, 1, 1, 1);
			if (i%2 == 0)
				barColor = barColor.darker().darker();
			else
				barColor = barColor.brighter().brighter();
			int color = barColor.getRGB();
			//ReikaGuiAPI.instance.drawRect(x+i*w, y+dy, w, h, color, true);
			boolean line = false;
			if (line) {
				GuiScreen.drawRect(x+i*w, y+ySize, x+i*w+w, y+ySize-h, 0xff000000);
				GuiScreen.drawRect(x+i*w+1, y+ySize-1, x+i*w+w-1, y+ySize-h+1, color);
			}
			else {
				GuiScreen.drawRect(x+i*w, y+ySize, x+i*w+w, y+ySize-h, color);
			}
			int dx = x+i*w+w/4;
			int dy = y+ySize;
			GL11.glTranslated(dx, dy, 0);
			GL11.glRotated(-90, 0, 0, 1);
			GL11.glTranslated(-dx, -dy, 0);
			Minecraft.getMinecraft().fontRenderer.drawString(String.valueOf(v), dx, dy, 0xffffff);
			GL11.glTranslated(dx, dy, 0);
			GL11.glRotated(90, 0, 0, 1);
			GL11.glTranslated(-dx, -dy, 0);
		}
		ReikaRenderHelper.exitGeoDraw();
		ReikaRenderHelper.disableLighting();
	}

}
