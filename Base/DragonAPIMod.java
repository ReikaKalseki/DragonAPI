/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Base;

import java.net.MalformedURLException;
import java.net.URL;

import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Auxiliary.CommandableUpdateChecker;
import Reika.DragonAPI.Exception.InstallationException;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Extras.ModVersion;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLFingerprintViolationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public abstract class DragonAPIMod {

	protected final boolean isDeObf;
	private final ModVersion version;
	//private static final ModVersion api_version;

	static {
		//api_version = ModVersion.readFromFile();
	}

	@EventHandler
	public final void invalidFingerprint(final FMLFingerprintViolationEvent event) {

	}

	protected DragonAPIMod() {
		isDeObf = ReikaObfuscationHelper.isDeObfEnvironment();
		if (isDeObf) {
			ReikaJavaLibrary.pConsole(this.getDisplayName()+" is running in a deobfuscated environment!");
		}
		else {
			ReikaJavaLibrary.pConsole(this.getDisplayName()+" is not running in a deobfuscated environment.");
		}

		version = ModVersion.readFromFile();

		if (this.getClass() != DragonAPIInit.class)
			;//this.validateDragonAPI();
	}

	@EventHandler
	public abstract void preload(FMLPreInitializationEvent evt);

	@EventHandler
	public abstract void load(FMLInitializationEvent event);

	@EventHandler
	public abstract void postload(FMLPostInitializationEvent evt);

	protected final void basicSetup(FMLPreInitializationEvent evt) {
		MinecraftForge.EVENT_BUS.register(this);
		ReikaRegistryHelper.setupModData(this, evt);
		CommandableUpdateChecker.instance.registerMod(this);
	}
	/*
	private void validateDragonAPI() {
		ModVersion mod = this.getModVersion();
		ModVersion api = this.getAPIVersion();
		if (mod.majorVersion != api.majorVersion || mod.isNewerMinorVersion(api)) {
			throw new VersionMismatchException(this, api_instance);
		}
	}*/

	protected final void onInit(FMLInitializationEvent event) {

	}

	protected final void onPostInit(FMLPostInitializationEvent evt) {

	}

	public abstract String getUpdateCheckURL();

	public abstract String getDisplayName();

	public abstract String getModAuthorName();

	public abstract URL getDocumentationSite();

	public abstract String getWiki();

	public final URL getWikiLink() {
		try {
			return new URL(this.getWiki());
		}
		catch (MalformedURLException e) {
			throw new RegistrationException(this, "The mod provided a malformed URL for its documentation site!");
		}
	}

	public final String getTechnicalName() {
		return this.getDisplayName().toUpperCase();
	}

	protected void hasNoDragonAPI() {
		throw new InstallationException(this, "This mod needs DragonAPI to function correctly!");
	}

	public abstract ModLogger getModLogger();

	@Override
	public final String toString() {
		return this.getTechnicalName();
	}
	/*
	private static final ModVersion getAPIVersion() {
		return api_version;//ModVersion.getFromString("@MAJOR_VERSION@"+"@MINOR_VERSION@");
	}*/

	public final ModVersion getModVersion() {/*
		//if (name.equals("bin"))
		//	return ModVersion.source;
		String major = this.getMajorVersion();
		String minor = this.getMinorVersion();
		if (major.startsWith("$")) { //dev environment
			return ModVersion.source;
		}
		return new ModVersion(Integer.parseInt(major), minor.isEmpty() ? '\0' : minor.charAt(0));
		//return ModVersion.getFromString(this.getMajorVersion()+this.getMinorVersion());*/
		return version;
	}

}
