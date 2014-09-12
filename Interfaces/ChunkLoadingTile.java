/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;

import java.util.Collection;

import net.minecraft.world.ChunkCoordIntPair;

public interface ChunkLoadingTile {

	//public void setTicket(Ticket t);

	public Collection<ChunkCoordIntPair> getChunksToLoad();

	//public boolean loadChunk(ChunkCoordIntPair chip);

}
