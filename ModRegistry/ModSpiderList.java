/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModRegistry;

import Reika.DragonAPI.Auxiliary.APIRegistry;

public enum ModSpiderList {

	HEATSCAR(APIRegistry.NATURA, "mods.natura.entity.FlameSpider"),
	KING(APIRegistry.TWILIGHT, "twilightforest.entity.EntityTFKingSpider"),
	HEDGE(APIRegistry.TWILIGHT, "twilightforest.entity.EntityTFHedgeSpider");

	private Class entityClass;

	public static final ModSpiderList[] spiderList = ModSpiderList.values();

	private ModSpiderList(APIRegistry req, String className) {

	}

}
