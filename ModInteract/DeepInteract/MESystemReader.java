/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.DeepInteract;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.implementations.tiles.IChestOrDrive;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IMachineSet;
import appeng.api.networking.crafting.ICraftingCallback;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.security.PlayerSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.ICellProvider;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AECableType;
import appeng.api.util.IReadOnlyCollection;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class MESystemReader implements IMEMonitorHandlerReceiver<IAEItemStack> {

	private static Class gridCache;
	private static Field activeCellProviders;
	private static Object tickHandlerInstance;
	private static Method getNetworks;

	private static Collection<MESystemEffect> systemEffects = new ArrayList();

	private final IGridNode node;
	private final BaseActionSource actionSource;

	private ICraftingRequester requester = null;

	private final IdentityHashMap<Future<ICraftingJob>, CraftCompleteCallback> crafting = new IdentityHashMap();
	private final MultiMap<CraftCompleteCallback, ICraftingLink> craftingLinks = new MultiMap().setNullEmpty();
	private final MultiMap<KeyedItemStack, ChangeCallback> changeCallbacks = new MultiMap();

	private Object monitorToken = new Object();

	public final boolean isEmpty;

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

		IReadOnlyCollection<IGridNode> nodes = ign.getGrid().getNodes();
		isEmpty = nodes.isEmpty() || (nodes.size() == 1 && nodes.iterator().next() == node);
	}

	public MESystemReader setRequester(ICraftingRequester icr) {
		requester = icr;
		return this;
	}

	public MESystemReader addCallback(ItemStack is, ChangeCallback call) {
		changeCallbacks.addValue(new KeyedItemStack(is).setSimpleHash(true).setIgnoreNBT(true), call);
		this.getStorage().addListener(this, monitorToken);
		return this;
	}

	public void clearCallbacks() {
		changeCallbacks.clear();
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

	/** Now NBT-Sensitive */
	public ItemHashMap<Long> getMESystemContents() {
		ItemHashMap<Long> map = new ItemHashMap().enableNBT();
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

	public Collection<ItemStack> getRawMESystemContents() {
		Collection<ItemStack> c = new ArrayList();
		if (node == null || node.getGrid() == null || this.getStorage() == null) {
			return c;
		}
		for (IAEItemStack iae : this.getStorage().getStorageList()) {
			if (iae.isItem() && iae.isMeaningful()) {
				c.add(iae.getItemStack());
			}
		}
		return c;
	}

	public static IAEItemStack createAEStack(ItemStack is) {
		return AEApi.instance().storage().createItemStack(is);
	}

	public static HashSet<ICellProvider> getAllCellContainers(IStorageGrid isg) {
		try {
			if (isg.getClass() == gridCache) {
				HashSet<ICellProvider> set = (HashSet<ICellProvider>)activeCellProviders.get(isg);
				return set;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static IChestOrDrive getContainer(ICellProvider icp) {
		return null;
	}

	/** Returns how many items removed */
	public long removeItem(ItemStack is, boolean simulate, boolean nbt) {
		IMEMonitor<IAEItemStack> mon = this.getStorage();
		if (!nbt) {
			Collection<IAEItemStack> c = this.getFuzzyItemList(is, FuzzyMode.IGNORE_ALL);
			IAEItemStack most = null;
			for (IAEItemStack iae : c) {
				if ((most == null || iae.getStackSize() >= most.getStackSize()) && is.getItem() == iae.getItem() && (is.getItemDamage() == iae.getItemDamage() || is.getItemDamage() == OreDictionary.WILDCARD_VALUE)) {
					most = iae;
				}
			}
			if (most == null)
				return 0;
			most.setStackSize(is.stackSize);
			ExtractedItem ei = this.extract(most, simulate);
			return ei != null ? ei.amount : 0;
		}
		ExtractedItem ei = this.extract(this.createAEStack(is), simulate);
		return ei != null ? ei.amount : 0;
	}

	/** Returns how many items removed. But Fuzzy! :D */
	public ExtractedItem removeItemFuzzy(ItemStack is, boolean simulate, FuzzyMode fz, boolean oredict, boolean nbt) {
		IMEMonitor<IAEItemStack> mon = this.getStorage();
		IAEItemStack ae = this.createAEStack(is);
		Collection<IAEItemStack> c = this.getFuzzyItemList(is, fz);
		IAEItemStack most = null;
		for (IAEItemStack iae : c) {
			if ((most == null || iae.getStackSize() >= most.getStackSize()) && (oredict || is.getItem() == iae.getItem()) && (!nbt || ReikaNBTHelper.areNBTTagsEqual(is.stackTagCompound, (iae.hasTagCompound() ? iae.getTagCompound().getNBTTagCompoundCopy() : null)))) {
				most = iae;
			}
		}
		if (most == null)
			return null;
		most.setStackSize(is.stackSize);
		return most != null ? this.extract(most, simulate) : null;
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
		Collection<IAEItemStack> c2 = new ArrayList();
		for (IAEItemStack iae : c) {
			ExtractedItem ei = this.extract(iae, true);
			if (ei != null) {
				IAEItemStack iae2 = this.createAEStack(ei.getItem());
				iae2.setStackSize(ei.amount);
				c2.add(iae2);
			}
		}
		return c2;
	}

	private ExtractedItem extract(IAEItemStack ae, boolean simulate) {
		IAEItemStack is = this.getStorage().extractItems(ae, simulate ? Actionable.SIMULATE : Actionable.MODULATE, actionSource);
		return is != null ? new ExtractedItem(is) : null;
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
		ExtractedItem ei = this.removeItemFuzzy(ReikaItemHelper.getSizedItemStack(is, Integer.MAX_VALUE), true, fz, ore, nbt);
		return ei != null ? ei.amount : 0;
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

	@Override
	public boolean isValid(Object verificationToken) {
		return verificationToken.equals(monitorToken);
	}

	@Override
	public void postChange(IBaseMonitor<IAEItemStack> monitor, Iterable<IAEItemStack> change, BaseActionSource actionSource) {
		for (IAEItemStack iae : change) {
			KeyedItemStack ks = new KeyedItemStack(iae.getItemStack()).setSimpleHash(true).setIgnoreNBT(true);
			Collection<ChangeCallback> c = changeCallbacks.get(ks);
			for (ChangeCallback cc : c) {
				cc.onItemChange(iae);
			}
		}
	}

	@Override
	public void onListUpdate() {
		for (ChangeCallback cc : changeCallbacks.allValues(false)) {
			cc.onItemChange(null);
		}
	}

	public static Collection<IGrid> getAllMENetworks() {
		try {
			return (Collection<IGrid>)getNetworks.invoke(tickHandlerInstance); //TickHandler.INSTANCE.getGridList();
		}
		catch (Exception e) {
			e.printStackTrace();
			return new ArrayList();
		}
	}

	public static void registerMESystemEffect(MESystemEffect effect) {
		systemEffects.add(effect);
	}

	public static void registerEffectHandler() {
		if (!systemEffects.isEmpty()) {
			TickRegistry.instance.registerTickHandler(new EffectHandler());
		}
	}

	private static class EffectHandler implements TickHandler {

		@Override
		public void tick(TickType type, Object... tickData) {
			Collection<IGrid> grids = null;
			long tick = DimensionManager.getWorld(0).getTotalWorldTime();
			for (MESystemEffect effect : systemEffects) {
				if (tick%effect.getTickFrequency() == 0) {
					if (grids == null) {
						grids = getAllMENetworks();
					}
					for (IGrid g : grids) {
						if (!g.isEmpty()) {
							effect.performEffect(g);
						}
					}
				}
			}
		}

		@Override
		public EnumSet<TickType> getType() {
			return EnumSet.of(TickType.SERVER);
		}

		@Override
		public boolean canFire(Phase p) {
			return p == Phase.END;
		}

		@Override
		public String getLabel() {
			return "ME System Tick";
		}

	}

	static {
		try {
			Class c = Class.forName("appeng.hooks.TickHandler");
			Field inst = c.getField("INSTANCE");
			tickHandlerInstance = inst.get(null);
			getNetworks = c.getDeclaredMethod("getGridList");

			gridCache = Class.forName("appeng.me.cache.GridStorageCache");
			activeCellProviders = gridCache.getDeclaredField("activeCellProviders");
			activeCellProviders.setAccessible(true);
		}
		catch (Exception e) {
			DragonAPICore.logError("Could not build getter to ME system list!");
			ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.APPENG, e);
		}
	}

	public static interface CraftCompleteCallback {

		public void onCraftingLinkReturned(ICraftingLink link);
		public void onCraftingComplete(ICraftingLink link);

	}

	public static interface ChangeCallback {

		/** May be null if a total AE list rebuild. Assume that means what you care about changed. */
		void onItemChange(IAEItemStack iae);

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

	public static enum MatchMode {
		EXACT(0xffcc00, "Exact Match"),
		FUZZY(0x00aaff, "Fuzzy Match"),
		FUZZYORE(0x00ff00, "Fuzzy/Ore Match"),
		FUZZYNBT(0xaa00ff, "Fuzzy/Ore Match, Ignore NBT");

		public final int color;
		public final String desc;

		public static final MatchMode[] list = values();

		private MatchMode(int c, String s) {
			color = c;
			desc = s;
		}

		public long countItems(MESystemReader net, ItemStack is) {
			switch(this) {
				case EXACT:
					return net.getItemCount(is, true);
				case FUZZY:
					return net.getFuzzyItemCount(is, FuzzyMode.IGNORE_ALL, false, true);
				case FUZZYORE:
					return net.getFuzzyItemCount(is, FuzzyMode.IGNORE_ALL, true, true);
				case FUZZYNBT:
					return net.getFuzzyItemCount(is, FuzzyMode.IGNORE_ALL, true, false);
			}
			return 0;
		}

		public ExtractedItem removeItems(MESystemReader net, ItemStack is, boolean simulate) {
			switch(this) {
				case EXACT:
					long amt = net.removeItem(is, simulate, true);
					return amt != 0 ? new ExtractedItem(is, amt) : null;
				case FUZZY:
					return net.removeItemFuzzy(is, simulate, FuzzyMode.IGNORE_ALL, false, true);
				case FUZZYORE:
					return net.removeItemFuzzy(is, simulate, FuzzyMode.IGNORE_ALL, true, true);
				case FUZZYNBT:
					return net.removeItemFuzzy(is, simulate, FuzzyMode.IGNORE_ALL, true, false);
			}
			return null;
		}

		public MatchMode next() {
			return list[(this.ordinal()+1)%list.length];
		}

		public boolean compare(ItemStack is1, ItemStack is2) {
			switch(this) {
				case EXACT:
					return ReikaItemHelper.matchStacks(is1, is2) && ItemStack.areItemStackTagsEqual(is1, is2);
				case FUZZY:
					return is1.getItem() == is2.getItem() && ItemStack.areItemStackTagsEqual(is1, is2);
				case FUZZYNBT:
					return is1.getItem() == is2.getItem();
				case FUZZYORE:
					return ReikaItemHelper.checkOreDictOverlap(is1, is2);
				default:
					return false;
			}
		}
	}

	public static final class ExtractedItem {

		private final ItemStack item;
		public final long amount;

		private ExtractedItem(ItemStack is, long amt) {
			item = is;
			amount = amt;
		}

		private ExtractedItem(IAEItemStack iae) {
			this(iae.getItemStack(), iae.getStackSize());
		}

		public ItemStack getItem() {
			return item.copy();
		}

	}

	public static interface MESystemEffect {

		public void performEffect(IGrid grid);

		/** The smaller you make this, the more computationally expensive it becomes. */
		public int getTickFrequency();

	}

	public static abstract class ItemInSystemEffect implements MESystemEffect {

		private final ItemStack itemKey;

		public ItemInSystemEffect(ItemStack is) {
			itemKey = is;
		}

		@Override
		public final void performEffect(IGrid grid) {
			if (grid.isEmpty() || grid.getNodes().isEmpty() || !grid.getNodes().iterator().hasNext())
				return;
			IMachineSet iah = grid.getMachines(IActionHost.class);
			MESystemReader me = null;
			if (iah.isEmpty()) {
				IReadOnlyCollection<IGridNode> ign = grid.getNodes();
				try {
					Iterator<IGridNode> it = ign.iterator();
					if (it.hasNext()) {
						IGridNode ign2 = it.next();
						me = new MESystemReader(ign2, new FakeActionSource(ign2));
					}
				}
				catch (Exception e) {
					DragonAPICore.logError("Detected invalid ME system when running "+this+": "+grid.getNodes()+"\n; Threw exception on access: ");
					e.printStackTrace();
				}
			}
			else {
				IActionHost ia = (IActionHost)iah.iterator().next();
				me = new MESystemReader(ia.getActionableNode(), new MachineSource(ia));
			}
			if (me == null)
				return;
			long amt = me.getItemCount(itemKey, itemKey.stackTagCompound != null);
			if (amt > 0) {
				this.doEffect(grid, amt);
			}
		}

		protected abstract void doEffect(IGrid grid, long amt);

	}

	private static class FakeActionSource extends MachineSource {

		private FakeActionSource(IGridNode ign) {
			super(new FakeActionHost(ign));
		}
	}

	private static class GenericActionSource extends BaseActionSource {

		@Override
		public boolean isMachine()
		{
			return true;
		}
	}

	private static class FakeActionHost implements IActionHost {

		private IGridNode node;

		private FakeActionHost(IGridNode ign) {
			node = ign;
		}

		@Override
		public IGridNode getGridNode(ForgeDirection dir) {
			return node;
		}

		@Override
		public AECableType getCableConnectionType(ForgeDirection dir) {
			return AECableType.GLASS;
		}

		@Override
		public void securityBreak() {

		}

		@Override
		public IGridNode getActionableNode() {
			return node;
		}

	}
}
