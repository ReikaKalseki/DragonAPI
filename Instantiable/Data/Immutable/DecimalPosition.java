/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Immutable;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import io.netty.buffer.ByteBuf;

public final class DecimalPosition implements Comparable<DecimalPosition> {

	private static final Random rand = new Random();

	public final double xCoord;
	public final double yCoord;
	public final double zCoord;

	public DecimalPosition(double x, double y, double z) {
		xCoord = x;
		yCoord = y;
		zCoord = z;
	}

	public DecimalPosition(TileEntity te) {
		this(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5);
	}

	public DecimalPosition(Coordinate c) {
		this(c.xCoord+0.5, c.yCoord+0.5, c.zCoord+0.5);
	}

	public DecimalPosition(Entity e) {
		this(e.posX, e.posY, e.posZ);
	}

	public DecimalPosition(Entity e, float ptick) {
		this(e.lastTickPosX+ptick*(e.posX-e.lastTickPosX), e.lastTickPosY+ptick*(e.posY-e.lastTickPosY), e.lastTickPosZ+ptick*(e.posZ-e.lastTickPosZ));
	}

	public DecimalPosition(DecimalPosition loc) {
		this(loc.xCoord, loc.yCoord, loc.zCoord);
	}

	public DecimalPosition(MovingObjectPosition hit) {
		this(hit.blockX+0.5, hit.blockY+0.5, hit.blockZ+0.5);
	}

	public DecimalPosition(WorldLocation src) {
		this(src.xCoord+0.5, src.yCoord+0.5, src.zCoord+0.5);
	}

	public DecimalPosition(Vec3 vec) {
		this(vec.xCoord, vec.yCoord, vec.zCoord);
	}

	public DecimalPosition offset(double dx, double dy, double dz) {
		return new DecimalPosition(xCoord+dx, yCoord+dy, zCoord+dz);
	}

	public DecimalPosition offset(ForgeDirection dir, double dist) {
		return this.offset(dir.offsetX*dist, dir.offsetY*dist, dir.offsetZ*dist);
	}

	public DecimalPosition offset(DecimalPosition p) {
		return this.offset(p.xCoord, p.yCoord, p.zCoord);
	}

	public boolean sharesBlock(DecimalPosition dec) {
		return this.sharesBlock(dec.xCoord, dec.yCoord, dec.zCoord);
	}

	public boolean sharesBlock(double x, double y, double z) {
		return this.matchX(x) && this.matchY(y) && this.matchZ(z);
	}

	private boolean matchX(double x) {
		return MathHelper.floor_double(x) == MathHelper.floor_double(xCoord);
	}

	private boolean matchY(double y) {
		return MathHelper.floor_double(y) == MathHelper.floor_double(yCoord);
	}

	private boolean matchZ(double z) {
		return MathHelper.floor_double(z) == MathHelper.floor_double(zCoord);
	}

	public void writeToNBT(String tag, NBTTagCompound NBT) {
		NBTTagCompound data = new NBTTagCompound();
		data.setDouble("x", xCoord);
		data.setDouble("y", yCoord);
		data.setDouble("z", zCoord);
		NBT.setTag(tag, data);
	}

	public static final DecimalPosition readFromNBT(String tag, NBTTagCompound NBT) {
		if (!NBT.hasKey(tag))
			return null;
		NBTTagCompound data = NBT.getCompoundTag(tag);
		if (data != null) {
			double x = data.getDouble("x");
			double y = data.getDouble("y");
			double z = data.getDouble("z");
			return new DecimalPosition(x, y, z);
		}
		return null;
	}

	public NBTTagCompound writeToTag() {
		NBTTagCompound data = new NBTTagCompound();
		data.setDouble("x", xCoord);
		data.setDouble("y", yCoord);
		data.setDouble("z", zCoord);
		return data;
	}

	public static final DecimalPosition readTag(NBTTagCompound data) {
		double x = data.getDouble("x");
		double y = data.getDouble("y");
		double z = data.getDouble("z");
		return new DecimalPosition(x, y, z);
	}

	public DecimalPosition copy() {
		return new DecimalPosition(xCoord, yCoord, zCoord);
	}

	@Override
	public String toString() {
		return "["+xCoord+", "+yCoord+", "+zCoord+"]";
	}

	@Override
	public int hashCode() {
		return (int)(xCoord + (zCoord * 256) + (yCoord * 65536));
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof DecimalPosition) {
			DecimalPosition w = (DecimalPosition)o;
			return this.equals(w.xCoord, w.yCoord, w.zCoord);
		}
		return false;
	}

	public boolean equals(double x, double y, double z) {
		return x == xCoord && y == yCoord && z == zCoord;
	}

	public double getDistanceTo(DecimalPosition src) {
		return this.getDistanceTo(src.xCoord, src.yCoord, src.zCoord);
	}

	public double getDistanceTo(double x, double y, double z) {
		return ReikaMathLibrary.py3d(x-xCoord, y-yCoord, z-zCoord);
	}

	public boolean isWithinSquare(Coordinate c, double d) {
		return this.isWithinSquare(c, d, d, d);
	}

	public boolean isWithinSquare(Coordinate c, double dx, double dy, double dz) {
		return Math.abs(c.xCoord-xCoord) <= dx && Math.abs(c.yCoord-yCoord) <= dy && Math.abs(c.zCoord-zCoord) <= dz;
	}

	public double[] toArray() {
		double[] a = new double[3];
		a[0] = xCoord;
		a[1] = yCoord;
		a[2] = zCoord;
		return a;
	}

	public Coordinate getCoordinate() {
		return new Coordinate(MathHelper.floor_double(xCoord), MathHelper.floor_double(yCoord), MathHelper.floor_double(zCoord));
	}

	public Block getBlock(World world) {
		return world != null ? this.getCoordinate().getBlock(world) : null;
	}

	public boolean isEmpty(World world) {
		return this.getBlock(world) == Blocks.air;
	}

	public int getBlockMetadata(World world) {
		return world != null ? this.getCoordinate().getBlockMetadata(world) : -1;
	}

	public TileEntity getTileEntity(World world) {
		return world != null ? this.getCoordinate().getTileEntity(world) : null;
	}

	public int getRedstone(World world) {
		return world != null ? this.getCoordinate().getRedstone(world) : 0;
	}

	public void triggerBlockUpdate(World world, boolean adjacent) {
		this.getCoordinate().triggerBlockUpdate(world, adjacent);
	}

	public void dropItem(World world, ItemStack is) {
		this.dropItem(world, is, 1);
	}

	public void dropItem(World world, ItemStack is, double vscale) {
		if (world != null && !world.isRemote) {
			ReikaItemHelper.dropItem(world, xCoord+rand.nextDouble(), yCoord+rand.nextDouble(), zCoord+rand.nextDouble(), is, vscale);
		}
	}

	public boolean setBlock(World world, Block b) {
		return this.setBlock(world, b, 0);
	}

	public boolean setBlock(World world, ItemStack is) {
		return this.setBlock(world, Block.getBlockFromItem(is.getItem()), is.getItemDamage());
	}

	public boolean setBlock(World world, Block id, int meta) {
		return world != null && this.getCoordinate().setBlock(world, id, meta);
	}

	public DecimalPosition negate() {
		return new DecimalPosition(xCoord, yCoord, zCoord);
	}

	public static DecimalPosition interpolate(DecimalPosition p1, DecimalPosition p2, double f) {
		return interpolate(p1.xCoord, p1.yCoord, p1.zCoord, p2.xCoord, p2.yCoord, p2.zCoord, f);
	}

	public static DecimalPosition interpolate(double x1, double y1, double z1, double x2, double y2, double z2, double f) {
		return new DecimalPosition(x1+(x2-x1)*f, y1+(y2-y1)*f, z1+(z2-z1)*f);
	}

	public void writeToBuf(ByteBuf buf) {
		buf.writeDouble(xCoord);
		buf.writeDouble(yCoord);
		buf.writeDouble(zCoord);
	}

	public static DecimalPosition readFromBuf(ByteBuf buf) {
		double x = buf.readDouble();
		double y = buf.readDouble();
		double z = buf.readDouble();
		return new DecimalPosition(x, y, z);
	}

	public String formattedString(int decimal) {
		String part = "%."+decimal+"f";
		return String.format(part+", "+part+", "+part, xCoord, yCoord, zCoord);
	}

	public Vec3 toVec3() {
		return Vec3.createVectorHelper(xCoord, yCoord, zCoord);
	}

	public AxisAlignedBB getAABB(double radius) {
		return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord).expand(radius, radius, radius);
	}

	@Override
	public int compareTo(DecimalPosition o) {
		int val = Integer.compare(this.hashCode(), o.hashCode());
		if (val != 0)
			return val;
		val = Double.compare(xCoord, o.xCoord);
		if (val != 0)
			return val;
		val = Double.compare(yCoord, o.yCoord);
		if (val != 0)
			return val;
		val = Double.compare(zCoord, o.zCoord);
		return val;
	}

	public static DecimalPosition average(DecimalPosition... pos) {
		double sx = 0;
		double sy = 0;
		double sz = 0;
		int n = pos.length;
		for (int i = 0; i < n; i++) {
			sx += pos[i].xCoord;
			sy += pos[i].yCoord;
			sz += pos[i].zCoord;
		}
		return new DecimalPosition(sx/n, sy/n, sz/n);
	}

	public static DecimalPosition getRandomWithin(Coordinate c, Random rand) {
		return new DecimalPosition(c.xCoord+rand.nextDouble(), c.yCoord+rand.nextDouble(), c.zCoord+rand.nextDouble());
	}

}
