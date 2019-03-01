/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
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
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.CubeDirections;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;

public abstract class ParticleEntity extends InertEntity implements IEntityAdditionalSpawnData {

	private int oldBlockX;
	private int oldBlockY;
	private int oldBlockZ;

	private Coordinate spawnLocation;

	private boolean outOfSpawn = false;

	public ParticleEntity(World world) {
		super(world);
		this.setSize((float)this.getHitboxSize(), (float)this.getHitboxSize());
	}

	public ParticleEntity(World world, int x, int y, int z) {
		super(world);
		oldBlockX = x;
		oldBlockY = y;
		oldBlockZ = z;
		spawnLocation = new Coordinate(x, y, z);
		this.setSize((float)this.getHitboxSize(), (float)this.getHitboxSize());
		this.setLocationAndAngles(x+0.5, y+0.5, z+0.5, 0, 0);
	}

	public ParticleEntity(World world, int x, int y, int z, ForgeDirection dir) {
		this(world, x, y, z);
		this.setDirection(dir, true);
	}

	public ParticleEntity(World world, int x, int y, int z, CubeDirections dir) {
		this(world, x, y, z);
		this.setDirection(dir, true);
	}

	@Override
	public final boolean isInRangeToRenderDist(double rsq) {
		return rsq <= this.getRenderRangeSquared();
	}

	public abstract double getRenderRangeSquared();

	public Coordinate getSpawnLocation() {
		return spawnLocation;
	}

	protected void setDirection(ForgeDirection dir, boolean setPos) {
		if (setPos)
			this.setLocationAndAngles(this.getBlockX()+0.5, this.getBlockY()+0.5, this.getBlockZ()+0.5, 0, 0);
		motionX = dir.offsetX*this.getSpeed();
		motionY = dir.offsetY*this.getSpeed();
		motionZ = dir.offsetZ*this.getSpeed();
		velocityChanged = true;
	}

	protected void setDirection(CubeDirections dir, boolean setPos) {
		//ReikaJavaLibrary.pConsole(worldObj.isRemote+": "+this.getBlockX()+":"+this.getBlockY()+":"+this.getBlockZ());
		if (setPos)
			this.setLocationAndAngles(this.getBlockX()+0.5, this.getBlockY()+0.5, this.getBlockZ()+0.5, 0, 0);
		motionX = dir.directionX*this.getSpeed();
		motionY = 0;
		motionZ = dir.directionZ*this.getSpeed();
		velocityChanged = true;
	}

	@Override
	protected void entityInit() {

	}

	public abstract double getHitboxSize();
	public abstract boolean despawnOverTime();
	public abstract boolean despawnOverDistance();

	protected double getDespawnDistance() {
		return 0;
	}

	public abstract boolean canInteractWithSpawnLocation();

	@Override
	public final void onUpdate()
	{
		this.onEntityUpdate();
		if (this.needsSpeedUpdates()) {
			this.updateSpeed();
		}

		if (this.dieOnNoVelocity() && (motionX == 0 && motionY == 0 && motionZ == 0) && ticksExisted > 20) {
			this.setDead();
			this.onDeath();
			return;
		}
		if (posY > 256 || posY < 0) {
			this.setDead();
			this.onDeath();
			return;
		}
		if (this.despawnOverTime() && ticksExisted > 120 && ReikaRandomHelper.doWithChance(ticksExisted-120)) {
			this.setDead();
			this.onDeath();
			return;
		}
		if (this.despawnOverDistance() && spawnLocation != null && spawnLocation.getDistanceTo(this) >= this.getDespawnDistance()) {
			this.setDead();
			this.onDeath();
			return;
		}

		//ReikaJavaLibrary.pConsole(String.format("%d, %d, %d :: %d, %d, %d", oldBlockX, oldBlockY, oldBlockZ, this.getBlockX(), this.getBlockY(), this.getBlockZ()));
		//ReikaJavaLibrary.pConsole(this.getBlockX()+", "+this.getBlockY()+", "+this.getBlockZ());
		if (this.isNewBlock()) {
			int x = this.getBlockX();
			int y = this.getBlockY();
			int z = this.getBlockZ();
			oldBlockX = x;
			oldBlockY = y;
			oldBlockZ = z;
			outOfSpawn = !new Coordinate(x, y, z).equals(spawnLocation);
			//ReikaJavaLibrary.pConsole(spawnLocation+" @ "+new Coordinate(this)+" > "+outOfSpawn);
			if (/*ticksExisted < 5 && */!this.canInteractWithSpawnLocation() && !outOfSpawn) {

			}
			else {
				if (this.onEnterBlock(worldObj, x, y, z)) {
					this.onDeath();
					this.setDead();
				}
			}
		}

		if (!worldObj.isRemote) {
			double s = this.getHitboxSize();
			AxisAlignedBB box = AxisAlignedBB.getBoundingBox(posX, posY, posZ, posX, posY, posZ).expand(s, s, s);
			List<Entity> inbox = worldObj.getEntitiesWithinAABB(Entity.class, box);
			for (Entity e : inbox) {
				this.applyEntityCollision(e);
			}
		}

		this.onTick();
	}

	protected boolean dieOnNoVelocity() {
		return true;
	}

	protected boolean needsSpeedUpdates() {
		return false;
	}

	protected void updateSpeed() {

	}

	protected void onDeath() {

	}

	protected abstract void onTick();

	public abstract double getSpeed();

	/** Returns true if the particle is absorbed */
	protected abstract boolean onEnterBlock(World world, int x, int y, int z);

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
		return !this.compareBlocks(x, y, z) && this.isInsideThreshold(x, y, z);
	}

	private boolean isInsideThreshold(int x, int y, int z) {
		double t = this.getBlockThreshold();
		return ReikaMathLibrary.isValueInsideBounds(x+0.5-t, x+0.5+t, posX) && ReikaMathLibrary.isValueInsideBounds(y+0.5-t, y+0.5+t, posY) && ReikaMathLibrary.isValueInsideBounds(z+0.5-t, z+0.5+t, posZ);
	}

	protected double getBlockThreshold() {
		return 0.5;
	}

	private final boolean compareBlocks(int x, int y, int z) {
		return x == oldBlockX && y == oldBlockY && z == oldBlockZ;
	}

	@Override
	protected final boolean canTriggerWalking()
	{
		return false;
	}

	@Override
	public final AxisAlignedBB getBoundingBox()
	{
		return null;
	}

	@Override
	public final boolean isEntityInvulnerable()
	{
		return true;
	}

	@Override
	public final boolean doesEntityNotTriggerPressurePlate()
	{
		return true;
	}

	@Override
	public final boolean canRenderOnFire()
	{
		return false;
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		if (spawnLocation != null)
			spawnLocation.writeToBuf(data);
		else
			new Coordinate(0, 0, 0).writeToBuf(data);
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		spawnLocation = Coordinate.readFromBuf(data);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		spawnLocation = Coordinate.readFromNBT("spawn", tag);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		if (spawnLocation != null)
			spawnLocation.writeToNBT("spawn", tag);
	}

	@Override
	public final void kill() {
		this.onDeath();
		super.kill();
	}

}
