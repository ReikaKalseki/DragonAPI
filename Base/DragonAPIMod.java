/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Base;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Auxiliary.Trackers.CommandableUpdateChecker;
import Reika.DragonAPI.Base.DragonAPIMod.LoadProfiler.LoadPhase;
import Reika.DragonAPI.Exception.InstallationException;
import Reika.DragonAPI.Exception.InvalidBuildException;
import Reika.DragonAPI.Exception.JarZipException;
import Reika.DragonAPI.Exception.MissingDependencyException;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Exception.VersionMismatchException;
import Reika.DragonAPI.Exception.VersionMismatchException.APIMismatchException;
import Reika.DragonAPI.Extras.ModVersion;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaFormatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLFingerprintViolationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public abstract class DragonAPIMod {

	protected final boolean isDeObf;
	private final ModVersion version;
	private static ModVersion apiVersion;
	private static final HashMap<String, ModVersion> modVersions = new HashMap();
	//private static final ModVersion api_version;

	private final LoadProfiler profiler;

	static {
		//api_version = ModVersion.readFromFile();
	}

	@EventHandler
	public final void invalidFingerprint(final FMLFingerprintViolationEvent event) {

	}

	protected DragonAPIMod() {
		profiler = new LoadProfiler(this);
		profiler.startTiming(LoadPhase.CONSTRUCT);
		isDeObf = ReikaObfuscationHelper.isDeObfEnvironment();
		if (isDeObf) {
			ReikaJavaLibrary.pConsole(this.getDisplayName()+" is running in a deobfuscated environment!");
		}
		else {
			ReikaJavaLibrary.pConsole(this.getDisplayName()+" is not running in a deobfuscated environment.");
		}

		version = ModVersion.readFromFile(this);
		modVersions.put(this.getClass().getSimpleName(), version);
		ReikaJavaLibrary.pConsole("Registered "+this+" as version "+version);
		if (this.getClass() == DragonAPIInit.class) {
			apiVersion = version;
		}

		ReikaJavaLibrary.pConsole(this.getTechnicalName()+": Active Classloader is: "+this.getClass().getClassLoader());
		profiler.finishTiming();
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

	protected final void verifyInstallation() {
		this.verifyVersions();
		if (this.getModFile().getName().endsWith(".zip"))
			throw new JarZipException(this);
		this.verifyHash();
	}

	private void verifyHash() {
		if (this.getModVersion().isCompiled()) {
			try {
				JarFile jf = new JarFile(this.getModFile());
				//JarFile jf = new JarFile("E:/My Documents/Desktop Stuff/Game Stuff/Modding/Minecraft/mcp 1.7/reobf/DragonAPI 1.7.10 V4a.jar");
				InputStream folder = ReikaFileReader.getFileInsideJar(jf, ReikaJavaLibrary.getTopLevelPackage(this.getClass())+"/");
				String hash = "@TEMPHASH@";//ReikaFileReader.getHash(folder, HashType.SHA256);
				Manifest mf = jf.getManifest();
				if (mf == null)
					throw new InvalidBuildException(this, this.getModFile());
				String attr = mf.getMainAttributes().getValue("ModHash");
				if (attr == null || hash == null || !hash.equals(attr))
					throw new InvalidBuildException(this, this.getModFile());
			}
			catch (IOException e) {
				throw new InvalidBuildException(this, this.getModFile());
			}
		}
	}

	protected final URL getClassFile() {
		return this.getClass().getProtectionDomain().getCodeSource().getLocation();
	}

	public final ModContainer getModContainer() {
		return Loader.instance().getModObjectList().inverse().get(this);
	}

	protected File getModFile() {
		return this.getModContainer().getSource();
	}

	private final void verifyVersions() {
		ModVersion mod = this.getModVersion();
		if (mod.verify()) {
			if (mod.majorVersion != apiVersion.majorVersion || mod.isNewerMinorVersion(apiVersion)) {
				throw new APIMismatchException(this, mod, apiVersion, DragonAPICore.last_API_Version);
			}
			HashMap<String, String> map = this.getDependencies();
			if (map != null) {
				for (String key : map.keySet()) {
					String req = map.get(key);
					ModVersion has = modVersions.get(key);
					if (has == null) {
						throw new MissingDependencyException(this, key);
					}
					else if (!has.isCompiled()) {

					}
					else if (!req.equals(has.toString())) {
						throw new VersionMismatchException(this, mod, key, has, req);
					}
				}
			}
		}
	}

	protected HashMap<String, String> getDependencies() {
		return null;
	}

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

	public final ModVersion getModVersion() {
		return version;
	}

	protected final void startTiming(LoadPhase p) {
		profiler.startTiming(p);
	}

	protected final void finishTiming() {
		profiler.finishTiming();
	}

	@Override
	public final boolean equals(Object o) {
		return o.getClass() == this.getClass() && ((DragonAPIMod)o).getTechnicalName().equalsIgnoreCase(this.getTechnicalName());
	}

	@Override
	public final int hashCode() {
		return ~this.getClass().hashCode()^this.getTechnicalName().hashCode();
	}

	public final boolean isReikasMod() {
		return this.getClass().getName().startsWith("Reika.");
	}

	public static final class LoadProfiler {

		private long time = -1;
		private long total;
		private LoadPhase phase = null;

		private final DragonAPIMod mod;

		private final EnumMap<LoadPhase, Boolean> loaded = new EnumMap(LoadPhase.class);

		private LoadProfiler(DragonAPIMod mod) {
			this.mod = mod;
		}

		private void startTiming(LoadPhase p) {
			if (time != -1)
				throw new IllegalStateException(mod.getTechnicalName()+" is already profiling phase "+phase+"!");
			if (loaded.containsKey(p) && loaded.get(p))
				throw new IllegalStateException(mod.getTechnicalName()+" already finished profiling phase "+phase+"!");
			phase = p;
			time = System.currentTimeMillis();
		}

		protected void finishTiming() {
			long duration = System.currentTimeMillis()-time;
			if (time == -1)
				throw new IllegalStateException(mod.getTechnicalName()+" cannot stop profiling before it starts!");
			time = -1;
			String s = ReikaFormatHelper.millisToHMSms(duration);
			ReikaJavaLibrary.pConsole(mod.getTechnicalName()+": Completed loading phase "+phase+" in "+duration+" ms ("+s+").");
			if (duration > 1800000) { //30 min
				ReikaJavaLibrary.pConsole("Loading time exceeded thirty minutes, indicating very weak hardware. Beware of low framerates.");
			}
			else if (duration > 300000) { //5 min
				ReikaJavaLibrary.pConsole("Loading time exceeded five minutes, indicating weaker hardware. Consider reducing settings.");
			}
			total += duration;
			if (phase == LoadPhase.POSTLOAD)
				ReikaJavaLibrary.pConsole("Total mod loading time: "+total+" ms ("+ReikaFormatHelper.millisToHMSms(total)+").");
			phase = null;
		}

		public static enum LoadPhase {
			CONSTRUCT(),
			PRELOAD(),
			LOAD(),
			POSTLOAD();
		}

	}

}
