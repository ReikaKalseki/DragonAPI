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
import java.util.ArrayList;
import java.util.Collection;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.IO.DirectResourceManager;
import Reika.DragonAPI.Interfaces.Registry.SoundEnum;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class SoundLoader {

	private Collection<SoundEnum> soundList;

	public SoundLoader(Collection<SoundEnum> sounds) {
		soundList = new ArrayList(sounds);
	}

	public SoundLoader(SoundEnum... sounds) {
		soundList = ReikaJavaLibrary.makeListFrom(sounds);
	}

	public void register() {
		for (SoundEnum e : soundList) {
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
