/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable;

import io.netty.buffer.ByteBuf;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import reika.dragonapi.libraries.java.ReikaRandomHelper;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;


public class EntityTumblingBlock extends EntityFallingBlock implements IEntityAdditionalSpawnData {

	private double rotationX = rand.nextDouble()*360;
	private double rotationY = rand.nextDouble()*360;
	private double rotationZ = rand.nextDouble()*360;

	private double rotationSpeedX = ReikaRandomHelper.getRandomBetween(0D, 10D);
	private double rotationSpeedY = ReikaRandomHelper.getRandomBetween(0D, 10D);
	private double rotationSpeedZ = ReikaRandomHelper.getRandomBetween(0D, 10D);

	public EntityTumblingBlock(World world) {
		super(world);
	}

	public EntityTumblingBlock(World world, double x, double y, double z, Block b, int meta) {
		super(world, x, y, z, b, meta);
	}

	public EntityTumblingBlock setRotationSpeed(double s) {
		return this.setRotationSpeed(s, s, s);
	}

	public EntityTumblingBlock setRotationSpeed(double vx, double vy, double vz) {
		rotationSpeedX = vx;
		rotationSpeedY = vy;
		rotationSpeedZ = vz;
		return this;
	}

	public EntityTumblingBlock align() {
		return this.rotate(0);
	}

	public EntityTumblingBlock rotate(double a) {
		return this.rotate(a, a, a);
	}

	public EntityTumblingBlock rotate(double rx, double ry, double rz) {
		rotationX = rx;
		rotationY = ry;
		rotationZ = rz;
		return this;
	}

	@Override
	public String getCommandSenderName() {
		return "Flying "+new ItemStack(this.func_145805_f(), 1, field_145814_a).getDisplayName();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		rotationX += rotationSpeedX;
		rotationY += rotationSpeedY;
		rotationZ += rotationSpeedZ;
	}

	public double angleX() {
		return rotationX;
	}

	public double angleY() {
		return rotationY;
	}

	public double angleZ() {
		return rotationZ;
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		buf.writeInt(Block.getIdFromBlock(this.func_145805_f()));
		buf.writeInt(field_145814_a);
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		Block b = Block.getBlockById(buf.readInt());
		try {
			Field f = EntityFallingBlock.class.getDeclaredField("field_145811_e");
			f.setAccessible(true);
			f.set(this, b);
		}
		catch (Exception e) {

		}
		field_145814_a = buf.readInt();
	}

}
