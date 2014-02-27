/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Base;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public abstract class ParticleEntity extends InertEntity {

	protected int oldBlockX;
	protected int oldBlockY;
	protected int oldBlockZ;

	public ParticleEntity(World par1World) {
		super(par1World);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {

	}

	@Override
	protected void entityInit() {

	}

	public abstract double getHitboxSize();

	@Override
	public final void onUpdate()
	{
		this.onEntityUpdate();

		if (motionX == 0 && motionY == 0 && motionZ == 0 && ticksExisted > 20)
			this.setDead();
		if (posY > 256 || posY < 0)
			this.setDead();

		//ReikaJavaLibrary.pConsole(String.format("%d, %d, %d :: %d, %d, %d", oldBlockX, oldBlockY, oldBlockZ, this.getBlockX(), this.getBlockY(), this.getBlockZ()));
		//ReikaJavaLibrary.pConsole(this.getBlockX()+", "+this.getBlockY()+", "+this.getBlockZ());
		if (this.isNewBlock()) {
			if (this.onEnterBlock(worldObj, this.getBlockX(), this.getBlockY(), this.getBlockZ())) {
				this.setDead();
			}
		}

		double s = this.getHitboxSize();
		AxisAlignedBB box = AxisAlignedBB.getAABBPool().getAABB(posX, posY, posZ, posX, posY, posZ).expand(s, s, s);
		List<Entity> inbox = worldObj.getEntitiesWithinAABB(Entity.class, box);
		for (int i = 0; i < inbox.size(); i++) {
			this.applyEntityCollision(inbox.get(i));
		}

		this.onTick();
	}

	protected abstract void onTick();

	public abstract double getSpeed();

	/** Returns true if the particle is absorbed */
	public abstract boolean onEnterBlock(World world, int x, int y, int z);

	@Override
	public abstract void applyEntityCollision(Entity e);

	public final int getBlockX() {
		return (int)Math.floor(posX);
	}

	public final int getBlockY() {
		return (int)Math.floor(posY);
	}

	public final int getBlockZ() {
		return (int)Math.floor(posZ);
	}

	public final boolean isNewBlock() {
		int x = this.getBlockX();
		int y = this.getBlockY();
		int z = this.getBlockZ();
		return !this.compareBlocks(x, y, z);
	}

	private final boolean compareBlocks(int x, int y, int z) {
		return x == oldBlockX && y == oldBlockY && z == oldBlockZ;
	}
}
