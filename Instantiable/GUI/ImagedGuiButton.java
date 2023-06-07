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

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;

public class ImagedGuiButton extends GuiButton {

	private final int baseU;
	private final int baseV;
	private int color;
	private boolean shadow = true;
	private String filepath;
	protected boolean hasToolTip;
	protected final Class modClass;

	public String sound = "gui.button.press";

	protected int u;
	protected int v;

	public TextAlign alignment = TextAlign.CENTER;
	public int textOffset = 0;
	public FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;

	public int textureSize = 256;

	public boolean invisible = false;

	private boolean lastHover;
	protected int hoverTicks;
	private float hoverFade;
	public float hoverFadeSpeedUp = 0.08F;
	public float hoverFadeSpeedDown = 0.15F;
	private int ticks = 0;
	private boolean isClicked;

	public IIcon icon = null;
	public int iconWidth = width;
	public int iconHeight = height;

	public ImagedGuiButton(int par1, int par2, int par3, String par4Str, Class mod)
	{
		super(par1, par2, par3, 200, 20, par4Str);

		hasToolTip = false;
		modClass = mod;
		baseU = u = 0;
		baseV = v = 0;
	}

	/** Draw a Gui Button with an image background. Args: id, x, y, width, height, u, v, filepath, class root */
	public ImagedGuiButton(int par1, int par2, int par3, int par4, int par5, int par7, int par8, String file, Class mod)
	{
		super(par1, par2, par3, 200, 20, null);
		enabled = true;
		visible = true;
		id = par1;
		xPosition = par2;
		yPosition = par3;
		width = par4;
		height = par5;
		displayString = null;

		u = par7;
		v = par8;
		baseU = u;
		baseV = v;
		filepath = file;

		hasToolTip = false;
		modClass = mod;
	}

	/** Draw a Gui Button with an image background and text overlay.
	 *Args: id, x, y, width, height, u, v, text overlay, text color, shadow, filepath, class root */
	public ImagedGuiButton(int par1, int par2, int par3, int par4, int par5, int par7, int par8, String par6Str, int par9, boolean par10, String file, Class mod)
	{
		super(par1, par2, par3, 200, 20, par6Str);
		enabled = true;
		visible = true;
		id = par1;
		xPosition = par2;
		yPosition = par3;
		width = par4;
		height = par5;
		displayString = par6Str;

		u = par7;
		v = par8;
		baseU = u;
		baseV = v;
		color = par9;
		shadow = par10;
		filepath = file;

		hasToolTip = false;
		modClass = mod;
	}

	/** Draw a Gui Button with an image background and text tooltip. Args: id, x, y, width, height, u, v, filepath, text tooltip, text color, shadow */
	public ImagedGuiButton(int par1, int par2, int par3, int par4, int par5, int par7, int par8, String file, String par6Str, int par9, boolean par10, Class mod)
	{
		super(par1, par2, par3, 200, 20, par6Str);
		enabled = true;
		visible = true;
		id = par1;
		xPosition = par2;
		yPosition = par3;
		width = par4;
		height = par5;
		displayString = par6Str;

		u = par7;
		v = par8;
		baseU = u;
		baseV = v;
		color = par9;
		shadow = par10;
		filepath = file;

		hasToolTip = true;
		modClass = mod;
	}

	public ImagedGuiButton setTextAlign(TextAlign ta) {
		alignment = ta;
		return this;
	}

	protected final String getButtonTexture() {
		return filepath;
	}

	@Override
	public final void drawButton(Minecraft mc, int mx, int my) {
		this.updateVisibility();

		if (visible && !invisible) {
			field_146123_n = this.isPositionWithin(mx, my);
			int k = this.getHoverState(field_146123_n);

			this.renderButton();

			this.mouseDragged(mc, mx, my);
			if (displayString != null && !hasToolTip) {
				//ReikaTextureHelper.bindFontTexture();
				//GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
				renderer.drawString(displayString, this.getLabelX()+alignment.getDX(renderer, displayString), this.getLabelY(), this.getLabelColor(), shadow);
			}
			else if (k == 2 && displayString != null && hasToolTip) {
				this.drawToolTip(mc, mx, my);
			}
			GL11.glColor4d(1, 1, 1, 1);

			if (icon != null) {
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_LIGHTING);
				BlendMode.DEFAULT.apply();
				ReikaTextureHelper.bindTerrainTexture();
				int dx = (width-iconWidth)/2;
				int dy = (height-iconHeight)/2;
				ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(xPosition+dx, yPosition+dy, icon, iconWidth, iconHeight);
				GL11.glPopAttrib();
			}

			if (!lastHover && field_146123_n && ticks > 1) {
				this.onHoverTo();
			}

			if (!field_146123_n)
				isClicked = false;

			lastHover = field_146123_n;
			hoverTicks = lastHover ? hoverTicks+1 : 0;
			if (lastHover) {
				hoverFade = Math.min(1, hoverFade+hoverFadeSpeedUp);
			}
			else {
				hoverFade = Math.max(0, hoverFade-hoverFadeSpeedDown);
			}
			ticks++;
		}
		else {
			isClicked = false;
		}
		if (!enabled)
			isClicked = false;
	}

	protected void updateVisibility() {

	}

	@Override
	public final void mouseReleased(int x, int y) {
		isClicked = false;
	}

	protected void renderButton() {
		//int tex = GL11.GL_TEXTURE_BINDING_2D;
		ReikaTextureHelper.bindTexture(modClass, this.getButtonTexture());
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexturedModalRect(xPosition, yPosition, u, v, width, height);
	}

	@Override
	public final boolean mousePressed(Minecraft mc, int x, int y) {
		if (visible && this.isPositionWithin(x, y)) {
			if (enabled) {
				isClicked = true;
				return true;
			}
			else {
				this.onFailedClick();
			}
		}
		return false;
	}

	protected void onFailedClick() {

	}

	protected boolean isPositionWithin(int mx, int my) {
		return mx >= xPosition && my >= yPosition && mx < xPosition+width && my < yPosition+height;
	}

	@Override
	public final int getHoverState(boolean flag) {
		int ret = super.getHoverState(flag);
		u = baseU;
		v = baseV;
		this.modifyTextureUV();
		if (ret == 2) {
			this.getHoveredTextureCoordinates();
		}
		return ret;
	}

	protected void modifyTextureUV() {

	}

	protected void getHoveredTextureCoordinates() {

	}

	protected void onHoverTo() {

	}

	@Override
	public void drawTexturedModalRect(int x, int y, int u, int v, int w, int h) {
		this.drawTexturedModalRect(x, y, u, v, w, h, 0xffffff, 255);
	}

	public void drawTexturedModalRect(int x, int y, int u, int v, int w, int h, int c, int a) {
		float f = 1F/textureSize;
		Tessellator v5 = Tessellator.instance;
		v5.startDrawingQuads();
		v5.setColorRGBA_I(c, a);
		v5.addVertexWithUV(x+0, y+h, zLevel, (u+0)*f, (v+h)*f);
		v5.addVertexWithUV(x+w, y+h, zLevel, (u+w)*f, (v+h)*f);
		v5.addVertexWithUV(x+w, y+0, zLevel, (u+w)*f, (v+0)*f);
		v5.addVertexWithUV(x+0, y+0, zLevel, (u+0)*f, (v+0)*f);
		v5.draw();
	}

	@Override
	public void func_146113_a(SoundHandler sh)
	{
		sh.playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation(sound), 1.0F));
	}

	protected int getLabelX() {
		int base = textOffset+xPosition;
		switch(alignment) {
			case CENTER:
				return base+width/2-renderer.getStringWidth(displayString)+1;
			case LEFT:
				return base+2;
			case RIGHT:
				return base+width-4-renderer.getStringWidth(displayString)*2;
			default:
				return base;
		}
	}

	protected int getLabelY() {
		return yPosition+(height-8)/2;
	}

	public int getLabelColor() {
		return color;
	}

	protected void drawToolTip(Minecraft mc, int mx, int my) {
		ReikaGuiAPI.instance.drawTooltip(mc.fontRenderer, displayString);
		ReikaTextureHelper.bindFontTexture();
	}

	public final float getHoverFade() {
		return hoverFade;
	}

	public boolean isClicked() {
		return isClicked;
	}

	public static enum TextAlign {
		LEFT(),
		CENTER(),
		RIGHT();

		public int getDX(FontRenderer f, String s) {
			switch(this) {
				case CENTER:
					return f.getStringWidth(s)/2;
				case LEFT:
					return 0;
				case RIGHT:
					return f.getStringWidth(s);
				default:
					return 0;
			}
		}
	}

}
