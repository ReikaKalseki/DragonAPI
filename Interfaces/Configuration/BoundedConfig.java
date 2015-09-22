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

import net.minecraftforge.common.config.Property;


public interface BoundedConfig extends ConfigList {

	public boolean isValueValid(Property p);

	public String getBoundsAsString();

}
