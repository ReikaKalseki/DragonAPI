/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.Configuration;

public interface DecimalConfig extends ConfigList {

	public boolean isDecimal();

	//public float setDecimal(Configuration config);

	public float getFloat();

	public float getDefaultFloat();

}
