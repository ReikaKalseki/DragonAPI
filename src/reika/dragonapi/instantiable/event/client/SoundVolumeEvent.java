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
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event;


public class SoundVolumeEvent extends Event {

	public final ISound sound;
	public final SoundPoolEntry pool;
	public final SoundCategory category;

	public final float originalVolume;
	public float volume;

	public SoundVolumeEvent(ISound snd, SoundPoolEntry pl, SoundCategory cat) {
		sound = snd;
		pool = pl;
		category = cat;

		originalVolume = this.getDefaultVolume(snd, pl, cat);
		volume = originalVolume;
	}

	private float getDefaultVolume(ISound snd, SoundPoolEntry pool, SoundCategory cat) {
		return (float)MathHelper.clamp_double(snd.getVolume() * pool.getVolume() * this.getSoundCategoryVolume(cat), 0.0D, 1.0D);
	}

	private float getSoundCategoryVolume(SoundCategory cat) {
		return cat != null && cat != SoundCategory.MASTER ? Minecraft.getMinecraft().gameSettings.getSoundLevel(cat) : 1.0F;
	}

	public static float fire(ISound snd, SoundPoolEntry pool, SoundCategory cat) {
		SoundVolumeEvent evt = new SoundVolumeEvent(snd, pool, cat);
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.volume;
	}

}
