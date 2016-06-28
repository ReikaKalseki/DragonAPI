/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.auxiliary;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.DragonOptions;
import reika.dragonapi.libraries.ReikaNBTHelper;
import reika.dragonapi.libraries.ReikaPlayerAPI;
import reika.dragonapi.libraries.io.ReikaTextureHelper;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class DebugOverlay {

	public static final DebugOverlay instance = new DebugOverlay();

	private DebugOverlay() {

	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void eventHandler(RenderGameOverlayEvent.Post event) {
		if (event.type == ElementType.HELMET) {
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

			if (DragonOptions.TABNBT.getState() && Keyboard.isKeyDown(DragonOptions.DEBUGKEY.getValue())) {
				//if (APIProxyClient.key_nbt.isPressed()) {
				Minecraft mc = Minecraft.getMinecraft();
				EntityPlayer ep = mc.thePlayer;
				FontRenderer f = mc.fontRenderer;
				if (mc.currentScreen == null) {
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
								ArrayList<String> li = new ArrayList();
								try {
									te.writeToNBT(NBT);
									li.addAll(ReikaNBTHelper.parseNBTAsLines(NBT));
								}
								catch (Exception e) {
									StackTraceElement[] el = e.getStackTrace();
									li.add(EnumChatFormatting.RED.toString()+e.getClass()+": "+e.getLocalizedMessage());
									for (int i = 0; i < 4; i++) {
										li.add(el[i].toString());
									}
								}
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
	}

}
