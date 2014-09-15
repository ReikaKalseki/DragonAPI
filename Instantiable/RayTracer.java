/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.Data.BlockMap.BlockKey;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public final class RayTracer {

	public final int originX;
	public final int originY;
	public final int originZ;
	public final int targetX;
	public final int targetY;
	public final int targetZ;
	public boolean softBlocksOnly = false;
	private Vec3 offset = Vec3.createVectorHelper(0, 0, 0);

	private final ArrayList<BlockKey> forbiddenBlocks = new ArrayList();

	public RayTracer(int x1, int y1, int z1, int x2, int y2, int z2) {
		originX = x1;
		originY = y1;
		originZ = z1;
		targetX = x2;
		targetY = y2;
		targetZ = z2;
	}

	public RayTracer setOrigins(int x1, int y1, int z1, int x2, int y2, int z2) {
		RayTracer ray = new RayTracer(x1, y1, z1, x2, y2, z2);
		ray.forbiddenBlocks.addAll(forbiddenBlocks);
		ray.offset = Vec3.createVectorHelper(offset.xCoord, offset.yCoord, offset.zCoord);
		ray.softBlocksOnly = softBlocksOnly;
		return ray;
	}

	public void setInternalOffsets(double x, double y, double z) {
		offset = Vec3.createVectorHelper(x, y, z);
	}

	public void addOpaqueBlock(Block b) {
		this.addOpaqueBlock(b, -1);
	}

	public void addOpaqueBlock(Block b, int meta) {
		forbiddenBlocks.add(new BlockKey(b, meta));
	}

	public boolean isClearLineOfSight(World world) {
		Vec3 vec1 = Vec3.createVectorHelper(originX+offset.xCoord, originY+offset.yCoord, originZ+offset.zCoord);
		Vec3 vec2 = Vec3.createVectorHelper(targetX+offset.xCoord, targetY+offset.yCoord, targetZ+offset.zCoord);
		Vec3 ray = ReikaVectorHelper.subtract(vec1, vec2);
		double dx = vec2.xCoord-vec1.xCoord;
		double dy = vec2.yCoord-vec1.yCoord;
		double dz = vec2.zCoord-vec1.zCoord;
		double dd = ReikaMathLibrary.py3d(dx, dy, dz);
		for (double d = 0.25; d <= dd; d += 0.25) {
			Vec3 vec0 = ReikaVectorHelper.scaleVector(ray, d);
			Vec3 vec = ReikaVectorHelper.scaleVector(ray, d-0.25);
			vec0.xCoord += vec1.xCoord;
			vec0.yCoord += vec1.yCoord;
			vec0.zCoord += vec1.zCoord;
			vec.xCoord += vec1.xCoord;
			vec.yCoord += vec1.yCoord;
			vec.zCoord += vec1.zCoord;
			MovingObjectPosition mov = world.rayTraceBlocks(vec, vec0);
			if (mov != null) {
				if (mov.typeOfHit == MovingObjectType.BLOCK) {
					int bx = mov.blockX;
					int by = mov.blockY;
					int bz = mov.blockZ;
					if (this.isNonTerminal(bx, by, bz)) {
						if (this.isDisallowedBlock(world, bx, by, bz)) {
							//ReikaJavaLibrary.pConsole(mov+":"+world.getBlock(bx, by, bz), Side.SERVER);
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private boolean isNonTerminal(int x, int y, int z) {
		return !((x == originX && y == originY && z == originZ) || (x == targetX && y == targetY && z == targetZ));
	}

	private boolean isDisallowedBlock(World world, int x, int y, int z) {
		if (!ReikaWorldHelper.softBlocks(world, x, y, z) && (softBlocksOnly || ReikaBlockHelper.isCollideable(world, x, y, z)))
			return true;
		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		BlockKey key = new BlockKey(b, meta);
		return forbiddenBlocks.contains(key);
	}

}
