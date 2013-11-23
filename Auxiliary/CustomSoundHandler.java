/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class CustomSoundHandler {

	public static final CustomSoundHandler instance = new CustomSoundHandler();

	private ArrayList<String> mods = new ArrayList();
	private HashMap<String, ArrayList<String>> sounds = new HashMap();

	private CustomSoundHandler() {

	}

	/** Do not add ".ogg" to the file name. All strings are forced to lowercase.
	 * To load the sound, put it in /assets/mod folder/sound */
	public void addSound(String mod, String sound) {
		String name = mod.toLowerCase();
		String file = sound.toLowerCase();
		if (mods.contains(name)) {
			ArrayList li = sounds.get(name);
			if (!li.contains(file))
				li.add(file);
		}
		else {
			mods.add(name);
			ArrayList<String> li = new ArrayList();
			li.add(file);
			sounds.put(name, li);
		}
	}

	@ForgeSubscribe
	public void onSound(SoundLoadEvent event) {
		for (int i = 0; i < mods.size(); i++) {
			String mod = mods.get(i);
			ArrayList<String> li = sounds.get(mod);
			for (int j = 0; j < li.size(); j++) {
				String sound = li.get(j);
				this.registerSound(event, mod, sound);
			}
		}
	}

	private void registerSound(SoundLoadEvent event, String mod, String sound) {
		String file = mod+":"+sound+".ogg";
		try {
			event.manager.addSound(file);
			DragonAPIInit.instance.getModLogger().log("Registering custom sound "+file);
		}
		catch (Exception e) {
			String type = e.getClass().getSimpleName();
			String msg = e.getMessage();
			ReikaJavaLibrary.pConsole("DRAGONAPI: Failed to load custom sound "+file+". Reason: "+msg+" ("+type+")");
			//e.printStackTrace();
		}
	}

}
