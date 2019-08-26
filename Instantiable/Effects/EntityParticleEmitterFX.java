package Reika.DragonAPI.Instantiable.Effects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityParticleEmitterFX extends EntityFX {

	private ParticleSpawner spawner;

	private double deltaX;
	private double deltaY;
	private double deltaZ;

	public EntityParticleEmitterFX(World world, double x, double y, double z, double vx, double vy, double vz, ParticleSpawner sp) {
		super(world, x, y, z, vx, vy, vz);
		motionX = vx;
		motionY = vy;
		motionZ = vz;
		spawner = sp;
	}

	public EntityParticleEmitterFX setVelocityDeltas(int dx, double dy, int dz) {
		deltaX = dx;
		deltaY = dy;
		deltaZ = dz;
		return this;
	}

	public EntityParticleEmitterFX setLife(int l) {
		particleMaxAge = l;
		return this;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		EntityFX fx = spawner.spawnParticle(this);
		if (fx != null)
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		motionX += deltaX;
		motionY += deltaY;
		motionZ += deltaZ;
		velocityChanged = true;
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void renderParticle(Tessellator v5, float ptick, float rx, float ry, float rz, float rw, float rh) {
		//Do nothing
	}

	public static interface ParticleSpawner {

		@SideOnly(Side.CLIENT)
		public EntityFX spawnParticle(EntityParticleEmitterFX fx);

		//@SideOnly(Side.CLIENT)
		//public void spawnParticle(World world, double x, double y, double z, int tick);

	}

}
