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

import net.minecraft.util.ResourceLocation;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.IO.DirectResourceManager;
import Reika.DragonAPI.Interfaces.Registry.SoundEnum;
import Reika.DragonAPI.Interfaces.Registry.StreamableSound;
import Reika.DragonAPI.Interfaces.Registry.VariableSound;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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

	@SideOnly(Side.CLIENT)
	public final void register() {
		for (Entry<SoundEnum, SoundResource> et : soundMap.entrySet()) {
			this.registerSound(et.getKey(), et.getValue());
		}
	}

	@SideOnly(Side.CLIENT)
	private void registerSound(SoundEnum e, SoundResource sr) {
		String p = e.getPath();
		boolean stream = e instanceof StreamableSound && ((StreamableSound)e).isStreamed();
		DirectResourceManager.getInstance().registerCustomPath(p, e.getCategory(), stream);
		this.onRegister(e, p);
		if (e.preload()) {
			try {
				sr.resource = (DirectResource)DirectResourceManager.getInstance().getResource(sr.reference);
				if (stream)
					sr.resource.cacheData = false;
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

		private DirectResource resource;

		private SoundResource(SoundEnum s) {
			sound = s;
			reference = FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT ? getReference(s) : null;
		}

		@SideOnly(Side.CLIENT)
		private static ResourceLocation getReference(SoundEnum s) {
			return DirectResourceManager.getResource(s.getPath());
		}

	}
}
