/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.TileEntity.ChunkLoadingTile;

import com.google.common.collect.ImmutableSet;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ChunkManager implements LoadingCallback {

	public static final ChunkManager instance = new ChunkManager();

	private final HashMap<WorldLocation, Ticket> tickets = new HashMap();

	private ChunkManager() {

	}

	public void register() {
		ForgeChunkManager.setForcedChunkLoadingCallback(DragonAPIInit.instance, this);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void unloadWorld(WorldEvent.Unload evt) {
		Iterator<WorldLocation> it = tickets.keySet().iterator();
		while (it.hasNext()) {
			WorldLocation loc = it.next();
			if (loc.dimensionID == evt.world.provider.dimensionId)
				it.remove();
		}
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
				WorldLocation loc = new WorldLocation(te);
				this.forceTicketChunks(ticket, tile.getChunksToLoad()); //this.getChunkSquare(x, z, tile.getRadius())
				this.cacheTicket(loc, ticket);
			}
			else {
				ForgeChunkManager.releaseTicket(ticket);
			}
		}
	}

	private void cacheTicket(WorldLocation loc, Ticket ticket) {
		tickets.put(loc, ticket);
	}

	public void unloadChunks(TileEntity te) {
		this.unloadChunks(new WorldLocation(te));
	}

	public void unloadChunks(World world, int x, int y, int z) {
		this.unloadChunks(new WorldLocation(world, x, y, z));
	}

	public void unloadChunks(WorldLocation loc) {
		Ticket ticket = tickets.remove(loc);
		//ReikaJavaLibrary.pConsole("Unloading "+ticket+" with "+ticket.getChunkList());
		ForgeChunkManager.releaseTicket(ticket);
	}

	public Ticket loadChunks(WorldLocation loc, Collection<ChunkCoordIntPair> chunks) {
		Ticket ticket = tickets.get(loc);
		if (ticket == null) {
			ticket = this.getNewTicket(loc);
			this.cacheTicket(loc, ticket);
		}
		this.forceTicketChunks(ticket, chunks);
		return ticket;
	}

	public void loadChunks(ChunkLoadingTile te) {
		WorldLocation loc = new WorldLocation((TileEntity)te);
		this.loadChunks(loc, te.getChunksToLoad());
	}

	private Ticket getNewTicket(WorldLocation loc) {
		Ticket ticket = ForgeChunkManager.requestTicket(DragonAPIInit.instance, loc.getWorld(), Type.NORMAL);
		NBTTagCompound nbt = ticket.getModData();
		nbt.setInteger("tileX", loc.xCoord);
		nbt.setInteger("tileY", loc.yCoord);
		nbt.setInteger("tileZ", loc.zCoord);
		return ticket;
	}

	private void forceTicketChunks(Ticket ticket, Collection<ChunkCoordIntPair> chunks) {
		ImmutableSet<ChunkCoordIntPair> ticketChunks = ticket.getChunkList();
		//ReikaJavaLibrary.pConsole("Parsing ticket "+ticket+", world="+ticket.world+", mod="+ticket.getModId()+", chunks="+chunks);
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

	/** Range is in CHUNKS, not blocks!! */
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
