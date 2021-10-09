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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;

import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Interfaces.BlockCheck;
import Reika.DragonAPI.Interfaces.BlockCheck.TileEntityCheck;
import Reika.DragonAPI.Interfaces.Registry.TileEnum;
import Reika.DragonAPI.Libraries.ReikaFluidHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class FilledBlockArray extends StructuredBlockArray {

	private final HashMap<Coordinate, BlockCheck> data = new HashMap();
	private final HashMap<Coordinate, BlockKey> placementOverrides = new HashMap();

	public FilledBlockArray(World world) {
		super(world);
	}

	@Override
	public void copyTo(BlockArray copy) {
		super.copyTo(copy);
		if (copy instanceof FilledBlockArray) {
			((FilledBlockArray)copy).data.putAll(data);
			((FilledBlockArray)copy).placementOverrides.putAll(placementOverrides);
		}
	}

	public void loadBlock(int x, int y, int z) {
		this.setBlock(x, y, z, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
	}

	public void loadBlockTo(int x, int y, int z, int xt, int yt, int zt) {
		this.setBlock(xt, yt, zt, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
	}

	public void setBlock(int x, int y, int z, Block id) {
		this.setBlock(x, y, z , new BlockKey(id));
	}

	public void setBlock(int x, int y, int z, Block id, int meta) {
		this.setBlock(x, y, z , new BlockKey(id, meta));
	}

	public void setTile(int x, int y, int z, Block id, int meta, TileEntity te, String... tags) {
		this.setBlock(x, y, z , new BasicTileEntityCheck(id, meta, te, tags));
	}

	/*
	public void setTile(int x, int y, int z, TileEnum tile, String... tags) {
		this.setBlock(x, y, z , new BasicTileEntityCheck(tile, tags));
	}
	 */

	public void setFluid(int x, int y, int z, Fluid f) {
		this.setFluid(x, y, z, f, true, true);
	}

	public void setFluid(int x, int y, int z, Fluid f, boolean needSource, boolean allowSource) {
		super.addBlockCoordinate(x, y, z);
		FluidCheck fc = new FluidCheck(f);
		fc.needsSourceBlock = needSource;
		fc.allowSourceBlock = allowSource;
		data.put(new Coordinate(x, y, z), fc);
	}

	public void setBlock(int x, int y, int z, BlockCheck bk) {
		super.addBlockCoordinate(x, y, z);
		data.put(new Coordinate(x, y, z), bk);
	}

	public void setEmpty(int x, int y, int z, boolean soft, boolean nonsolid, Block... exceptions) {
		super.addBlockCoordinate(x, y, z);
		data.put(new Coordinate(x, y, z), new EmptyCheck(soft, nonsolid, exceptions));
	}

	public void addEmpty(int x, int y, int z, boolean soft, boolean nonsolid, Block... exceptions) {
		super.addBlockCoordinate(x, y, z);
		this.addBlockToCoord(new Coordinate(x, y, z), new EmptyCheck(soft, nonsolid, exceptions));
	}

	public void addBlock(int x, int y, int z, Block id) {
		this.addBlock(x, y, z , new BlockKey(id));
	}

	public void addBlock(int x, int y, int z, Block id, int meta) {
		this.addBlock(x, y, z , new BlockKey(id, meta));
	}

	public void addBlock(int x, int y, int z, BlockCheck b) {
		super.addBlockCoordinate(x, y, z);
		this.addBlockToCoord(new Coordinate(x, y, z), b);
	}

	private void addBlock(int x, int y, int z, BlockKey bk) {
		super.addBlockCoordinate(x, y, z);
		this.addBlockToCoord(new Coordinate(x, y, z), bk);
	}

	private void addBlockToCoord(Coordinate c, BlockCheck bk) {
		BlockCheck bc = data.get(c);
		if (bc == null || bc instanceof EmptyCheck) {
			MultiKey mk = new MultiKey();
			mk.add(bk);
			if (bc != null)
				mk.add(bc);
			data.put(c, mk);
			bc = mk;
		}
		else if (bc instanceof BlockKey) {
			MultiKey mk = new MultiKey();
			mk.add(bc);
			mk.add(bk);
			data.put(c, mk);
		}
		else {
			((MultiKey)bc).add(bk);
		}
	}

	public void setPlacementOverride(int x, int y, int z, Block id, int meta) {
		placementOverrides.put(new Coordinate(x, y, z), new BlockKey(id, meta));
	}

	private BlockCheck getBlockKey(int x, int y, int z) {
		return data.get(new Coordinate(x, y, z));
	}

	/*
	public Block getBlock(int x, int y, int z) {
		return this.getBlockKey(x, y, z).blockID;
	}

	public int getBlockMetadata(int x, int y, int z) {
		return Math.max(0, this.getBlockKey(x, y, z).metadata);
	}
	 */

	@Override
	public boolean addBlockCoordinate(int x, int y, int z) {
		if (super.addBlockCoordinate(x, y, z)) {
			data.put(new Coordinate(x, y, z), BlockKey.getAt(world, x, y, z));
			return true;
		}
		return false;
	}

	public void place() {
		this.placeExcept(null, 3);
	}

	public void place(int flags) {
		this.placeExcept(null, flags);
	}

	public void placeExcept(Coordinate e, int flags) {
		for (Entry<Coordinate, BlockCheck> et : data.entrySet()) {
			//Block b = this.getBlock(x, y, z);
			//int meta = this.getBlockMetadata(x, y, z);
			//world.setBlock(x, y, z, b, meta, 3);
			Coordinate c = et.getKey();
			if (!c.equals(e)) {
				BlockKey po = placementOverrides.get(c);
				if (po != null) {
					po.place(world, c.xCoord, c.yCoord, c.zCoord, flags);
				}
				else {
					et.getValue().place(world, c.xCoord, c.yCoord, c.zCoord, flags);
				}
			}
		}
	}

	public void placeExcept(int flags, PlacementExclusionHook h) {
		for (Entry<Coordinate, BlockCheck> et : data.entrySet()) {
			Coordinate c = et.getKey();
			BlockCheck bc = et.getValue();
			if (!h.skipPlacement(c, bc)) {
				BlockKey po = placementOverrides.get(c);
				if (po != null) {
					po.place(world, c.xCoord, c.yCoord, c.zCoord, flags);
				}
				else {
					bc.place(world, c.xCoord, c.yCoord, c.zCoord, flags);
				}
			}
		}
	}

	public ItemStack getDisplayAt(int x, int y, int z) {
		BlockCheck bk = this.getBlockKey(x, y, z);
		return bk != null ? bk.getDisplay() : null;
	}

	public boolean hasBlockAt(int x, int y, int z, Block b) {
		return this.hasBlockAt(x, y, z, b, -1);
	}

	public boolean hasBlockAt(int x, int y, int z, Block b, int meta) {
		BlockCheck bc = this.getBlockKey(x, y, z);
		return bc != null ? bc.match(b, meta) : false;
	}

	public boolean matchInWorld() {
		return this.matchInWorld(null);
	}

	public boolean matchInWorld(BlockMatchFailCallback call) {
		if (world.isRemote)
			return true;
		for (Coordinate c : data.keySet()) {
			int x = c.xCoord;
			int y = c.yCoord;
			int z = c.zCoord;
			BlockCheck bk = this.getBlockKey(x, y, z);
			if (!bk.matchInWorld(world, x, y, z)) {
				//ReikaJavaLibrary.pConsole(x+","+y+","+z+" > Wanted ["+bk.getClass().getSimpleName()+"] "+bk.asBlockKey().blockID.getLocalizedName()+":"+bk.asBlockKey().metadata+", found "+world.getBlock(x, y, z).getLocalizedName()+":"+world.getBlockMetadata(x, y, z));
				//bk.place(world, x, y, z, 3);
				//world.setBlock(x, y, z, Blocks.brick_block);
				if (call != null)
					call.onBlockFailure(world, x, y, z, bk);
				return false;
			}
		}
		return true;
	}

	@Override
	public BlockKey getBlockKeyAt(int x, int y, int z) {
		return this.hasBlock(x, y, z) ? data.get(new Coordinate(x, y, z)).asBlockKey() : null;
	}

	public boolean isMultiKey(int x, int y, int z) {
		return data.get(new Coordinate(x, y, z)) instanceof MultiKey;
	}

	public MultiKey getMultiKeyAt(int x, int y, int z) {
		if (!this.hasBlock(x, y, z))
			return null;
		BlockCheck b = data.get(new Coordinate(x, y, z));
		return b instanceof MultiKey ? (MultiKey)b : null;
	}

	public ArrayList<BlockKey> getMultiListAt(int x, int y, int z) {
		if (!this.hasBlock(x, y, z))
			return null;
		BlockCheck b = data.get(new Coordinate(x, y, z));
		if (b instanceof MultiKey) {
			ArrayList<BlockKey> li = new ArrayList();
			for (BlockCheck bc : ((MultiKey)b).keys) {
				li.add(bc.asBlockKey());
			}
			return li;
		}
		else {
			return ReikaJavaLibrary.makeListFrom(b.asBlockKey());
		}
	}

	@Override
	public Block getBlockAt(int x, int y, int z) {
		return this.hasBlock(x, y, z) ? data.get(new Coordinate(x, y, z)).asBlockKey().blockID : null;
	}

	@Override
	public int getMetaAt(int x, int y, int z) {
		return this.hasBlock(x, y, z) ? data.get(new Coordinate(x, y, z)).asBlockKey().metadata : -1;
	}

	@SideOnly(Side.CLIENT)
	public TileEntity getTileEntityAt(int x, int y, int z) {
		if (!this.hasBlock(x, y, z))
			return null;
		BlockCheck b = data.get(new Coordinate(x, y, z));
		return b instanceof TileEntityCheck ? ((TileEntityCheck)b).getTileEntity() : null;
	}

	public ItemHashMap<Integer> tally() {
		ItemHashMap<Integer> map = new ItemHashMap();
		for (BlockCheck bc : data.values()) {
			ItemStack key = bc.asItemStack();
			if (this.count(key)) {
				Integer get = map.get(key);
				int has = get != null ? get.intValue() : 0;
				map.put(key, has+1);
			}
		}
		return map;
	}

	private boolean count(ItemStack is) {
		if (is == null)
			return false;
		Item it = is.getItem();
		if (it == null)
			return false;
		if (it instanceof ItemBlock) {
			Block b = Block.getBlockFromItem(it);
			if (b instanceof BlockLiquid || b instanceof BlockFluidBase) {
				if (is.getItemDamage() > 0)
					return false;
			}
			if (b != null && b.getMaterial() == Material.air)
				return false;
		}
		return true;
	}

	@Override
	public void remove(int x, int y, int z) {
		super.remove(x, y, z);
		data.remove(new Coordinate(x, y, z));
	}

	@Override
	public BlockArray offset(int x, int y, int z) {
		super.offset(x, y, z);
		HashMap map = new HashMap();
		for (Coordinate key : data.keySet()) {
			int dx = key.xCoord;
			int dy = key.yCoord;
			int dz = key.zCoord;
			dx += x;
			dy += y;
			dz += z;
			map.put(new Coordinate(dx, dy, dz), data.get(key));
		}
		data.clear();
		data.putAll(map);
		this.recalcLimits();
		return this;
	}

	public void populateBlockData() {
		for (int i = 0; i < this.getSize(); i++) {
			Coordinate c = this.getNthBlock(i);
			int x = c.xCoord;
			int y = c.yCoord;
			int z = c.zCoord;
			Block b = world.getBlock(x, y, z);
			int meta = world.getBlockMetadata(x, y, z);
			this.setBlock(x, y, z, b, meta);
		}
	}

	@Override
	public String toString() {
		return data.toString();
	}

	@Override
	protected BlockArray instantiate() {
		return new FilledBlockArray(world);
	}

	@Override
	public void addAll(BlockArray arr) {
		super.addAll(arr);
		if (arr instanceof FilledBlockArray) {
			data.putAll(((FilledBlockArray)arr).data);
		}
	}


	public void fillFrom(SlicedBlockBlueprint sbb, int x, int y, int z, ForgeDirection dir) {
		sbb.putInto(this, x, y, z, dir);
	}

	@Override
	public BlockArray rotate90Degrees(int ox, int oz, boolean left) {
		FilledBlockArray b = (FilledBlockArray)super.rotate90Degrees(ox, oz, left);
		for (Coordinate c : data.keySet()) {
			BlockCheck bc = data.get(c);
			Coordinate c2 = c.rotate90About(ox, oz, left);
			b.data.put(c2, bc);
		}
		return b;
	}

	@Override
	public BlockArray rotate180Degrees(int ox, int oz) {
		FilledBlockArray b = (FilledBlockArray)super.rotate180Degrees(ox, oz);
		for (Coordinate c : data.keySet()) {
			BlockCheck bc = data.get(c);
			Coordinate c2 = c.rotate180About(ox, oz);
			b.data.put(c2, bc);
		}
		return b;
	}

	@Override
	public void clear() {
		super.clear();
		data.clear();
	}

	@Override
	public BlockArray flipX() {
		FilledBlockArray b = (FilledBlockArray)super.flipX();
		for (Coordinate c : data.keySet()) {
			BlockCheck bc = data.get(c);
			Coordinate c2 = new Coordinate(-c.xCoord, c.yCoord, c.zCoord);
			b.data.put(c2, bc);
		}
		return b;
	}

	@Override
	public BlockArray flipZ() {
		FilledBlockArray b = (FilledBlockArray)super.flipZ();
		for (Coordinate c : data.keySet()) {
			BlockCheck bc = data.get(c);
			Coordinate c2 = new Coordinate(c.xCoord, c.yCoord, -c.zCoord);
			b.data.put(c2, bc);
		}
		return b;
	}

	public Collection<Coordinate> getAllLocationsOf(BlockCheck key) {
		HashSet<Coordinate> set = new HashSet();
		for (Coordinate c : data.keySet()) {
			BlockCheck bc = data.get(c);
			if (bc.match(key)) {
				set.add(c);
			}
		}
		return set;
	}

	public boolean isSpaceEmpty(World world, boolean allowSoft) {
		for (Coordinate c : this.keySet()) {
			Block b = c.getBlock(world);
			if (b.isAir(world, c.xCoord, c.yCoord, c.zCoord) || (allowSoft && ReikaWorldHelper.softBlocks(world, c.xCoord, c.yCoord, c.zCoord))) {

			}
			else {
				return false;
			}
		}
		return true;
	}

	public static class MultiKey implements BlockCheck {

		private ArrayList<BlockCheck> keys = new ArrayList();

		public void add(BlockCheck key) {
			if (!keys.contains(key))
				keys.add(key);
		}

		@Override
		public boolean matchInWorld(World world, int x, int y, int z) {
			for (BlockCheck b : keys) {
				if (b.matchInWorld(world, x, y, z))
					return true;
			}
			return false;
		}

		@Override
		public boolean match(Block b, int meta) {
			for (BlockCheck c : keys) {
				if (c.match(b, meta))
					return true;
			}
			return false;
		}

		public void place(World world, int x, int y, int z, int flags) {
			keys.get(0).place(world, x, y, z, flags);
		}

		@Override
		public String toString() {
			return keys.toString();
		}

		@Override
		public ItemStack asItemStack() {
			return keys.get(0).asItemStack();
		}

		public BlockKey asBlockKey() {
			return keys.get(0).asBlockKey();
		}

		public ItemStack getDisplay() {
			return this.asItemStack();
		}

		@Override
		public boolean match(BlockCheck bc) {
			return bc instanceof MultiKey && ((MultiKey)bc).keys.equals(keys);
		}

		public List<BlockCheck> viewKeys() {
			return Collections.unmodifiableList(keys);
		}

	}

	private static class FluidCheck implements BlockCheck {

		public final Fluid fluid;
		public boolean needsSourceBlock = true;
		public boolean allowSourceBlock = true;

		private FluidCheck(Fluid f) {
			if (!f.canBePlacedInWorld())
				throw new MisuseException("You cannot require non-placeable fluids!");
			fluid = f;
		}

		@Override
		public boolean matchInWorld(World world, int x, int y, int z) {
			return this.match(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
		}

		@Override
		public boolean match(Block b, int meta) {
			boolean fmatch = ReikaFluidHelper.lookupFluidForBlock(b) == fluid;
			if (!fmatch)
				return false;
			if (allowSourceBlock) {
				if (needsSourceBlock)
					return this.isSource(b, meta);
				else
					return true;
			}
			else {
				return !this.isSource(b, meta);
			}
		}

		private boolean isSource(Block b, int meta) {
			return b instanceof BlockFluidFinite ? meta == 7 : meta == 0;
		}

		@Override
		public void place(World world, int x, int y, int z, int flags) {
			world.setBlock(x, y, z, this.getBlock(), allowSourceBlock ? 0 : 1, flags);
		}

		private Block getBlock() {
			return fluid.getBlock();
		}

		@Override
		public ItemStack asItemStack() {
			ItemStack is = ReikaItemHelper.getContainerForFluid(fluid);
			return is != null ? is : new ItemStack(this.getBlock());
		}

		public BlockKey asBlockKey() {
			return new BlockKey(this.getBlock(), 0);
		}

		public ItemStack getDisplay() {
			return new ItemStack(this.getBlock());
		}

		@Override
		public boolean match(BlockCheck bc) {
			return bc instanceof FluidCheck && ((FluidCheck)bc).fluid == fluid;
		}

	}

	public static class EmptyCheck implements BlockCheck {

		public final boolean allowNonSolid;
		public final boolean allowSoft;
		private final Collection<Block> exceptions;

		private EmptyCheck(boolean soft, boolean nonsolid, Block... exc) {
			allowNonSolid = nonsolid;
			allowSoft = soft;
			exceptions = ReikaJavaLibrary.makeListFromArray(exc);
		}

		@Override
		public boolean matchInWorld(World world, int x, int y, int z) {
			Block b = world.getBlock(x, y, z);
			if (exceptions.contains(b))
				return false;
			if (b == Blocks.air || b.isAir(world, x, y, z))
				return true;
			if (allowSoft && ReikaWorldHelper.softBlocks(world, x, y, z))
				return true;
			if (allowNonSolid && b.getCollisionBoundingBoxFromPool(world, x, y, z) == null)
				return true;
			return false;
		}

		@Override
		public boolean match(Block b, int meta) {
			if (exceptions.contains(b))
				return false;
			if (b == Blocks.air || b instanceof BlockAir)
				return true;
			if (allowSoft && ReikaWorldHelper.softBlocks(b))
				return true;
			if (allowNonSolid && b.getMaterial().blocksMovement())
				return true;
			return false;
		}

		@Override
		public void place(World world, int x, int y, int z, int flags) {
			world.setBlock(x, y, z, Blocks.air);
		}

		@Override
		public String toString() {
			return "[Empty]";
		}

		@Override
		public ItemStack asItemStack() {
			return null;
		}

		public BlockKey asBlockKey() {
			return new BlockKey(Blocks.air);
		}

		public ItemStack getDisplay() {
			return null;
		}

		@Override
		public boolean match(BlockCheck bc) {
			if (bc instanceof EmptyCheck) {
				EmptyCheck ec = (EmptyCheck)bc;
				return ec.allowNonSolid == allowNonSolid && ec.allowSoft == allowSoft && ec.exceptions.equals(exceptions);
			}
			return false;
		}

	}

	private static class BasicTileEntityCheck implements TileEntityCheck {

		private final BlockKey block;
		private final Class tileClass;
		private final NBTTagCompound matchTag;
		private WeakReference<TileEntity> tileRef;

		private BasicTileEntityCheck(TileEnum te, String... tags) {
			this(te.getBlock(), te.getBlockMetadata(), te.getTEClass(), tags);
		}

		private BasicTileEntityCheck(Block b, int meta, Class<? extends TileEntity> c, String... tags) {
			block = new BlockKey(b, meta);
			matchTag = new NBTTagCompound();
			tileClass = c;
		}

		private BasicTileEntityCheck(Block b, int meta, TileEntity te, String... tags) {
			this(b, meta, te.getClass(), tags);
			NBTTagCompound tag = new NBTTagCompound();
			te.writeToNBT(tag);
			for (int i = 0; i < tags.length; i++) {
				NBTBase nbt = tag.getTag(tags[i]);
				if (nbt != null)
					matchTag.setTag(tags[i], nbt);
			}
			tileRef = new WeakReference(te);
		}

		public ItemStack asItemStack() {
			return new ItemStack(block.blockID, 1, block.hasMetadata() ? block.metadata : 0);
		}

		public ItemStack getDisplay() {
			return this.asItemStack();
		}

		public boolean match(Block b, int meta) {
			return b == block.blockID && (!block.hasMetadata() || meta == block.metadata);
		}

		public boolean matchInWorld(World world, int x, int y, int z) {
			return this.match(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z)) && this.matchTile(world.getTileEntity(x, y, z));
		}

		private boolean matchTile(TileEntity te) {
			if (te == null || te.getClass() != tileClass)
				return false;
			NBTTagCompound tag = new NBTTagCompound();
			te.writeToNBT(tag);
			return ReikaNBTHelper.tagContains(tag, matchTag);
		}

		@Override
		public void place(World world, int x, int y, int z, int flags) {
			world.setBlock(x, y, z, block.blockID, block.hasMetadata() ? block.metadata : 0, flags);
			TileEntity te = world.getTileEntity(x, y, z);
			if (te != null && te.getClass() == tileClass) {
				NBTTagCompound NBT = new NBTTagCompound();
				te.writeToNBT(NBT);
				ReikaNBTHelper.overwriteNBT(NBT, matchTag);
				te.readFromNBT(NBT);
			}
		}

		@Override
		public BlockKey asBlockKey() {
			return block;
		}

		@Override
		public TileEntity getTileEntity() {
			return tileRef.get();
		}

		@Override
		public String toString() {
			return block.toString()+"; NBT "+matchTag;
		}

		@Override
		public boolean match(BlockCheck bc) {
			if (bc instanceof BasicTileEntityCheck) {
				BasicTileEntityCheck bt = (BasicTileEntityCheck)bc;
				return bt.block.equals(block) && bt.tileClass == tileClass && bt.matchTag.equals(matchTag);
			}
			return false;
		}
	}

	public static interface BlockMatchFailCallback {

		public void onBlockFailure(World world, int x, int y, int z, BlockCheck seek);

	}

	public static interface PlacementExclusionHook {

		public boolean skipPlacement(Coordinate c, BlockCheck bc);

	}

}
