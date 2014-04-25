/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import Reika.DragonAPI.ModList;

public class ModClass {

	public final ModList mod;
	public final String className;

	public ModClass(ModList mod, String c) {
		this.mod = mod;
		className = c;
	}

}
