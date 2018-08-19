/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Extras;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ChangePacketRenderer {

	public static final ChangePacketRenderer instance = new ChangePacketRenderer();

	private final HashMap<Coordinate, RenderBox> data = new HashMap();

	public static boolean isActive = false;

	private static final int LIFETIME = 60;

	private ChangePacketRenderer() {

	}

	public static void onBlockChangePacket(int x, int y, int z, Block b, int meta) {
		if (isActive) {
			//DragonAPICore.log("Received block change packet @ "+x+", "+y+", "+z+" for "+b.getClass().getName()+" | "+b.getUnlocalizedName()+" meta "+meta);
			instance.addCoordinate(x, y, z, false);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void onChunkRerender(int mx, int my, int mz, int px, int py, int pz, WorldRenderer r) {
		if (isActive) {
			if (py == 257 && my == -1 && px-mx == 17 && pz-mz == 17) { //chunk (un)loading
				//long ckey = ChunkCoordIntPair.chunkXZ2Int(mx >> 4, mz >> 4);
				return;
			}
			//DragonAPICore.log("Triggering rerender @ "+new BlockBox(mx, my, mz, px, py, pz));
			//Thread.dumpStack();
			if (px-mx > 0 && py-my > 0 && pz-mz > 0) {
				for (int x = mx+1; x <= px-1; x++) {
					for (int y = my+1; y <= py-1; y++) {
						for (int z = mz+1; z <= pz-1; z++) {
							instance.addCoordinate(x, y, z, true);
						}
					}
				}
			}
			else {
				instance.addCoordinate(px, py, pz, true);
			}
		}
	}

	public void addCoordinate(int x, int y, int z, boolean reRender) {
		Coordinate c = new Coordinate(x, y, z);
		if (!reRender) {
			RenderBox rb = data.get(c);
			if (rb != null && rb.isReRender) {
				rb.renderLife = LIFETIME;
				return;
			}
		}
		data.put(c, new RenderBox(c, reRender ? 0x45ff0000 : 0x60ffd050, reRender));
	}

	public void clear() {
		data.clear();
	}

	public void render() {
		if (data.isEmpty())
			return;

		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		//GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		BlendMode.DEFAULT.apply();
		GL11.glDepthMask(false);
		ReikaRenderHelper.disableEntityLighting();
		GL11.glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

		Tessellator v5 = Tessellator.instance;

		v5.startDrawingQuads();

		HashMap<Coordinate, Integer> subChunks = new HashMap();
		Iterator<Entry<Coordinate, RenderBox>> it = data.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Coordinate, RenderBox> e = it.next();
			Coordinate c = e.getKey();
			RenderBox b = e.getValue();
			this.renderPosition(v5, b);
			Integer get = subChunks.get(b.subchunk);
			int put = Math.max(b.renderLife, get != null ? Math.abs(get.intValue()) : 0);
			if (b.isReRender || (get != null && get.intValue() < 0))
				put = -put;
			subChunks.put(b.subchunk, put);

			if (b.renderLife > 1) {
				b.renderLife--;
			}
			else {
				it.remove();
			}
		}
		v5.draw();

		float w = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
		GL11.glLineWidth(6);
		v5.startDrawing(GL11.GL_LINES);

		for (Coordinate c : subChunks.keySet()) {
			int val = subChunks.get(c);
			float f = (float)Math.abs(val)/LIFETIME;
			f = f <= 0.5 ? f*2 : 1;
			double o = 0.03125;
			v5.setColorRGBA_I(0xffffff, (int)(255*f));

			v5.addVertex(c.xCoord, c.yCoord, c.zCoord);
			v5.addVertex(c.xCoord+16, c.yCoord, c.zCoord);

			v5.addVertex(c.xCoord+16, c.yCoord, c.zCoord);
			v5.addVertex(c.xCoord+16, c.yCoord, c.zCoord+16);

			v5.addVertex(c.xCoord+16, c.yCoord, c.zCoord+16);
			v5.addVertex(c.xCoord, c.yCoord, c.zCoord+16);

			v5.addVertex(c.xCoord, c.yCoord, c.zCoord+16);
			v5.addVertex(c.xCoord, c.yCoord, c.zCoord);

			v5.addVertex(c.xCoord, c.yCoord+16, c.zCoord);
			v5.addVertex(c.xCoord+16, c.yCoord+16, c.zCoord);

			v5.addVertex(c.xCoord+16, c.yCoord+16, c.zCoord);
			v5.addVertex(c.xCoord+16, c.yCoord+16, c.zCoord+16);

			v5.addVertex(c.xCoord+16, c.yCoord+16, c.zCoord+16);
			v5.addVertex(c.xCoord, c.yCoord+16, c.zCoord+16);

			v5.addVertex(c.xCoord, c.yCoord+16, c.zCoord+16);
			v5.addVertex(c.xCoord, c.yCoord+16, c.zCoord);

			v5.addVertex(c.xCoord, c.yCoord, c.zCoord);
			v5.addVertex(c.xCoord, c.yCoord+16, c.zCoord);

			v5.addVertex(c.xCoord+16, c.yCoord, c.zCoord);
			v5.addVertex(c.xCoord+16, c.yCoord+16, c.zCoord);

			v5.addVertex(c.xCoord+16, c.yCoord, c.zCoord+16);
			v5.addVertex(c.xCoord+16, c.yCoord+16, c.zCoord+16);

			v5.addVertex(c.xCoord, c.yCoord, c.zCoord+16);
			v5.addVertex(c.xCoord, c.yCoord+16, c.zCoord+16);

			v5.setColorRGBA_I(0xffffff, (int)(48*f));
			for (int i = 1; i <= 15; i++) {
				v5.addVertex(c.xCoord+i, c.yCoord, c.zCoord);
				v5.addVertex(c.xCoord+i, c.yCoord+16, c.zCoord);

				v5.addVertex(c.xCoord+i, c.yCoord, c.zCoord+16);
				v5.addVertex(c.xCoord+i, c.yCoord+16, c.zCoord+16);

				v5.addVertex(c.xCoord, c.yCoord, c.zCoord+i);
				v5.addVertex(c.xCoord, c.yCoord+16, c.zCoord+i);

				v5.addVertex(c.xCoord+16, c.yCoord, c.zCoord+i);
				v5.addVertex(c.xCoord+16, c.yCoord+16, c.zCoord+i);
			}

			if (val < 0) {
				v5.setColorRGBA_I(0xff50e0, (int)(255*f));
				v5.addVertex(c.xCoord+0.5, c.yCoord+0.5, c.zCoord+0.5);
				v5.addVertex(c.xCoord+15.5, c.yCoord+0.5, c.zCoord+0.5);

				v5.addVertex(c.xCoord+15.5, c.yCoord+0.5, c.zCoord+0.5);
				v5.addVertex(c.xCoord+15.5, c.yCoord+0.5, c.zCoord+15.5);

				v5.addVertex(c.xCoord+15.5, c.yCoord+0.5, c.zCoord+15.5);
				v5.addVertex(c.xCoord+0.5, c.yCoord+0.5, c.zCoord+15.5);

				v5.addVertex(c.xCoord+0.5, c.yCoord+0.5, c.zCoord+15.5);
				v5.addVertex(c.xCoord+0.5, c.yCoord+0.5, c.zCoord+0.5);

				v5.addVertex(c.xCoord+0.5, c.yCoord+15.5, c.zCoord+0.5);
				v5.addVertex(c.xCoord+15.5, c.yCoord+15.5, c.zCoord+0.5);

				v5.addVertex(c.xCoord+15.5, c.yCoord+15.5, c.zCoord+0.5);
				v5.addVertex(c.xCoord+15.5, c.yCoord+15.5, c.zCoord+15.5);

				v5.addVertex(c.xCoord+15.5, c.yCoord+15.5, c.zCoord+15.5);
				v5.addVertex(c.xCoord+0.5, c.yCoord+15.5, c.zCoord+15.5);

				v5.addVertex(c.xCoord+0.5, c.yCoord+15.5, c.zCoord+15.5);
				v5.addVertex(c.xCoord+0.5, c.yCoord+15.5, c.zCoord+0.5);

				v5.addVertex(c.xCoord+0.5, c.yCoord+0.5, c.zCoord+0.5);
				v5.addVertex(c.xCoord+0.5, c.yCoord+15.5, c.zCoord+0.5);

				v5.addVertex(c.xCoord+15.5, c.yCoord+0.5, c.zCoord+0.5);
				v5.addVertex(c.xCoord+15.5, c.yCoord+15.5, c.zCoord+0.5);

				v5.addVertex(c.xCoord+15.5, c.yCoord+0.5, c.zCoord+15.5);
				v5.addVertex(c.xCoord+15.5, c.yCoord+15.5, c.zCoord+15.5);

				v5.addVertex(c.xCoord+0.5, c.yCoord+0.5, c.zCoord+15.5);
				v5.addVertex(c.xCoord+0.5, c.yCoord+15.5, c.zCoord+15.5);
			}
		}

		v5.draw();
		GL11.glLineWidth(w);

		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

	private void renderPosition(Tessellator v5, RenderBox b) {
		float f = (float)b.renderLife/LIFETIME;
		f = f <= 0.5 ? f*2 : 1;
		double o = 0.03125;
		v5.setColorRGBA_I(b.renderColor, (int)(ReikaColorAPI.getAlpha(b.renderColor)*f));
		Coordinate c = b.location;

		v5.addVertex(c.xCoord-o, c.yCoord-o, c.zCoord-o);
		v5.addVertex(c.xCoord+1+o, c.yCoord-o, c.zCoord-o);
		v5.addVertex(c.xCoord+1+o, c.yCoord-o, c.zCoord+1+o);
		v5.addVertex(c.xCoord-o, c.yCoord-o, c.zCoord+1+o);

		v5.addVertex(c.xCoord-o, c.yCoord+1+o, c.zCoord+1+o);
		v5.addVertex(c.xCoord+1+o, c.yCoord+1+o, c.zCoord+1+o);
		v5.addVertex(c.xCoord+1+o, c.yCoord+1+o, c.zCoord-o);
		v5.addVertex(c.xCoord-o, c.yCoord+1+o, c.zCoord-o);

		v5.addVertex(c.xCoord+1+o, c.yCoord-o, c.zCoord-o);
		v5.addVertex(c.xCoord+1+o, c.yCoord+1+o, c.zCoord-o);
		v5.addVertex(c.xCoord+1+o, c.yCoord+1+o, c.zCoord+1+o);
		v5.addVertex(c.xCoord+1+o, c.yCoord-o, c.zCoord+1+o);

		v5.addVertex(c.xCoord-o, c.yCoord-o, c.zCoord+1+o);
		v5.addVertex(c.xCoord-o, c.yCoord+1+o, c.zCoord+1+o);
		v5.addVertex(c.xCoord-o, c.yCoord+1+o, c.zCoord-o);
		v5.addVertex(c.xCoord-o, c.yCoord-o, c.zCoord-o);

		v5.addVertex(c.xCoord+1+o, c.yCoord-o, c.zCoord+1+o);
		v5.addVertex(c.xCoord+1+o, c.yCoord+1+o, c.zCoord+1+o);
		v5.addVertex(c.xCoord-o, c.yCoord+1+o, c.zCoord+1+o);
		v5.addVertex(c.xCoord-o, c.yCoord-o, c.zCoord+1+o);

		v5.addVertex(c.xCoord-o, c.yCoord-o, c.zCoord-o);
		v5.addVertex(c.xCoord-o, c.yCoord+1+o, c.zCoord-o);
		v5.addVertex(c.xCoord+1+o, c.yCoord+1+o, c.zCoord-o);
		v5.addVertex(c.xCoord+1+o, c.yCoord-o, c.zCoord-o);
	}

	private static class RenderBox {

		private final Coordinate subchunk;
		private final Coordinate location;
		private final int renderColor;
		private int renderLife = LIFETIME;
		private final boolean isReRender;

		private RenderBox(Coordinate c, int clr, boolean re) {
			location = c;
			renderColor = clr;
			subchunk = new Coordinate(ReikaMathLibrary.roundDownToX(16, c.xCoord), ReikaMathLibrary.roundDownToX(16, c.yCoord), ReikaMathLibrary.roundDownToX(16, c.zCoord));
			isReRender = re;
		}

	}


}
