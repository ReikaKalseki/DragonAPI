/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI;

import Reika.DragonAPI.Interfaces.Configuration.BooleanConfig;
import Reika.DragonAPI.Interfaces.Configuration.IntegerConfig;
import Reika.DragonAPI.Interfaces.Configuration.UserSpecificConfig;

public enum DragonOptions implements IntegerConfig, BooleanConfig, UserSpecificConfig {

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
	APRIL("Enable Vernal Amusement Behavior", true),
	NOALPHATEST("Disable Alpha Clipping in WorldRenderer", true),
	PARTICLELIMIT("Particle Limit (Vanilla = 4000)", 4000),
	DEBUGKEY("Debug Overlay Key (LWJGL ID)", 0x0F), //Keyboard.KEY_TAB
	//RECURSE("Recursion Limit Override", -1),
	//COMPOUNDSYNC("Compound Sync Packet System - Use at own risk", false);
	DIRECTOC("Direct OpenComputers Support", true),
	AUTOREBOOT("Automatic Reboot Interval (Seconds)", -1),
	XPMERGE("Merge XP Orbs Like Items", true),
	RAINTICK("Extra Block Ticks When Raining", true),
	PROTECTNEW("Prevent Mobs From Targeting Players Immediately After Logging In", true),
	SKINCACHE("Cache Skins", true),
	;

	private String label;
	private boolean defaultState;
	private int defaultValue;
	private String defaultString;
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
			case NOALPHATEST:
			case TABNBT:
			case PARTICLELIMIT:
			case DEBUGKEY:
				return true;
			default:
				return false;
		}
	}

}
