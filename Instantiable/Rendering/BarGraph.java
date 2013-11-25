package Reika.DragonAPI.Instantiable.Rendering;

import java.awt.Color;
import java.util.List;

import Reika.DragonAPI.Instantiable.Data.BarGraphData;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;

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
		for (int i = 0; i < data.getNumberEntries(); i++) {
			int h = this.getBarHeight(i);
			int dy = ySize-h;
			if (i%2 == 0)
				barColor = barColor.darker().darker();
			int color = barColor.getRGB()+0xff000000;
			ReikaGuiAPI.instance.drawRect(x+i*w, y+dy, w, h, color, false);
		}
	}

}
