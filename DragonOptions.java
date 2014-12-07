/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI;

import Reika.DragonAPI.Interfaces.ConfigList;

public enum DragonOptions implements ConfigList {

	LOGLOADING("Console Loading Info", true),
	DEBUGMODE("Debug Mode", false),
	SYNCPACKET("Sync Packet ID", 182),
	NORENDERS("Disable Renders For Debug", false),
	TABNBT("Show TileEntity NBT when using TAB", false),
	SOUNDCHANNELS("Increase sound channel count", true),
	UNNERFOBSIDIAN("Restore Obsidian Blast Resistance", true);

	private String label;
	private boolean defaultState;
	private int defaultValue;
	private float defaultFloat;
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

	private DragonOptions(String l, float d) {
		label = l;
		defaultFloat = d;
		type = float.class;
	}

	public boolean isBoolean() {
		return type == boolean.class;
	}

	public boolean isNumeric() {
		return type == int.class;
	}

	public boolean isDecimal() {
		return type == float.class;
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

	public float getFloat() {
		return (Float)DragonAPIInit.config.getControl(this.ordinal());
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
	public float getDefaultFloat() {
		return defaultFloat;
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
