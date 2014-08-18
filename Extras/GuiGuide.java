/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Extras;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.IO.XMLInterface;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.opengl.GL11;

public class GuiGuide extends GuiScreen {

	private static final ArrayList<ModList> mods = new ArrayList();
	private static final String PARENT = "Resources/";
	private static final XMLInterface guide = new XMLInterface(DragonAPICore.class, PARENT+"guide.xml", !ReikaObfuscationHelper.isDeObfEnvironment());
	private static HashMap<String, String> data = new HashMap();

	private static final String[] tabLabels = {"Info", "Getting Started", "Useful Notes", "Tips and Tricks"};
	private static final String[] tabTags = {"info", "tutorial", "notes", "tips"};

	private int screen;
	private int page;

	protected final int xSize = 256;
	protected final int ySize = 220;

	public GuiGuide() {
		guide.reread();
		this.loadData();
	}

	private static void loadData() {
		for (int i = 0; i < mods.size(); i++) {
			ModList mod = mods.get(i);
			for (int j = 0; j < tabTags.length; j++) {
				String tag = "dragonapi:"+mod.name().toLowerCase()+":"+tabTags[j];
				String desc = guide.getValueAtNode(tag);
				//ReikaJavaLibrary.pConsole(tag+" ;; "+desc);
				data.put(tag, desc);
			}
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2 - 8;

		String file = "/Reika/DragonAPI/Resources/guidetab.png";
		buttonList.add(new GuiButton(10, j-19, 17+k+163, 20, 20, "-")); //Prev Page
		buttonList.add(new GuiButton(11, j-19, 17+k+143, 20, 20, "+"));	//Next page
		buttonList.add(new GuiButton(15, j-19, 17+k+183, 20, 20, "<<"));	//1st page
		buttonList.add(new GuiButton(12, j+xSize-27, k+6, 20, 20, "X"));	//Close gui button


		if (screen > 0) {
			for (int i = 0; i < tabLabels.length; i++) {
				String s = tabLabels[i];
				buttonList.add(new GuiButton(i, j-19-65+1, k+20*i, 85, 20, s)); //Prev Page
			}
		}
	}

	/**
	 * Returns true if this GUI should pause the game when it is displayed in single-player
	 */
	@Override
	public boolean doesGuiPauseGame()
	{
		return true;
	}

	private void drawModLogo(ModList mod) {

	}

	@Override
	public void actionPerformed(GuiButton button) {
		if (button.id == 12) {
			mc.thePlayer.closeScreen();
			return;
		}
		if (button.id == 15) {
			screen = 0;
			page = 0;
			this.initGui();
			return;
		}
		if (button.id == 10) {
			if (screen > 0) {
				screen--;
				page = 0;
			}
			this.initGui();
			return;
		}
		if (button.id == 11) {
			if (screen < this.getMaxPage()) {
				screen++;
				page = 0;
			}
			else {
				screen = 0;
				page = 0;
			}
			this.initGui();
			return;
		}
		page = button.id;
		this.initGui();
	}

	private int getMaxPage() {
		return mods.size();
	}

	private void drawTabIcons() {
		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2;
	}

	private void drawGraphics() {
		int posX = (width - xSize) / 2-2;
		int posY = (height - ySize) / 2-8;

		String s = "";
		if (screen >= 1)
			s = mods.get(screen-1).getDisplayName();
		else
			s = "Reika's Mods";
		fontRendererObj.drawString(String.format("%s", s), posX+10, posY+8, 0);
		if (screen >= 1) {
			int w = fontRendererObj.getStringWidth(s+" ");
			if (mods.get(screen-1).isLoaded()) {
				fontRendererObj.drawString("(Installed)", posX+10+w, posY+8, 0x007700);
			}
			else {
				fontRendererObj.drawString("(Not Installed)", posX+10+w, posY+8, 0x770000);
			}
		}
	}

	@Override
	public void drawScreen(int x, int y, float f)
	{
		String var4 = "/Reika/DragonAPI/Resources/guidebcg.png";
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ReikaTextureHelper.bindTexture(DragonAPICore.class, var4);

		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2 - 8;

		this.drawTexturedModalRect(posX, posY, 0, 0, xSize, ySize);

		int xo = 0;
		int yo = 0;
		/*
		fontRendererObj.drawString(HandbookRegistry.getEntry(screen, page).getTitle(), posX+xo+6, posY+yo+6, 0x000000);
		HandbookRegistry h = HandbookRegistry.getEntry(screen, page);
		 */
		String s = "This book contains basic information about each of Reika's mods.";
		if (screen > 0) {
			String tag = "dragonapi:"+mods.get(screen-1).name().toLowerCase()+":"+tabTags[page];
			s = data.get(tag);
		}
		fontRendererObj.drawSplitString(String.format("%s", s), posX+9, posY+88, 241, 0xffffff);

		this.drawGraphics();

		super.drawScreen(x, y, f);

		this.drawTabIcons();
	}

	static {
		loadData();

		List<ModList> reika = ModList.getReikasMods();
		for (int i = 0; i < reika.size(); i++) {
			//if (reika.get(i).isLoaded())
			mods.add(reika.get(i));
		}
	}
}
