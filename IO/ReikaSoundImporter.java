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
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.ModLogger;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

import com.google.common.collect.Lists;

public class ReikaSoundImporter {

	private static final String DEOBF_MAP = "nameToSoundPoolEntriesMapping";
	private static final String OBF_MAP = "field_77461_d";

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
			Field f = c.getDeclaredField(getMapFieldName());
			f.setAccessible(true);
			return (Map)f.get(sounds);
		}
		catch (NoSuchFieldException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not load the Sound Pool!");
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not load the Sound Pool!");
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not load the Sound Pool!");
		}
		catch (SecurityException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not load the Sound Pool!");
		}
	}

	private static String getMapFieldName() {
		return DragonAPICore.isDeObfEnvironment() ? DEOBF_MAP : OBF_MAP;
	}

}
