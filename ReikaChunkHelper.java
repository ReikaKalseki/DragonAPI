package Reika.DragonAPI;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public abstract class ReikaChunkHelper {
	
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
	
	/** Forcibly loads a chunk. Args: World, x, z */
	public static void forceLoadChunk(World world, int x, int z) {
		Chunk in = world.getChunkFromBlockCoords(x, z);
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
	public static void deleteChunk(World world, int x, int z, int id) {
		for (int d = 1; d < 256; d++) {
			if (id != d)
				removeIDFromChunk(world, x, z, d);
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
	public static void removeBlocksFromChunk(World world, int x, int z, int id, int meta) {
		if (meta == -1) {
			removeIDFromChunk(world, x, z, id);
			return;
		}
		replaceBlocksInChunk(world, x, z, id, meta, 0, 0);
	}
	
	/** Replaces all blocks of specified ID and metadata in the chunk with specified ID and metadata.
	 * Set metadata to -1 for all. Args: World, x, z, Block ID, metadata, ID-to, meta-to */
	public static void replaceBlocksInChunk(World world, int x, int z, int id, int meta, int setid, int setmeta) {
		while (x%16 > 0) {
			x--;
		}
		while (z%16 > 0) {
			z--;
		}
		if (meta == -1) {
			replaceIDInChunk(world, x, z, id, setid, setmeta);
			return;
		}
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				for (int k = 0; k < 256; k++) {
					int idread = world.getBlockId(x+i, k, z+j);
					int metaread = world.getBlockMetadata(x+i, k, z+j);
					if (idread == id && metaread == meta)
						ReikaWorldHelper.legacySetBlockAndMetadataWithNotify(world, x+i, k, z+j, setid, setmeta);
				}
			}
		}
	}
	
	private static void replaceIDInChunk(World world, int x, int z, int id, int setid, int setmeta) {
		while (x%16 > 0) {
			x--;
		}
		while (z%16 > 0) {
			z--;
		}
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				for (int k = 0; k < 256; k++) {
					int idread = world.getBlockId(x+i, k, z+j);
					if (idread == id)
						ReikaWorldHelper.legacySetBlockAndMetadataWithNotify(world, x+i, k, z+j, setid, setmeta);
				}
			}
		}
	}
	
	private static void removeIDFromChunk(World world, int x, int z, int id) {
		replaceIDInChunk(world, x, z, id, 0, 0);
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
}