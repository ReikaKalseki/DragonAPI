/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.ReikaPacketHelper;
import Reika.RotaryCraft.TileEntities.TileEntityDisplay;

public final class GuiStringBuilder extends GuiScreen {

	private EntityPlayer ep;
	private World world;
	private int x;
	private int y;
	private int z;

	private StringBuilder sb = new StringBuilder();
	private Gui2DTextField text;

	private int xSize = 176;
	private int ySize = 166;

	private String message;

	private int packetID;
	private String packetChannel;

	public GuiStringBuilder(EntityPlayer player, World worldObj, int xCoord, int yCoord, int zCoord, String packet, int id) {
		ep = player;
		world = worldObj;
		x = xCoord;
		y = yCoord;
		z = zCoord;
		packetChannel = packet;
		packetID = id;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		text = new Gui2DTextField(fontRenderer, j+5, k+10, xSize-10, ySize-20, TileEntityDisplay.displayWidth, TileEntityDisplay.displayHeight);
		text.setFocused(false);
		//text.setMaxStringLength(TileEntityDisplay.displayWidth);
		buttonList.add(new GuiButton(0, j+4, k-11, xSize/2-4, 20, "Save"));
		buttonList.add(new GuiButton(1, j+xSize/2, k-11, xSize/2-4, 20, "Save To File"));
	}

	@Override
	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		this.initGui();
		if (button.id == 0)
			this.sendPacket();
		if (button.id == 1)
			;//this.saveStringToFile();
	}

	@Override
	public void keyTyped(char c, int i){
		super.keyTyped(c, i);
		text.textboxKeyTyped(c, i);
	}

	@Override
	public void mouseClicked(int i, int j, int k){
		super.mouseClicked(i, j, k);
		text.mouseClicked(i, j, k);
	}

	@Override
	public void drawScreen(int a, int b, float f)
	{
		super.drawScreen(a, b, f);
		text.drawTextBox();
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		message = text.getText();
	}/*

	public void addCharacter(char c) {
		sb.append(c);
	}

	public void backSp() {
		sb.deleteCharAt(sb.length()-1);
	}

	public void clear() {
		sb = new StringBuilder();
	}*/

	public String getFinalString() {
		//return sb.toString();
		return message;
	}

	public void sendPacket() {
		ReikaPacketHelper.sendStringPacket(packetChannel, packetID, this.getFinalString(), world, x, y, z);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

}
