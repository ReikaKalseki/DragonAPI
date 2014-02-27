/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;


/** This is an interface for ENUMS! */
public interface ConfigList {

	public boolean isBoolean();

	public boolean isNumeric();

	public boolean isDecimal();

	public Class getPropertyType();

	//public int setValue(Configuration config);

	//public float setDecimal(Configuration config);

	public String getLabel();

	//public boolean setState(Configuration config);

	public boolean getState();

	public int getValue();

	public float getFloat();

	public boolean getDefaultState();

	public int getDefaultValue();

	public float getDefaultFloat();

	//public boolean isDummiedOut();

	public boolean isEnforcingDefaults();

}
