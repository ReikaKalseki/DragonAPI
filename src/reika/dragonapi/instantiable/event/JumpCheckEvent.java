/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import cpw.mods.fml.common.eventhandler.Event.HasResult;

@HasResult
public class JumpCheckEvent extends PlayerEvent {

	public final C03PacketPlayer packet;
	public final double yOffset;

	public final boolean defaultResult;

	public JumpCheckEvent(EntityPlayer e, C03PacketPlayer pk, double d5) {
		super(e);

		packet = pk;
		yOffset = d5;

		defaultResult = this.getDefaultResult();
	}

	private boolean getDefaultResult() {
		return entityLiving.onGround && !packet.func_149465_i() && yOffset > 0.0D;
	}

	public static boolean fire(EntityPlayer ep, C03PacketPlayer pk) {
		double d2 = pk.func_149466_j() ? pk.func_149467_d() : ep.posY; //the d5 DLOAD was causing issues, recalc manually
		double d5 = d2-ep.posY;

		JumpCheckEvent evt = new JumpCheckEvent(ep, pk, d5);
		MinecraftForge.EVENT_BUS.post(evt);
		switch(evt.getResult()) {
			case ALLOW:
				return true;
			case DEFAULT:
			default:
				return evt.defaultResult;
			case DENY:
				return false;
		}
	}

}
