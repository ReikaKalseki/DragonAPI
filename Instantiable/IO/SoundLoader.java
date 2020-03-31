/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.IO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.client.resources.IResource;

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

	public final void register() {
		for (SoundEnum e : soundList) {
			String p = e.getPath();
			DirectResourceManager.getInstance().registerCustomPath(p, e.getCategory(), false);
			this.onRegister(e, p);
			if (e.preload()) {
				try {
					IResource res = DirectResourceManager.getInstance().getResource(DirectResourceManager.getResource(p));
				}
				catch (IOException ex) {
					DragonAPICore.logError("Caught error when preloading sound '"+e+"':");
					ex.printStackTrace();
				}
			}
		}
	}

	protected void onRegister(SoundEnum e, String p) {

	}
}
