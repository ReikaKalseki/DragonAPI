/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.DeepInteract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingCallback;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.security.PlayerSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEItemStack;

public class MESystemReader {

	private final IGridNode node;
	private final BaseActionSource actionSource;

	private ICraftingRequester requester = null;

	private final IdentityHashMap<Future<ICraftingJob>, CraftCompleteCallback> crafting = new IdentityHashMap();
	private final MultiMap<CraftCompleteCallback, ICraftingLink> craftingLinks = new MultiMap().setNullEmpty();

	public MESystemReader(IGridNode ign, EntityPlayer ep) {
		this(ign, new PlayerSource(ep, null));
	}

	public MESystemReader(IGridNode ign, IActionHost iah) {
		this(ign, new MachineSource(iah));
	}

	/** For loading all the data of the old reader */
	public MESystemReader(IGridNode ign, MESystemReader reader) {
		this(ign, reader.actionSource);

		crafting.putAll(reader.crafting);
		craftingLinks.putAll(reader.craftingLinks);

		requester = reader.requester;
	}

	private MESystemReader(IGridNode ign, BaseActionSource src) {
		node = ign;
		actionSource = src;
	}

	public MESystemReader setRequester(ICraftingRequester icr) {
		requester = icr;
		return this;
	}

	private IMEMonitor<IAEItemStack> getStorage() {
		if (node == null || node.getGrid() == null)
			return null;
		return ((IStorageGrid)node.getGrid().getCache(IStorageGrid.class)).getItemInventory();
	}

	private ICraftingGrid getCraftingGrid() {
		if (node == null || node.getGrid() == null)
			return null;
		return ((ICraftingGrid)node.getGrid().getCache(ICraftingGrid.class));
	}

	public ItemHashMap<Long> getMESystemContents() {
		ItemHashMap<Long> map = new ItemHashMap();
		if (node == null || node.getGrid() == null || this.getStorage() == null) {
			/*
			StringBuilder sb = new StringBuilder();
			sb.append(node+"/");
			if (node != null) {
				sb.append(node.getGrid());
				if (node.getGrid() != null) {
					sb.append(node.getGrid().getCache(IStorageGrid.class)+"/");
					if (node.getGrid().getCache(IStorageGrid.class) != null) {
						sb.append(((IStorageGrid)node.getGrid().getCache(IStorageGrid.class)).getItemInventory());
					}
					else {
						sb.append("/[No storage grid cache]");
					}
				}
				else {
					sb.append("/[No grid]");
				}
			}
			else {
				sb.append("/[No node]");
			}
			DragonAPICore.log("No grid: "+sb.toString());*/
			return map;
		}
		for (IAEItemStack iae : this.getStorage().getStorageList()) {
			if (iae.isItem() && iae.isMeaningful()) {
				map.put(iae.getItemStack(), iae.getStackSize());
			}
		}
		return map;
	}

	private IAEItemStack createAEStack(ItemStack is) {
		return AEApi.instance().storage().createItemStack(is);
	}

	/** Returns how many items removed */
	public long removeItem(ItemStack is, boolean simulate, boolean nbt) {
		IMEMonitor<IAEItemStack> mon = this.getStorage();
		if (!nbt) {
			Collection<IAEItemStack> c = this.getFuzzyItemList(is, FuzzyMode.IGNORE_ALL);
			IAEItemStack most = null;
			for (IAEItemStack iae : c) {
				if ((most == null || iae.getStackSize() >= most.getStackSize()) && is.getItem() == iae.getItem() && is.getItemDamage() == iae.getItemDamage()) {
					most = iae;
				}
			}
			if (most == null)
				return 0;
			most.setStackSize(is.stackSize);
			return most != null ? this.extract(most, simulate) : 0;
		}
		return this.extract(this.createAEStack(is), simulate);
	}

	/** Returns how many items removed. But Fuzzy! :D */
	public long removeItemFuzzy(ItemStack is, boolean simulate, FuzzyMode fz, boolean oredict, boolean nbt) {
		IMEMonitor<IAEItemStack> mon = this.getStorage();
		IAEItemStack ae = this.createAEStack(is);
		Collection<IAEItemStack> c = this.getFuzzyItemList(is, fz);
		IAEItemStack most = null;
		for (IAEItemStack iae : c) {
			if ((most == null || iae.getStackSize() >= most.getStackSize()) && (oredict || is.getItem() == iae.getItem()) && (!nbt || ReikaNBTHelper.areNBTTagsEqual(is.stackTagCompound, iae.getTagCompound().getNBTTagCompoundCopy()))) {
				most = iae;
			}
		}
		if (most == null)
			return 0;
		most.setStackSize(is.stackSize);
		return most != null ? this.extract(most, simulate) : 0;
	}

	private Collection<IAEItemStack> getFuzzyItemList(ItemStack is, FuzzyMode fz) {
		IMEMonitor<IAEItemStack> mon = this.getStorage();
		IAEItemStack ae = this.createAEStack(is);
		Collection<IAEItemStack> c = new ArrayList(mon.getStorageList().findFuzzy(ae, fz)); //wrap in list so we can add entries
		if (is.getItemDamage() == OreDictionary.WILDCARD_VALUE) { //since AE does not handle this, a simple hack
			ItemStack cp = is.copy();
			cp.setItemDamage(0);
			ae = this.createAEStack(cp);
			c.addAll(mon.getStorageList().findFuzzy(ae, fz));
		}
		return c;
	}

	private long extract(IAEItemStack ae, boolean simulate) {
		IAEItemStack is = this.getStorage().extractItems(ae, simulate ? Actionable.SIMULATE : Actionable.MODULATE, actionSource);
		return is != null ? is.getStackSize() : 0;
	}

	/** Returns how many items NOT added */
	public long addItem(ItemStack is, boolean simulate) {
		IAEItemStack ret = this.getStorage().injectItems(this.createAEStack(is), simulate ? Actionable.SIMULATE : Actionable.MODULATE, actionSource);
		return ret != null ? ret.getStackSize() : 0;
	}

	public long getItemCount(ItemStack is, boolean nbt) {
		return this.removeItem(ReikaItemHelper.getSizedItemStack(is, Integer.MAX_VALUE), true, nbt);
	}

	public long getFuzzyItemCount(ItemStack is, FuzzyMode fz, boolean ore, boolean nbt) {
		return this.removeItemFuzzy(ReikaItemHelper.getSizedItemStack(is, Integer.MAX_VALUE), true, fz, ore, nbt);
	}

	public void triggerFuzzyCrafting(World world, ItemStack is, long amt, ICraftingCallback callback, CraftCompleteCallback callback2) {
		this.triggerCrafting(world, is, amt, callback, callback2);
	}

	/** Triggers the native crafting system to craft a given amount of a given item. Callbacks is optional. */
	public void triggerCrafting(World world, ItemStack is, long amt, ICraftingCallback callback, CraftCompleteCallback callback2) {
		if (node == null || node.getGrid() == null)
			return;
		IAEItemStack iae = this.createAEStack(is);
		iae.setStackSize(amt);
		if (callback == null) {
			callback = new NoopCraftingCallback();
		}
		Future<ICraftingJob> f = this.getCraftingGrid().beginCraftingJob(world, node.getGrid(), actionSource, iae, callback);
		if (callback2 != null)
			crafting.put(f, callback2);
	}

	/** You are required to call this on your reader if you want things like crafting triggers to work. */
	public void tick() {
		ICraftingGrid cache = this.getCraftingGrid();
		if (cache != null) {

			HashSet<Future<ICraftingJob>> removeCalls = new HashSet();
			for (Future<ICraftingJob> f : crafting.keySet()) {
				if (f.isDone()) {
					try {
						ICraftingJob job = f.get();
						ICraftingLink l = cache.submitJob(job, requester, null, true, actionSource);
						if (l == null) {
							DragonAPICore.logError(job+" to craft "+job.getOutput()+" returned a null link!");
						}
						else {
							CraftCompleteCallback ccc = crafting.get(f);
							if (ccc != null) {
								craftingLinks.addValue(ccc, l);
								ccc.onCraftingLinkReturned(l);
							}
						}
						removeCalls.add(f);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
					catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
			}
			for (Future<ICraftingJob> f : removeCalls)
				crafting.remove(f);

			MultiMap<CraftCompleteCallback, ICraftingLink> removeLinks = new MultiMap().setNullEmpty();
			for (CraftCompleteCallback ccc : craftingLinks.keySet()) {
				Collection<ICraftingLink> c = craftingLinks.get(ccc);
				if (c != null && !c.isEmpty()) {
					for (ICraftingLink l : c) {
						if (l.isDone()) {
							ccc.onCraftingComplete(l);
							removeLinks.addValue(ccc, l);
						}
					}
				}
			}
			//DragonAPICore.log("CRAFT: "+craftingLinks, !craftingLinks.isEmpty());
			//DragonAPICore.log("REM: "+removeLinks, !removeLinks.isEmpty());
			for (CraftCompleteCallback ccc : removeLinks.keySet()) {
				Collection<ICraftingLink> c = removeLinks.get(ccc);
				if (c != null) {
					for (ICraftingLink l : c) {
						craftingLinks.remove(ccc, l);
					}
				}
			}
		}
	}

	public static interface CraftCompleteCallback {

		public void onCraftingLinkReturned(ICraftingLink link);
		public void onCraftingComplete(ICraftingLink link);

	}
	/*
	private static class AEStack implements IAEItemStack, Comparable<AEStack> {

		private final ItemStack item;
		private long size;

		private AEStack(ItemStack is) {
			this(is, is.stackSize);
		}

		private AEStack(ItemStack is, long s) {
			item = is;
			size = s;
		}

		@Override
		public long getStackSize() {
			return size;
		}

		@Override
		public IAEItemStack setStackSize(long stackSize) {
			size = stackSize;
			return this;
		}

		@Override
		public long getCountRequestable() {
			return 0;
		}

		@Override
		public IAEItemStack setCountRequestable(long countRequestable) {
			return null;
		}

		@Override
		public boolean isCraftable() {
			return false;
		}

		@Override
		public IAEItemStack setCraftable(boolean isCraftable) {
			return this;
		}

		@Override
		public IAEItemStack reset() {
			return this;
		}

		@Override
		public boolean isMeaningful() {
			return true;
		}

		@Override
		public void incStackSize(long i) {
			size += i;
		}

		@Override
		public void decStackSize(long i) {
			size -= i;
		}

		@Override
		public void incCountRequestable(long i) {

		}

		@Override
		public void decCountRequestable(long i) {

		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			item.writeToNBT(tag);
			tag.setLong("fullsize", size);
		}

		@Override
		public boolean fuzzyComparison(Object st, FuzzyMode mode) {
			return false;
		}

		@Override
		public void writeToPacket(ByteBuf data) throws IOException {
			data.writeInt(Item.getIdFromItem(this.getItem()));
			data.writeInt(item.getItemDamage());
			data.writeLong(size);
			if (item.stackTagCompound != null) {
				data.writeBytes(CompressedStreamTools.compress(item.stackTagCompound));
			}
		}

		@Override
		public IAEItemStack empty() {
			return this;
		}

		@Override
		public IAETagCompound getTagCompound() {
			return null;
		}

		@Override
		public boolean isItem() {
			return true;
		}

		@Override
		public boolean isFluid() {
			return false;
		}

		@Override
		public StorageChannel getChannel() {
			return StorageChannel.ITEMS;
		}

		@Override
		public int compareTo(AEStack o) {
			return Item.getIdFromItem(this.getItem())-Item.getIdFromItem(o.getItem());
		}

		@Override
		public ItemStack getItemStack() {
			return item.copy();
		}

		@Override
		public IAEItemStack copy() {
			return new AEStack(item, size);
		}

		@Override
		public boolean hasTagCompound() {
			return item.stackTagCompound != null;
		}

		@Override
		public void add(IAEItemStack option) {
			size += option.getStackSize();
		}

		@Override
		public Item getItem() {
			return item.getItem();
		}

		@Override
		public int getItemDamage() {
			return item.getItemDamage();
		}

		@Override
		public boolean sameOre(IAEItemStack is) {
			return OreDictionary.getOreID(item) == OreDictionary.getOreID(is.getItemStack());
		}

		@Override
		public boolean isSameType(IAEItemStack otherStack) {
			return ReikaItemHelper.matchStacks(otherStack.getItemStack(), item);
		}

		@Override
		public boolean isSameType(ItemStack stored) {
			return ReikaItemHelper.matchStacks(stored, item);
		}

	}*/

	private static class NoopCraftingCallback implements ICraftingCallback {

		@Override
		public void calculationComplete(ICraftingJob job) {

		}

	}
}
