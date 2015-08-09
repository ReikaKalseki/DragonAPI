/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import Reika.DragonAPI.Interfaces.Registry.ModEntry;
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
