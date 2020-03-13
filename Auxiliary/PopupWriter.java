/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.ArrayList;
import java.util.Collection;

import org.lwjgl.opengl.GL11;

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

import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;
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

	private final ArrayList<Warning> list = new ArrayList();

	private int buttonX;
	private int buttonY;
	private int buttonSize;

	private boolean ungrabbed = false;

	private final ArrayList<Warning> serverMessages = new ArrayList();

	private final PlayerMap<Collection<Warning>> alreadySent = new PlayerMap();

	private PopupWriter() {
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}

	public void addMessage(String w) {
		this.addMessage(new Warning(w));
	}

	public void addMessage(Warning w) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			serverMessages.add(w);
		}
		else {
			//sb.append(" CTRL-ALT-click to close this message.");
			String sg = w.text+" Hold CTRL to be able to click this message.";
			list.add(new Warning(sg, w.width));
		}
	}

	public void sendServerMessages(EntityPlayerMP ep) {
		PacketTarget pt = new PacketTarget.PlayerTarget(ep);
		Collection<Warning> c = alreadySent.get(ep);
		if (c == null) {
			c = new ArrayList();
		}
		for (Warning s : serverMessages) {
			if (c.contains(s))
				continue;
			ReikaPacketHelper.sendStringIntPacket(DragonAPIInit.packetChannel, PacketIDs.POPUP.ordinal(), pt, s.text, s.width);
			c.add(s);
		}
		alreadySent.put(ep, c);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void drawOverlay(RenderGameOverlayEvent evt) {
		if (!list.isEmpty() && evt.type == ElementType.HELMET) {
			Warning s = list.get(0);
			int x = 2;
			int y = 2;
			int w = s.width;
			int sw = w-25;
			FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
			int lines = fr.listFormattedStringToWidth(s.text, sw).size();
			int h = 7+(lines)*(fr.FONT_HEIGHT);
			Gui.drawRect(x, y, x+w, y+h, 0xff4a4a4a);
			ReikaGuiAPI.instance.drawRectFrame(x, y, w, h, 0xb0b0b0);
			ReikaGuiAPI.instance.drawRectFrame(x+2, y+2, w-4, h-4, 0xcfcfcf);
			fr.drawSplitString(s.text, x+4, y+4, sw, 0xffffff);

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

	public static class Warning {

		public final String text;
		public final int width;

		public Warning(String s) {
			this(s, Math.max(calcMinSizeForText(s), 192));
		}

		private static int calcMinSizeForText(String s) { //at w=192, 74 chars becomes 4 lines, or about 18 chars a line (1 char = 11px); ideally keep line count <= 6
			int w = 192;
			int c = 18;
			int lines = s.length()/c;
			while (lines > 6) {
				w += 16;
				c += 2;
				lines = s.length()/c;
			}
			return w;
		}

		public Warning(String s, int w) {
			text = s;
			width = Math.min(300, w);
		}

		@Override
		public int hashCode() {
			return text.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof Warning && text.equals(((Warning)o).text);
		}

	}
}
