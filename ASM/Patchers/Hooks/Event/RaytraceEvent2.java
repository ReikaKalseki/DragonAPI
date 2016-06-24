package Reika.DragonAPI.ASM.Patchers.Hooks.Event;

import Reika.DragonAPI.ASM.Patchers.RaytracePatcher;


public class RaytraceEvent2 extends RaytracePatcher {

	public RaytraceEvent2() {
		super("net.minecraft.entity.projectile.EntityThrowable", "zk");
	}
}
