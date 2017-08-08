/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.Registry;

import Reika.DragonAPI.Interfaces.Configuration.MatchingConfig;


public interface IDRegistry extends MatchingConfig {

	public String getConfigName();

	public int getDefaultID();

	public String getCategory();

}
