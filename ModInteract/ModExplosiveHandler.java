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

import net.minecraft.entity.Entity;

public class ModExplosiveHandler {

	private static final ModExplosiveHandler instance = new ModExplosiveHandler();

	private final Class ic2;
	private final Class exp_plus;
	private final Class icbm;

	private ModExplosiveHandler() {
		Class ic = null;
		Class exp = null;
		Class miss = null;
		try {
			ic = Class.forName("ic2.core.Blocks.EntityIC2Explosive");
			exp = Class.forName("???");
			miss = Class.forName(""); //The code is in another language... T_T
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		ic2 = ic;
		exp_plus = exp;
		icbm = miss;
	}

	public static ModExplosiveHandler getInstance() {
		return instance;
	}

	public boolean isModExplosive(Entity e) {
		return this.isIC2Explosive(e) || this.isExPlusExplosive(e);
	}

	private boolean isIC2Explosive(Entity e) {
		if (ic2 == null)
			return false;
		return ic2.isAssignableFrom(e.getClass());
	}

	private boolean isExPlusExplosive(Entity e) {
		if (exp_plus == null)
			return false;
		return exp_plus.isAssignableFrom(e.getClass());
	}

}
