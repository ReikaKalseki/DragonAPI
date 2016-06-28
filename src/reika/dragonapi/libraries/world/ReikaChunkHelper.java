/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.libraries.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.libraries.ReikaEntityHelper.ClassEntitySelector;
import reika.dragonapi.libraries.mathsci.ReikaMathLibrary;

public final class ReikaChunkHelper extends DragonAPICore {

	/** Returns the chunk at the given coords. Args: World, x, z */
	public static Chunk getChunk(World world, int x, int z) {
		Chunk in = world.getChunkFromBlockCoords(x, z);
		return in;
	}

	/** Regens a chunk from the seed. Args: World, x, z */
	public static void regenChunk(World world, int x, int z) {
		Chunk in = world.getChunkFromBlockCoords(x, z);
		in.setChunkModified();
		//TODO
	}

	/** Deletes ALL entities of all kinds (except players) in a chunk.
	 * Only goes up to y=255! Args: World, x, z */
	public static void emptyChunk(World world, int x, int z) {
		while (x%16 > 0) {
			x--;
		}
		while (z%16 > 0) {
			z--;
		}
		AxisAlignedBB chunk = AxisAlignedBB.getBoundingBox(x, 0, z, x+16, 255, z+16);
		List inChunk = world.getEntitiesWithinAABB(Entity.class, chunk);
		for (int i = 0; i < inChunk.size(); i++) {
			Entity ent = (Entity)inChunk.get(i);
			if (!(ent instanceof EntityPlayer))
				ent.setDead();
		}
	}

	/** Deletes all the blocks in an entire chunk, except the specified ID. Enter -1 to
	 * empty the chunk entirely. Args: World, x, z, id to save */
	public static void deleteChunk(World world, int x, int z, Block id) {
		Iterator it = Block.blockRegistry.iterator();
		while (it.hasNext()) {
			Block b = (Block)it.next();
			if (id != b)
				removeIDFromChunk(world, x, z, b);
		}
	}

	/** Deletes all entities of the given class in a chunk.
	 * Only goes up to y=255! Args: World, x, z */
	public static void removeFromChunk(World world, int x, int z, Class entityClass) {
		while (x%16 > 0) {
			x--;
		}
		while (z%16 > 0) {
			z--;
		}
		AxisAlignedBB chunk = AxisAlignedBB.getBoundingBox(x, 0, z, x+16, 255, z+16);
		List inChunk = world.getEntitiesWithinAABB(entityClass, chunk);
		for (int i = 0; i < inChunk.size(); i++) {
			Entity ent = (Entity)inChunk.get(i);
			ent.setDead();
		}
	}

	/** Removes all blocks of specified ID and metadata from the chunk (replaces with air).
	 * Set metadata to -1 for all. Args: World, x, z, Block ID, metadata */
	public static void removeBlocksFromChunk(World world, int x, int z, Block id, int meta) {
		if (meta == -1) {
			removeIDFromChunk(world, x, z, id);
			return;
		}
		replaceBlocksInChunk(world, x, z, id, meta, Blocks.air, 0);
	}

	/** Replaces all blocks of specified ID and metadata in the chunk with specified ID and metadata.
	 * Set metadata to -1 for all. Args: World, x, z, Block ID, metadata, ID-to, meta-to */
	public static void replaceBlocksInChunk(World world, int x, int z, Block id, int meta, Block setid, int setmeta) {
		boolean nx = false;
		boolean nz = false;
		if (x < 0) {
			x = -x;
			nx = true;
		}
		if (z < 0) {
			z = -z;
			nz = true;
		}
		while (x%16 > 0) {
			if (nx)
				x++;
			else
				x--;
		}
		while (z%16 > 0) {
			if (nx)
				z++;
			else
				z--;
		}
		if (nx)
			x = -x;
		if (nz)
			z = -z;
		if (meta == -1) {
			replaceIDInChunk(world, x, z, id, setid, setmeta);
			return;
		}
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				for (int k = 0; k < 256; k++) {
					Block idread = world.getBlock(x+i, k, z+j);
					int metaread = world.getBlockMetadata(x+i, k, z+j);
					if (idread == id && metaread == meta)
						world.setBlock(x+i, k, z+j, setid, setmeta, 3);
				}
			}
		}
	}

	private static void replaceIDInChunk(World world, int x, int z, Block id, Block setid, int setmeta) {
		boolean nx = false;
		boolean nz = false;
		if (x < 0) {
			x = -x;
			nx = true;
		}
		if (z < 0) {
			z = -z;
			nz = true;
		}
		while (x%16 > 0) {
			if (nx)
				x++;
			else
				x--;
		}
		while (z%16 > 0) {
			if (nx)
				z++;
			else
				z--;
		}
		if (nx)
			x = -x;
		if (nz)
			z = -z;
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				for (int k = 0; k < 256; k++) {
					Block idread = world.getBlock(x+i, k, z+j);
					if (idread == id)
						world.setBlock(x+i, k, z+j, setid, setmeta, 3);
				}
			}
		}
	}

	private static void removeIDFromChunk(World world, int x, int z, Block id) {
		replaceIDInChunk(world, x, z, id, Blocks.air, 0);
	}

	/** Returns the distance (3d cartesian) to the nearest entity of this species.
	 * Args: World, entity, x,y,z of source, search range */
	public static double getPoplnDensity(World world, Entity entity, double x, double y, double z, double r) {
		double dist;
		Entity entityfound = world.findNearestEntityWithinAABB(entity.getClass(), AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1, z+1).expand(r, r, r), entity);
		if (entityfound == null)
			return -1;	// If no entity in range, return -1
		dist = ReikaMathLibrary.py3d(x-entityfound.posX, y-entityfound.posY, z-entityfound.posZ);
		return dist;
	}

	/** s/e. Returns the number of the specified entity class in the chunk containing by x, z.
	 * Can only detect entities 'within' the chunk. Args: World, entity class, x, z */
	public static int getChunkPopln(World world, Class entity, int x, int z) {
		while (x % 16 > 0)
			x--;
		while (z % 16 > 0)
			z--;
		int entitiesfound = world.getEntitiesWithinAABB(entity, AxisAlignedBB.getBoundingBox(x, 0, z, x+16, 255, z+16)).size();
		return entitiesfound;

	}

	/** s/e. Returns the number of the specified entity class in the chunk range containing by x, z.
	 * Can only detect entities 'within' the chunk. Args: World, entity class, xmin, zmin, xmax, zmax */
	public static int getChunkRangePopln(World world, Class entity, int x, int z, int x2, int z2) {
		while (x % 16 > 0)
			x--;
		while (z % 16 > 0)
			z--;
		while (x % 16 > 0)
			x2--;
		while (z % 16 > 0)
			z2++;
		int entitiesfound = world.getEntitiesWithinAABB(entity, AxisAlignedBB.getBoundingBox(x, 0, z, x2, 255, z2)).size();
		return entitiesfound;

	}

	public static Collection<Entity> getEntities(Chunk ch, ClassEntitySelector sel) {
		Collection<Entity> c = new ArrayList();
		for (int i = 0; i < ch.entityLists.length; i++) {
			List<Entity> li = ch.entityLists[i];
			for (Entity e : li) {
				if (sel == null || sel.isEntityApplicable(e)) {
					c.add(e);
				}
			}
		}
		return c;
	}

	public static void clearEntities(Chunk ch, ClassEntitySelector sel) {
		for (int i = 0; i < ch.entityLists.length; i++) {
			List<Entity> li = ch.entityLists[i];
			for (Entity e : li) {
				if (sel == null || sel.isEntityApplicable(e)) {
					e.setDead();
				}
			}
		}
	}
}
