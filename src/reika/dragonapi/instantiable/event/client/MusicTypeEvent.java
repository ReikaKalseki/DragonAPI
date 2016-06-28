/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.event.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.MusicTicker.MusicType;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MusicTypeEvent extends Event {

	public final MusicType originalType;
	public MusicType type;

	public MusicTypeEvent(Minecraft mc) {
		originalType = this.getDefault(mc);
		type = originalType;
	}

	private static MusicType getDefault(Minecraft mc) {
		if (mc.currentScreen instanceof GuiWinGame)
			return MusicType.CREDITS;
		EntityPlayer ep = mc.thePlayer;
		if (ep == null)
			return MusicType.MENU;
		WorldProvider world = ep.worldObj.provider;
		if (world instanceof WorldProviderHell) {
			return MusicType.NETHER;
		}
		else if (world instanceof WorldProviderEnd) {
			if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) {
				return MusicType.END_BOSS;
			}
			else {
				return MusicType.END;
			}
		}
		else {
			if (ep.capabilities.isCreativeMode && ep.capabilities.allowFlying) {
				return MusicType.CREATIVE;
			}
			else {
				return MusicTicker.MusicType.GAME;
			}
		}
	}

	public static MusicType fire(Minecraft mc) {
		MusicTypeEvent evt = new MusicTypeEvent(mc);
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.type;
	}

}
