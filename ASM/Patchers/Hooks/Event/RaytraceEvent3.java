package Reika.DragonAPI.ASM.Patchers.Hooks.Event;

import Reika.DragonAPI.ASM.Patchers.RaytracePatcher;


public class RaytraceEvent3 extends RaytracePatcher {

	public RaytraceEvent3() {
		super("net.minecraft.entity.projectile.EntityFireball", "ze");
	}
}
