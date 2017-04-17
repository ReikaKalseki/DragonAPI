package Reika.DragonAPI.Instantiable.GUI;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class FractionalBar {

	public final int posX;
	public final int posY;

	public final int sizeX;
	public final int sizeY;

	public final int minValue;
	public final int maxValue;

	private int currentValue;

	public FractionalBar(int x, int y, int sx, int sy, int min, int max) {
		this(x, y, sx, sy, min, max, min);
	}

	public FractionalBar(int x, int y, int sx, int sy, int min, int max, int init) {
		posX = x;
		posY = y;
		sizeX = sx;
		sizeY = sy;
		minValue = min;
		maxValue = max;
		currentValue = init;
	}

	public int setScaledValue(int px) {
		currentValue = minValue+(maxValue-minValue)*(px-posX)/(sizeX);
		return currentValue;
	}

	public int getCurrentValue() {
		return currentValue;
	}

	@SideOnly(Side.CLIENT)
	public void draw(Gui g, int u, int v, int w, int h, int slu, int slv, int slw, int slh) {
		g.drawTexturedModalRect(posX, posY, u, v, w, h);
		g.drawTexturedModalRect(posX+slw/2-1+(sizeX-slw)*(currentValue-minValue)/(maxValue-minValue), posY+2, slu, slv, slw, slh);
	}

	@SideOnly(Side.CLIENT)
	public void drawTitle(FontRenderer f, String s, int c) {
		f.drawString(s+" ("+currentValue+")", posX, posY-f.FONT_HEIGHT, c);
	}

	@SideOnly(Side.CLIENT)
	public boolean handleClick(int x, int y, int b) {
		if (x >= posX && x <= posX+sizeX && y >= posY && y <= posY+sizeY) {
			this.setScaledValue(x);
			return true;
		}
		return false;
	}

}
