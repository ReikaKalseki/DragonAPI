/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.IO;

import java.io.IOException;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.IO.DirectResourceManager;
import Reika.DragonAPI.Interfaces.Registry.SoundEnum;

public class SoundLoader {

	private SoundEnum[] soundList;

	public SoundLoader(SoundEnum[] sounds) {
		soundList = sounds;
	}

	public void register() {
		for (int i = 0; i < soundList.length; i++) {
			SoundEnum e = soundList[i];
			DirectResourceManager.getInstance().registerCustomPath(e.getPath(), e.getCategory(), false);
			if (e.preload()) {
				try {
					DirectResourceManager.getInstance().getResource(DirectResourceManager.getResource(e.getPath()));
				}
				catch (IOException ex) {
					DragonAPICore.logError("Caught error when preloading sound '"+e+"':");
					ex.printStackTrace();
				}
			}
		}
	}
}
