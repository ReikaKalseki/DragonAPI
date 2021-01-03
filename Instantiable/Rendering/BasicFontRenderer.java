/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Rendering;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import Reika.DragonAPI.Instantiable.Event.Client.TextureReloadEvent;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gnu.trove.map.hash.TCharIntHashMap;

/** Cloned from the vanilla FontRenderer to allow for rewrites and to avoid Optifine compat issues */
@SideOnly(Side.CLIENT)
public abstract class BasicFontRenderer extends FontRenderer implements IResourceManagerReloadListener {

	private static final TCharIntHashMap charMap = new TCharIntHashMap(256);
	private static final TCharIntHashMap formatMap = new TCharIntHashMap(32);

	private static final ResourceLocation[] unicodePageLocations = new ResourceLocation[256];
	/** Array of width of all the characters in default.png */
	protected int[] charWidth = new int[256];
	/** the height in pixels of default text */
	public final int FONT_HEIGHT = 9;
	public Random fontRandom = new Random();
	/** Array of the start/end column (in upper/lower nibble) for every glyph in the /font directory. */
	protected byte[] glyphWidth = new byte[65536];
	/**
	 * Array of RGB triplets defining the 16 standard chat colors followed by 16 darker version of the same colors for
	 * drop shadows.
	 */
	protected final int[] colorCode = new int[32];
	/** The RenderEngine used to load and setup glyph textures. */
	/** Current X coordinate at which to draw the next character. */
	protected float posX;
	/** Current Y coordinate at which to draw the next character. */
	protected float posY;
	/** If true, strings should be rendered with Unicode fonts instead of the default.png font */
	private boolean unicodeFlag;
	/** If true, the Unicode Bidirectional Algorithm should be run before rendering any string. */
	protected boolean bidiFlag;
	/** Used to specify new red value for the current color. */
	protected float red;
	/** Used to specify new blue value for the current color. */
	protected float blue;
	/** Used to specify new green value for the current color. */
	protected float green;
	/** Used to speify new alpha value for the current color. */
	protected float alpha;
	/** Text color of the currently rendering string. */
	protected int textColor;
	/** Set if the "k" style (random) is active in currently rendering string */
	protected boolean randomStyle;
	/** Set if the "l" style (bold) is active in currently rendering string */
	protected boolean boldStyle;
	/** Set if the "o" style (italic) is active in currently rendering string */
	protected boolean italicStyle;
	/** Set if the "n" style (underlined) is active in currently rendering string */
	protected boolean underlineStyle;
	/** Set if the "m" style (strikethrough) is active in currently rendering string */
	protected boolean strikethroughStyle;

	protected Kerning kerning = Kerning.NORMAL;
	protected String currentString;

	private WipeEffect wipe;

	public boolean parseBBCode;

	public BasicFontRenderer(boolean unicode) {
		super(Minecraft.getMinecraft().gameSettings, ReikaTextureHelper.font, Minecraft.getMinecraft().renderEngine, unicode);
		unicodeFlag = unicode;

		for (int i = 0; i < 32; i++) {
			int j = (i >> 3 & 1) * 85;
			int k = (i >> 2 & 1) * 170+j;
			int l = (i >> 1 & 1) * 170+j;
			int i1 = (i >> 0 & 1) * 170+j;

			if (i == 6) {
				k += 85;
			}

			if (Minecraft.getMinecraft().gameSettings.anaglyph) {
				int j1 = (k * 30+l * 59+i1 * 11) / 100;
				int k1 = (k * 30+l * 70) / 100;
				int l1 = (k * 30+i1 * 70) / 100;
				k = j1;
				l = k1;
				i1 = l1;
			}

			if (i >= 16) {
				k /= 4;
				l /= 4;
				i1 /= 4;
			}

			colorCode[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
		}

		this.readGlyphSizes();
	}

	@SubscribeEvent
	public final void reloadTextures(TextureReloadEvent evt) {
		this.onReload();
	}

	@Override
	public final void onResourceManagerReload(IResourceManager irm) {
		this.onReload();
	}

	protected void onReload() {
		this.readFontTexture();
	}

	protected abstract void readFontTexture();

	private void readGlyphSizes() {
		try {
			glyphWidth = new byte[65536];
			InputStream in = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("font/glyph_sizes.bin")).getInputStream();
			in.read(glyphWidth);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Pick how to render a single character and return the width used.
	 */
	protected float renderCharAtPos(int charIndex, char c, boolean italic, int index) {
		if (wipe != null && wipe.lastChar == index+1) {
			GL11.glColor4f(ReikaColorAPI.getRed(wipe.wipeColor)/255F, ReikaColorAPI.getGreen(wipe.wipeColor)/255F, ReikaColorAPI.getBlue(wipe.wipeColor)/255F, ReikaColorAPI.getAlpha(wipe.wipeColor)/255F);
		}
		else {
			GL11.glColor4f(red, blue, green, alpha);
		}
		return c == 32 ? 4.0F : (!unicodeFlag && charMap.contains(c) ? this.renderCharFraction(charIndex, italic, this.getFraction(index)) : this.renderUnicodeChar(c, italic));
	}

	private float getFraction(int index) {
		return wipe != null && wipe.lastChar == index+1 ? wipe.charFraction : 1;
	}

	@Override
	protected float renderDefaultChar(int charIndex, boolean italic) {
		return this.renderCharFraction(charIndex, italic, 1);
	}

	/**
	 * Render a single character with the default.png font at current (posX,posY) location...
	 */
	protected float renderCharFraction(int charIndex, boolean italic, float fraction) {
		float columnPos = charIndex%16*8;
		float rowPos = charIndex/16*8;

		float f2 = italic ? 1F : 0F;

		if (this.needsGLBlending())
			GL11.glEnable(GL11.GL_BLEND);

		this.bindTexture();
		float f3 = charWidth[charIndex]-0.01F;
		float f4 = f3*fraction;

		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		GL11.glTexCoord2f(columnPos/128F, rowPos/128F);
		GL11.glVertex3f(posX+f2, posY, 0F);
		GL11.glTexCoord2f(columnPos/128F, (rowPos+7.99F)/128F);
		GL11.glVertex3f(posX-f2, posY+7.99F, 0F);
		GL11.glTexCoord2f((columnPos+f4-1F)/128F, rowPos/128F);
		GL11.glVertex3f(posX+f4-1F+f2, posY, 0F);
		GL11.glTexCoord2f((columnPos+f4-1F)/128F, (rowPos+7.99F)/128F);
		GL11.glVertex3f(posX+f4-1F-f2, posY+7.99F, 0F);
		GL11.glEnd();

		return charWidth[charIndex]+kerning.spaceModifier;
	}

	protected abstract void bindTexture();

	protected boolean needsGLBlending() {
		return false;
	}

	private ResourceLocation getUnicodePageLocation(int gl) {
		if (unicodePageLocations[gl] == null) {
			unicodePageLocations[gl] = new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", new Object[] {gl}));
		}
		return unicodePageLocations[gl];
	}

	/**
	 * Load one of the /font/glyph_XX.png into a new GL texture and store the texture ID in glyphTextureName array.
	 */
	private void loadGlyphTexture(int gl) {
		Minecraft.getMinecraft().renderEngine.bindTexture(this.getUnicodePageLocation(gl));
	}

	/**
	 * Render a single Unicode character at current (posX,posY) location using one of the /font/glyph_XX.png files...
	 */
	@Override
	protected float renderUnicodeChar(char c, boolean italic) {
		if (glyphWidth[c] == 0) {
			return 0.0F;
		}
		else {
			int i = c / 256;
			this.loadGlyphTexture(i);
			int j = glyphWidth[c] >>> 4;
			int k = glyphWidth[c] & 15;
			float f = j;
			float f1 = k+1;
			float f2 = c % 16 * 16+f;
			float f3 = (c & 255) / 16 * 16;
			float f4 = f1-f-0.02F;
			float f5 = italic ? 1.0F : 0.0F;
			GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
			GL11.glTexCoord2f(f2 / 256.0F, f3 / 256.0F);
			GL11.glVertex3f(posX+f5, posY, 0.0F);
			GL11.glTexCoord2f(f2 / 256.0F, (f3+15.98F) / 256.0F);
			GL11.glVertex3f(posX-f5, posY+7.99F, 0.0F);
			GL11.glTexCoord2f((f2+f4) / 256.0F, f3 / 256.0F);
			GL11.glVertex3f(posX+f4 / 2.0F+f5, posY, 0.0F);
			GL11.glTexCoord2f((f2+f4) / 256.0F, (f3+15.98F) / 256.0F);
			GL11.glVertex3f(posX+f4 / 2.0F-f5, posY+7.99F, 0.0F);
			GL11.glEnd();
			return (f1-f) / 2.0F+1.0F;
		}
	}

	/**
	 * Draws the specified string with a shadow.
	 */
	@Override
	public final int drawStringWithShadow(String sg, int x, int y, int color) {
		return this.drawString(sg, x, y, color, true);
	}

	/**
	 * Draws the specified string.
	 */
	@Override
	public final int drawString(String sg, int x, int y, int color) {
		return this.drawString(sg, x, y, color, false);
	}

	/**
	 * Draws the specified string. Args: string, x, y, color, dropShadow
	 */
	@Override
	public final int drawString(String sg, int x, int y, int color, boolean shadow) {
		return this.doDrawString(sg, x, y, color, shadow);
	}

	public final int drawStringFloatPos(String sg, float x, float y, int color, boolean shadow) {
		wipe = null;
		return this.doDrawString(sg, x, y, color, shadow);
	}

	private int doDrawString(String sg, float x, float y, int color, boolean shadow) {
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		this.resetStyles();
		int l;

		if (shadow) {
			l = this.renderString(sg, x+1, y+1, color, true);
			l = Math.max(l, this.renderString(sg, x, y, color, false));
		}
		else {
			l = this.renderString(sg, x, y, color, false);
		}

		return l;
	}

	public final int drawFractionalString(String sg, int x, int y, int color, int cflash, boolean shadow, float fraction) {
		if (fraction >= 1) {
			return this.drawString(sg, x, y, color, shadow);
		}
		int len = /*ReikaStringParser.stripSpaces(sg).length();/*/sg.length();
		float fs = fraction*len;
		float charFraction = ReikaMathLibrary.getDecimalPart(fs);
		int lastCharIndex = (int)fs+1;
		/*
		for (int i = lastCharIndex-1; i >= 0; i--) {
			if (sg.charAt(i) == ' ') {
				lastCharIndex++;
			}
		}
		//while (sg.charAt(lastCharIndex-1) == ' ' && lastCharIndex < sg.length()) {
		//	lastCharIndex++;
		//}
		 * */
		wipe = new WipeEffect(charFraction, lastCharIndex, cflash);
		String sg2 = sg.substring(0, lastCharIndex);
		return this.doDrawString(sg2, x, y, color, shadow);
	}

	/**
	 * Apply Unicode Bidirectional Algorithm to string and return a new possibly reordered string for visual rendering.
	 */
	private String bidiReorder(String sg) {
		try {
			Bidi bidi = new Bidi((new ArabicShaping(8)).shape(sg), 127);
			bidi.setReorderingMode(0);
			return bidi.writeReordered(2);
		}
		catch (ArabicShapingException as) {
			return sg;
		}
	}

	/**
	 * Reset all style flag fields in the class to false; called at the start of string rendering
	 */
	protected void resetStyles() {
		randomStyle = false;
		boldStyle = false;
		italicStyle = false;
		underlineStyle = false;
		strikethroughStyle = false;
	}

	/**
	 * Render a single line string at the current (posX,posY) and update posX
	 */
	private void renderStringAtPos(String sg, boolean shadow) {
		if (parseBBCode) {
			sg = sg.replaceAll("\\[i\\]", EnumChatFormatting.ITALIC.toString());
			sg = sg.replaceAll("\\[b\\]", EnumChatFormatting.BOLD.toString());
			sg = sg.replaceAll("\\[u\\]", EnumChatFormatting.UNDERLINE.toString());
			sg = sg.replaceAll("\\[s\\]", EnumChatFormatting.STRIKETHROUGH.toString());
			sg = sg.replaceAll("\\[/i\\]", EnumChatFormatting.RESET.toString());
			sg = sg.replaceAll("\\[/b\\]", EnumChatFormatting.RESET.toString());
			sg = sg.replaceAll("\\[/u\\]", EnumChatFormatting.RESET.toString());
			sg = sg.replaceAll("\\[/s\\]", EnumChatFormatting.RESET.toString());
		}
		currentString = sg;
		for (int i = 0; i < sg.length(); i++) {
			if (this.renderCharInString(sg, i, shadow))
				i++;
		}
	}

	private int getFormatIndex(char c) {
		return formatMap.get(c);
	}

	protected boolean renderCharInString(String sg, int idx, boolean shadow) { //return true if hit a formatter
		char c0 = sg.charAt(idx);
		int j;
		int k;

		if (c0 == 167 && idx+1 < sg.length()) {
			j = this.getFormatIndex(sg.toLowerCase().charAt(idx+1));

			if (j < 16) {
				randomStyle = false;
				boldStyle = false;
				strikethroughStyle = false;
				underlineStyle = false;
				italicStyle = false;

				if (j < 0 || j > 15)
					j = 15;

				if (shadow)
					j += 16;

				k = colorCode[j];
				textColor = k;
				GL11.glColor4f((k >> 16) / 255.0F, (k >> 8 & 255) / 255.0F, (k & 255) / 255.0F, alpha);
			}
			else if (j == 16) {
				randomStyle = true;
			}
			else if (j == 17) {
				boldStyle = true;
			}
			else if (j == 18) {
				strikethroughStyle = true;
			}
			else if (j == 19) {
				underlineStyle = true;
			}
			else if (j == 20) {
				italicStyle = true;
			}
			else if (j == 21) {
				randomStyle = false;
				boldStyle = false;
				strikethroughStyle = false;
				underlineStyle = false;
				italicStyle = false;
				GL11.glColor4f(red, blue, green, alpha);
			}

			idx++;
			return true;
		}
		else {
			j = this.getCharGridIndex(c0);

			if (randomStyle && j != -1) {
				do {
					k = fontRandom.nextInt(charWidth.length);
				}
				while (charWidth[j] != charWidth[k]);

				j = k;
			}

			float f1 = unicodeFlag ? 0.5F : 1.0F;
			boolean flag1 = (c0 == 0 || j == -1 || unicodeFlag) && shadow;

			if (flag1) {
				posX -= f1;
				posY -= f1;
			}

			float f = this.renderCharAtPos(j, c0, italicStyle, idx);

			if (flag1) {
				posX += f1;
				posY += f1;
			}

			if (boldStyle) {
				posX += f1;

				if (flag1) {
					posX -= f1;
					posY -= f1;
				}

				this.renderCharAtPos(j, c0, italicStyle, idx);
				posX -= f1;

				if (flag1) {
					posX += f1;
					posY += f1;
				}

				f++;
			}

			Tessellator v5 = Tessellator.instance;;

			if (strikethroughStyle) {
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				v5.startDrawingQuads();
				v5.addVertex(posX, posY+FONT_HEIGHT / 2, 0.0D);
				v5.addVertex(posX+f, posY+FONT_HEIGHT / 2, 0.0D);
				v5.addVertex(posX+f, posY+FONT_HEIGHT / 2-1.0F, 0.0D);
				v5.addVertex(posX, posY+FONT_HEIGHT / 2-1.0F, 0.0D);
				v5.draw();
				GL11.glEnable(GL11.GL_TEXTURE_2D);
			}

			if (underlineStyle) {
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				v5.startDrawingQuads();
				int l = underlineStyle ? -1 : 0;
				v5.addVertex(posX+l, posY+FONT_HEIGHT, 0.0D);
				v5.addVertex(posX+f, posY+FONT_HEIGHT, 0.0D);
				v5.addVertex(posX+f, posY+FONT_HEIGHT-1.0F, 0.0D);
				v5.addVertex(posX+l, posY+FONT_HEIGHT-1.0F, 0.0D);
				v5.draw();
				GL11.glEnable(GL11.GL_TEXTURE_2D);
			}

			posX += ((int)f);
			return false;
		}
	}

	/**
	 * Render string either left or right aligned depending on bidiFlag
	 */
	private int renderStringAligned(String sg, int x, int y, int p_78274_4_, int color, boolean shadow) {
		if (bidiFlag)
		{
			int i1 = this.getStringWidth(this.bidiReorder(sg));
			x = x+p_78274_4_-i1;
		}

		return this.renderString(sg, x, y, color, shadow);
	}

	/**
	 * Render single line string by setting GL color, current (posX,posY), and calling renderStringAtPos()
	 */
	private int renderString(String sg, float x, float y, int color, boolean shadow) {
		if (sg == null) {
			return 0;
		}
		else {
			if (bidiFlag)
				sg = this.bidiReorder(sg);

			if ((color & -67108864) == 0)
				color |= -16777216;

			if (shadow)
				color = (color & 16579836) >> 2 | color & -16777216;

				red = (color >> 16 & 255) / 255.0F;
				blue = (color >> 8 & 255) / 255.0F;
				green = (color & 255) / 255.0F;
				alpha = (color >> 24 & 255) / 255.0F;
				GL11.glColor4f(red, blue, green, alpha);
				posX = x;
				posY = y;
				this.renderStringAtPos(sg, shadow);
				return (int)posX;
		}
	}

	/**
	 * Returns the width of this string. Equivalent of FontMetrics.stringWidth(String s).
	 */
	@Override
	public final int getStringWidth(String sg) {
		if (sg == null) {
			return 0;
		}
		else {
			int i = 0;
			boolean flag = false;

			for (int j = 0; j < sg.length(); j++) {
				char c0 = sg.charAt(j);
				int k = this.getCharWidth(c0);

				if (k < 0 && j < sg.length()-1) {
					j++;
					c0 = sg.charAt(j);

					if (c0 != 108 && c0 != 76) {
						if (c0 == 114 || c0 == 82)
							flag = false;
					}
					else {
						flag = true;
					}

					k = 0;
				}

				i += k;

				if (flag && k > 0)
					i++;
			}

			return i;
		}
	}

	/**
	 * Returns the width of this character as rendered.
	 */
	@Override
	public int getCharWidth(char c) {
		if (c == 167) {
			return -1;
		}
		else if (c == 32) {
			return 4;
		}
		else {
			int i = this.getCharGridIndex(c);

			if (c > 0 && i != -1 && !unicodeFlag) {
				return charWidth[i];
			}
			else if (glyphWidth[c] != 0) {
				int j = glyphWidth[c] >>> 4;
				int k = glyphWidth[c] & 15;

				if (k > 7) {
					k = 15;
					j = 0;
				}

				k++;
				return (k-j) / 2+1;
			}
			else {
				return 0;
			}
		}
	}

	/**
	 * Trims a string to fit a specified Width.
	 */
	@Override
	public String trimStringToWidth(String sg, int width) {
		return this.trimStringToWidth(sg, width, false);
	}

	/**
	 * Trims a string to a specified width, and will reverse it if par3 is set.
	 */
	@Override
	public String trimStringToWidth(String sg, int width, boolean rev) {
		StringBuilder stringbuilder = new StringBuilder();
		int j = 0;
		int k = rev ? sg.length()-1 : 0;
		int l = rev ? -1 : 1;
		boolean flag1 = false;
		boolean flag2 = false;

		for (int i1 = k; i1 >= 0 && i1 < sg.length() && j < width; i1 += l) {
			char c0 = sg.charAt(i1);
			int j1 = this.getCharWidth(c0);

			if (flag1) {
				flag1 = false;

				if (c0 != 108 && c0 != 76) {
					if (c0 == 114 || c0 == 82)
						flag2 = false;
				}
				else {
					flag2 = true;
				}
			}
			else if (j1 < 0) {
				flag1 = true;
			}
			else {
				j += j1;

				if (flag2)
					j++;
			}

			if (j > width)
				break;

			if (rev)
				stringbuilder.insert(0, c0);
			else
				stringbuilder.append(c0);
		}

		return stringbuilder.toString();
	}

	/**
	 * Remove all newline characters from the end of the string
	 */
	private String trimStringNewline(String sg) {
		while (sg != null && sg.endsWith("\n"))
			sg = sg.substring(0, sg.length()-1);
		return sg;
	}

	/**
	 * Splits and draws a String with wordwrap
	 */
	@Override
	public final void drawSplitString(String sg, int x, int y, int color, int width) {
		this.resetStyles();
		textColor = width;
		sg = this.trimStringNewline(sg);
		wipe = null;
		this.renderSplitString(sg, x, y, color, false);
	}

	/**
	 * Perform actual work of rendering a multi-line string with wordwrap and with darker drop shadow color if flag is
	 * set
	 */
	private void renderSplitString(String sg, int x, int y, int color, boolean shadow) {
		List<String> list = this.listFormattedStringToWidth(sg, color);

		for (Iterator<String> iterator = list.iterator(); iterator.hasNext(); y += FONT_HEIGHT) {
			String s1 = iterator.next();
			this.renderStringAligned(s1, x, y, color, textColor, shadow);
		}
	}

	/**
	 * Returns the width of the wordwrapped String
	 */
	@Override
	public int splitStringWidth(String sg, int width) {
		return FONT_HEIGHT * this.listFormattedStringToWidth(sg, width).size();
	}

	/**
	 * Set unicodeFlag controlling whether strings should be rendered with Unicode fonts instead of the default.png
	 * font.
	 */
	@Override
	public void setUnicodeFlag(boolean uni) {
		unicodeFlag = uni;
	}

	/**
	 * Get unicodeFlag controlling whether strings should be rendered with Unicode fonts instead of the default.png
	 * font.
	 */
	@Override
	public boolean getUnicodeFlag() {
		return unicodeFlag;
	}

	/**
	 * Set bidiFlag to control if the Unicode Bidirectional Algorithm should be run before rendering any string.
	 */
	@Override
	public void setBidiFlag(boolean bidi) {
		bidiFlag = bidi;
	}

	/**
	 * Breaks a string into a list of pieces that will fit a specified width.
	 */
	@Override
	public final List<String> listFormattedStringToWidth(String sg, int width) {
		return Arrays.asList(this.wrapFormattedString(sg, width).split("\n"));
	}

	/**
	 * Inserts newline and formatting into a string to wrap it within the specified width.
	 */
	private String wrapFormattedString(String sg, int width) {
		int j = this.sizeStringToWidth(sg, width);

		if (sg.length() <= j) {
			return sg;
		}
		else {
			String s1 = sg.substring(0, j);
			char c0 = sg.charAt(j);
			boolean flag = c0 == 32 || c0 == 10;
			String s2 = getFormatFromString(s1)+sg.substring(j+(flag ? 1 : 0));
			return s1+"\n"+this.wrapFormattedString(s2, width);
		}
	}

	/**
	 * Determines how many characters from the string will fit into the specified width.
	 */
	private int sizeStringToWidth(String sg, int w) {
		int j = sg.length();
		int k = 0;
		int l = 0;
		int i1 = -1;

		for (boolean flag = false; l < j; l++) {
			char c0 = sg.charAt(l);

			switch (c0) {
				case 10:
					l--;
					break;
				case 167:
					if (l < j-1) {
						l++;
						char c1 = sg.charAt(l);

						if (c1 != 108 && c1 != 76) {
							if (c1 == 114 || c1 == 82 || isFormatColor(c1))
								flag = false;
						}
						else {
							flag = true;
						}
					}

					break;
				case 32:
					i1 = l;
				default:
					k += this.getCharWidth(c0);

					if (flag)
						k++;
			}

			if (c0 == 10) {
				l++;
				i1 = l;
				break;
			}

			if (k > w)
				break;
		}

		return l != j && i1 != -1 && i1 < l ? i1 : l;
	}

	/**
	 * Checks if the char code is a hexadecimal character, used to set colour.
	 */
	private static boolean isFormatColor(char c) {
		return c >= 48 && c <= 57 || c >= 97 && c <= 102 || c >= 65 && c <= 70;
	}

	/**
	 * Checks if the char code is O-K...lLrRk-o... used to set special formatting.
	 */
	private static boolean isFormatSpecial(char c) {
		return c >= 107 && c <= 111 || c >= 75 && c <= 79 || c == 114 || c == 82;
	}

	/**
	 * Digests a string for nonprinting formatting characters then returns a string containing only that formatting.
	 */
	private static String getFormatFromString(String sg) {
		String s1 = "";
		int i = -1;
		int j = sg.length();

		while ((i = sg.indexOf(167, i+1)) != -1) {
			if (i < j-1) {
				char c0 = sg.charAt(i+1);
				if (isFormatColor(c0))
					s1 = "\u00a7"+c0;
				else if (isFormatSpecial(c0))
					s1 = s1+"\u00a7"+c0;
			}
		}

		return s1;
	}

	/**
	 * Get bidiFlag that controls if the Unicode Bidirectional Algorithm should be run before rendering any string
	 */
	@Override
	public boolean getBidiFlag()
	{
		return bidiFlag;
	}

	protected static int getCharGridIndex(char c) {
		return charMap.get(c);
	}

	static {
		String key = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000";
		char[] chars = key.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			charMap.put(chars[i], i);
		}

		String form = "0123456789abcdefklmnor";
		chars = form.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			formatMap.put(chars[i], i);
		}
	}

	public static enum Kerning {
		VERYNARROW(-2),
		NARROW(-1),
		NORMAL(0),
		WIDE(1),
		VERYWIDE(2);

		public final int spaceModifier;

		private Kerning(int s) {
			spaceModifier = s;
		}
	}

	private static class WipeEffect {

		private final int lastChar;
		private final float charFraction;
		private final int wipeColor;

		private WipeEffect(float f, int ch, int color) {
			lastChar = ch;
			charFraction = f;
			wipeColor = color;
		}

	}
}
