/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Base;

import java.net.URL;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public abstract class DragonAPIMod {

	//public abstract void invalidFingerprint(final FMLFingerprintViolationEvent event);

	public abstract void preload(FMLPreInitializationEvent evt);

	public abstract void load(FMLInitializationEvent event);

	public abstract void postload(FMLPostInitializationEvent evt);

	public abstract String getDisplayName();

	public abstract String getModAuthorName();

	public abstract URL getDocumentationSite();

	public abstract boolean hasWiki();

	public abstract URL getWiki();

	protected final boolean checkForDragonAPI() {
		try {
			Class.forName("Reika.DragonAPI.DragonAPICore", false, this.getClass().getClassLoader());
			return true;
		}
		catch (ClassNotFoundException e) {
			return false;
		}
	}

	protected final void hasNoDragonAPI() {
		throw new RuntimeException("Error loading "+this.getDisplayName()+": This mod needs DragonAPI to function correctly!");
	}
}
