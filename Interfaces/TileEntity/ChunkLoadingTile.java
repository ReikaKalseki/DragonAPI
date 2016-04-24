/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.TileEntity;

import java.util.Collection;

import net.minecraft.world.ChunkCoordIntPair;

/** For TileEntities that load chunks. Only implement this on a TileEntity! */
public interface ChunkLoadingTile extends BreakAction {

	//public void setTicket(Ticket t);

	public Collection<ChunkCoordIntPair> getChunksToLoad();

	//public boolean loadChunk(ChunkCoordIntPair chip);

}
