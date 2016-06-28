/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.interfaces.config;

public interface IntegerConfig extends ConfigList {

	public boolean isNumeric();

	//public int setValue(Configuration config);

	public int getValue();

	public int getDefaultValue();

}
