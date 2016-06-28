package reika.dragonapi.asm.patchers.hooks.event;

import reika.dragonapi.asm.patchers.RaytracePatcher;


public class RaytraceEvent3 extends RaytracePatcher {

	public RaytraceEvent3() {
		super("net.minecraft.entity.projectile.EntityFireball", "ze");
	}
}
