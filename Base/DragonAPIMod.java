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

import Reika.DragonAPI.Exception.InstallationException;
import Reika.DragonAPI.Libraries.ReikaJavaLibrary;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public abstract class DragonAPIMod {

	//public abstract void invalidFingerprint(final FMLFingerprintViolationEvent event);

	@PreInit
	public abstract void preload(FMLPreInitializationEvent evt);

	@Init
	public abstract void load(FMLInitializationEvent event);

	@PostInit
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

	protected final void checkAPI() {
		if (!this.checkForDragonAPI())
			this.hasNoDragonAPI();
	}

	protected final boolean checkForDragonAPI() {
		try {
			Class.forName("Reika.DragonAPI.DragonAPICore", false, this.getClass().getClassLoader());
			return true;
		}
		catch (ClassNotFoundException e) {
			return false;
		}
	}

	protected void hasNoDragonAPI() {
		throw new InstallationException(this, "This mod needs DragonAPI to function correctly!");
	}

	protected final boolean isDeObfEnvironment() {
		try {
			Class.forName("net.minecraft.world.gen.NoiseGeneratorOctaves");
			ReikaJavaLibrary.pConsole(this.getDisplayName()+" is running in a deobfuscated environment!");
			return true;
		}
		catch (ClassNotFoundException e) {
			ReikaJavaLibrary.pConsole(this.getDisplayName()+" is not running in a deobfuscated environment.");
			return false;
		}
	}
}
