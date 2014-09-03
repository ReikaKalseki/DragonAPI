/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.IO;

import Reika.DragonAPI.IO.CustomResourceManager;
import Reika.DragonAPI.Interfaces.SoundEnum;

public class SoundLoader {

	private SoundEnum[] soundList;

	public SoundLoader(SoundEnum[] sounds) {
		soundList = sounds;
	}

	public void register() {
		Reika.DragonAPI.IO.CustomResourceManager mg = CustomResourceManager.getRegisteredInstance();
		for (int i = 0; i < soundList.length; i++) {
			SoundEnum e = soundList[i];
			mg.registerCustomPath(e.getPath(), e.getCategory());
		}
	}
}
