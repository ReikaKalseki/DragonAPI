/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class DebugOverlay {

	public static final DebugOverlay instance = new DebugOverlay();

	private DebugOverlay() {

	}

	@SubscribeEvent
	public void eventHandler(RenderGameOverlayEvent event) {
		if (DragonAPICore.debugtest) {
			Minecraft mc = Minecraft.getMinecraft();
			FontRenderer f = mc.fontRenderer;

			double d = 3;
			GL11.glScaled(d, d, d);
			String s = "Debug Mode Enabled!";
			f.drawString(s, 1, 1, 0xffffff);
			GL11.glScaled(1/d, 1/d, 1/d);
			ReikaTextureHelper.bindHUDTexture();
		}

		if (DragonOptions.TABNBT.getState() && Keyboard.isKeyDown(Keyboard.KEY_TAB)) {
			//if (APIProxyClient.key_nbt.isPressed()) {
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayer ep = mc.thePlayer;
			FontRenderer f = mc.fontRenderer;

			float reach = 4;
			MovingObjectPosition hit = ReikaPlayerAPI.getLookedAtBlockClient(4, false);
			if (hit != null) {
				int x = hit.blockX;
				int y = hit.blockY;
				int z = hit.blockZ;
				Block b = ep.worldObj.getBlock(x, y, z);
				if (b.hasTileEntity(ep.worldObj.getBlockMetadata(x, y, z))) {
					TileEntity te = ep.worldObj.getTileEntity(x, y, z);
					if (te != null) {
						NBTTagCompound NBT = new NBTTagCompound();
						te.writeToNBT(NBT);
						ArrayList<String> li = ReikaNBTHelper.parseNBTAsLines(NBT);
						for (int i = 0; i < li.size(); i++) {
							String s = li.get(i);
							f.drawString(s, 1+event.resolution.getScaledWidth()/2*(i/24), 1+f.FONT_HEIGHT*(i%24), 0xffffff);
							ReikaTextureHelper.bindHUDTexture();
						}
					}
				}
			}
		}
	}

}
