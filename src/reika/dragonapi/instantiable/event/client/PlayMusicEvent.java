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

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
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

}
