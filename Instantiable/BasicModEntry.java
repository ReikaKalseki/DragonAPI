package Reika.DragonAPI.Instantiable;

import Reika.DragonAPI.Interfaces.ModEntry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class BasicModEntry implements ModEntry {

	public final String modId;
	private final ModContainer container;

	public BasicModEntry(String id) {
		this(Loader.instance().getIndexedModList().get(id));
	}

	public BasicModEntry(ModContainer mc) {
		modId = mc.getModId();
		container = mc;
	}

	@Override
	public boolean isLoaded() {
		return Loader.isModLoaded(modId);
	}

	@Override
	public String getVersion() {
		return container.getDisplayVersion();
	}

	@Override
	public String getModLabel() {
		return modId;
	}

	@Override
	public String getDisplayName() {
		return container.getName();
	}

	@Override
	public Class getBlockClass() {
		return null;
	}

	@Override
	public Class getItemClass() {
		return null;
	}

}
