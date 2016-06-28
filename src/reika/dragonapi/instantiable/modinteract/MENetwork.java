/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.modinteract;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import reika.dragonapi.ModList;
import reika.dragonapi.exception.MisuseException;
import reika.dragonapi.instantiable.data.blockstruct.BlockArray;
import reika.dragonapi.instantiable.data.immutable.Coordinate;
import reika.dragonapi.instantiable.data.maps.ItemHashMap;
import reika.dragonapi.libraries.registry.ReikaItemHelper;
import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.ICellProvider;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAETagCompound;
import appeng.api.storage.data.IItemList;

public class MENetwork {

	private static final Class baseTile;

	private BlockArray blocks = new BlockArray();
	private final Collection<IMEInventoryHandler> itemCache = new ArrayList();
	private final World world;

	private MENetwork() {
		this(null);
	}

	private MENetwork(World world) {
		this.world = world;
		if (!ModList.APPENG.isLoaded()) {
			throw new MisuseException("How do you plan to create an ME network object without AE installed?!");
		}
	}

	public boolean isEmpty() {
		return blocks.isEmpty();
	}

	public int getSize() {
		return blocks.getSize();
	}

	private void initializeStorage() {
		for (int i = 0; i < blocks.getSize(); i++) {
			Coordinate c = blocks.getNthBlock(i);
			TileEntity te = world.getTileEntity(c.xCoord, c.yCoord, c.zCoord);
			if (te instanceof ICellProvider) {
				ICellProvider icp = (ICellProvider)te;
				List<IMEInventoryHandler> li = icp.getCellArray(StorageChannel.ITEMS);
				for (IMEInventoryHandler ime : li) {
					itemCache.add(ime);
				}
			}
		}
	}

	public ItemHashMap<Long> getMEContents() {
		ItemHashMap<Long> cache = new ItemHashMap();
		ItemList items = new ItemList();
		for (IMEInventoryHandler ime : itemCache) {
			items = (ItemList)ime.getAvailableItems(items);
		}
		for (IAEStack item : items.list) {
			if (item instanceof IAEItemStack) {
				cache.put(((IAEItemStack)item).getItemStack(), item.getStackSize());
			}
		}
		return cache;
	}

	public int removeFromMESystem(ItemStack is, int diff) {
		int rem = 0;
		for (IMEInventoryHandler ime : itemCache) {
			//IAEStack rem = ime.extractItems(new AEStack(is, diff), Actionable.MODULATE, new BaseActionSource());
			//diff -= rem.getStackSize();
			//if (diff <= 0)
			//	break;
			ItemList items = new ItemList();
			items = (ItemList)ime.getAvailableItems(items);
			for (IAEStack item : items.list) {
				if (item instanceof IAEItemStack) {
					IAEItemStack iae = (IAEItemStack)item;
					if (ReikaItemHelper.matchStacks(is, iae.getItemStack())) {
						int dec = (int)Math.min(iae.getStackSize(), diff);
						//ReikaJavaLibrary.pConsole(dec+"/"+diff+"/"+iae.getStackSize());
						//iae.decStackSize(dec);
						//iae.setStackSize(dec);
						IAEStack removed = ime.extractItems(iae, Actionable.MODULATE, new BaseActionSource());
						if (removed != null) {
							removed.decStackSize(dec);
							if (removed.getStackSize() > 0)
								ime.injectItems(removed, Actionable.MODULATE, new BaseActionSource());
							//ReikaJavaLibrary.pConsole(removed.getStackSize());
							diff -= dec;
							rem += dec;
							if (diff <= 0)
								return rem;
						}
					}
				}
			}
			if (ime instanceof TileEntity)
				((TileEntity)ime).markDirty();
		}
		return rem;
		///me terminal does not update clientside, but otherwise works
	}

	@Override
	public String toString() {
		return blocks.toString()+" > "+itemCache.toString();
	}

	public static MENetwork getFromGridHost(IGridHost te, ForgeDirection dir) {
		if (!(te instanceof TileEntity))
			return null;
		TileEntity tile = (TileEntity)te;
		MENetwork net = new MENetwork(tile.worldObj);
		IGrid ig = te.getGridNode(dir).getGrid();
		net.populate(ig);
		return net;
	}

	public static MENetwork getFromGridHost(IGridNode ign) {
		MENetwork net = new MENetwork();
		net.populate(ign.getGrid());
		return net;
	}

	private void populate(IGrid ig) {
		IStorageGrid isg = ig.getCache(IStorageGrid.class);
		IMEMonitor<IAEItemStack> mon = isg.getItemInventory();
		IItemList contained = mon.getStorageList();
	}

	public static MENetwork getConnectedTo(TileEntity te) {
		return getConnectedTo(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
	}

	public static MENetwork getConnectedTo(World world, int x, int y, int z) {
		MENetwork net = new MENetwork(world);
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			TileEntity te = world.getTileEntity(dx, dy, dz);
			if (te != null && baseTile.isAssignableFrom(te.getClass())) {
				net.blocks.addBlockCoordinate(dx, dy, dz);
				recursiveConnect(world, dx, dy, dz, net);
			}
		}
		net.initializeStorage();
		return net;
	}

	private static void recursiveConnect(World world, int x, int y, int z, MENetwork net) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (!net.blocks.hasBlock(dx, dy, dz)) {
				TileEntity te = world.getTileEntity(dx, dy, dz);
				if (te != null && baseTile.isAssignableFrom(te.getClass())) {
					net.blocks.addBlockCoordinate(dx, dy, dz);
					recursiveConnect(world, dx, dy, dz, net);
				}
			}
		}
	}

	static {
		Class c = null;
		try {
			c = Class.forName("appeng.tile.AEBaseTile");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		baseTile = c;
	}

	private static class AEStack implements IAEItemStack, Comparable<AEStack> {

		private final ItemStack stack;

		private AEStack(ItemStack is, int size) {
			stack = ReikaItemHelper.getSizedItemStack(is, size);
		}

		@Override
		public long getStackSize() {
			return stack.stackSize;
		}

		@Override
		public IAEItemStack setStackSize(long l) {
			stack.stackSize = (int)l;
			return this;
		}

		@Override
		public long getCountRequestable() {return 0;}
		@Override
		public IAEItemStack setCountRequestable(long paramLong) {return this;}
		@Override
		public boolean isCraftable() {return false;}
		@Override
		public IAEItemStack setCraftable(boolean paramBoolean) {return this;}
		@Override
		public IAEItemStack reset() {return this;}
		@Override
		public void incStackSize(long l) {stack.stackSize += l;}
		@Override
		public void decStackSize(long l) {stack.stackSize -= l;}
		@Override
		public void incCountRequestable(long paramLong) {}
		@Override
		public void decCountRequestable(long paramLong) {}
		@Override
		public void writeToNBT(NBTTagCompound tag) {stack.writeToNBT(tag);}
		@Override
		public boolean fuzzyComparison(Object obj, FuzzyMode mode) {return false;}
		@Override
		public void writeToPacket(ByteBuf buf) throws IOException {}
		@Override
		public IAEItemStack empty() {return this;}
		@Override
		public IAETagCompound getTagCompound() {return null;}
		@Override
		public boolean isItem() {return true;}
		@Override
		public boolean isFluid() {return false;}
		@Override
		public StorageChannel getChannel() {return StorageChannel.ITEMS;}
		@Override
		public ItemStack getItemStack() {return stack;}
		@Override
		public IAEItemStack copy() {return new AEStack(stack, stack.stackSize);}
		@Override
		public boolean hasTagCompound() {return false;}
		@Override
		public void add(IAEItemStack iae) {}
		@Override
		public Item getItem() {return stack.getItem();}
		@Override
		public int getItemDamage() {return stack.getItemDamage();}
		@Override
		public boolean sameOre(IAEItemStack iae) {return Arrays.equals(OreDictionary.getOreIDs(stack), OreDictionary.getOreIDs(iae.getItemStack()));}
		@Override
		public boolean isSameType(IAEItemStack iae) {return ReikaItemHelper.matchStacks(stack, iae.getItemStack());}
		@Override
		public boolean isSameType(ItemStack is) {return ReikaItemHelper.matchStacks(stack, is);}

		@Override
		public int compareTo(AEStack o) {
			return (int)(o.getStackSize()-this.getStackSize());
		}
		public boolean isMeaningful() {return false;}
		public boolean isMeaninful() {return false;} //for back compat

	}

	private	static class ItemList implements IItemList {

		private final ArrayList<IAEStack> list = new ArrayList();

		@Override
		public String toString() {return list.toString();}
		@Override
		public void add(IAEStack option) {list.add(option);}
		@Override
		public IAEStack findPrecise(IAEStack i) {return null;}
		@Override
		public Collection findFuzzy(IAEStack input, FuzzyMode fuzzy) {return null;}
		@Override
		public boolean isEmpty() {return list.isEmpty(); }
		@Override
		public void addStorage(IAEStack option) {}
		@Override
		public void addCrafting(IAEStack option) {}
		@Override
		public void addRequestable(IAEStack option) {}
		@Override
		public IAEStack getFirstItem() {return list.get(0);}
		@Override
		public int size() {return list.size();}
		@Override
		public Iterator iterator() {return list.iterator();}
		@Override
		public void resetStatus() {list.clear();}
	}

}
