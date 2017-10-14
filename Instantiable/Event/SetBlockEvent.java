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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;

/** Fired when a setBlock propagates and succeeds inside a chunk. This is fired both client and server side. */
public abstract class SetBlockEvent extends PositionEvent {

	private final Chunk chunk;

	public final ChunkCoordIntPair chunkLocation;

	public SetBlockEvent(Chunk ch, int x, int y, int z) {
		super(ch.worldObj, ch.xPosition*16+x, y, ch.zPosition*16+z);
		chunk = ch;
		chunkLocation = new ChunkCoordIntPair(ch.xPosition, ch.zPosition);
	}

	public final boolean isAir() {
		return this.getBlock().isAir(world, xCoord, yCoord, zCoord);
	}

	public final Block getBlock() {
		return world.getBlock(xCoord, yCoord, zCoord);
	}

	public final int getMetadata() {
		return world.getBlockMetadata(xCoord, yCoord, zCoord);
	}

	public final TileEntity getTileEntity() {
		return world.getTileEntity(xCoord, yCoord, zCoord);
	}

	public static class Post extends SetBlockEvent {

		public Post(Chunk ch, int x, int y, int z) {
			super(ch, x, y, z);
		}

	}

	public static class Pre extends SetBlockEvent {

		public final Block currentBlock;
		public final Block newBlock;
		public final int currentMeta;
		public final int newMeta;

		public Pre(Chunk ch, int x, int y, int z, Block b, int meta) {
			super(ch, x, y, z);

			currentBlock = this.getBlock();
			currentMeta = this.getMetadata();

			newBlock = b != null ? b : currentBlock; //is a call to setMeta
			newMeta = meta;
		}

		public static void fire(Chunk ch, int x, int y, int z, Block b, int meta) {
			MinecraftForge.EVENT_BUS.post(new Pre(ch, x, y, z, b, meta));
		}

		public static void fire_meta(Chunk ch, int x, int y, int z, int meta) {
			MinecraftForge.EVENT_BUS.post(new Pre(ch, x, y, z, null, meta));
		}

	}

}
