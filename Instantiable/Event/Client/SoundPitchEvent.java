/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event.Client;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event;


public class SoundPitchEvent extends Event {

	public final ISound sound;
	public final SoundPoolEntry pool;

	public final float originalPitch;
	public final double unclampedPitch;
	public float pitch;

	public SoundPitchEvent(ISound snd, SoundPoolEntry pl) {
		sound = snd;
		pool = pl;

		unclampedPitch = this.getDefaultPitch(snd, pl);
		originalPitch = (float)MathHelper.clamp_double(unclampedPitch, 0.5D, 2.0D);
		pitch = originalPitch;
	}

	private double getDefaultPitch(ISound snd, SoundPoolEntry pool) {
		return snd.getPitch() * pool.getPitch();
	}

	public static float fire(ISound snd, SoundPoolEntry pool) {
		SoundPitchEvent evt = new SoundPitchEvent(snd, pool);
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.pitch;
	}

}
