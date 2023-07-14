/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.BlockStruct;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.IO.NBTFile;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerator.TileCallback;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;


public class StructureExport extends NBTFile {

	private final HashMap<Coordinate, BlockData> data = new HashMap();
	private final HashSet<String> watchedNBT = new HashSet();
	private final HashMap<String, Object> extraNBT = new HashMap();
	private final HashMap<String, NBTCallback> overrides = new HashMap();
	private final HashSet<BlockKey> ignoreSet = new HashSet();

	private BlockBox bounds = BlockBox.nothing();

	public PlacementCallback placeCallback;

	public StructureExport(String name) {
		super(name, new File(DragonAPICore.getMinecraftDirectory(), "StructureData/"+name+".struct"));
	}

	public StructureExport(String name, String path, Class c) {
		super(name, path+"/"+name+".struct", c);
	}

	public StructureExport addWatchedNBT(String tag) {
		watchedNBT.add(tag);
		return this;
	}

	public StructureExport addWatchedNBT(Collection<String> c) {
		for (String s : c) {
			this.addWatchedNBT(s);
		}
		return this;
	}

	public StructureExport addIgnoredBlock(BlockKey bk) {
		ignoreSet.add(bk);
		return this;
	}

	public void setExtraNBTTag(String s, Object val) {
		NBTBase b = ReikaNBTHelper.getTagForObject(val);
		if (b != null) {
			extraNBT.put(s, val);
			for (BlockData dat : data.values()) {
				if (dat.hasTileEntity) {
					dat.tileData.setTag(s, b);
				}
			}
		}
	}

	public void addNBTOverride(String s, NBTCallback call) {
		overrides.put(s, call);
		for (BlockData dat : data.values()) {
			if (dat.hasTileEntity && dat.tileData != null) {
				NBTBase tag = dat.tileData.getTag(s);
				if (tag != null) {
					dat.tileData.setTag(s, call.getOverriddenValue(dat.position, dat.block, s, tag.copy(), (NBTTagCompound)dat.tileData.copy()));
				}
			}
		}
	}

	public void addRegion(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				for (int z = z1; z <= z2; z++) {
					this.addCoordinate(world, x, y, z);
				}
			}
		}
	}

	public void addCoordinate(World world, int x, int y, int z) {
		if (world.getBlock(x, y, z).isAir(world, x, y, z))
			return;
		if (ignoreSet.contains(BlockKey.getAt(world, x, y, z)))
			return;
		BlockData dat = new BlockData(world, x, y, z, watchedNBT, extraNBT, overrides);
		data.put(dat.position, dat);
		//ReikaJavaLibrary.pConsole("Generated "+dat);
		this.calcBounds();
	}

	private void calcBounds() {
		//bounds = BlockBox.nothing();
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int minZ = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		int maxZ = Integer.MIN_VALUE;
		//ReikaJavaLibrary.pConsole("PRE "+bounds);
		for (Coordinate c : data.keySet()) {
			minX = Math.min(minX, c.xCoord);
			minY = Math.min(minY, c.yCoord);
			minZ = Math.min(minZ, c.zCoord);
			maxX = Math.max(maxX, c.xCoord);
			maxY = Math.max(maxY, c.yCoord);
			maxZ = Math.max(maxZ, c.zCoord);
		}
		bounds = new BlockBox(minX, minY, minZ, maxX, maxY, maxZ);
		//ReikaJavaLibrary.pConsole(data.size()+": POST "+bounds+" for "+data.keySet());
	}

	public BlockBox getBounds() {
		return bounds;
	}

	public void place(World world) {
		for (BlockData dat : data.values()) {
			dat.place(world);
			if (placeCallback != null) {
				placeCallback.onPlace(dat.position, dat.block, dat.tileData != null ? (NBTTagCompound)dat.tileData.copy() : null);
			}
		}
	}

	public void place(ChunkSplicedGenerationCache world) {
		for (BlockData dat : data.values()) {
			dat.place(world);
			if (placeCallback != null) {
				placeCallback.onPlace(dat.position, dat.block, dat.tileData != null ? (NBTTagCompound)dat.tileData.copy() : null);
			}
		}
	}

	public void offset(Coordinate offset) {
		HashMap<Coordinate, BlockData> next = new HashMap();
		for (Coordinate c : data.keySet()) {
			BlockData b = data.get(c);
			next.put(c.offset(offset), b.offset(offset));
		}
		data.clear();
		data.putAll(next);
		bounds = bounds.offset(offset);
	}

	private static class BlockData {

		private final Coordinate position;
		private final BlockKey block;

		private final boolean hasTileEntity;
		private final NBTTagCompound tileData;

		private BlockData(World world, int x, int y, int z, HashSet<String> tags, HashMap<String, Object> extraTags, HashMap<String, NBTCallback> overrides) {
			position = new Coordinate(x, y, z);
			block = BlockKey.getAt(world, x, y, z);
			TileEntity te = world.getTileEntity(x, y, z);
			hasTileEntity = te != null;
			tileData = te != null ? new NBTTagCompound() : null;
			if (te != null) {
				te.writeToNBT(tileData);
				this.filterNBT(tags, tileData);
				ReikaNBTHelper.addMapToTags(tileData, extraTags);
				for (String s : overrides.keySet()) {
					NBTBase tag = tileData.getTag(s);
					if (tag != null) {
						tag = overrides.get(s).getOverriddenValue(position, block, s, tag.copy(), (NBTTagCompound)tileData.copy());
						tileData.setTag(s, tag);
					}
				}
			}
		}

		private BlockData(Coordinate c, BlockKey bk, boolean tile, NBTTagCompound tileDat) {
			position = c;
			block = bk;
			hasTileEntity = tile;
			tileData = tileDat;
		}

		private NBTTagCompound writeToNBT() {
			NBTTagCompound NBT = new NBTTagCompound();
			if (tileData != null)
				NBT.setTag("tiledat", tileData);
			NBT.setBoolean("tile", hasTileEntity);
			position.writeToNBT("loc", NBT);
			block.writeToNBT("type", NBT);
			return NBT;
		}

		private static BlockData readFromNBT(NBTTagCompound NBT, HashSet<String> tags, HashMap<String, Object> extraTags) {
			NBTTagCompound tileDat = null;
			if (NBT.hasKey("tiledat")) {
				tileDat = NBT.getCompoundTag("tiledat");
				filterNBT(tags, tileDat);
				ReikaNBTHelper.addMapToTags(tileDat, extraTags);
			}
			Coordinate c = Coordinate.readFromNBT("loc", NBT);
			BlockKey bk = BlockKey.readFromNBT("type", NBT);
			if (bk == null)
				return null;
			boolean tile = NBT.getBoolean("tile");
			return new BlockData(c, bk, tile, tileDat);
		}

		private static void filterNBT(HashSet<String> tags, NBTTagCompound tag) {
			Iterator<String> it = tag.func_150296_c().iterator();
			while (it.hasNext()) {
				String s = it.next();
				if (!tags.contains(s))
					it.remove();
			}
		}

		private void place(World world) {
			block.place(world, position.xCoord, position.yCoord, position.zCoord);
			if (hasTileEntity) {
				TileEntity te = position.getTileEntity(world);
				if (te != null) {
					NBTTagCompound tag = new NBTTagCompound();
					te.writeToNBT(tag);
					ReikaNBTHelper.overwriteNBT(tag, tileData);
					te.readFromNBT(tag);
				}
			}
		}

		private void place(ChunkSplicedGenerationCache world) {
			if (hasTileEntity) {
				TileCallback call = new TileNBTCallback(tileData);
				world.setTileEntity(position.xCoord, position.yCoord, position.zCoord, block.blockID, block.metadata, call);
			}
			else {
				world.setBlock(position.xCoord, position.yCoord, position.zCoord, block);
			}
		}

		public BlockData offset(Coordinate offset) {
			return new BlockData(position.offset(offset), block, hasTileEntity, tileData);
		}

		@Override
		public String toString() {
			return block+" @ "+position+" {"+tileData+"}";
		}

	}

	private static class TileNBTCallback implements TileCallback {

		private final NBTTagCompound tileData;

		private TileNBTCallback(NBTTagCompound tag) {
			tileData = tag;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (te != null) {
				NBTTagCompound tag = new NBTTagCompound();
				te.writeToNBT(tag);
				ReikaNBTHelper.overwriteNBT(tag, tileData);
				te.readFromNBT(tag);
			}
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			tag.setTag("data", tileData.copy());
		}

		public void readFromNBT(NBTTagCompound tag) {
			ReikaNBTHelper.clearTagCompound(tileData);
			ReikaNBTHelper.overwriteNBT(tileData, tag);
		}

	}

	public static interface PlacementCallback {

		public void onPlace(Coordinate c, BlockKey bk, NBTTagCompound data);

	}

	public static interface NBTCallback {

		public NBTBase getOverriddenValue(Coordinate c, BlockKey bk, String key, NBTBase original, NBTTagCompound data);

	}

	@Override
	protected void readHeader(NBTTagCompound header) {
		NBTTagList li = header.getTagList("tags", NBTTypes.STRING.ID);
		for (Object o : li.tagList) {
			NBTTagString s = (NBTTagString)o;
			watchedNBT.add(s.func_150285_a_());
		}
	}

	@Override
	protected void readData(NBTTagList li) {
		for (Object o : li.tagList) {
			NBTTagCompound dat = (NBTTagCompound)o;
			BlockData b = BlockData.readFromNBT(dat, watchedNBT, extraNBT);
			if (b != null) {
				if (!ignoreSet.contains(b.block)) {
					data.put(b.position, b);
					//ReikaJavaLibrary.pConsole("Loaded "+b);
				}
			}
		}
		this.calcBounds();
	}

	@Override
	protected void readExtraData(NBTTagCompound extra) {
		extraNBT.clear();
		extraNBT.putAll((Map<String, Object>)ReikaNBTHelper.getValue(extra));
	}

	@Override
	protected void writeHeader(NBTTagCompound header) {
		NBTTagList li = new NBTTagList();
		for (String s : watchedNBT) {
			li.appendTag(new NBTTagString(s));
		}
		header.setTag("tags", li);
	}

	@Override
	protected void writeData(NBTTagList li) {
		for (BlockData b : data.values()) {
			//ReikaJavaLibrary.pConsole("Saving "+b);
			NBTTagCompound tag = b.writeToNBT();
			li.appendTag(tag);
		}
	}

	@Override
	protected NBTTagCompound writeExtraData() {
		return (NBTTagCompound)ReikaNBTHelper.getTagForObject(extraNBT);
	}

	public StructureExport copy() {
		StructureExport cp = new StructureExport(name+"_copy");
		cp.encryptData = encryptData;
		cp.compressData = compressData;
		cp.data.putAll(data);
		cp.watchedNBT.addAll(watchedNBT);
		cp.extraNBT.putAll(extraNBT);
		cp.overrides.putAll(overrides);
		cp.ignoreSet.addAll(ignoreSet);
		cp.bounds = bounds;

		return cp;
	}

	public StructureExport copy(String path, Class c) {
		StructureExport cp = new StructureExport(name+"_copy", path, c);
		cp.encryptData = encryptData;
		cp.compressData = compressData;
		cp.data.putAll(data);
		cp.watchedNBT.addAll(watchedNBT);
		cp.extraNBT.putAll(extraNBT);
		cp.overrides.putAll(overrides);
		cp.ignoreSet.addAll(ignoreSet);
		cp.bounds = bounds;

		return cp;
	}

}
