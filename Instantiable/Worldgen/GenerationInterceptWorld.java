/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Worldgen;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaReflectionHelper;


public final class GenerationInterceptWorld extends World {

	private World delegate;

	private final HashSet<BlockKey> disallowedBlocks = new HashSet();
	private final HashSet<Coordinate> changeList = new HashSet();
	private final Collection<TileHook> hooks = new ArrayList();

	public GenerationInterceptWorld() {
		super(new NoSaveHandler(), null, new WorldSettings(0, GameType.NOT_SET, false, false, WorldType.DEFAULT), null, null);
	}

	public void link(World world) {
		//changeList.clear();

		if (delegate == world)
			return;

		delegate = world;
		try {
			boolean obf = !ReikaObfuscationHelper.isDeObfEnvironment();
			ReikaReflectionHelper.setFinalField(World.class, obf ? "field_73019_z" : "saveHandler", this, world.getSaveHandler());
			ReikaReflectionHelper.setFinalField(World.class, obf ? "field_73011_w" : "provider", this, world.provider);
			worldInfo = world.getWorldInfo();
			ReikaReflectionHelper.setFinalField(World.class, obf ? "field_72984_F" : "theProfiler", this, world.theProfiler);
			mapStorage = world.mapStorage;
			chunkProvider = world.getChunkProvider();
			ReikaReflectionHelper.setFinalField(World.class, obf ? "perWorldStorage" : "perWorldStorage", this, world.perWorldStorage);
			villageCollectionObj = world.villageCollectionObj;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void disallowBlock(Block b) {
		disallowedBlocks.add(new BlockKey(b));
	}

	public void disallowBlock(Block b, int meta) {
		disallowedBlocks.add(new BlockKey(b, meta));
	}

	public void disallowBlock(BlockKey bk) {
		disallowedBlocks.add(bk);
	}

	public void addHook(TileHook th) {
		hooks.add(th);
	}

	private boolean check(Block b, int meta) {
		return !disallowedBlocks.contains(new BlockKey(b, meta));
	}

	@Override
	public boolean setBlock(int x, int y, int z, Block b) {
		boolean flag = this.check(b, 0) ? delegate.setBlock(x, y, z, b) : false;
		if (flag) {
			this.markHook(x, y, z);
		}
		return flag;
	}

	@Override
	public boolean setBlock(int x, int y, int z, Block b, int meta, int flags) {
		boolean flag = this.check(b, meta) ? delegate.setBlock(x, y, z, b, meta, flags) : false;
		if (flag) {
			this.markHook(x, y, z);
		}
		return flag;
	}

	@Override
	public boolean setBlockMetadataWithNotify(int x, int y, int z, int meta, int flags) {
		boolean flag = this.check(delegate.getBlock(x, y, z), meta) ? delegate.setBlockMetadataWithNotify(x, y, z, meta, flags) : false;
		if (flag) {
			this.markHook(x, y, z);
		}
		return flag;
	}

	@Override
	public void setTileEntity(int x, int y, int z, TileEntity te) {
		//if (changeList.contains(new Coordinate(x, y, z)))
		delegate.setTileEntity(x, y, z, te);
		this.markHook(x, y, z);
	}

	/*
	@Override
	public Block getBlock(int x, int y, int z) {
		return delegate.getBlock(x, y, z);
	}

	@Override
	public int getBlockMetadata(int x, int y, int z) {
		return delegate.getBlockMetadata(x, y, z);
	}

	@Override
	public TileEntity getTileEntity(int x, int y, int z) {
		return delegate.getTileEntity(x, y, z);
	}

	@Override
	public int getBlockLightValue(int x, int y, int z) {
		return delegate.getBlockLightValue(x, y, z);
	}

	@Override
	public int getTopSolidOrLiquidBlock(int x, int z) {
		return delegate.getTopSolidOrLiquidBlock(x, z);
	}

	@Override
	protected boolean chunkExists(int x, int z) {
		return delegate.chunkExists(x, z);
	}

	@Override
	public BiomeGenBase getBiomeGenForCoordsBody(int x, int z) {
		return delegate.getBiomeGenForCoordsBody(x, z);
	}
	 */

	private void markHook(int x, int y, int z) {
		changeList.add(new Coordinate(x, y, z));
	}
	/*
	private void runHooks(int x, int y, int z) {
		TileEntity te = delegate.getTileEntity(x, y, z);
		for (TileHook th : hooks) {
			if (th.shouldRun(delegate, x, y, z))
				th.onTileChanged(te);
		}
	}
	 */

	public void runHooks() {
		for (Coordinate c : changeList) {
			TileEntity te = c.getTileEntity(delegate);
			for (TileHook th : hooks) {
				if (th.shouldRun(delegate, c.xCoord, c.yCoord, c.zCoord))
					th.onTileChanged(te);
			}
		}
		changeList.clear();
	}

	@Override
	public boolean spawnEntityInWorld(Entity e) {
		return delegate.spawnEntityInWorld(e);
	}

	@Override
	protected IChunkProvider createChunkProvider() {
		return null;
	}

	@Override
	protected int func_152379_p() {
		return 10; //chunk load distance
	}

	@Override
	public Entity getEntityByID(int id) {
		return delegate.getEntityByID(id);
	}

	private static class NoSaveHandler implements ISaveHandler {

		@Override
		public WorldInfo loadWorldInfo() {
			return null;
		}

		@Override
		public void checkSessionLock() throws MinecraftException {}

		@Override
		public IChunkLoader getChunkLoader(WorldProvider p_75763_1_) {
			return null;
		}

		@Override
		public void saveWorldInfoWithPlayer(WorldInfo p_75755_1_, NBTTagCompound p_75755_2_) {}

		@Override
		public void saveWorldInfo(WorldInfo p_75761_1_) {}

		@Override
		public IPlayerFileData getSaveHandler() {
			return null;
		}

		@Override
		public void flush() {}

		@Override
		public File getWorldDirectory() {
			return null;
		}

		@Override
		public File getMapFileFromName(String p_75758_1_) {
			return null;
		}

		@Override
		public String getWorldDirectoryName() {
			return null;
		}

	}

	public static interface TileHook {

		/** May be null! */
		public void onTileChanged(TileEntity te);

		public boolean shouldRun(World world, int x, int y, int z);

	}

}
