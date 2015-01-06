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

import java.util.logging.Level;

import net.minecraft.util.EnumChatFormatting;
import Reika.DragonAPI.Interfaces.ConfigList;

public class Alert {

	private final ConfigList option;
	private final String message;
	public final Level severity;
	public final String ID;

	public Alert(String id, ConfigList cfg, Level lvl, String msg) {
		option = cfg;
		message = msg;
		severity = lvl;
		ID = id;
	}

	@Override
	public String toString() {
		String c = this.isSevere() ? "(Severe!)" : "";
		return ID+" "+this.getValue()+" (normally "+this.getDefault()+"):"+message+"; "+c;
	}

	public final boolean isSevere() {
		return severity == Level.SEVERE;
	}

	private EnumChatFormatting getColor() {
		if (severity == Level.SEVERE)
			return EnumChatFormatting.RED;
		if (severity == Level.WARNING)
			return EnumChatFormatting.GOLD;
		return EnumChatFormatting.WHITE;
	}

	public String getMessage() {
		return this.getColor().toString()+message+"\nDefault: "+this.getDefault()+"\nActual Value: "+this.getValue();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Alert && ((Alert)o).option == option && ((Alert)o).message.equals(message);
	}

	private final Object getDefault() {
		if (option.isBoolean()) {
			return option.getDefaultState();
		}
		else if (option.isNumeric()) {
			return option.getDefaultValue();
		}
		else if (option.isDecimal()) {
			return option.getDefaultFloat();
		}
		else {
			return null;
		}
	}

	private final Object getValue() {
		if (option.isBoolean()) {
			return option.getState();
		}
		else if (option.isNumeric()) {
			return option.getValue();
		}
		else if (option.isDecimal()) {
			return option.getFloat();
		}
		else {
			return null;
		}
	}

}
