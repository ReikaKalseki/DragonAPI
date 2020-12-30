/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2018
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event.Base;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;


public abstract class WorldPositionEvent extends PositionEventBase {

	public final World world;

	public WorldPositionEvent(World world, int x, int y, int z) {
		super(world, x, y, z);
		this.world = world;
	}

	public final BiomeGenBase getBiome() {
		return isFakeWorld ? BiomeGenBase.ocean : world.getBiomeGenForCoords(xCoord, zCoord);
	}

	public final void setBiome(BiomeGenBase b) {
		if (isFakeWorld)
			return;
		ReikaWorldHelper.setBiomeForXZ(world, xCoord, zCoord, b);
	}

	public final boolean setBlock(Block b) {
		return world.setBlock(xCoord, yCoord, zCoord, b);
	}

	public final boolean setBlock(Block b, int meta, int flags) {
		if (isFakeWorld)
			return false;
		return world.setBlock(xCoord, yCoord, zCoord, b, meta, flags);
	}

	public final int dimensionID() {
		return world.provider != null ? world.provider.dimensionId : 0;
	}

}
