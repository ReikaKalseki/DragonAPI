/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.IO;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;
import java.util.Map;

import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPool;
import net.minecraft.client.audio.SoundPoolEntry;
import Reika.DragonAPI.Instantiable.ModLogger;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;

import com.google.common.collect.Lists;

public class ReikaSoundImporter {

	public static void addSound(ModLogger log, String name, URL url, SoundManager manager) {
		SoundPool sounds = manager.soundPoolSounds;
		Map mappings = getMappings(sounds);
		if (mappings == null) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Could not load SoundPool mappings!");
			return;
		}

		String s1 = name;

		name = name.substring(0, name.indexOf("."));
		while (Character.isDigit(name.charAt(name.length() - 1)))
			name = name.substring(0, name.length() - 1);
		name = name.replaceAll("/", ".");

		Object object = mappings.get(name);

		if (object == null) {
			object = Lists.newArrayList();
			mappings.put(name, object);
		}

		((List)object).add(new SoundPoolEntry(s1, url));
		SoundPoolEntry ent = (SoundPoolEntry)((List)object).get(0);
		log.log("Adding custom-URL sound "+name+" @ "+url);
	}

	private static Map getMappings(SoundPool sounds) {
		try {
			Class c = sounds.getClass();
			Field f = ReikaObfuscationHelper.getField("nameToSoundPoolEntriesMapping");
			f.setAccessible(true);
			return (Map)f.get(sounds);
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not load the Sound Pool!");
		}
	}

}
