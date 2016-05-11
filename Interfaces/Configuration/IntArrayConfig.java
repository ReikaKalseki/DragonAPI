/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.Configuration;

public interface IntArrayConfig extends ConfigList {

	public boolean isIntArray();

	public int[] getIntArray();

	public int[] getDefaultIntArray();

}
