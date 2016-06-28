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

import net.minecraftforge.common.config.Property;


public interface BoundedConfig extends ConfigList {

	public boolean isValueValid(Property p);

	public String getBoundsAsString();

}
