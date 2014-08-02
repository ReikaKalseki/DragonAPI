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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class DebugOverlay {

	public static final DebugOverlay instance = new DebugOverlay();

	private DebugOverlay() {

	}

	@ForgeSubscribe
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

		if (Keyboard.isKeyDown(Keyboard.KEY_TAB)) {
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayer ep = mc.thePlayer;
			FontRenderer f = mc.fontRenderer;

			float reach = 4;
			Vec3 vec = Vec3.createVectorHelper(ep.posX, (ep.posY + 1.62) - ep.yOffset, ep.posZ);
			Vec3 vec2 = ep.getLook(1.0F);
			Vec3 vec3 = vec.addVector(vec2.xCoord*reach, vec2.yCoord*reach, vec2.zCoord*reach);
			MovingObjectPosition hit = ep.worldObj.clip(vec, vec3);

			if (hit != null && hit.typeOfHit == EnumMovingObjectType.TILE) {
				TileEntity te = ep.worldObj.getBlockTileEntity(hit.blockX, hit.blockY, hit.blockZ);
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
