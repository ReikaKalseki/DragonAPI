/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.IO;

import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.IO.ReikaSoundImporter;
import Reika.DragonAPI.Interfaces.SoundList;

public class SoundLoader {

	private SoundList[] soundList;
	private DragonAPIMod mod;
	private boolean override;
	private String customURL;

	public SoundLoader(DragonAPIMod m, SoundList[] sounds) {
		soundList = sounds;
		mod = m;
		override = false;
	}

	public SoundLoader(DragonAPIMod m, SoundList[] sounds, String customURL) {
		soundList = sounds;
		mod = m;
		override = true;
	}

	@ForgeSubscribe
	public void onSoundLoad(SoundLoadEvent event) {
		for (int i = 0; i < soundList.length; i++) {
			try {
				String path = soundList[i].getPath();
				mod.getModLogger().log("Loading sound "+soundList[i].getName()+" @ "+path);
				if (override) {
					ReikaSoundImporter.addSound(mod.getModLogger(), path, soundList[i].getURL(), event.manager);
				}
				else {
					event.manager.addSound(path);
				}
			}
			catch (Exception e) {
				throw new RegistrationException(mod, "Sound file "+soundList[i].getName()+" not found!");
			}
		}
	}
}
