package Reika.DragonAPI.Instantiable.Data.Collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class ItemCollection {

	private final ArrayList<ItemStack> data = new ArrayList();

	public ItemCollection() {

	}

	public ItemCollection(Collection<ItemStack> c) {
		this.add(c);
	}

	public void add(ItemStack is) {
		data.add(is);
	}

	public void add(Collection<ItemStack> c) {
		data.addAll(c);
	}

	public int count() {
		int ret = 0;
		for (ItemStack is : data) {
			ret += is.stackSize;
		}
		return ret;
	}

	public void drop(World world, int x, int y, int z) {
		for (ItemStack is : data) {
			while (is.stackSize > 0) {
				int num = Math.min(is.stackSize, is.getMaxStackSize());
				ItemStack is2 = ReikaItemHelper.getSizedItemStack(is, num);
				is.stackSize -= num;
				ReikaItemHelper.dropItem(world, x+world.rand.nextDouble(), y+world.rand.nextDouble(), z+world.rand.nextDouble(), is2);
			}
		}
	}

	@Override
	public String toString() {
		return data.toString();
	}

	public void clear() {
		data.clear();
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public void writeToNBT(NBTTagCompound NBT) {
		ReikaNBTHelper.writeCollectionToNBT(data, NBT, "items");
	}

	public void readFromNBT(NBTTagCompound NBT) {
		ReikaNBTHelper.readCollectionFromNBT(data, NBT, "items");
	}

	public int removeItems(int amt) {
		int ret = 0;
		Iterator<ItemStack> it = data.iterator();
		while (it.hasNext()) {
			ItemStack is = it.next();
			int rem = Math.min(is.stackSize, amt);
			is.stackSize -= rem;
			ret += rem;
			amt -= rem;
			if (is.stackSize <= 0)
				it.remove();
			if (amt <= 0)
				break;
		}
		return ret;
	}

}
