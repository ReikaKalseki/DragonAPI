/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI;

import Reika.DragonAPI.Interfaces.Configuration.BooleanConfig;
import Reika.DragonAPI.Interfaces.Configuration.IntegerConfig;

public enum DragonOptions implements IntegerConfig, BooleanConfig {

	LOGLOADING("Console Loading Info", true),
	DEBUGMODE("Debug Mode", false),
	SYNCPACKET("Sync Packet ID", 182),
	NORENDERS("Disable Renders For Debug", false),
	TABNBT("Show TileEntity NBT when using TAB", false),
	SOUNDCHANNELS("Increase sound channel count", true),
	UNNERFOBSIDIAN("Restore Obsidian Blast Resistance", true),
	CHATERRORS("Log errors to chat", true),
	SORTCREATIVE("Sort Creative Tabs Alphabetically", true),
	CUSTOMRENDER("Custom/Donator Renders", true),
	OPONLYUPDATE("Only show update notice to Ops or SSP", false),
	PACKONLYUPDATE("Only show update notice to pack creator", false),
	GREGORES("Force Gregtech Ore Compatibility", true),
	LOGSYNCCME("Log Sync Packet CME Avoidance", true),
	SLOWSYNC("Slow Sync Packets - Only use this as a last resort", false),
	;//COMPOUNDSYNC("Compound Sync Packet System - Use at own risk", false);

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

}
