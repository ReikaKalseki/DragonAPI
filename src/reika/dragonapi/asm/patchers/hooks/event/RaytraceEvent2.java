package reika.dragonapi.asm.patchers.hooks.event;

import reika.dragonapi.asm.patchers.RaytracePatcher;


public class RaytraceEvent2 extends RaytracePatcher {

	public RaytraceEvent2() {
		super("net.minecraft.entity.projectile.EntityThrowable", "zk");
	}
}
