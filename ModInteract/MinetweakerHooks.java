/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import java.util.Collection;
import java.util.HashSet;

import minetweaker.MineTweakerAPI;

public class MinetweakerHooks {

	public static final MinetweakerHooks instance = new MinetweakerHooks();

	private final Collection<Class> hooks = new HashSet();

	private MinetweakerHooks() {

	}

	public void registerClass(Class c) {
		//if (!c.isAnnotationPresent(ZenClass.class))
		//	throw new MisuseException("You cannot register non-ZS classes!");
		if (!hooks.contains(c)) {
			hooks.add(c);
		}
	}

	public void registerAll() {
		for (Class c : hooks) {
			MineTweakerAPI.registerClass(c);
		}
	}

}
