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
import java.util.HashMap;
import java.util.TreeMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;


public class PianoWheel {

	public final MusicKey root;
	public final int octaveRange;
	public final int radius;

	public final int originX;
	public final int originY;

	private final int innerRadius;

	private final PianoGui guiInstance;

	private final TreeMap<Integer, Ring> rings = new TreeMap();
	private final ArrayList<Ring> ringList = new ArrayList();

	private final HashMap<MusicKey, Integer> colorTag = new HashMap();

	private static final int WHITE_COLOR = 0xEFEFEF;
	private static final int BLACK_COLOR = 0x161616;

	public PianoWheel(PianoGui gui, MusicKey start, int octaves, int r, int x, int y, boolean linearDivision) {
		root = start;
		octaveRange = octaves;
		radius = r;
		guiInstance = gui;

		originX = x;
		originY = y;

		innerRadius = (int)(radius/(octaveRange*1.8));

		int tr = innerRadius;

		int[] split = new int[octaveRange];
		for (int i = 0; i < octaveRange; i++) {
			split[i] = (radius-innerRadius)/octaveRange;
		}

		if (!linearDivision) {
			int mid = octaveRange/2;
			int d = 12/octaveRange;
			if (octaveRange%2 == 0) {
				for (int i = 0; i < mid; i++) {
					//ReikaJavaLibrary.pConsole(i+" + "+d+" > "+(mid-1-i)+" & "+(mid+i)+" $ "+split[mid-1-i]+" & "+split[mid+i]);
					split[mid-1-i] += d*(i+1);
					split[mid+i] -= d*(i+1);
				}
			}
			else { //has ctr point
				for (int i = 1; i < mid; i++) {
					split[mid-i] += d*(i+1);
					split[mid+i] -= d*(i+1);
				}
			}
		}

		for (int i = 0; i < octaveRange; i++) {
			int dr = split[i];

			int rmin = tr;//(int)(r*Math.sqrt((i-1)*12D/12D/octaves));
			int rmax = tr+dr;//(int)(r*Math.sqrt(i*12D/12D/octaves));

			tr += dr;

			Ring rng = new Ring(rmin, rmax);
			for (int k = 0; k < 12; k++) {
				MusicKey key = MusicKey.getByIndex(root.ordinal()+1+k+i*12);
				rng.sections.put(k*30, key);
			}
			rings.put(rmin, rng);
			ringList.add(rng);
		}
	}

	public void mouseClicked(int b, int x, int y) {
		if (b == 0) {
			MusicKey key = this.getKey(x, y);
			if (key != null) {
				guiInstance.onKeyPressed(key);
				colorTag.put(key, 40);
			}
		}
	}

	private MusicKey getKey(int x, int y) {
		int dx = x-originX;
		int dy = y-originY;
		int r = (int)ReikaMathLibrary.py3d(dx, 0, dy);
		if (r > radius)
			return null;
		if (r <= innerRadius) {
			return root;
		}
		int ang = 90+(int)Math.toDegrees(Math.atan2(dy, dx))-15;

		//ReikaJavaLibrary.pConsole(ang);
		ang = (ang%360+360)%360;

		return this.getRing(r).getSection(ang);
	}

	private Ring getRing(int r) {
		return rings.get(rings.floorKey(r));
	}

	private int getColor(MusicKey key) {
		int c = key.getNote().isPure() ? WHITE_COLOR : BLACK_COLOR;

		if (colorTag.containsKey(key)) {
			int val = colorTag.get(key);
			float f = Math.min(1, val/32F);
			if (f > 0) {
				int c1 = guiInstance.getColor(key);
				if (f < 1) {
					c = ReikaColorAPI.mixColors(c1, c, f);
				}
				else {
					c = c1;
				}
				if (val > 1)
					colorTag.put(key, val-1);
				else
					colorTag.remove(key);
			}
			else {
			}
		}
		return c;
	}

	public void draw(boolean label, boolean verbose, boolean renderLines) {
		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
		Tessellator v5 = Tessellator.instance;

		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		v5.startDrawing(GL11.GL_TRIANGLE_FAN);
		v5.setColorOpaque_I(this.getColor(root));
		v5.addVertex(originX, originY, 0);
		for (int i = 0; i <= 360; i += 2) {
			double dx = innerRadius*Math.sin(Math.toRadians(i));
			double dy = innerRadius*Math.cos(Math.toRadians(i));

			v5.addVertex(originX+dx, originY+dy, 0);
		}
		v5.draw();

		if (label) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			String s = root.displayName();
			fr.drawString(s, originX-fr.getStringWidth(s)/2, originY-fr.FONT_HEIGHT/2, root.getNote().isPure() ? 0x000000 : 0xffffff);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}

		for (int i = 0; i < octaveRange; i++) {
			Ring r = ringList.get(i);

			for (int k = 0; k < 12; k++) {
				MusicKey key = MusicKey.getByIndex(root.ordinal()+1+k+i*12);
				if (key == null) {
					ReikaJavaLibrary.pConsole((1+k+i*12)+" above "+root+" is null??  i="+i+",  k="+k);
					continue;
				}
				//ReikaJavaLibrary.pConsole("i="+i+", k="+k+" > "+key.name());

				int a1 = k*30;
				int a2 = (k+1)*30;

				v5.startDrawing(GL11.GL_TRIANGLE_STRIP);
				v5.setColorOpaque_I(this.getColor(key));
				for (int a = a1; a <= a2; a += 2) {
					double dx1 = r.innerRadius*Math.sin(Math.toRadians(180-a-15));
					double dy1 = r.innerRadius*Math.cos(Math.toRadians(180-a-15));
					double dx2 = r.outerRadius*Math.sin(Math.toRadians(180-a-15));
					double dy2 = r.outerRadius*Math.cos(Math.toRadians(180-a-15));
					v5.addVertex(originX+dx2, originY+dy2, 0);
					v5.addVertex(originX+dx1, originY+dy1, 0);
				}
				v5.draw();

				if (label && (verbose || key.getNote() == root.getNote())) {
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					int mida = k*30+15;
					double midr = r.innerRadius+(r.outerRadius-r.innerRadius)/2D;
					String s = key.displayName();
					double px = originX+midr*Math.sin(Math.toRadians(180-mida-15))-fr.getStringWidth(s)/2D;
					double py = originY+midr*Math.cos(Math.toRadians(180-mida-15))-fr.FONT_HEIGHT/2D;
					fr.drawString(s, (int)px, (int)py, key.getNote().isPure() ? 0x000000 : 0xffffff);
					GL11.glDisable(GL11.GL_TEXTURE_2D);
				}

			}

			if (renderLines) {
				v5.startDrawing(GL11.GL_LINE_STRIP);
				v5.setColorOpaque_I(0x000000);
				for (int k = 0; k <= 360; k += 2) {
					double dx = r.innerRadius*Math.sin(Math.toRadians(k));
					double dy = r.innerRadius*Math.cos(Math.toRadians(k));
					v5.addVertex(originX+dx, originY+dy, 0);
				}
				v5.draw();

				v5.startDrawing(GL11.GL_LINE_STRIP);
				v5.setColorOpaque_I(0x000000);
				for (int k = 0; k <= 360; k += 2) {
					double dx = r.outerRadius*Math.sin(Math.toRadians(k));
					double dy = r.outerRadius*Math.cos(Math.toRadians(k));
					v5.addVertex(originX+dx, originY+dy, 0);
				}
				v5.draw();
			}
		}

		if (renderLines) {
			v5.startDrawing(GL11.GL_LINES);
			v5.setColorOpaque_I(0x000000);
			for (int k = 0; k < 12; k++) {
				int a1 = k*30;
				int a2 = (k+1)*30;

				double dx1 = innerRadius*Math.sin(Math.toRadians(a1-15));
				double dy1 = innerRadius*Math.cos(Math.toRadians(a1-15));
				double dx2 = radius*Math.sin(Math.toRadians(a1-15));
				double dy2 = radius*Math.cos(Math.toRadians(a1-15));
				v5.addVertex(originX+dx1, originY+dy1, 0);
				v5.addVertex(originX+dx2, originY+dy2, 0);

				dx1 = innerRadius*Math.sin(Math.toRadians(a2-15));
				dy1 = innerRadius*Math.cos(Math.toRadians(a2-15));
				dx2 = radius*Math.sin(Math.toRadians(a2-15));
				dy2 = radius*Math.cos(Math.toRadians(a2-15));
				v5.addVertex(originX+dx1, originY+dy1, 0);
				v5.addVertex(originX+dx2, originY+dy2, 0);
			}
			v5.draw();
		}

		GL11.glPopAttrib();
	}

	private static class Ring {

		private final TreeMap<Integer, MusicKey> sections = new TreeMap();

		private final int innerRadius;
		private final int outerRadius;

		private Ring(int r1, int r2) {
			innerRadius = r1;
			outerRadius = r2;
		}

		private MusicKey getSection(int ang) {
			return sections.get(sections.floorKey(ang));
		}

	}

	public static interface PianoGui {

		public void onKeyPressed(MusicKey key);
		public int getColor(MusicKey key);

	}

}
