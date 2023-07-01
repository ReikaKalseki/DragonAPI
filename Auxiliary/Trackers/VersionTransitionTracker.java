package Reika.DragonAPI.Auxiliary.Trackers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Auxiliary.PopupWriter;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.IO.ReikaFileReader;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class VersionTransitionTracker {

	public static final VersionTransitionTracker instance = new VersionTransitionTracker();

	private final HashMap<String, String> lastVersions = new HashMap();
	private final HashSet<String> newVersions = new HashSet();

	private VersionTransitionTracker() {

	}

	private File getFilename(World world) {
		return new File(world.getSaveHandler().getWorldDirectory(), "modversions.list");
	}

	public void onWorldLoad(World world) {
		if (world.provider.dimensionId == 0 && !world.isRemote && DragonOptions.VERSIONCHANGEWARN.getValue() > 0) {
			this.loadCacheAndUpdate(world);
			this.saveCache(world);
		}
	}

	private void loadCacheAndUpdate(World world) {
		lastVersions.clear();
		newVersions.clear();

		File f = this.getFilename(world);
		if (f.exists()) {
			List<String> li = ReikaFileReader.getFileAsLines(f, true, Charsets.UTF_8);
			for (String s : li) {
				String[] parts = s.split("=");
				lastVersions.put(parts[0], parts[1]);
			}

			for (ModContainer mc : Loader.instance().getActiveModList()) {
				if (this.updated(mc)) {
					newVersions.add(mc.getModId());
				}
			}
		}

		this.saveCache(world);
	}

	private void saveCache(World world) {
		try {
			File f = this.getFilename(world);
			f.delete();
			f.getParentFile().mkdirs();
			f.createNewFile();

			ArrayList<String> li = new ArrayList();

			for (ModContainer mc : Loader.instance().getActiveModList()) {
				li.add(mc.getModId()+"="+this.parseModVersion(mc));
			}

			ReikaFileReader.writeLinesToFile(f, li, true, Charsets.UTF_8);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String parseModVersion(ModContainer mc) {
		String ret = mc.getMod() instanceof DragonAPIMod ? ((DragonAPIMod)mc.getMod()).getModVersion().toString() : mc.getVersion();
		return Strings.isNullOrEmpty(ret) ? "[NONE]" : ret;
	}

	private String getDisplayName(ModContainer mc) {
		return mc.getMod() instanceof DragonAPIMod ? ((DragonAPIMod)mc.getMod()).getDisplayName() : mc.getName();
	}

	public String getPreviousModVersion(ModContainer mod) {
		return lastVersions.get(mod.getModId());
	}

	public boolean updated(ModContainer mod) {
		if (DragonOptions.VERSIONCHANGEWARN.getValue() == 1) {
			Object modo = mod.getMod();
			if (modo instanceof DragonAPIMod) {
				if (!((DragonAPIMod)modo).isReikasMod())
					return false;
			}
			else {
				return false;
			}
		}
		return !this.parseModVersion(mod).equals(this.getPreviousModVersion(mod));
	}

	public void notifyPlayerOfVersionChanges(EntityPlayerMP emp) {
		if (this.haveModsUpdated()) {
			String s0 = newVersions.size()+" of your mods have changed version (see the log for more details). It is strongly recommended you read their changelogs.";
			PopupWriter.instance.addMessage(s0);
			DragonAPICore.log(newVersions.size()+" mod version changes detected: ");
			Map<String, ModContainer> mods = Loader.instance().getIndexedModList();
			for (String s : newVersions) {
				String old = lastVersions.get(s);
				ModContainer mc = mods.get(s);
				DragonAPICore.log(this.getDisplayName(mc)+": "+old+" --> "+this.parseModVersion(mc));
			}
		}
	}

	public boolean haveModsUpdated() {
		return !newVersions.isEmpty();
	}

}
