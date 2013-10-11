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

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Exception.InstallationException;
import Reika.DragonAPI.Instantiable.ModLogger;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public abstract class DragonAPIMod {

	protected final boolean isDeObf;

	//public abstract void invalidFingerprint(final FMLFingerprintViolationEvent event);

	public DragonAPIMod() {
		isDeObf = DragonAPICore.isDeObfEnvironment();
		if (isDeObf) {
			ReikaJavaLibrary.pConsole(this.getDisplayName()+" is running in a deobfuscated environment!");
		}
		else {
			ReikaJavaLibrary.pConsole(this.getDisplayName()+" is not running in a deobfuscated environment!");
		}
	}

	@EventHandler
	public abstract void preload(FMLPreInitializationEvent evt);

	@EventHandler
	public abstract void load(FMLInitializationEvent event);

	@EventHandler
	public abstract void postload(FMLPostInitializationEvent evt);

	public abstract String getDisplayName();

	public abstract String getModAuthorName();

	public abstract URL getDocumentationSite();

	public abstract boolean hasWiki();

	public abstract URL getWiki();

	public abstract boolean hasVersion();

	public abstract String getVersionName();

	public final String getTechnicalName() {
		return this.getDisplayName().toUpperCase();
	}

	protected void hasNoDragonAPI() {
		throw new InstallationException(this, "This mod needs DragonAPI to function correctly!");
	}

	public abstract ModLogger getModLogger();
}
