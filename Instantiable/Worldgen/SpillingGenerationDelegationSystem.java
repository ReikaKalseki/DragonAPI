package Reika.DragonAPI.Instantiable.Worldgen;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Event.ChunkGenerationEvent;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class SpillingGenerationDelegationSystem extends ChunkSplicedGenerator {

	private int currentChunkX;
	private int currentChunkZ;

	public SpillingGenerationDelegationSystem() {
		super(true);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onChunkGenerated(ChunkGenerationEvent evt) {
		this.placeChunk(evt.world, evt.getChunk().getChunkCoordIntPair());
	}

	@SubscribeEvent
	public void onChunkLoaded(ChunkEvent.Load evt) {
		this.placeChunk(evt.world, evt.getChunk().getChunkCoordIntPair());
	}

	public boolean setBlock(WorldServer world, int x, int y, int z, Block b) {
		return this.setBlock(world, x, y, z, b, 0);
	}

	public boolean setBlock(WorldServer world, int x, int y, int z, Block b, int meta) {
		return this.setBlock(world, x, y, z, b, meta, 3);
	}

	public boolean setBlock(WorldServer world, int x, int y, int z, Block b, int meta, int flags) {
		if (((x >> 4) == currentChunkX && (z >> 4) == currentChunkZ) || ReikaWorldHelper.isChunkGenerated(world, x, z)) {
			world.setBlock(x, y, z, b, meta, flags);
			return true;
		}
		else {
			this.setBlock(x, y, z, b, meta);
			return false;
		}
	}

	@Override
	protected void place(int x, int y, int z, BlockPlace sb) {
		this.put(this.getKey(x, z), new Coordinate(x, y, z), sb);
	}

	public void placeChunk(World world, int cx, int cz) {
		this.placeChunk(world, new ChunkCoordIntPair(cx, cz));
	}

	public void placeChunkBlockCoords(World world, int x, int z) {
		this.placeChunk(world, x >> 4, z >> 4);
	}

	public final void placeChunk(World world, ChunkCoordIntPair cp) {
		this.generate(world, cp);
	}

	public void setCurrent(int cx, int cz) {
		currentChunkX = cx;
		currentChunkZ = cz;
	}

	public void setCurrentBlockCoords(int x, int z) {
		this.setCurrent(x >> 4, z >> 4);
	}

	public void readFromNBT(NBTTagCompound NBT) {
		data.clear();
		NBTTagList li = NBT.getTagList("blocks", NBTTypes.COMPOUND.ID);
		for (Object o : li.tagList) {
			NBTTagCompound tag = (NBTTagCompound)o;
			Coordinate c = Coordinate.readFromNBT("location", tag);
			ChunkCoordIntPair key = getKey(c.xCoord, c.zCoord);
			BlockPlace bp = BlockPlace.readFromTag(tag.getCompoundTag("block"));
			this.put(key, c, bp);
		}
	}

	public void writeToNBT(NBTTagCompound NBT) {
		NBTTagList li = new NBTTagList();
		for (Map<Coordinate, BlockPlace> set : data.values()) {
			for (Entry<Coordinate, BlockPlace> e : set.entrySet()) {
				NBTTagCompound tag = new NBTTagCompound();
				e.getKey().writeToNBT("location", tag);
				tag.setTag("block", e.getValue().writeToNBT());
				li.appendTag(tag);
			}
		}
		NBT.setTag("blocks", li);
	}

}
