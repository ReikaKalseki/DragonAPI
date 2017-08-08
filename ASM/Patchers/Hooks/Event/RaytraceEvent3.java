/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event;

import Reika.DragonAPI.ASM.Patchers.RaytracePatcher;


public class RaytraceEvent3 extends RaytracePatcher {

	public RaytraceEvent3() {
		super("net.minecraft.entity.projectile.EntityFireball", "ze");
	}
}
