/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import java.util.logging.Level;

import net.minecraft.util.EnumChatFormatting;
import Reika.DragonAPI.Interfaces.Configuration.BooleanConfig;
import Reika.DragonAPI.Interfaces.Configuration.ConfigList;
import Reika.DragonAPI.Interfaces.Configuration.DecimalConfig;
import Reika.DragonAPI.Interfaces.Configuration.IntArrayConfig;
import Reika.DragonAPI.Interfaces.Configuration.IntegerConfig;
import Reika.DragonAPI.Interfaces.Configuration.StringArrayConfig;
import Reika.DragonAPI.Interfaces.Configuration.StringConfig;

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
		if (option instanceof BooleanConfig && ((BooleanConfig)option).isBoolean()) {
			return ((BooleanConfig)option).getDefaultState();
		}
		else if (option instanceof IntegerConfig && ((IntegerConfig)option).isNumeric()) {
			return ((IntegerConfig)option).getDefaultValue();
		}
		else if (option instanceof DecimalConfig && ((DecimalConfig)option).isDecimal()) {
			return ((DecimalConfig)option).getDefaultFloat();
		}
		else if (option instanceof StringConfig && ((StringConfig)option).isString()) {
			return ((StringConfig)option).getDefaultString();
		}
		else if (option instanceof IntArrayConfig && ((IntArrayConfig)option).isIntArray()) {
			return ((IntArrayConfig)option).getDefaultIntArray();
		}
		else if (option instanceof StringArrayConfig && ((StringArrayConfig)option).isStringArray()) {
			return ((StringArrayConfig)option).getDefaultStringArray();
		}
		else {
			return null;
		}
	}

	private final Object getValue() {
		if (option instanceof BooleanConfig && ((BooleanConfig)option).isBoolean()) {
			return ((BooleanConfig)option).getState();
		}
		else if (option instanceof IntegerConfig && ((IntegerConfig)option).isNumeric()) {
			return ((IntegerConfig)option).getValue();
		}
		else if (option instanceof DecimalConfig && ((DecimalConfig)option).isDecimal()) {
			return ((DecimalConfig)option).getFloat();
		}
		else if (option instanceof StringConfig && ((StringConfig)option).isString()) {
			return ((StringConfig)option).getString();
		}
		else if (option instanceof IntArrayConfig && ((IntArrayConfig)option).isIntArray()) {
			return ((IntArrayConfig)option).getIntArray();
		}
		else if (option instanceof StringArrayConfig && ((StringArrayConfig)option).isStringArray()) {
			return ((StringArrayConfig)option).getStringArray();
		}
		else {
			return null;
		}
	}

}
