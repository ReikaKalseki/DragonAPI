/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI;

import net.minecraft.world.ChunkCoordIntPair;

import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher.Key;
import Reika.DragonAPI.Interfaces.Configuration.BooleanConfig;
import Reika.DragonAPI.Interfaces.Configuration.IntegerConfig;
import Reika.DragonAPI.Interfaces.Configuration.StringArrayConfig;
import Reika.DragonAPI.Interfaces.Configuration.StringConfig;
import Reika.DragonAPI.Interfaces.Configuration.UserSpecificConfig;

public enum DragonOptions implements IntegerConfig, BooleanConfig, StringArrayConfig, StringConfig, UserSpecificConfig {

	LOGLOADING("Console Loading Info", true),
	FILELOG("Log Loading Info To Separate File", false),
	DEBUGMODE("Debug Mode", false),
	SYNCPACKET("Sync Packet ID", 182),
	NORENDERS("Disable Renders For Debug", false),
	TABNBT("Show TileEntity NBT when using Debug Key", false),
	SOUNDCHANNELS("Increase sound channel count", true),
	UNNERFOBSIDIAN("Restore Obsidian Blast Resistance", true),
	NOHOTBARSWAP("Disable Hotbar Swapping", false),
	CHATERRORS("Log errors to chat", true),
	SORTCREATIVE("Sort Creative Tabs Alphabetically", true),
	CUSTOMRENDER("Custom/Donator Renders", true),
	OPONLYUPDATE("Only show update notice to Ops or SSP", false),
	PACKONLYUPDATE("Only show update notice to pack creator", false),
	GREGORES("Force Gregtech Ore Compatibility", true),
	LOGSYNCCME("Log Sync Packet CME Avoidance", true),
	SLOWSYNC("Slow Sync Packets - Only use this as a last resort", false),
	NONULLITEMS("Disallow Null-Item ItemStacks to Prevent Crashes", true),
	LAGWARNING("Minimum Delay (ms) for 'Can't Keep Up!' Log Warning", 0),
	CHECKSANITY("Check Environment Sanity", false),
	FIXSANITY("Attempt to Repair Environment Sanity", false),
	ADMINPERMBYPASS("Admins Bypass Permissions", true),
	SOUNDHASHMAP("Use HashMap for Sound Categories - Only use if necessary", false),
	FILEHASH("Compare mod file hashes between client and server", true),
	APRIL("Enable Temporally Dependent Amusement Behavior", true),
	//NOALPHATEST("Disable Alpha Clipping in WorldRenderer", true),
	PARTICLELIMIT("Particle Limit (Vanilla = 4000)", 4000),
	DEBUGKEY("Debug Overlay Key (LWJGL ID)", 0x0F), //Keyboard.KEY_TAB
	//RECURSE("Recursion Limit Override", -1),
	//COMPOUNDSYNC("Compound Sync Packet System - Use at own risk", false);
	DIRECTOC("Direct OpenComputers Support", false),
	AUTOREBOOT("Automatic Reboot Interval (Seconds)", -1),
	XPMERGE("Merge XP Orbs Like Items", true),
	RAINTICK("Extra Block Ticks When Raining", true),
	PROTECTNEW("Prevent Mobs From Targeting Players Immediately After Logging In", true),
	SKINCACHE("Cache Skins", true),
	BIOMEFIRE("Biome Humidity Dependent Fire Spread", true),
	ADMINPROFILERS("Restrict profiling abilities to admins", true),
	BYTECODELIST("Bytecodeexec command user UUID whitelist", new String[0]),
	CTRLCOLLECT("Automatic Collection of Inventories; set to 'NULL' to disable", Key.LCTRL.name()),
	AFK("AFK Timer Threshold (Seconds); Set to 0 to Disable", 120), //2 min
	REROUTEEYES("Reroute Ender Eyes to Stronghold Entrances", false),
	WORLDSIZE("Expected Approximate Maximum World Size (Radius)", 5000),
	WORLDCENTERX("Expected Approximate World Center Location X", 0),
	WORLDCENTERZ("Expected Approximate World Center Location Z", 0),
	NORAINFX("Disable rain sound and particles", false);
	;

	private String label;
	private boolean defaultState;
	private int defaultValue;
	private String defaultString;
	private String[] defaultStringArray;
	private Class type;
	private boolean enforcing = false;

	public static final DragonOptions[] optionList = values();

	private DragonOptions(String l, boolean d) {
		label = l;
		defaultState = d;
		type = boolean.class;
	}

	private DragonOptions(String l, boolean d, boolean tag) {
		this(l, d);
		enforcing = true;
	}

	private DragonOptions(String l, int d) {
		label = l;
		defaultValue = d;
		type = int.class;
	}

	private DragonOptions(String l, String s) {
		label = l;
		defaultString = s;
		type = String.class;
	}

	private DragonOptions(String l, String[] s) {
		label = l;
		defaultStringArray = s;
		type = String[].class;
	}

	public boolean isBoolean() {
		return type == boolean.class;
	}

	public boolean isNumeric() {
		return type == int.class;
	}

	public boolean isString() {
		return type == String.class;
	}

	public Class getPropertyType() {
		return type;
	}

	public String getLabel() {
		return label;
	}

	public boolean getState() {
		return (Boolean)DragonAPIInit.config.getControl(this.ordinal());
	}

	public int getValue() {
		return (Integer)DragonAPIInit.config.getControl(this.ordinal());
	}

	public String getString() {
		return (String)DragonAPIInit.config.getControl(this.ordinal());
	}

	public boolean isDummiedOut() {
		return type == null;
	}

	@Override
	public boolean getDefaultState() {
		return defaultState;
	}

	@Override
	public int getDefaultValue() {
		return defaultValue;
	}

	@Override
	public boolean isEnforcingDefaults() {
		return enforcing;
	}

	@Override
	public boolean shouldLoad() {
		return true;
	}

	@Override
	public boolean isUserSpecific() {
		switch(this) {
			case LOGLOADING:
			case FILELOG:
			case DEBUGMODE:
			case NORENDERS:
			case SOUNDCHANNELS:
			case NOHOTBARSWAP:
			case CHATERRORS:
			case SORTCREATIVE:
			case CUSTOMRENDER:
			case APRIL:
				//case NOALPHATEST:
			case TABNBT:
			case PARTICLELIMIT:
			case DEBUGKEY:
			case NORAINFX:
				return true;
			default:
				return false;
		}
	}

	@Override
	public boolean isStringArray() {
		return type == String[].class;
	}

	@Override
	public String[] getStringArray() {
		return (String[])DragonAPIInit.config.getControl(this.ordinal());
	}

	@Override
	public String[] getDefaultStringArray() {
		return defaultStringArray;
	}

	@Override
	public String getDefaultString() {
		return defaultString;
	}

	public static Key getCollectKey() {
		if (CTRLCOLLECT.getString().equalsIgnoreCase("null"))
			return null;
		return Key.readFromConfig(DragonAPIInit.instance, CTRLCOLLECT);
	}

	/** In block coords */
	public static ChunkCoordIntPair getWorldCenter() {
		return new ChunkCoordIntPair(WORLDCENTERX.getValue(), WORLDCENTERZ.getValue());
	}

}
