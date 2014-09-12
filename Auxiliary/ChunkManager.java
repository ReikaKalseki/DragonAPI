/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;
import Reika.DragonAPI.Interfaces.ChunkLoadingTile;

import com.google.common.collect.ImmutableSet;

public class ChunkManager implements LoadingCallback {

	public static final ChunkManager instance = new ChunkManager();

	private final HashMap<WorldLocation, Ticket> tickets = new HashMap();

	private ChunkManager() {

	}

	public void register() {
		ForgeChunkManager.setForcedChunkLoadingCallback(DragonAPIInit.instance, this);
	}

	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world) {
		for (Ticket ticket : tickets) {
			NBTTagCompound nbt = ticket.getModData();
			int x = nbt.getInteger("tileX");
			int y = nbt.getInteger("tileY");
			int z = nbt.getInteger("tileZ");
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof ChunkLoadingTile) {
				ChunkLoadingTile tile = (ChunkLoadingTile)te;
				this.forceTicketChunks(ticket, tile.getChunksToLoad()); //this.getChunkSquare(x, z, tile.getRadius())
				this.cacheTicket(world, x, y, z, ticket);
			}
			else {
				ForgeChunkManager.releaseTicket(ticket);
			}
		}
	}

	private void cacheTicket(World world, int x, int y, int z, Ticket ticket) {
		tickets.put(new WorldLocation(world, x, y, z), ticket);
	}

	public void unloadChunks(World world, int x, int y, int z) {
		Ticket ticket = tickets.get(new WorldLocation(world, x, y, z));
		ForgeChunkManager.releaseTicket(ticket);
	}

	public void loadChunks(World world, int x, int y, int z, ChunkLoadingTile te) {
		Ticket ticket = tickets.get(new WorldLocation(world, x, y, z));
		if (ticket == null) {
			ticket = this.getNewTicket(world, x, y, z);
			this.cacheTicket(world, x, y, z, ticket);
		}
		this.forceTicketChunks(ticket, te.getChunksToLoad());
	}

	private Ticket getNewTicket(World world, int x, int y, int z) {
		Ticket ticket = ForgeChunkManager.requestTicket(DragonAPIInit.instance, world, Type.NORMAL);
		NBTTagCompound nbt = ticket.getModData();
		nbt.setInteger("tileX", x);
		nbt.setInteger("tileY", y);
		nbt.setInteger("tileZ", z);
		return ticket;
	}

	private void forceTicketChunks(Ticket ticket, Collection<ChunkCoordIntPair> chunks) {
		ImmutableSet<ChunkCoordIntPair> ticketChunks = ticket.getChunkList();
		for (ChunkCoordIntPair coord : ticketChunks) {
			if (!chunks.contains(coord)) {
				//ReikaJavaLibrary.pConsole("Unforcing chunk "+coord.chunkXPos+", "+coord.chunkZPos);
				ForgeChunkManager.unforceChunk(ticket, coord);
			}
		}
		for (ChunkCoordIntPair coord : chunks) {
			if (!ticketChunks.contains(coord)) {
				//ReikaJavaLibrary.pConsole("Forcing chunk "+coord.chunkXPos+", "+coord.chunkZPos);
				ForgeChunkManager.forceChunk(ticket, coord);
			}
		}
	}

	public static Collection<ChunkCoordIntPair> getChunkSquare(int x, int z, int r) {
		int x2 = x >> 4;
		int z2 = z >> 4;
		Collection<ChunkCoordIntPair> chunkList = new ArrayList();
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				chunkList.add(new ChunkCoordIntPair(x2+i, z2+k));
			}
		}
		return chunkList;
	}

	@Override
	public String toString() {
		return tickets.toString();
	}

}
