/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Interfaces.SoundList;

public class SoundLoader {

	private SoundList[] soundList;
	private DragonAPIMod mod;

	public SoundLoader(DragonAPIMod m, SoundList[] sounds) {
		soundList = sounds;
		mod = m;
	}

	@ForgeSubscribe
	public void onSoundLoad(SoundLoadEvent event) {
		for (int i = 0; i < soundList.length; i++) {
			try {
				event.manager.soundPoolSounds.addSound(soundList[i].getPath()/*, soundList[i].getURL()*/);
			}
			catch (Exception e) {
				throw new RegistrationException(mod, "Sound file "+soundList[i].getName()+" not found!");
			}
		}
	}
}
