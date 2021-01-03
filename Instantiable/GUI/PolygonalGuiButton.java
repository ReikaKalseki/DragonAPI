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

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;

import Reika.DragonAPI.Instantiable.Math.DoublePolygon;
import Reika.DragonAPI.Instantiable.Rendering.ComplexSubdividedTexture;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;

public class PolygonalGuiButton extends ImagedGuiButton {

	private final DoublePolygon shape;
	private final ComplexSubdividedTexture texture;

	private int labelX;
	private int labelY;
	private boolean relativeLabel;

	private int defaultColor = 0xffffff;
	private int hoverColor = 0xffffff;

	private ButtonColorHook colorHook = null;

	public PolygonalGuiButton(int par1, String par4Str, Class mod, DoublePolygon p, ComplexSubdividedTexture tex) {
		super(par1, (int)p.getBounds().x, (int)p.getBounds().y, par4Str, mod);
		shape = p;
		texture = tex;
	}

	/** Draw a Gui Button with an image background and text overlay.
	 *Args: id, text overlay, text color, shadow, filepath, class root */
	public PolygonalGuiButton(int par1, String par6Str, int par9, boolean par10, String file, Class mod, DoublePolygon p, ComplexSubdividedTexture tex)
	{
		super(par1, (int)p.getBounds().x, (int)p.getBounds().y, (int)p.getBounds().width, (int)p.getBounds().height, 0, 0, par6Str, par9, par10, file, mod);
		shape = p;
		texture = tex;
	}

	/** Draw a Gui Button with an image background and text tooltip. Args: id, filepath, text tooltip, text color, shadow */
	public PolygonalGuiButton(int par1, String file, String par6Str, int par9, boolean par10, Class mod, DoublePolygon p, ComplexSubdividedTexture tex)
	{
		super(par1, (int)p.getBounds().x, (int)p.getBounds().y, (int)p.getBounds().width, (int)p.getBounds().height, 0, 0, file, par6Str, par9, par10, mod);
		shape = p;
		texture = tex;
	}

	public PolygonalGuiButton setLabelPosition(int x, int y, boolean relative) {
		labelX = x;
		labelY = y;
		relativeLabel = relative;
		if (relativeLabel) {
			labelX += shape.getBounds().x;
			labelY += shape.getBounds().y+shape.getBounds().height/2-renderer.FONT_HEIGHT/2;
		}
		return this;
	}

	@Override
	public ImagedGuiButton setTextAlign(TextAlign ta) {
		super.setTextAlign(ta);
		if (relativeLabel) {
			switch(alignment) {
				case LEFT:
					break;
				case CENTER:
					//labelX -= shape.getBounds().width/2;
					break;
				case RIGHT:
					//labelX -= shape.getBounds().width;
					break;
			}
		}
		return this;
	}

	public PolygonalGuiButton setColorBlend(int basic, int hover) {
		defaultColor = basic;
		hoverColor = hover;
		return this;
	}

	public PolygonalGuiButton setColorCallback(ButtonColorHook bk) {
		colorHook = bk;
		return this;
	}

	@Override
	protected boolean isPositionWithin(int mx, int my) {
		return shape.contains(mx, my);
	}

	@Override
	protected void renderButton() {
		//int tex = GL11.GL_TEXTURE_BINDING_2D;
		//GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.DEFAULT.apply();
		ReikaTextureHelper.bindTexture(modClass, this.getButtonTexture());
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		double cx = shape.getBounds().x+shape.getBounds().width/2;
		double cy = shape.getBounds().y+shape.getBounds().height/2;
		Tessellator.instance.startDrawing(shape.npoints == 4 ? GL11.GL_QUADS : GL11.GL_TRIANGLE_FAN);
		int c = ReikaColorAPI.mixColors(hoverColor, defaultColor, this.getHoverFade());
		if (colorHook != null) {
			c = colorHook.getColor(c);
			//ReikaJavaLibrary.pConsole(Integer.toHexString(c));
		}
		int a = (c >> 24) & 0xff;
		if (a <= 0)
			a = 255;
		//ReikaJavaLibrary.pConsole(a);
		Tessellator.instance.setColorRGBA_I(c & 0xFFFFFF, a);
		for (int i = 0; i < shape.npoints; i++) {
			double x = shape.xpoints[i];
			double y = shape.ypoints[i];
			texture.addVertex(x, y);
			//double dx = x-cx;
			//double dy = y-cy;
			//double d = 1.15;
			//double rx = MathHelper.clamp_double(cx+dx*d, shape.getBounds().x, shape.getBounds().x+shape.getBounds().width);
			//double ry = MathHelper.clamp_double(cy+dy*d, shape.getBounds().y, shape.getBounds().y+shape.getBounds().height);
			//texture.addVertex(rx, ry);
		}
		Tessellator.instance.draw();

		if (displayString != null && alignment != TextAlign.CENTER) {
			//ReikaGuiAPI.instance.drawLine((int)cx, (int)cy, this.getLabelX(), this.getLabelY(), c);
			GL11.glLineWidth(3);
			int ty = labelY+renderer.FONT_HEIGHT;
			int x2 = alignment == TextAlign.RIGHT ? labelX-renderer.getStringWidth(displayString)-2 : labelX+renderer.getStringWidth(displayString)+2;
			int x22 = alignment == TextAlign.RIGHT ? labelX+2 : labelX-2;
			ReikaGuiAPI.instance.drawLine(x2, ty, x22, ty, c);
			double dx = x22-cx;
			double dy = ty-cy;
			double dr = Math.abs(dy) < 1 ? 0.5 : 0.2;
			ReikaGuiAPI.instance.drawLine((int)Math.round(cx+dx*dr), (int)Math.round(cy+dy*dr), x22, ty, c);
		}
	}

	@Override
	protected int getLabelX() {
		//ReikaJavaLibrary.pConsole(displayString+" > "+alignment.getDX(renderer, displayString));
		return labelX-alignment.getDX(renderer, displayString)*2;
	}

	@Override
	protected int getLabelY() {
		return labelY;
	}

	@Override
	public int getLabelColor() {
		int base = super.getLabelColor();
		if (colorHook != null) {
			return colorHook.getColor(base);
		}
		return base;
	}

	public static interface ButtonColorHook {

		public int getColor(int orig);

	}

}
