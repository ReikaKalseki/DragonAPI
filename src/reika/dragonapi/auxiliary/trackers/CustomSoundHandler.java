/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.auxiliary.trackers;

import java.util.HashMap;
import java.util.Locale;

import net.minecraft.client.audio.SoundCategory;
import net.minecraft.util.ResourceLocation;
import reika.dragonapi.io.DirectResourceManager;

@Deprecated
public class CustomSoundHandler {

	public static final CustomSoundHandler instance = new CustomSoundHandler();

	private HashMap<String, ResourceLocation> sounds = new HashMap();

	private CustomSoundHandler() {

	}

	/** Do not add ".ogg" to the file name. All strings are forced to lowercase.
	 * To load the sound, put it in /assets/mod folder/sound */
	public void addSound(String mod, String sound, SoundCategory cat) {
		String name = mod.toLowerCase(Locale.ENGLISH);
		String file = sound.toLowerCase(Locale.ENGLISH);
		sounds.put(sound, new ResourceLocation(mod, sound));
		DirectResourceManager.getInstance().registerSound(mod, sound+".ogg", cat);
	}

	public ResourceLocation getSoundResource(String file) {
		return sounds.get(file);
	}

}
