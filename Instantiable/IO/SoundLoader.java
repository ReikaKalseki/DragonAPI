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
import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.IO.DirectResourceManager;
import Reika.DragonAPI.Interfaces.Registry.SoundEnum;
import Reika.DragonAPI.Interfaces.Registry.VariableSound;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;

public class SoundLoader {

	private final Class<? extends SoundEnum> soundClass;
	private final HashMap<SoundEnum, SoundResource> soundMap = new HashMap();

	public SoundLoader(SoundEnum... ss) {
		if (ss.length == 0)
			throw new IllegalArgumentException("You cannot register an empty sound list!");
		soundClass = ss[0].getClass();
		for (SoundEnum s : ss) {
			this.addToMap(s);
		}
		this.init();
	}

	public SoundLoader(Class<? extends SoundEnum> c) {
		soundClass = c;
		for (SoundEnum s : c.getEnumConstants()) {
			this.addToMap(s);
		}
		this.init();
	}

	private void addToMap(SoundEnum s) {
		soundMap.put(s, new SoundResource(s));
		if (s instanceof VariableSound) {
			for (SoundVariant var : ((VariableSound)s).getVariants()) {
				this.addToMap(var);
			}
		}
	}

	private void init() {
		if (soundClass == SingleSound.class) {
			for (SoundEnum s : soundMap.keySet())
				ReikaSoundHelper.registerSingleSound((SingleSound)s);
		}
		else {
			ReikaSoundHelper.registerSoundSet(soundClass);
		}
	}

	public final void register() {
		for (Entry<SoundEnum, SoundResource> et : soundMap.entrySet()) {
			this.registerSound(et.getKey(), et.getValue());
		}
	}

	private void registerSound(SoundEnum e, SoundResource sr) {
		String p = e.getPath();
		DirectResourceManager.getInstance().registerCustomPath(p, e.getCategory(), false);
		this.onRegister(e, p);
		if (e.preload()) {
			try {
				sr.resource = DirectResourceManager.getInstance().getResource(sr.reference);
			}
			catch (IOException ex) {
				DragonAPICore.logError("Caught error when preloading sound '"+e+"':");
				ex.printStackTrace();
			}
		}
	}

	public final ResourceLocation getResource(SoundEnum sound) {
		return soundMap.get(sound).reference;
	}

	protected void onRegister(SoundEnum e, String p) {

	}

	private static class SoundResource {

		private final SoundEnum sound;
		private final ResourceLocation reference;

		private IResource resource;

		private SoundResource(SoundEnum s) {
			sound = s;
			reference = DirectResourceManager.getResource(s.getPath());
		}

	}
}
