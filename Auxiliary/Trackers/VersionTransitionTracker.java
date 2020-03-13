package Reika.DragonAPI.Auxiliary.Trackers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.PopupWriter;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Extras.ModVersion;
import Reika.DragonAPI.IO.ReikaFileReader;

public class VersionTransitionTracker {

	public static final VersionTransitionTracker instance = new VersionTransitionTracker();

	private final HashMap<String, ModVersion> lastVersions = new HashMap();
	private final HashSet<String> newVersions = new HashSet();

	private VersionTransitionTracker() {

	}

	private File getFilename(World world) {
		return new File(world.getSaveHandler().getWorldDirectory(), "DragonAPI/modversions.list");
	}

	public void loadCacheAndUpdate(World world) {
		File f = this.getFilename(world);
		if (f.exists()) {
			ArrayList<String> li = ReikaFileReader.getFileAsLines(f, true);
			for (String s : li) {
				String[] parts = s.split("=");
				lastVersions.put(parts[0], ModVersion.getFromString(parts[1]));
			}
		}

		for (DragonAPIMod mod : DragonAPIMod.getAllMods()) {
			if (this.updated(world, mod)) {
				newVersions.add(mod.getTechnicalName());
			}
		}
	}

	public void onLogin() {

	}

	public void saveCache(World world) {
		try {
			File f = this.getFilename(world);
			f.delete();
			f.getParentFile().mkdirs();
			f.createNewFile();

			ArrayList<String> li = new ArrayList();

			for (DragonAPIMod mod : DragonAPIMod.getAllMods()) {
				li.add(mod.getTechnicalName()+"="+mod.getModVersion().toString());
			}

			ReikaFileReader.writeLinesToFile(f, li, true);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ModVersion getPreviousModVersion(World world, DragonAPIMod mod) {
		return lastVersions.get(mod.getTechnicalName());
	}

	public boolean updated(World world, DragonAPIMod mod) {
		return !mod.getModVersion().equals(this.getPreviousModVersion(world, mod));
	}

	public void notifyPlayerOfVersionChanges(EntityPlayerMP emp) {
		if (!newVersions.isEmpty()) {
			String s0 = newVersions.size()+" of your mods have changed version (see the log for more details). It is strongly recommended you read their changelogs.";
			PopupWriter.instance.addMessage(s0);
			DragonAPICore.log(newVersions.size()+" mod version changes detected: ");
			for (String s : newVersions) {
				ModVersion old = lastVersions.get(s);
				DragonAPIMod mod = DragonAPIMod.getByName(s);
				DragonAPICore.log(mod.getDisplayName()+": "+old.toString()+" --> "+mod.getModVersion().toString());
			}
		}
	}

}
