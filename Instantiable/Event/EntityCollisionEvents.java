package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;

import Reika.DragonAPI.Instantiable.Event.Base.WorldPositionEvent;

public class EntityCollisionEvents {

	public static class CollisionBoxEvent extends WorldPositionEvent {

		public final Entity entity;
		public AxisAlignedBB box;

		public CollisionBoxEvent(Entity e, World world, int x, int y, int z) {
			super(world, x, y, z);
			entity = e;
			box = this.getDefaultAABB();
		}

		public AxisAlignedBB getDefaultAABB() {
			return this.getBlock().getCollisionBoundingBoxFromPool(world, xCoord, yCoord, zCoord);
		}

	}

	public static class RaytraceEvent extends EntityEvent {

		public MovingObjectPosition result;
		public final Vec3 pos1;
		public final Vec3 pos2;
		public final boolean flag1;
		public final boolean flag2;
		public final boolean flag3;

		public RaytraceEvent(Entity e, Vec3 vec1, Vec3 vec2, boolean b1, boolean b2, boolean b3) {
			super(e);
			pos1 = vec1;
			pos2 = vec2;
			flag1 = b1;
			flag2 = b2;
			flag3 = b3;
			result = this.getDefaultResult();
		}

		public MovingObjectPosition getDefaultResult() {
			return entity.worldObj.func_147447_a(pos1, pos2, flag1, flag2, flag3);
		}

	}

	public static AxisAlignedBB getInterceptedCollisionBox(Entity e, World world, int x, int y, int z) {
		CollisionBoxEvent evt = new CollisionBoxEvent(e, world, x, y, z);
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.box;
	}

	public static MovingObjectPosition getInterceptedRaytrace(Entity e, Vec3 vec1, Vec3 vec2) {
		return getInterceptedRaytrace(e, vec1, vec2, false, false, false);
	}

	public static MovingObjectPosition getInterceptedRaytrace(Entity e, Vec3 vec1, Vec3 vec2, boolean b1, boolean b2, boolean b3) {
		RaytraceEvent evt = new RaytraceEvent(e, vec1, vec2, b1, b2, b3);
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.result;
	}
}
