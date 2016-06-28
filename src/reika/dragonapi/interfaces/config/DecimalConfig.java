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

public interface DecimalConfig extends ConfigList {

	public boolean isDecimal();

	//public float setDecimal(Configuration config);

	public float getFloat();

	public float getDefaultFloat();

}
