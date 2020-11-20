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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;

import cpw.mods.fml.common.eventhandler.Event;


public abstract class PositionEventBase extends Event {

	public final IBlockAccess access;
	public final int xCoord;
	public final int yCoord;
	public final int zCoord;

	public PositionEventBase(IBlockAccess world, int x, int y, int z) {
		access = world;
		xCoord = x;
		yCoord = y;
		zCoord = z;
	}

	public final BiomeGenBase getBiome() {
		return access.getBiomeGenForCoords(xCoord, zCoord);
	}

	public final Block getBlock() {
		return access.getBlock(xCoord, yCoord, zCoord);
	}

	public final int getMetadata() {
		return access.getBlockMetadata(xCoord, yCoord, zCoord);
	}

	public final TileEntity getTileEntity() {
		return access.getTileEntity(xCoord, yCoord, zCoord);
	}

	public final boolean isAir() {
		return this.getBlock().isAir(access, xCoord, yCoord, zCoord);
	}

}
