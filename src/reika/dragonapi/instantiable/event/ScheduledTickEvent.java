/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.event;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import reika.dragonapi.instantiable.data.maps.TimerMap.TimerCallback;
import reika.dragonapi.interfaces.registry.SoundEnum;
import cpw.mods.fml.common.eventhandler.Event;

public final class ScheduledTickEvent extends Event implements TimerCallback {

	private final ScheduledEvent action;

	public ScheduledTickEvent(ScheduledEvent evt) {
		action = evt;
	}

	public final void fire() {
		MinecraftForge.EVENT_BUS.post(this);
		action.fire();
	}
	/*
	public static class ScheduledPlayerEvent implements ScheduledEvent {

		private final EntityPlayer player;

		public ScheduledPlayerEvent(EntityPlayer ep) {
			player = ep;
		}

		@Override
		public void fire() {

		}

	}
	 */
	public static class ScheduledSoundEvent implements ScheduledEvent {

		private final SoundEnum sound;
		private final float volume;
		private final float pitch;

		private World world;
		private double posX;
		private double posY;
		private double posZ;

		private Entity entity;

		private ScheduledSoundEvent(SoundEnum s, float v, float p) {
			sound = s;
			volume = v;
			pitch = p;
		}

		public ScheduledSoundEvent(SoundEnum s, Entity e, float v, float p) {
			this(s, v, p);
			entity = e;
		}

		public ScheduledSoundEvent(SoundEnum s, World w, double x, double y, double z, float v, float p) {
			this(s, v, p);
			world = w;
			posX = x;
			posY = y;
			posZ = z;
		}

		@Override
		public void fire() {
			sound.playSound(this.getWorld(), this.getX(), this.getY(), this.getZ(), volume, pitch);
		}

		protected Entity getEntity() {
			return entity;
		}

		private World getWorld() {
			return entity != null ? entity.worldObj : world;
		}

		private double getX() {
			return entity != null ? entity.posX : posX;
		}

		private double getY() {
			return entity != null ? entity.posY : posY;
		}

		private double getZ() {
			return entity != null ? entity.posZ : posZ;
		}

	}

	public static interface ScheduledEvent {

		public abstract void fire();

	}

	@Override
	public void call() {
		this.fire();
	}
}
