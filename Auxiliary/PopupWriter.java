/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PopupWriter {

	public static final PopupWriter instance = new PopupWriter();

	private final ArrayList<String> list = new ArrayList();

	private int buttonX;
	private int buttonY;
	private int buttonSize;

	private boolean ungrabbed = false;

	private final ArrayList<String> serverMessages = new ArrayList();

	private PopupWriter() {
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}

	public void addMessage(String s) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			serverMessages.add(s);
		}
		else {
			//sb.append(" CTRL-ALT-click to close this message.");
			String sg = s+" Hold CTRL to be able to click this message.";
			list.add(sg);
		}
	}

	public void sendServerMessages(EntityPlayerMP ep) {
		PacketTarget pt = new PacketTarget.PlayerTarget(ep);
		for (String s : serverMessages) {
			ReikaPacketHelper.sendStringPacket(DragonAPIInit.packetChannel, PacketIDs.POPUP.ordinal(), s, pt);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void drawOverlay(RenderGameOverlayEvent evt) {
		if (!list.isEmpty() && evt.type == ElementType.HELMET) {
			String s = list.get(0);
			int x = 2;
			int y = 2;
			int w = 192;
			int sw = w-25;
			FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
			int lines = fr.listFormattedStringToWidth(s, sw).size();
			int h = 7+(lines)*(fr.FONT_HEIGHT);
			Gui.drawRect(x, y, x+w, y+h, 0xff4a4a4a);
			ReikaGuiAPI.instance.drawRectFrame(x, y, w, h, 0xb0b0b0);
			ReikaGuiAPI.instance.drawRectFrame(x+2, y+2, w-4, h-4, 0xcfcfcf);
			fr.drawSplitString(s, x+4, y+4, sw, 0xffffff);

			Tessellator v5 = Tessellator.instance;
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glEnable(GL11.GL_BLEND);

			ReikaTextureHelper.bindFinalTexture(DragonAPICore.class, "Resources/warning.png");
			v5.startDrawingQuads();

			int sz = 24;
			int dx = x+w-sz;
			int dy = y;
			v5.addVertexWithUV(dx, dy+sz, 0, 0, 1);
			v5.addVertexWithUV(dx+sz, dy+sz, 0, 1, 1);
			v5.addVertexWithUV(dx+sz, dy, 0, 1, 0);
			v5.addVertexWithUV(dx, dy, 0, 0, 0);

			v5.draw();

			//if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
			ReikaTextureHelper.bindFinalTexture(DragonAPICore.class, "Resources/buttons.png");
			v5.startDrawingQuads();

			sz = 16;
			dx = x+w-sz-4;
			dy = y+h-sz-4;

			int sc = evt.resolution.getScaleFactor();
			buttonX = dx*sc;
			buttonY = dy*sc;
			buttonSize = sz*sc;

			v5.addVertexWithUV(dx, dy+sz, 0, 0.5, 0.25);
			v5.addVertexWithUV(dx+sz, dy+sz, 0, 0.75, 0.25);
			v5.addVertexWithUV(dx+sz, dy, 0, 0.75, 0);
			v5.addVertexWithUV(dx, dy, 0, 0.5, 0);

			v5.draw();

			//}

			GL11.glPopAttrib();
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void keyHandle(KeyInputEvent evt) {
		if (!list.isEmpty() || ungrabbed) {
			if (GuiScreen.isCtrlKeyDown() && !ungrabbed) {
				//ReikaJavaLibrary.pConsole("Press");
				Minecraft.getMinecraft().mouseHelper.ungrabMouseCursor();
				ungrabbed = true;
			}
			else if (ungrabbed) {
				//ReikaJavaLibrary.pConsole("Release");
				Minecraft.getMinecraft().mouseHelper.grabMouseCursor();
				ungrabbed = false;
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void click(MouseEvent evt) {
		if (!list.isEmpty() && evt.buttonstate && evt.button == 0 && ungrabbed && buttonX > 0 && buttonY > 0) {
			int x = evt.x;
			int y = Minecraft.getMinecraft().displayHeight-evt.y;
			//ReikaJavaLibrary.pConsole(x+","+y+ " / "+buttonX+","+buttonY+ " ? "+(x/(double)buttonX)+", "+(y/(double)buttonY));

			if (x >= buttonX && x <= buttonX+buttonSize) {
				if (y >= buttonY && y <= buttonY+buttonSize) {
					Minecraft.getMinecraft().thePlayer.playSound("random.click", 1, 1);
					list.remove(0);
				}
			}
		}
	}
}
