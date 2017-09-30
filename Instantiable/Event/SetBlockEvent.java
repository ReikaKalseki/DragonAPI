/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;

/** Fired when a setBlock propagates and succeeds inside a chunk. This is fired both client and server side. */
public class SetBlockEvent extends PositionEvent {

	private final Chunk chunk;

	public final ChunkCoordIntPair chunkLocation;

	public SetBlockEvent(Chunk ch, int x, int y, int z) {
		super(ch.worldObj, ch.xPosition*16+x, y, ch.zPosition*16+z);
		chunk = ch;
		chunkLocation = new ChunkCoordIntPair(ch.xPosition, ch.zPosition);
	}

	public boolean isAir() {
		return this.getBlock() instanceof BlockAir;
	}

	public Block getBlock() {
		return world.getBlock(xCoord, yCoord, zCoord);
	}

	public int getMetadata() {
		return world.getBlockMetadata(xCoord, yCoord, zCoord);
	}

	public TileEntity getTileEntity() {
		return world.getTileEntity(xCoord, yCoord, zCoord);
	}

}
