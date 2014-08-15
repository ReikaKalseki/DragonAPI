/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.GUI;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class Gui2DTextField extends GuiTextField {

	private int maxLineWidth;
	private int maxLines;

	private int guiX;
	private int guiY;
	private int guiW;
	private int guiH;
	private FontRenderer fr;

	private static final boolean enableBackgroundDrawing = true;

	public Gui2DTextField(FontRenderer f, int x, int y, int w, int h, int width, int lines) {
		super(f, x, y, w, h);
		fr = f;
		guiX = x;
		guiY = y;
		guiH = h;
		guiW = w;
		maxLines = lines;
		maxLineWidth = width;
	}

	/*
	@Override
	public void drawTextBox()
	{
		if (this.getVisible())
		{
			if (this.getEnableBackgroundDrawing())
			{
				drawRect(guiX - 1, guiY - 1, guiX + guiW + 1, guiY + guiH + 1, -6250336);
				drawRect(guiX, guiY, guiX + guiW, guiY + guiH, -16777216);
			}

			int i = isEnabled ? enabledColor : disabledColor;
			int j = cursorPosition - lineScrollOffset;
			int k = selectionEnd - lineScrollOffset;
			String s = fr.trimStringToWidth(text.substring(lineScrollOffset), this.getWidth());
			boolean flag = j >= 0 && j <= s.length();
			boolean flag1 = isFocused && cursorCounter / 6 % 2 == 0 && flag;
			int l = enableBackgroundDrawing ? guiX + 4 : guiX;
			int i1 = enableBackgroundDrawing ? guiY + (guiH - 8) / 2 : guiY;
			int j1 = l;

			if (k > s.length())
			{
				k = s.length();
			}

			if (s.length() > 0)
			{
				String s1 = flag ? s.substring(0, j) : s;
				j1 = fr.drawStringWithShadow(s1, l, i1, i);
			}

			boolean flag2 = cursorPosition < text.length() || text.length() >= this.getMaxStringLength();
			int k1 = j1;

			if (!flag)
			{
				k1 = j > 0 ? l + guiW : l;
			}
			else if (flag2)
			{
				k1 = j1 - 1;
				--j1;
			}

			if (s.length() > 0 && flag && j < s.length())
			{
				fr.drawStringWithShadow(s.substring(j), j1, i1, i);
			}

			if (flag1)
			{
				if (flag2)
				{
					Gui.drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + fr.FONT_HEIGHT, -3092272);
				}
				else
				{
					fr.drawStringWithShadow("_", k1, i1, i);
				}
			}

			if (k != j)
			{
				int l1 = l + fr.getStringWidth(s.substring(0, k));
				this.drawCursorVertical(k1, i1 - 1, l1 - 1, i1 + 1 + fr.FONT_HEIGHT);
			}
		}
	}*/
}