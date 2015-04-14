/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class NightVisionBrightnessEvent extends Event {

	public float brightness;

	public final float partialTickTime;
	public final EntityPlayer player;

	public NightVisionBrightnessEvent(EntityPlayer ep, float ptick) {
		partialTickTime = ptick;
		player = ep;

		brightness = this.getDefault();
	}

	public float getDefault() {
		int time = player.getActivePotionEffect(Potion.nightVision).getDuration();
		return time > 200 ? 1F : 0.7F+MathHelper.sin((time-partialTickTime)*(float)Math.PI*0.2F)*0.3F;
	}

}
