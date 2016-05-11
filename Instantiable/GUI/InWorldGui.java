/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.GUI;

import java.awt.Rectangle;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class InWorldGui {

	public final int xSize;
	public final int ySize;

	public int displayWidth;
	public int displayHeight;

	private ArrayList<Button> buttons = new ArrayList();

	public InWorldGui(int x, int y) {
		xSize = x;
		ySize = y;
		this.init();
	}

	protected void init() {
		buttons.clear();
	}

	protected void onButtonClicked(int id) {

	}

	protected final void addButton(int id, int x, int y, int w, int h) {
		buttons.add(new Button(id, x, y, w, h));
	}

	protected final void addButton(int id, int x, int y, int w, int h, int u, int v, String tex, Class root) {
		buttons.add(new TexturedButton(id, x, y, w, h, u, v, root, tex));
	}

	public void click(int x, int y, int button) {
		//ReikaJavaLibrary.pConsole(x+", "+y);
		this.init();
		for (Button b : buttons) {
			if (b.bounds.contains(x, y)) {
				this.onButtonClicked(b.ID);
				//ReikaJavaLibrary.pConsole(b);
				if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
					this.playSound("random.click");
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void playSound(String s) {
		Minecraft.getMinecraft().thePlayer.playSound(s, 1, 1);
	}

	@SideOnly(Side.CLIENT)
	public final void render(float ptick) {
		Tessellator v5 = Tessellator.instance;
		v5.startDrawingQuads();
		this.bindMainTexture();
		double u = xSize/256D;
		double v = ySize/256D;
		GL11.glPushMatrix();
		GL11.glTranslated(-1, 0, 0);
		GL11.glScaled(displayWidth, displayHeight, 1);
		v5.addVertexWithUV(0, 0, 0, 0, v);
		v5.addVertexWithUV(1, 0, 0, u, v);
		v5.addVertexWithUV(1, 1, 0, u, 0);
		v5.addVertexWithUV(0, 1, 0, 0, 0);
		v5.draw();

		v5.startDrawingQuads();
		for (Button b : buttons) {
			b.bindTexture();
			double dx = (double)b.bounds.x/xSize;
			double dy = 1-(double)(b.bounds.y+b.bounds.height)/ySize;
			double dx2 = dx+(double)b.bounds.width/xSize;
			double dy2 = dy+(double)b.bounds.height/ySize;
			u = 0;
			v = 0.2578125;
			double du = 0.77734375;
			double dv = 0.33203125;
			if (b instanceof TexturedButton) {
				TexturedButton tb = (TexturedButton)b;
				u = tb.u;
				v = tb.v;
				du = u+b.bounds.width/256D;
				dv = v+b.bounds.height/256D;
			}
			double t = 0.001;
			v5.addVertexWithUV(dx, dy, t, u, dv);
			v5.addVertexWithUV(dx2, dy, t, du, dv);
			v5.addVertexWithUV(dx2, dy2, t, du, v);
			v5.addVertexWithUV(dx, dy2, t, u, v);
		}
		v5.draw();
		this.renderCallback(ptick);
		GL11.glPopMatrix();
	}

	protected void renderCallback(float ptick) {

	}

	public abstract void bindMainTexture();

	private static class Button {

		private final Rectangle bounds;
		public final int ID;

		private Button(int id, int x, int y, int w, int h) {
			bounds = new Rectangle(x, y, w, h);
			ID = id;
		}

		protected void bindTexture() {
			ReikaTextureHelper.bindGuiTexture();
		}

	}

	private static class TexturedButton extends Button {

		private final String tex;
		private final Class root;
		private final double u;
		private final double v;

		private TexturedButton(int id, int x, int y, int w, int h, int u, int v, Class c, String t) {
			super(id, x, y, w, h);
			tex = t;
			root = c;
			this.u = u/256D;
			this.v = v/256D;
		}

		@Override
		protected void bindTexture() {
			ReikaTextureHelper.bindTexture(root, tex);
		}
	}

}
