/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.IO;

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
			DirectResourceManager.getInstance().registerCustomPath(e.getPath(), e.getCategory());
		}
	}
}
