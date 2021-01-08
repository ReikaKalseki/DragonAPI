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
import java.util.Arrays;

import net.minecraft.client.resources.IResource;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.IO.DirectResourceManager;
import Reika.DragonAPI.Interfaces.Registry.SoundEnum;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;

public class SoundLoader {

	private final Class<? extends SoundEnum> soundClass;
	private final SoundEnum[] soundList;

	public SoundLoader(SoundEnum... ss) {
		if (ss.length == 0)
			throw new IllegalArgumentException("You cannot register an empty sound list!");
		soundClass = ss[0].getClass();
		soundList = Arrays.copyOf(ss, ss.length);
		this.init();
	}

	public SoundLoader(Class<? extends SoundEnum> c) {
		soundClass = c;
		soundList = c.getEnumConstants();
		this.init();
	}

	private void init() {
		if (soundClass == SingleSound.class) {
			for (SoundEnum s : soundList)
				ReikaSoundHelper.registerSingleSound((SingleSound)s);
		}
		else {
			ReikaSoundHelper.registerSoundSet(soundClass);
		}
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
