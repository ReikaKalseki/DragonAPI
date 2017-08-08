/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event.Client;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import cpw.mods.fml.common.eventhandler.Event.HasResult;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@HasResult
@SideOnly(Side.CLIENT)
public class JumpCheckEventClient extends LivingEvent {

	public final int jumpTick;

	public final boolean defaultResult;

	public JumpCheckEventClient(EntityLivingBase e, int ticks) {
		super(e);

		jumpTick = ticks;

		defaultResult = this.getDefaultResult();
	}

	private boolean getDefaultResult() {
		return entityLiving.onGround && jumpTick == 0;
	}

	public static boolean fire(EntityLivingBase e, int ticks) {
		JumpCheckEventClient evt = new JumpCheckEventClient(e, ticks);
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
