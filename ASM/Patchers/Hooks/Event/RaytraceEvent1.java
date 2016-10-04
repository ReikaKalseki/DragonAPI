/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event;

import Reika.DragonAPI.ASM.Patchers.RaytracePatcher;


public class RaytraceEvent1 extends RaytracePatcher {

	public RaytraceEvent1() {
		super("net.minecraft.entity.projectile.EntityArrow", "zc");
	}
}
