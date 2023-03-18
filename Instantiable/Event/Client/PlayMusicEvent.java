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

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Cancelable
public class PlayMusicEvent extends Event {

	public final PositionedSoundRecord music;
	public final int timer;

	public PlayMusicEvent(ISound sound, int timer) {
		music = (PositionedSoundRecord)sound;
		this.timer = timer;
	}

	public static void fire(SoundHandler sh, ISound snd) {
		fire(sh, snd, Minecraft.getMinecraft().mcMusicTicker);
	}

	public static void fire(SoundHandler sh, ISound snd, MusicTicker mus) {
		if (!MinecraftForge.EVENT_BUS.post(new PlayMusicEvent(snd, mus.field_147676_d))) {
			Minecraft.getMinecraft().getSoundHandler().playSound(snd);
		}
	}

}
