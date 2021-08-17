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

import net.minecraft.client.audio.ISound;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.Event;

public class SoundAttenuationDistanceEvent extends Event {

	public final ISound sound;
	public final float originalDistance;

	public float distance;

	public SoundAttenuationDistanceEvent(ISound s, float d) {
		sound = s;
		originalDistance = d;
		distance = originalDistance;
	}

	public static float fire(float d, ISound s) {
		SoundAttenuationDistanceEvent evt = new SoundAttenuationDistanceEvent(s, d);
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.distance;
	}

}
