/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Extras;

import Reika.DragonAPI.Libraries.Java.ReikaStringParser;

public enum IDType {

	BLOCK(),
	ITEM(),
	ENTITY(),
	BIOME(),
	POTION(),
	FLUID(),
	FLUIDCONTAINER();

	public static final IDType[] list = values();

	public String getName() {
		return ReikaStringParser.capFirstChar(this.name());
	}
}
