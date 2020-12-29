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
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import cpw.mods.fml.common.eventhandler.Event;


public abstract class PositionEventBase extends Event {

	public final IBlockAccess access;
	public final int xCoord;
	public final int yCoord;
	public final int zCoord;

	/** Whether the world is a half-assed fake clone missing most of its data, and probably would crash if most of its functions are called. */
	public final boolean isFakeWorld;

	public PositionEventBase(IBlockAccess world, int x, int y, int z) {
		access = world;
		xCoord = x;
		yCoord = y;
		zCoord = z;

		isFakeWorld = this.isFake();
	}

	public final BiomeGenBase getBiome() {
		return isFakeWorld ? BiomeGenBase.ocean : access.getBiomeGenForCoords(xCoord, zCoord);
	}

	public final Block getBlock() {
		return isFakeWorld ? Blocks.air : access.getBlock(xCoord, yCoord, zCoord);
	}

	public final int getMetadata() {
		return isFakeWorld ? 0 : access.getBlockMetadata(xCoord, yCoord, zCoord);
	}

	public final TileEntity getTileEntity() {
		return isFakeWorld ? null : access.getTileEntity(xCoord, yCoord, zCoord);
	}

	public final int getLightLevel() {
		return isFakeWorld ? 0 : access.getLightBrightnessForSkyBlocks(xCoord, yCoord, zCoord, 0);
	}

	public final boolean isAir() {
		return isFakeWorld ? true : this.getBlock().isAir(access, xCoord, yCoord, zCoord);
	}

	private final boolean isFake() {
		return access instanceof World && ReikaWorldHelper.isFakeWorld((World)access);
	}

}
