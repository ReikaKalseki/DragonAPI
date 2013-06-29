/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;

import net.minecraftforge.common.Configuration;

/** This is an interface for ENUMS! */
public interface ConfigRegistry {

	public boolean isBoolean();

	public boolean isNumeric();

	public Class getPropertyType();

	public int setValue(Configuration config);

	public String getLabel();

	public boolean setState(Configuration config);

	public boolean getState();

	public int getValue();

}
