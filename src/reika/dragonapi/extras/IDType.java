/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.extras;

import reika.dragonapi.libraries.java.ReikaStringParser;

public enum IDType {

	BLOCK(),
	ITEM(),
	ENTITY(),
	BIOME(),
	POTION(),
	ENCHANTMENT(),
	FLUID(),
	FLUIDCONTAINER();

	public static final IDType[] list = values();

	public String getName() {
		return ReikaStringParser.capFirstChar(this.name());
	}
}
