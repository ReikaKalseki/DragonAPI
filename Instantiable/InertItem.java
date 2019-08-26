/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Mostly for rendering purposes. */
public class InertItem extends EntityItem {

	public boolean simulatePhysics = false;
	public int ageSpeed = 1;
	public double gravity = 0.03999999910593033D;

	public InertItem(World world, ItemStack item) {
		super(world);
		this.setEntityItemStack(item);
		lifespan = Integer.MAX_VALUE;
	}

	@Override
	public float getShadowSize() {
		return 0.0001F;
	}

	@Override
	public void onUpdate() {
		age += ageSpeed;
		if (age >= lifespan)
			this.setDead();
		if (simulatePhysics) {
			this.runPhysics();
		}
	}

	private void runPhysics() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		motionY -= gravity;
		if (!noClip)
			noClip = this.func_145771_j(posX, (boundingBox.minY + boundingBox.maxY) / 2.0D, posZ);
		this.moveEntity(motionX, motionY, motionZ);

		float f = 0.98F;
		if (onGround) {
			f = worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(boundingBox.minY) - 1, MathHelper.floor_double(posZ)).slipperiness * 0.98F;
		}

		motionX *= f;
		motionY *= 0.9800000190734863D;
		motionZ *= f;

		if (onGround) {
			motionY *= -0.5D;
		}

		velocityChanged = true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float ptick) {
		return 15728880;
	}

	@Override
	public float getBrightness(float ptick) {
		return 1;
	}

	@Override
	public final boolean isEntityInvulnerable() {
		return true;
	}

	@Override
	public final boolean doesEntityNotTriggerPressurePlate() {
		return true;
	}

	@Override
	public final boolean canRenderOnFire() {
		return false;
	}

}
