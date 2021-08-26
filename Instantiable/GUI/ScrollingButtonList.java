package Reika.DragonAPI.Instantiable.GUI;

import net.minecraft.util.MathHelper;

public class ScrollingButtonList {

	public final int maxRows;
	public final int maxCols;

	private int currentScroll;
	private int maxScroll;

	private int allButtons = 0;

	public ScrollingButtonList(int r, int c) {
		maxCols = c;
		maxRows = r;
	}

	public void addButton() {
		allButtons++;
		int rowMax = MathHelper.ceiling_float_int(this.getTotalSize()/(float)maxCols);
		maxScroll = rowMax-maxRows;
	}

	public boolean scrollUp() {
		if (currentScroll > 0) {
			currentScroll--;
			return true;
		}
		return false;
	}

	public boolean scrollDown() {
		if (currentScroll < maxScroll) {
			currentScroll++;
			return true;
		}
		return false;
	}

	public int getOffsetPosition(int pos) {
		int idx = pos+this.getBaseOffset();
		return idx;
	}

	public int getBaseOffset() {
		return currentScroll*maxCols;
	}

	public int getHighestVisible() {
		return this.getBaseOffset()+this.getWindowSize()-1;
	}

	public int getWindowSize() {
		return maxRows*maxCols;
	}

	public int getTotalSize() {
		return allButtons;
	}

	public int getScroll() {
		return currentScroll;
	}

	public void reset() {
		currentScroll = 0;
	}

	public void clear() {
		this.reset();
		allButtons = 0;
		maxScroll = 0;
	}

	public int[] getPositionOf(int idx) {
		if (idx < 0 || idx >= allButtons)
			return null;
		int row = idx/maxCols-currentScroll;
		if (row < 0 || row >= maxRows)
			return null;
		int col = idx%maxCols;
		return new int[] {col, row};
	}
}
